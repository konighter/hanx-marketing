<template>
  <!-- set a robust container height -->
  <div class="warehouse-container h-[calc(100vh-160px)] overflow-hidden">
    <el-row :gutter="10" class="h-full">
      <!-- 左侧：仓库列表 -->
      <el-col :span="5" :xs="24" class="h-full">
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
      <el-col :span="19" :xs="24" class="h-full">
        <ContentWrap :title="selectedWarehouseName ? `库存列表 - ${selectedWarehouseName}` : '请选择仓库'" class="!mb-0 h-full">


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
                <!-- <span class="text-16px font-bold">{{ `货物列表 - ${selectedWarehouseName}` }}</span> -->
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
                  <el-button type="success" size="small" v-if="selectedWarehouse.type === 0">
                    <Icon icon="ep:upload" class="mr-5px" />
                    发货
                  </el-button>
                </div>

              </div>

              <el-table v-loading="loadingStock" :data="stockList" :stripe="true" :show-overflow-tooltip="true"
                class="flex-1" height="100%">
                <el-table-column label="图片" align="center" width="100">
                  <template #default="scope">
                    <el-image :src="scope.row.image" class="w-60px h-60px border-rounded" preview-teleported
                      :preview-src-list="[scope.row.image]" fit="cover" />
                  </template>
                </el-table-column>
                <el-table-column label="产品编号" align="left" prop="sellerSku" width="300">
                  <template #default="{ row }">
                    <div class="product-info-cell group">
                      <div class="mb-5px line-clamp-2">
                        <span>Fun & Chic 17cm Doll Clothes,Pink Princess Dress with Sequin & Pom-Pom Bow , Perfectly Fit
                          Outfit
                          (No Doll)</span>
                      </div>
                      <div class="flex items-center gap-5px text-gray-500 text-sm">
                        <span>{{ row.sellerSku }}</span>
                        <el-button link class="opacity-0 group-hover:opacity-100 transition-opacity"
                          @click="handleCopy(row.sellerSku)">
                          <Icon icon="ep:copy-document" :size="14" />
                        </el-button>
                      </div>
                    </div>
                  </template>
                </el-table-column>

                <el-table-column label="库存数量" align="center" prop="totalCount">
                  <template #default="scope">
                    <span :class="{ 'text-red-500': scope.row.count < 10 }" class="font-bold">{{ scope.row.totalCount
                    }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="入库中" align="center" prop="inboundingCount" />
                <el-table-column label="出库中" align="center" prop="outboundingCount" />
                <el-table-column label="更新时间" align="center" prop="updateTime" width="180" :formatter="dateFormatter" />
              </el-table>

              <Pagination class="justify-end" :total="stockTotal" v-model:page="stockParams.pageNo"
                v-model:limit="stockParams.pageSize" @pagination="fetchStockList(selectedWarehouseId!)" />

            </template>
            <el-empty v-else description="请从左侧选择一个仓库以查看库存明细" class="flex-1" />
          </div>
        </ContentWrap>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { WarehouseApi, WarehouseTypes, WarehouseTypeMap } from '@/app/erplus/api/stock/warehouse'
import { ContentWrap } from '@/components/ContentWrap'
import { ref, reactive, computed, onMounted } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { useClipboard } from '@vueuse/core'
import { useMessage } from '@/hooks/web/useMessage'

// 状态
const message = useMessage()
const { copy } = useClipboard()
const loadingList = ref(false)
const warehouseList = ref<any[]>([])
const warehouseTotal = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
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
  pageSize: 10,
  keyword: undefined,
})

// 计算是否为最后一页
const isLastPage = computed(() => {
  return queryParams.pageNo * queryParams.pageSize >= warehouseTotal.value
})

// 获取仓库列表 (对接 API)
const getList = async () => {
  loadingList.value = true
  try {
    const data = await WarehouseApi.getWarehousePage(queryParams)
    warehouseList.value = data.list
    warehouseTotal.value = data.total

    // 如果没有选过仓库，或者选过的仓库不在当前列表，默认选第一个
    // 并且当前列表有仓库
    if (warehouseList.value.length > 0 && (!selectedWarehouseId.value || !warehouseList.value.some(w => w.id === selectedWarehouseId.value))) {
      selectWarehouse(warehouseList.value[0])
    } else if (warehouseList.value.length === 0) {
      // 如果当前页没有仓库，清空选中状态和库存列表
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
  // TODO: Add form dialog logic here
}

// 重置查询
const resetQueryParams = () => {
  queryParams.name = undefined
  queryParams.type = undefined
  handleSearch()
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
  stockParams.pageNo = 1 // 切换仓库重置分页
  stockParams.keyword = undefined
  fetchStockList(warehouse.id)
}

// Mock 库存数据
const allStockData = {
  1: [
    { productId: 'P001', productName: 'iPhone 15 Pro', model: '256G 钛金属', unitName: '台', count: 120, updateTime: '2024-01-09 10:00:00', image: 'https://img0.baidu.com/it/u=2270928090,3239103858&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500' },
    { productId: 'P002', productName: 'MacBook Air M3', model: '16G 512G', unitName: '台', count: 45, updateTime: '2024-01-08 15:30:00', image: 'https://img2.baidu.com/it/u=307000298,3939634289&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500' },
  ],
  2: [
    { productId: 'P003', productName: 'iPad Pro', model: '11英寸 256G', unitName: '台', count: 5, updateTime: '2024-01-09 09:20:00', image: 'https://img0.baidu.com/it/u=2580746654,1961603953&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500' },
    { productId: 'P001', productName: 'iPhone 15 Pro', model: '256G 钛金属', unitName: '台', count: 200, updateTime: '2024-01-07 11:00:00', image: 'https://img0.baidu.com/it/u=2270928090,3239103858&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500' },
  ],
  3: [
    { productId: 'P004', productName: 'Apple Watch Ultra', model: '蜂窝版', unitName: '块', count: 80, updateTime: '2024-01-09 11:00:00', image: 'https://img2.baidu.com/it/u=180128710,2178652391&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500' },
  ],
  4: [
    { productId: 'P005', productName: 'AirPods Pro 2', model: '带MagSafe充电盒', unitName: '个', count: 300, updateTime: '2024-01-09 12:00:00', image: 'https://img0.baidu.com/it/u=1833502575,2002131976&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500' },
  ],
  5: [
    { productId: 'P006', productName: 'HomePod mini', model: '深空灰色', unitName: '个', count: 20, updateTime: '2024-01-09 13:00:00', image: 'https://img0.baidu.com/it/u=219760777,1596409540&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500' },
  ]
}

// 获取库存列表 (保持 Mock)
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
:deep(.el-card) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.warehouse-item {
  border: 1px solid transparent;
}

.warehouse-item:hover {
  background-color: var(--el-color-primary-light-9);
}

.warehouse-item.active {
  border-color: var(--el-color-primary);
}
</style>
