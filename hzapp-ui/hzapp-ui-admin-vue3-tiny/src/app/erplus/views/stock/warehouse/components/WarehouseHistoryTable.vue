<template>
  <div class="h-full flex flex-col overflow-hidden">
    <div class="flex justify-between items-center mb-10px flex-shrink-0">
      <div class="flex gap-2">
        <el-select v-model="queryParams.type" placeholder="记录类型" clearable class="!w-120px" size="small">
          <el-option label="全部" value="" />
          <el-option label="采购入库" :value="10" />
          <el-option label="出库发货" :value="20" />
          <el-option label="调拨变动" :value="30" />
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

    // 如果指定了 SKU，则查询明细分页；否则查询账单分页（按单展示）
    let data
    let isItemView = false

    if (params.sellerSku) {
      data = await InventoryBillApi.getInventoryBillItemPage(params)
      isItemView = true
    } else {
      data = await InventoryBillApi.getInventoryBillPage(params)
    }

    list.value = data.list.map(record => {
      // 这里的 count 应该是该仓库对应的变动
      // 简单逻辑：如果是出库（fromId=warehouseId），数量为负；如果是入库（toId=warehouseId），数量为正
      // 注意：明细查询返回的 record.fromId / toId 是 flattened 后的，逻辑一致
      const direction = record.fromId === props.warehouseId ? -1 : 1

      if (isItemView) {
        // 明细视图：list item 是 InventoryBillItemRespVO (Flat)
        return {
          ...record,
          typeLabel: getBillTypeLabel(record.type),
          count: (record.qty || 0) * direction,
          afterCount: record.snapshotQty || 0
        }
      } else {
        // 账单视图：list item 是 InventoryBillDo (Header)，可能包含 items
        // 如果 WarehouseHistoryTable 指向的是某个仓库的所有变动，
        // 那么理想情况下，后端应该提供一个 InventoryBillItem 的分页查询，带上 Header 信息。
        // 这里为了兼容旧逻辑，我们取 items 的第一个作为代表（或者假设后端做了平铺）
        const firstItem = record.items?.[0] || {}
        return {
          ...record,
          typeLabel: getBillTypeLabel(record.type),
          sellerSku: record.sellerSku || firstItem.sellerSku || '-',
          count: (record.totalCount || firstItem.count || 0) * direction, // Header 通常用 totalCount
          afterCount: record.snapshotQty || firstItem.snapshotQty || 0 // Header 没有 snapshotQty，取 item 的
        }
      }
    })
    total.value = data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getBillTypeLabel = (type: number) => {
  const map: Record<number, string> = {
    10: '采购入库',
    20: '出库发货',
    30: '调拨变动',
    40: '库存调整'
  }
  return map[type] || '未知'
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
