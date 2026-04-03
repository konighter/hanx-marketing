<template>
  <div class="product-listing-v2-container p-4 dark:bg-slate-900 min-h-screen">
    <!-- 顶部筛选与状态切换 -->
    <ContentWrap class="mb-4">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="100px" :rules="rules">
        <el-form-item label="店铺" prop="shopId">
          <ShopCascaderSelect
            v-model="selectedShopPath"
            :emit-path="true"
            placeholder="请选择平台 / 店铺"
            @change="handleCascaderChange"
            class="!w-240px"
          />
        </el-form-item>
        <el-form-item label="搜索" prop="keyword">
          <el-input v-model="queryParams.keyword" placeholder="标题 / SKU / 平台 ID" clearable @keyup.enter="handleQuery" class="!w-280px">
            <template #prefix><Icon icon="ep:search" /></template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button @click="handleQuery" type="primary" class="rounded-lg px-6"><Icon icon="ep:search" class="mr-1" />查询</el-button>
          <el-button @click="resetQuery" class="rounded-lg"><Icon icon="ep:refresh" class="mr-1" />重置</el-button>
          <el-button @click="openSyncDialog" class="rounded-lg" type="success" plain><Icon icon="ep:refresh-right" class="mr-1" />平台同步</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 状态 Tabs 分类 -->
      <div class="mt-4 border-t border-gray-100 dark:border-slate-800 pt-4 flex items-center justify-between">
        <div class="flex-1 min-w-0">
          <el-tabs v-model="queryParams.status" @tab-change="handleQuery" class="status-tabs">
            <el-tab-pane v-for="tab in statusTabs" :key="tab.value" :name="tab.value">
              <template #label>
                <div class="flex items-center gap-1">
                  <span>{{ tab.label }}</span>
                  <span v-if="tab.count" class="tab-count-badge">{{ tab.count }}</span>
                </div>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>
        
        <div class="flex items-center gap-2">
           <el-dropdown trigger="click">
             <el-button text class="flex items-center gap-1 text-xs text-gray-500">排序: 最新更新 <Icon icon="ep:arrow-down" /></el-button>
             <template #dropdown>
               <el-dropdown-menu>
                 <el-dropdown-item>销量最高</el-dropdown-item>
                 <el-dropdown-item>评分最高</el-dropdown-item>
                 <el-dropdown-item>最近更新</el-dropdown-item>
               </el-dropdown-menu>
             </template>
           </el-dropdown>
           <el-button-group class="ml-2">
             <el-button size="small" :type="viewMode === 'grid' ? 'primary' : 'default'" @click="viewMode = 'grid'"><Icon icon="ep:grid" /></el-button>
             <el-button size="small" :type="viewMode === 'list' ? 'primary' : 'default'" @click="viewMode = 'list'"><Icon icon="ep:list" /></el-button>
           </el-button-group>
        </div>
      </div>
    </ContentWrap>

    <!-- 3. 批量操作工具栏 (参考订单管理样式) -->
    <transition name="el-zoom-in-top">
      <div v-if="selectedIds.length > 0" class="batch-bar mb-4">
        <el-alert type="success" :closable="false" shadow="never" class="!bg-indigo-50/50 !border-indigo-200 dark:!bg-slate-800/50 dark:!border-slate-700">
          <template #title>
            <div class="flex justify-between items-center w-full py-1">
              <div class="flex items-center gap-3">
                <el-checkbox 
                  :model-value="isAllSelected" 
                  :indeterminate="isIndeterminate" 
                  @change="handleSelectAll"
                />
                <span class="text-sm font-medium text-gray-700 dark:text-slate-200">
                  已选中 <b class="text-indigo-600 dark:text-indigo-400">{{ selectedIds.length }}</b> 个商品
                </span>
              </div>
              <div class="flex gap-2">
                <el-button type="primary" size="small" @click="handleBatchSync" class="!rounded-md">批量同步</el-button>
                <el-button size="small" @click="handleBatchPrice" class="!rounded-md">批量调价</el-button>
                <el-button size="small" @click="clearSelection" class="!rounded-md">取消选择</el-button>
              </div>
            </div>
          </template>
        </el-alert>
      </div>
    </transition>

    <!-- 商品列表展示区 -->
    <div v-loading="loading" class="listing-grid-container min-h-[400px]">
      <div 
        v-if="list.length > 0"
        :class="viewMode === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6' : 'flex flex-col gap-4'"
      >
        <ListingItemV2 
          v-for="item in list" 
          :key="item.id" 
          :listing="item"
          :is-selected="selectedIds.includes(item.id)"
          :view-mode="viewMode"
          @select="handleItemSelect(item.id, $event)"
          @sync="handleSingleSync"
          @detail="handleShowDetail"
        />
      </div>
      <el-empty v-else description="暂无匹配商品，请调整筛选条件" />
      
      <!-- 分页栏 -->
      <div class="mt-8 flex justify-center pb-20">
        <el-pagination
          v-model:current-page="queryParams.pageNo"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[12, 24, 48, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
          background
        />
      </div>
    </div>
    
    <!-- 详情抽屉 -->
    <ListingDetailDrawer ref="detailDrawerRef" />

    <!-- 平台同步对话框 -->
    <el-dialog v-model="syncDialogVisible" title="同步平台商品" width="450px" destroy-on-close>
      <el-form :model="syncForm" label-width="100px" class="mt-4 pr-6">
        <el-form-item label="店铺" required>
          <ShopCascaderSelect
            v-model="syncForm.shopPath"
            :emit-path="true"
            placeholder="请选择需要同步的平台 / 店铺"
            class="!w-full"
          />
        </el-form-item>
        <el-form-item label="同步周期" required>
          <el-radio-group v-model="syncForm.timeRange">
            <el-radio value="all">全部</el-radio>
            <el-radio value="incremental">增量</el-radio>
          </el-radio-group>
          <div v-if="syncForm.timeRange === 'incremental'" class="mt-2 text-left w-full">
            <el-date-picker
              v-model="syncForm.customDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              class="!w-full"
            />
          </div>
          <div class="text-xs text-gray-400 mt-2 block w-full leading-snug">建议仅进行增量同步，同步『全部』商品可能由于数量过大而等待较长时间。</div>
        </el-form-item>
        <el-form-item label="特定商品 ID">
          <el-input 
            v-model="syncForm.productIds" 
            type="textarea" 
            :rows="3" 
            placeholder="支持输入 ASIN 或 平台 Item ID。&#10;多个 ID 之间请用英文逗号或换行隔开（选填）" 
          />
          <div class="text-xs text-gray-400 mt-1 w-full leading-snug text-left">
            如填写特定商品 ID，云端系统将只同步这些商品且忽略上方的时间周期设置。
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="syncDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="syncing" @click="handlePlatformSync">开始同步</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ListingV2VO, CrossProductSyncRequest } from './types'
import ListingItemV2 from './components/ListingItemV2.vue'
import ListingDetailDrawer from './components/ListingDetailDrawer.vue'
import * as CrossListingApi from '@/app/erplus/api/product/listing'
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue'

defineOptions({ name: 'CrossListingV2' })

const message = useMessage()
const loading = ref(false)
const list = ref<ListingV2VO[]>([])
const total = ref(0)
const viewMode = ref<'grid' | 'list'>('grid')
const selectedItems = ref<any[]>([])
const selectedIds = computed(() => selectedItems.value.map((o) => o.id))
const selectedShopPath = ref<any[]>([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 12,
  platformId: undefined,
  shopId: undefined,
  status: 'all',
  keyword: ''
})

const queryFormRef = ref()
const rules = reactive({
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }]
})

