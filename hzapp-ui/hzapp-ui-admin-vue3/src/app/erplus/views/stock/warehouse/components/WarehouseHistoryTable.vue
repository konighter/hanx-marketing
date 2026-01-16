<template>
  <div class="h-full flex flex-col overflow-hidden">
    <div class="flex justify-between items-center mb-10px flex-shrink-0">
      <div class="flex gap-2">
        <el-select v-model="queryParams.type" placeholder="记录类型" clearable class="!w-120px" size="small">
          <el-option label="全部" :value="undefined" />
          <el-option label="采购入库" :value="10" />
          <el-option label="出库发货" :value="20" />
          <el-option label="调拨变动" :value="30" />
        </el-select>
        <el-date-picker v-model="queryParams.timeRange" type="daterange" size="small" range-separator="-"
          start-placeholder="开始" end-placeholder="结束" class="!w-240px" value-format="YYYY-MM-DD" />
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="list" stripe border class="flex-1" height="100%">
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

    <Pagination class="justify-end mt-10px" :total="total" v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { Icon } from '@/components/Icon'
import { dateFormatter } from '@/utils/formatTime'

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
    // Mock simulation
    await new Promise(resolve => setTimeout(resolve, 400))
    list.value = [
      { id: 101, createTime: new Date().getTime() - 86400000, type: 10, typeLabel: '采购入库', sellerSku: 'SKU-001', count: 100, afterCount: 150, billCode: 'PO-20240115-01', remark: '正品入库' },
      { id: 102, createTime: new Date().getTime() - 43200000, type: 20, typeLabel: '出库发货', sellerSku: 'SKU-001', count: -20, afterCount: 130, billCode: 'SO-20240115-05', remark: '订单发货' },
      { id: 103, createTime: new Date().getTime() - 3600000, type: 30, typeLabel: '调拨变动', sellerSku: 'SKU-002', count: -10, afterCount: 40, billCode: 'TR-20240115-02', remark: '移仓调拨' },
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
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
    getList()
  }
})

onMounted(() => {
  getList()
})
</script>

<style scoped></style>
