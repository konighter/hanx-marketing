<template>
  <div class="h-full flex flex-col overflow-hidden">
    <!-- 查询表单 -->
    <div class="flex justify-between items-center mb-10px flex-shrink-0">
      <div class="flex gap-2">
        <el-select v-model="queryParams.type" placeholder="单据类型" clearable class="!w-120px" size="small">
          <el-option label="入库单" :value="10" />
          <el-option label="出库单" :value="20" />
          <el-option label="调拨单" :value="30" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="单据状态" clearable class="!w-120px" size="small">
          <el-option label="待收货" :value="20" />
          <el-option label="已完成" :value="10" />
        </el-select>
        <el-input
          v-model="queryParams.billCode"
          placeholder="搜索单号"
          clearable
          class="!w-180px"
          size="small"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <Icon icon="ep:search" />
          </template>
        </el-input>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
      </div>
    </div>

    <!-- 数据列表 -->
    <el-table v-loading="loading" :data="list" stripe border class="flex-1" height="100%">
      <el-table-column label="单号" prop="billCode" width="160">
        <template #default="{ row }">
          <el-link type="primary" @click="viewDetail(row)">{{ row.billCode }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="单据类型" prop="type" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getBillTypeTag(row.type)" size="small">
            {{ getBillTypeLabel(row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)" effect="plain" size="small">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源" min-width="120" show-overflow-tooltip>
        <template #default="{ row }">
          {{ warehouseMap[String(row.fromId)] || row.fromId || '外部' }}
        </template>
      </el-table-column>
      <el-table-column label="去向" min-width="120" show-overflow-tooltip>
        <template #default="{ row }">
          {{ warehouseMap[String(row.toId)] || row.toId || '外部' }}
        </template>
      </el-table-column>
      <el-table-column label="业务类型" prop="refType" width="120" align="center">
        <template #default="{ row }">
          {{ getRefTypeLabel(row.refType) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" :formatter="dateFormatter" />
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template #default="{ row }">
          <!-- 确认收货逻辑：调拨单 && 状态为待收货 && 当前仓库是接收仓 -->
          <el-button
            v-if="row.type === 30 && row.status === 20 && String(row.toId) === String(warehouseId)"
            type="success"
            link
            @click="handleReceive(row)"
          >
            确认收货
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <Pagination
      class="justify-end mt-10px"
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 明细抽屉 -->
    <el-drawer v-model="detailVisible" title="账单明细" size="50%">
      <div v-if="currentBill" class="p-20px">
        <el-descriptions border :column="2" class="mb-20px">
          <el-descriptions-item label="单号">{{ currentBill.billCode }}</el-descriptions-item>
          <el-descriptions-item label="单据类型">
            <el-tag :type="getBillTypeTag(currentBill.type)" size="small">
              {{ getBillTypeLabel(currentBill.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="来源">
            {{ warehouseMap[String(currentBill.fromId)] || currentBill.fromId || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="去向">
            {{ warehouseMap[String(currentBill.toId)] || currentBill.toId || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="业务类型">{{ currentBill.refType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTag(currentBill.status)" effect="plain" size="small">
              {{ getStatusLabel(currentBill.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="关联单号">{{ currentBill.refCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ currentBill.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-table :data="currentBill.items" border stripe>
          <el-table-column label="物料" min-width="200">
            <template #default="{ row }">
              <span class="font-bold">{{ row.sellerSku }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" prop="qty" width="100" align="center" />
          <el-table-column label="流水快照" prop="snapshotQty" width="100" align="center" />
        </el-table>
      </div>
      <template #footer>
        <div class="flex justify-end p-20px">
          <el-button
            v-if="currentBill?.type === 30 && currentBill?.status === 20 && String(currentBill?.toId) === String(warehouseId)"
            type="primary"
            @click="handleReceive(currentBill)"
          >
            确认收货
          </el-button>
          <el-button @click="detailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { InventoryBillApi } from '@/app/erplus/api/stock/inventoryBill'
import { WarehouseApi } from '@/app/erplus/api/stock/warehouse'
import { ErpInventoryBillRefTypeLabelMap } from './constants'
import { Icon } from '@/components/Icon'

defineOptions({ name: 'InventoryBillTable' })

const props = defineProps<{
  warehouseId: number | null
}>()

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  billCode: undefined,
  type: undefined,
  status: undefined,
  warehouseId: undefined as any
})

const detailVisible = ref(false)
const currentBill = ref<any>(null)
const message = useMessage()
const warehouseMap = ref<Record<string, string>>({})

const getList = async () => {
  if (!props.warehouseId) return
  loading.value = true
  try {
    // 仓库单据查询：需要查询发货或者收货是该仓库的所有单据
    // 后端接口 InventoryBillApi.getInventoryBillPage 
    const params = {
      ...queryParams,
      warehouseId: props.warehouseId // 后端该参数需要覆盖 fromId 或 toId
    }
    const data = await InventoryBillApi.getInventoryBillPage(params)
    list.value = data.list
    total.value = data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNo = 1
  getList()
}

const getBillTypeLabel = (type: number) => {
  const map: any = { 10: '入库单', 20: '出库单', 30: '调拨单', 40: '库存调整' }
  return map[type] || '未知'
}

const getBillTypeTag = (type: number) => {
  const map: any = { 10: 'success', 20: 'warning', 30: 'primary', 40: 'info' }
  return map[type] || ''
}

const getStatusLabel = (status: number) => {
  const map: any = { 10: '已完成', 20: '待收货', 90: '已作废' }
  return map[status] || '未知'
}

const getRefTypeLabel = (refType: string) => {
  return ErpInventoryBillRefTypeLabelMap[refType] || refType || '-'
}

const getStatusTag = (status: number) => {
  const map: any = { 10: 'success', 20: 'danger', 90: 'info' }
  return map[status] || ''
}

const viewDetail = async (row: any) => {
  currentBill.value = await InventoryBillApi.getInventoryBill(row.id)
  detailVisible.value = true
}

const handleReceive = async (row: any) => {
  try {
    await message.confirm('确认已收到该批次所有物资吗？确认后库存将正式结转。')
    loading.value = true
    await InventoryBillApi.receiveInventoryBill(row.id)
    message.success('收货成功')
    detailVisible.value = false
    getList()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

watch(() => props.warehouseId, (newId) => {
  if (newId) {
    queryParams.pageNo = 1
    getList()
  }
}, { immediate: true })

onMounted(async () => {
  // 加载仓库名称映射
  try {
    const list = await WarehouseApi.getWarehouseList()
    const map: Record<string, string> = {}
    list.forEach((w: any) => { map[String(w.id)] = w.name })
    warehouseMap.value = map
  } catch (e) {
    console.error('加载仓库列表失败', e)
  }
})
</script>