const getDefaultDateRange = () => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 7)
  const format = (d: Date) => {
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${y}-${m}-${day}`
  }
  return [format(start), format(end)]
}

const detailDrawerRef = ref<InstanceType<typeof ListingDetailDrawer>>()
const syncDialogVisible = ref(false)
const syncing = ref(false)
const syncForm = reactive({
  shopPath: [] as any[],
  timeRange: 'incremental',
  customDateRange: getDefaultDateRange(),
  productIds: ''
})

const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '在线', value: 'active', count: 342 },
  { label: '审核中', value: 'pending', count: 122 },
  { label: '素材预警', value: 'warning', count: 15 },
  { label: '已售罄', value: 'soldout' }
]


/** 数据加载 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CrossListingApi.queryCrossProductListingPage(queryParams)
    list.value = data.list
    total.value = data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  selectedShopPath.value = []
  queryParams.platformId = undefined
  queryParams.shopId = undefined
  queryParams.status = 'all'
  queryParams.keyword = ''
  handleQuery()
}

/** 选择逻辑 (参考订单管理) */
const isAllSelected = computed(() => {
  return list.value.length > 0 && list.value.every(item => selectedIds.value.includes(item.id))
})
const isIndeterminate = computed(() => {
  return selectedIds.value.length > 0 && !isAllSelected.value
})

const handleItemSelect = (id: number, checked: boolean) => {
  const item = list.value.find(i => i.id === id)
  if (!item) return
  
  if (checked) {
    if (!selectedIds.value.includes(id)) {
      selectedItems.value = [...selectedItems.value, item]
    }
  } else {
    selectedItems.value = selectedItems.value.filter(i => i.id !== id)
  }
}

const handleSelectAll = (val: boolean) => {
  if (val) {
    list.value.forEach((item) => {
      if (!selectedIds.value.includes(item.id)) {
        selectedItems.value = [...selectedItems.value, item]
      }
    })
  } else {
    const currentIds = list.value.map((item) => item.id)
    selectedItems.value = selectedItems.value.filter((i) => !currentIds.includes(i.id))
  }
}

const clearSelection = () => {
  selectedItems.value = []
}

/** 同步逻辑 */
const handleSingleSync = async (listing: ListingV2VO) => {
  try {
    await message.confirm(`确定要同步商品 "${listing.title}" 吗？`)
    await CrossListingApi.syncProductListing({ id: listing.id })
    message.success('同步请求已发送')
  } catch {}
}

const handleBatchSync = async () => {
  try {
    await message.confirm(`确定要同步选中的 ${selectedIds.value.length} 个商品吗？`)
    await CrossListingApi.syncProductListing({ ids: selectedIds.value })
    message.success('批量同步请求已发送')
    clearSelection()
  } catch {}
}

const handleBatchPrice = () => {
  message.info('批量调价功能正在开发中...')
}

const handleShowDetail = (listing: ListingV2VO) => {
  detailDrawerRef.value?.open(listing)
}

const handleCascaderChange = () => {
  if (selectedShopPath.value && selectedShopPath.value.length === 2) {
    queryParams.platformId = selectedShopPath.value[0]
    queryParams.shopId = selectedShopPath.value[1]
    handleQuery()
  } else {
    queryParams.platformId = undefined
    queryParams.shopId = undefined
  }
}

const openSyncDialog = () => {
  syncForm.shopPath = selectedShopPath.value && selectedShopPath.value.length === 2 ? [...selectedShopPath.value] : []
  syncForm.timeRange = 'incremental'
  syncForm.customDateRange = getDefaultDateRange()
  syncForm.productIds = ''
  syncDialogVisible.value = true
}

const convertCrossProductSyncRequest = (form: typeof syncForm): CrossProductSyncRequest => {
  const req: CrossProductSyncRequest = {
    platformId: form.shopPath[0] as number,
    shopId: form.shopPath[1] as number,
    syncType: form.timeRange
  }
  
  if (form.timeRange === 'incremental' && form.customDateRange && form.customDateRange.length === 2) {
    const start = form.customDateRange[0]
    const end = form.customDateRange[1]
    req.createTimeStart = start.includes(' ') ? start : `${start} 00:00:00`
    req.createTimeEnd = end.includes(' ') ? end : `${end} 23:59:59`
  }
  
  if (form.productIds && form.productIds.trim() !== '') {
    req.productIds = form.productIds.split(/[,\n]+/).map(i => i.trim()).filter(Boolean)
  }

  return req
}

const handlePlatformSync = async () => {
  if (!syncForm.shopPath || syncForm.shopPath.length !== 2) {
    message.warning('请选择需要同步的平台和店铺')
    return
  }
  
  syncing.value = true
  try {
    const req = convertCrossProductSyncRequest(syncForm)

    await CrossListingApi.syncPlatformListing(req)
    // await new Promise(resolve => setTimeout(resolve, 800)) // Removed mock
    message.success('已下发同步任务，将在后台执行，请稍后刷新查看')
    syncDialogVisible.value = false
    // handleQuery()
  } catch (e) {
    console.error(e)
  } finally {
    syncing.value = false
  }
}

onMounted(async () => {
  if (queryParams.shopId) {
    getList()
  }
})
</script>

<style scoped>
.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-up-enter-from, .slide-up-leave-to {
  transform: translate(-50%, 100%);
  opacity: 0;
}

/* 一些深色模式局部微调 */
/* 状态标签页样式 */
:deep(.status-tabs) {
  .el-tabs__header {
    margin: 0;
  }
  .el-tabs__nav-wrap::after {
    display: none;
  }
  .el-tabs__item {
    font-size: 14px;
    padding: 0 20px;
    height: 40px;
    line-height: 40px;
  }
}

.tab-count-badge {
  font-size: 10px;
  padding: 0 4px;
  height: 14px;
  line-height: 14px;
  background-color: #f0f2f5;
  color: #909399;
  border-radius: 4px;
  margin-left: 2px;
}

.dark .tab-count-badge {
  background-color: #1e293b;
  color: #64748b;
}

.dark .product-listing-v2-container {
  background-color: #0f172a;
}
</style>
