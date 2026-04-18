<template>
  <div class="h-full flex flex-col overflow-hidden">
    <div class="flex justify-between items-center mb-10px flex-shrink-0">
      <div class="flex gap-2">
        <el-select v-model="queryParams.refType" placeholder="业务类型" clearable class="!w-120px" size="small">
          <el-option label="全部" value="" />
          <el-option label="手动操作" value="MANUAL" />
          <el-option label="采购入库" value="PURCHASE" />
          <el-option label="销售出库" value="SALE" />
          <el-option label="生产入库" value="PRODUCTION" />
          <el-option label="装配出库" value="ASSEMBLY" />
          <el-option label="调拨收货" value="TRANSFER_RECEIPT" />
          <el-option label="平台货件" value="SHIPMENT" />
        </el-select>
        <el-input
v-model="queryParams.sellerSku" placeholder="搜索 SKU" clearable class="!w-180px" size="small"
          @keyup.enter="handleSearch">
          <template #prefix>
            <Icon icon="ep:search" />
          </template>
        </el-input>
        <el-date-picker
v-model="queryParams.timeRange" type="daterange" size="small" range-separator="-"
          start-placeholder="开始" end-placeholder="结束" class="!w-240px" value-format="YYYY-MM-DD" />
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="list" stripe border class="flex-1" height="100%">
      <template #empty>
        <el-empty description="暂无历史记录" />
      </template>
      <el-table-column label="操作时间" prop="createTime" width="160" :formatter="dateFormatter" />
      <el-table-column label="操作类型" prop="typeLabel" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getTypeTag(row.type)" size="small">
            {{ row.typeLabel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="SKU" prop="sellerSku" width="150" sortable />
      <el-table-column label="变动数量" prop="count" width="100" align="center">
        <template #default="{ row }">
          <span :class="row.count > 0 ? 'text-green-600' : 'text-red-600'" class="font-mono font-bold">
            {{ row.count > 0 ? '+' : '' }}{{ row.count }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="期末结存" prop="afterCount" width="100" align="center" />
      <el-table-column label="关联单据" prop="billCode" width="160" />
      <el-table-column label="备注" prop="remark" show-overflow-tooltip />
    </el-table>

    <Pagination
class="justify-end mt-10px" :total="total" v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { InventoryBillApi } from '@/app/erplus/api/stock/inventoryBill'
import { ErpInventoryBillTypeOptions } from './constants'

defineOptions({ name: 'WarehouseHistoryTable' })

const props = defineProps<{
  warehouseId: number | null
}>()

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  type: undefined,
  refType: undefined,
  timeRange: [],
  sellerSku: undefined,
})

const getTypeTag = (type: number) => {
  if (type === 10) return 'success'
  if (type === 20) return 'warning'
  return 'info'
}

const getList = async () => {
  if (!props.warehouseId) return
  loading.value = true
  try {
    const params = {
      ...queryParams,
      warehouseId: props.warehouseId,
    }

    // 统一使用明细分页接口，以获取平铺的变动记录和库存快照
    const data = await InventoryBillApi.getInventoryBillItemPage(params)

    list.value = data.list.map(record => {
      // 这里的 count 应该是该仓库对应的变动
      // 逻辑：如果是出库（fromId 等于当前仓库），显示变动为负；如果是入库（toId 等于当前仓库），显示变动为正
      // 注意：由于 ID 可能存在 String/Number 混用，强制转换 String 后对比
      const currentWhId = String(props.warehouseId)
      const fromWhId = String(record.fromId || '')
      const toWhId = String(record.toId || '')
      
      let count = record.qty || 0
      // 调调出逻辑：如果是两步法调拨的发货方，qty 虽然存的是正数（代表调出量），但在流水视图应显示为减少
      // 这种情况下 fromWhId == currentWhId
      if (fromWhId === currentWhId && toWhId !== currentWhId) {
        count = -Math.abs(count)
      } else if (toWhId === currentWhId && fromWhId !== currentWhId) {
        count = Math.abs(count)
      }

      return {
        ...record,
        typeLabel: getBillTypeLabel(record),
        count: count,
        afterCount: record.snapshotQty || 0
      }
    })
    total.value = data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getBillTypeLabel = (record: any) => {
  const typeMap: Record<number, string> = {
    10: '入库',
    20: '出库',
    30: '调拨',
    40: '调整'
  }
  
  // 优先展示具体的业务类型标签 (如: 生产入库, 调拨收货)
  const options = [...(ErpInventoryBillTypeOptions[10] || []), ...(ErpInventoryBillTypeOptions[20] || []), ...(ErpInventoryBillTypeOptions[30] || [])]
  const match = options.find(opt => opt.value === record.refType)
  
  if (match) return match.label
  return typeMap[record.type] || '未知'
}

const handleSearch = () => {
  queryParams.pageNo = 1
  getList()
}

watch(() => props.warehouseId, (newId) => {
  if (newId) {
    queryParams.pageNo = 1
    queryParams.type = undefined
    queryParams.timeRange = []
    queryParams.sellerSku = undefined
    getList()
  }
})

onMounted(() => {
  getList()
})
</script>

<style scoped></style>
