<template>
  <div class="h-full overflow-hidden">
    <el-row :gutter="10" class="h-full">
      <!-- 左侧：仓库列表 -->
      <el-col :span="6" :xs="24" class="h-full">
        <ContentWrap title="仓库列表" class="!mb-0 h-full">
          <div class="flex-1 flex flex-col overflow-hidden">
            <!-- 简单搜索 -->
            <div class="mb-10px flex-shrink-0">
              <el-input v-model="queryParams.name" placeholder="搜仓库名称" clearable @input="handleSearch">
                <template #prefix>
                  <Icon icon="ep:search" />
                </template>
              </el-input>
            </div>

            <!-- 列表滚动区 -->
            <el-scrollbar class="flex-1 overflow-hidden">
              <div v-loading="loadingList" class="pr-10px">
                <div v-for="item in warehouseList" :key="item.id"
                  class="warehouse-item p-10px mb-5px cursor-pointer border-rounded transition-all"
                  :class="{ 'active bg-[var(--el-color-primary-light-9)] border-primary': selectedWarehouseId === item.id }"
                  @click="selectWarehouse(item)">
                  <div class="flex justify-between items-center">
                    <span class="font-bold text-14px">{{ item.name }}</span>
                    <el-tag :type="WarehouseTypeMap.get(item.type) === '平台仓' ? 'success' : 'info'" size="small">
                      {{ WarehouseTypeMap.get(item.type) }}
                    </el-tag>
                  </div>
                </div>
                <el-empty v-if="warehouseList.length === 0" description="未搜索到仓库" />
              </div>
            </el-scrollbar>

            <!-- 分页 -->
            <div
              class="flex justify-between items-center mt-10px pt-10px border-t border-[var(--el-border-color-lighter)] flex-shrink-0">
              <el-button size="small" :disabled="queryParams.pageNo <= 1" @click="handlePageChange(-1)">
                上一页
              </el-button>
              <span class="text-12px text-gray-500">{{ queryParams.pageNo }}</span>
              <el-button size="small" :disabled="isLastPage" @click="handlePageChange(1)">
                下一页
              </el-button>
            </div>
          </div>
        </ContentWrap>
      </el-col>

      <!-- 右侧：记录列表 -->
      <el-col :span="18" :xs="24" class="h-full">
        <ContentWrap :title="selectedWarehouseName ? `仓库：${selectedWarehouseName}` : '请先选择仓库'" class="!mb-0 h-full">
          <template #header>
            <div class="flex justify-end gap-2 w-full">
              <el-select v-model="recordParams.type" placeholder="记录类型" clearable class="!w-120px" size="small">
                <el-option label="全部" :value="undefined" />
                <el-option label="采购入库" :value="10" />
                <el-option label="出库发货" :value="20" />
                <el-option label="调拨变动" :value="30" />
              </el-select>
              <el-date-picker v-model="recordParams.timeRange" type="daterange" size="small" range-separator="-"
                start-placeholder="开始" end-placeholder="结束" class="!w-240px" value-format="YYYY-MM-DD" />
              <el-button type="primary" size="small" @click="fetchRecordList">查询</el-button>
            </div>
          </template>

          <div class="flex-1 flex flex-col overflow-hidden">
            <template v-if="selectedWarehouseId">
              <el-table v-loading="loadingRecords" :data="recordList" stripe border class="flex-1" height="100%">
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

              <Pagination class="justify-end mt-10px" :total="recordTotal" v-model:page="recordParams.pageNo"
                v-model:limit="recordParams.pageSize" @pagination="fetchRecordList" />
            </template>
            <el-empty v-else description="请从左侧选择一个仓库以查看变动记录" class="flex-1" />
          </div>
        </ContentWrap>
      </el-col>
      </row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { WarehouseApi, WarehouseTypeMap } from '@/app/erplus/api/stock/warehouse'
import { ContentWrap } from '@/components/ContentWrap'
import { Icon } from '@/components/Icon'
import { dateFormatter } from '@/utils/formatTime'

defineOptions({ name: 'WarehouseHistory' })

// 状态
const loadingList = ref(false)
const warehouseList = ref<any[]>([])
const warehouseTotal = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  name: undefined,
})

const selectedWarehouseId = ref<number | null>(null)
const selectedWarehouseName = ref('')

const loadingRecords = ref(false)
const recordList = ref<any[]>([])
const recordTotal = ref(0)
const recordParams = reactive({
  pageNo: 1,
  pageSize: 20,
  type: undefined,
  timeRange: [],
})

// 计算是否为最后一页
const isLastPage = computed(() => {
  return queryParams.pageNo * queryParams.pageSize >= warehouseTotal.value
})

const getTypeTag = (type: number) => {
  if (type === 10) return 'success'
  if (type === 20) return 'warning'
  return 'info'
}

// 获取仓库列表
const getList = async () => {
  loadingList.value = true
  try {
    const data = await WarehouseApi.getWarehousePage(queryParams)
    warehouseList.value = data.list
    warehouseTotal.value = data.total

    if (warehouseList.value.length > 0 && (!selectedWarehouseId.value || !warehouseList.value.some(w => w.id === selectedWarehouseId.value))) {
      selectWarehouse(warehouseList.value[0])
    }
  } finally {
    loadingList.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNo = 1
  getList()
}

const handlePageChange = (delta: number) => {
  queryParams.pageNo += delta
  getList()
}

const selectWarehouse = (warehouse: any) => {
  selectedWarehouseId.value = warehouse.id
  selectedWarehouseName.value = warehouse.name
  recordParams.pageNo = 1
  fetchRecordList()
}

// 获取记录列表 (Mock)
const fetchRecordList = async () => {
  if (!selectedWarehouseId.value) return
  loadingRecords.value = true
  try {
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 400))
    // Mock 数据
    recordList.value = [
      { id: 101, createTime: new Date().getTime() - 86400000, type: 10, typeLabel: '采购入库', sellerSku: 'SKU-001', count: 100, afterCount: 150, billCode: 'PO-20240115-01', remark: '正品入库' },
      { id: 102, createTime: new Date().getTime() - 43200000, type: 20, typeLabel: '出库发货', sellerSku: 'SKU-001', count: -20, afterCount: 130, billCode: 'SO-20240115-05', remark: '订单发货' },
      { id: 103, createTime: new Date().getTime() - 3600000, type: 30, typeLabel: '调拨变动', sellerSku: 'SKU-002', count: -10, afterCount: 40, billCode: 'TR-20240115-02', remark: '移仓调拨' },
    ]
    recordTotal.value = 3
  } finally {
    loadingRecords.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.warehouse-item {
  border: 1px solid transparent;
}

.warehouse-item:hover {
  background-color: var(--el-color-primary-light-9);
}

.warehouse-item.active {
  background-color: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary);
}
</style>
