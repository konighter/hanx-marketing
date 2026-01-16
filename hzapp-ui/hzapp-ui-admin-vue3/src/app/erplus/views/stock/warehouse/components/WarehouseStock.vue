<template>
  <div class="h-full overflow-hidden">
    <el-row :gutter="10" class="h-full">
      <!-- 左侧：仓库列表 -->
      <el-col :span="6" :xs="24" class="h-full">
        <ContentWrap title="仓库列表" class="!mb-0 h-full">
          <template #header>
            <div class="flex justify-end w-full">
              <el-button type="primary" size="small" @click="handleAdd">
                <Icon icon="ep:plus" class="mr-5px" />
                新增
              </el-button>
            </div>
          </template>
          <div class="flex-1 flex flex-col overflow-hidden">
            <!-- 搜索栏 -->
            <div class="flex gap-5px mb-10px flex-shrink-0">
              <el-input v-model="queryParams.name" placeholder="搜仓库名称" clearable @input="handleSearch" class="flex-1">
                <template #prefix>
                  <Icon icon="ep:search" />
                </template>
              </el-input>

              <el-popover placement="bottom-end" :width="280" trigger="click">
                <template #reference>
                  <el-button link>
                    <Icon icon="ep:more-filled" />
                  </el-button>
                </template>
                <el-form :model="queryParams" label-width="80px" size="small">
                  <el-form-item label="仓库类型">
                    <el-select v-model="queryParams.type" placeholder="请选择类型" clearable @change="handleSearch">
                      <el-option v-for="item in WarehouseTypes" :key="item.value" :label="item.label"
                        :value="item.value" />
                    </el-select>
                  </el-form-item>
                </el-form>
              </el-popover>
            </div>

            <!-- 列表滚动区 -->
            <el-scrollbar class="flex-1 overflow-hidden">
              <div v-loading="loadingList" class="pr-10px">
                <div v-for="item in warehouseList" :key="item.id"
                  class="warehouse-item p-10px mb-5px cursor-pointer border-rounded transition-all"
                  :class="{ 'active bg-[var(--el-color-primary-light-9)] border-primary': selectedWarehouseId === item.id }"
                  @click="selectWarehouse(item)">
                  <div class="flex justify-between items-center mb-5px">
                    <span class="font-bold text-14px">{{ item.name }}</span>
                    <el-tag :type="WarehouseTypeMap.get(item.type) === '平台仓' ? 'success' : 'info'" size="small">
                      {{ WarehouseTypeMap.get(item.type) }}
                    </el-tag>
                  </div>
                  <div class="text-12px text-gray-500 truncate">{{ item.remark || '暂无备注' }}</div>
                </div>
                <el-empty v-if="warehouseList.length === 0" description="未搜索到仓库" />
              </div>
            </el-scrollbar>

            <!-- 分页组件 - 强制固定在底部 -->
            <div
              class="flex justify-between items-center mt-10px pt-10px border-t border-[var(--el-border-color-lighter)] flex-shrink-0">
              <el-button size="small" :disabled="queryParams.pageNo <= 1" @click="handlePageChange(-1)">
                上一页
              </el-button>
              <span class="text-12px text-gray-500">第 {{ queryParams.pageNo }} 页</span>
              <el-button size="small" :disabled="isLastPage" @click="handlePageChange(1)">
                下一页
              </el-button>
            </div>
          </div>
        </ContentWrap>
      </el-col>

      <!-- 右侧：货物列表 -->
      <el-col :span="18" :xs="24" class="h-full">
        <ContentWrap :title="selectedWarehouseName ? `仓库：${selectedWarehouseName}` : '请先选择仓库'" class="!mb-0 h-full">
          <template #header>
            <div class="flex justify-end w-full">
              <el-input v-model="stockParams.keyword" placeholder="搜索产品名称/编号" clearable class="!w-240px"
                @input="fetchStockList(selectedWarehouseId!)">
                <template #prefix>
                  <Icon icon="ep:search" />
                </template>
              </el-input>
            </div>
          </template>

          <div class="flex-1 flex flex-col overflow-hidden">
            <template v-if="selectedWarehouseId">
              <div class="flex justify-between items-center mb-10px flex-shrink-0">
                <div>
                  <el-button size="small">
                    <Icon icon="ep:plus" class="mr-5px" />
                    入库
                  </el-button>
                  <el-button size="small">
                    <Icon icon="ep:upload" class="mr-5px" />
                    出库
                  </el-button>
                  <el-button size="small">
                    <Icon icon="ep:refresh" class="mr-5px" />
                    调拨
                  </el-button>
                  <el-button type="success" size="small" v-if="selectedWarehouse?.type === 0">
                    <Icon icon="ep:upload" class="mr-5px" />
                    发货
                  </el-button>
                </div>
              </div>

              <el-table v-loading="loadingStock" :data="stockList" :stripe="true" :show-overflow-tooltip="true"
                class="flex-1" height="100%">
                <el-table-column label="图片" align="center" width="80">
                  <template #default="scope">
                    <el-image :src="scope.row.image" class="w-48px h-48px border-rounded" preview-teleported
                      :preview-src-list="[scope.row.image]" fit="cover" />
                  </template>
                </el-table-column>
                <el-table-column label="SKU / 商品信息" align="left" prop="sellerSku" min-width="250">
                  <template #default="{ row }">
                    <div class="product-info-cell group">
                      <div class="mb-2px line-clamp-1 text-gray-700 font-bold">
                        <span>{{ row.sellerSku }}</span>
                      </div>
                      <div class="flex items-center gap-5px text-gray-400 text-xs truncate">
                        <span>{{ row.name || 'Fun & Chic 17cm Doll Clothes,Pink Princess Dress with Sequin & Pom-Pom
                          Bow' }}</span>
                      </div>
                    </div>
                  </template>
                </el-table-column>

                <el-table-column label="可用库存" align="center" prop="totalCount" width="100">
                  <template #default="scope">
                    <span :class="{ 'text-red-500': scope.row.totalCount < 10 }" class="font-bold">
                      {{ scope.row.totalCount }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column label="入库中" align="center" prop="inboundingCount" width="100" />
                <el-table-column label="出库中" align="center" prop="outboundingCount" width="100" />
                <el-table-column label="更新时间" align="center" prop="updateTime" width="180" :formatter="dateFormatter" />
              </el-table>

              <Pagination class="justify-end mt-10px" :total="stockTotal" v-model:page="stockParams.pageNo"
                v-model:limit="stockParams.pageSize" @pagination="fetchStockList(selectedWarehouseId!)" />
            </template>
            <el-empty v-else description="请从左侧选择一个仓库以查看库存" class="flex-1" />
          </div>
        </ContentWrap>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { WarehouseApi, WarehouseTypes, WarehouseTypeMap } from '@/app/erplus/api/stock/warehouse'
import { ContentWrap } from '@/components/ContentWrap'
import { Icon } from '@/components/Icon'
import { dateFormatter } from '@/utils/formatTime'
import { useClipboard } from '@vueuse/core'
import { useMessage } from '@/hooks/web/useMessage'

defineOptions({ name: 'WarehouseStock' })

// 状态
const message = useMessage()
const { copy } = useClipboard()

const loadingList = ref(false)
const warehouseList = ref<any[]>([])
const warehouseTotal = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  name: undefined,
  type: undefined,
})

const selectedWarehouse = ref<any>(null)
const selectedWarehouseId = ref<number | null>(null)
const selectedWarehouseName = ref('')

const loadingStock = ref(false)
const stockList = ref<any[]>([])
const stockTotal = ref(0)
const stockParams = reactive({
  pageNo: 1,
  pageSize: 20,
  keyword: undefined,
})

// 计算是否为最后一页
const isLastPage = computed(() => {
  return queryParams.pageNo * queryParams.pageSize >= warehouseTotal.value
})

// 获取仓库列表
const getList = async () => {
  loadingList.value = true
  try {
    const data = await WarehouseApi.getWarehousePage(queryParams)
    warehouseList.value = data.list
    warehouseTotal.value = data.total

    if (warehouseList.value.length > 0 && (!selectedWarehouseId.value || !warehouseList.value.some(w => w.id === selectedWarehouseId.value))) {
      selectWarehouse(warehouseList.value[0])
    } else if (warehouseList.value.length === 0) {
      selectedWarehouseId.value = null
      selectedWarehouseName.value = ''
      stockList.value = []
    }
  } catch (err) {
    console.error('Failed to fetch warehouses:', err)
  } finally {
    loadingList.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  queryParams.pageNo = 1
  getList()
}

// 复制处理
const handleCopy = async (text: string) => {
  try {
    await copy(text)
    message.success('复制成功')
  } catch (err) {
    message.error('复制失败')
  }
}

// 新增仓库
const handleAdd = () => {
  console.log('Open new warehouse form')
}

// 分页处理
const handlePageChange = (delta: number) => {
  queryParams.pageNo += delta
  getList()
}

// 选择仓库
const selectWarehouse = (warehouse: any) => {
  selectedWarehouse.value = warehouse
  selectedWarehouseId.value = warehouse.id
  selectedWarehouseName.value = warehouse.name
  stockParams.pageNo = 1
  stockParams.keyword = undefined
  fetchStockList(warehouse.id)
}

// 获取库存列表
const fetchStockList = async (id: number | string) => {
  const params = {
    pageNo: stockParams.pageNo,
    pageSize: stockParams.pageSize,
    keyword: stockParams.keyword,
    warehouseId: id,
  }
  loadingStock.value = true
  try {
    const ret = await WarehouseApi.getWarehouseInventoryPage(params)
    stockList.value = ret.list
    stockTotal.value = ret.total
  } finally {
    loadingStock.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.warehouse-item {
  border: 1px solid transparent;
  margin-bottom: 8px;
}

.warehouse-item:hover {
  background-color: var(--el-color-primary-light-9);
}

.warehouse-item.active {
  background-color: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary);
}

.product-info-cell:hover .opacity-0 {
  opacity: 1;
}
</style>
