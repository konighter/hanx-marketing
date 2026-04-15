<template>
  <div class="app-container">
    <ContentWrap :body-style="{ padding: '12px 15px' }">
      <!-- 1. 精简搜索工作栏 -->
      <el-form class="-mb-10px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="60px" :rules="rules">
        <el-form-item label="店铺" prop="shopId">
          <ShopCascaderSelect
            v-model="selectedShopPath"
            :emit-path="true"
            placeholder="请选择平台/店铺"
            @change="handleShopChange"
            class="!w-200px"
          />
        </el-form-item>
        <el-form-item label="配送" prop="fulfillType">
          <el-select v-model="queryParams.fulfillType" placeholder="全部" clearable class="!w-120px">
            <el-option label="FBA (平台发货)" :value="1" />
            <div v-if="queryParams.platformId === 3">
               <el-option label="FBM (卖家自发货)" :value="2" />
            </div>
          </el-select>
        </el-form-item>
        <el-form-item label="SKU" prop="sellerProductCode">
          <el-input v-model="queryParams.sellerProductCode" placeholder="输入本地/买家SKU" clearable class="!w-180px" />
        </el-form-item>
        <el-form-item label="日期" prop="createTimeRange">
          <el-date-picker
            v-model="queryParams.createTimeRange"
            value-format="YYYY-MM-DD HH:mm:ss"
            type="daterange"
            start-placeholder="开始"
            end-placeholder="结束"
            :default-time="[new Date('2024-01-01 00:00:00'), new Date('2024-01-01 23:59:59')]"
            class="!w-240px"
          />
        </el-form-item>
        <el-form-item label="单号" prop="orderId">
          <el-input v-model="queryParams.orderId" placeholder="请输入平台订单号" clearable class="!w-180px" />
        </el-form-item>
        
        <el-form-item>
          <el-button @click="handleQuery" type="primary" size="small">
            <Icon icon="ep:search" class="mr-5px" /> 搜索
          </el-button>
          <el-button @click="resetQuery" size="small">
            <Icon icon="ep:refresh" class="mr-5px" /> 重置
          </el-button>
          <el-button @click="handSync" type="success" plain size="small">
            <Icon icon="ep:refresh" class="mr-5px" /> 同步订单
          </el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <!-- 2. 状态标签页 -->
    <el-tabs v-model="activeStatusTab" class="mt-5px" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待处理" name="pending" />
      <el-tab-pane label="待出库" name="to_ship" />
      <el-tab-pane label="运送中" name="shipped" />
      <el-tab-pane label="已完成" name="completed" />
      <el-tab-pane label="已取消" name="cancelled" />
    </el-tabs>

    <!-- 3. 批量操作工具栏 -->
    <transition name="el-zoom-in-top">
      <div v-if="selectedIds.length > 0" class="batch-bar mb-10px">
        <el-alert type="success" :closable="false" shadow="never">
          <template #title>
            <div class="flex justify-between items-center w-full py-4px">
              <div class="flex items-center">
                <el-checkbox 
                  :model-value="isAllSelected" 
                  :indeterminate="isIndeterminate" 
                  @change="handleSelectAll"
                  class="mr-15px"
                />
                <span>已选中 <b class="text-primary">{{ selectedIds.length }}</b> 个订单</span>
              </div>
              <div class="flex gap-10px">
                <el-button type="primary" size="small" @click="handleBatchSync">批量同步</el-button>
                <el-button size="small">批量打印</el-button>
                <el-button size="small">批量备注</el-button>
                <el-button size="small" @click="clearSelection">取消选择</el-button>
              </div>
            </div>
          </template>
        </el-alert>
      </div>
    </transition>

    <!-- 4. 自定义列表区域 -->
    <div v-loading="loading" class="order-list-container min-h-400px">
      <div v-if="list.length > 0">
        <OrderItem 
          v-for="item in list" 
          :key="item.id" 
          :order="item" 
          :selected="selectedIds.includes(item.id)"
          @selection-change="handleItemSelection"
          @sync-request="handleSingleSync"
        />
      </div>
      <el-empty v-else description="暂无订单数据" />

      <!-- 分页 (Stick to bottom or normal) -->
      <div class="mt-20px flex justify-end">
        <Pagination
          :total="total"
          v-model:page="queryParams.pageNo"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </div>

    <!-- 同步对话框 -->
    <el-dialog title="同步订单" v-model="syncDialogVisible" width="500px">
       <el-form :model="syncForm" label-width="100px">
        <el-form-item label="同步方式">
          <el-radio-group v-model="syncForm.syncType">
            <el-radio label="time">时间范围</el-radio>
            <el-radio label="order">订单号</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="syncForm.syncType === 'time'" label="日期范围">
          <el-date-picker
            v-model="syncForm.timeRange"
            type="daterange"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="(date: Date) => date.getTime() > Date.now()"
          />
        </el-form-item>
        <el-form-item v-if="syncForm.syncType === 'order'" label="平台订单号">
          <el-input v-model="syncForm.platformOrderIds" type="textarea" placeholder="多个单号换行分割" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="syncLoading" @click="handleDoSync">执行同步</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { ShopApi } from '@/app/erplus/api/system/shop'
import * as OrderApi from '@/app/erplus/api/order/order'
import OrderItem from './components/OrderItem.vue'
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue'

defineOptions({ name: 'CrossOrderListV2' })

const message = useMessage()
const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const activeStatusTab = ref('all')

const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  platformId: undefined,
  shopId: undefined,
  fulfillType: undefined,
  sellerProductCode: undefined,
  orderId: undefined,
  createTimeRange: [],
  status: undefined
})

const selectedOrders = ref<any[]>([])
const selectedIds = computed(() => selectedOrders.value.map((o) => o.id))
const selectedShopPath = ref<any[]>([])

const queryFormRef = ref()
const rules = reactive({
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }]
})

// 同步相关复用
const syncDialogVisible = ref(false)
const syncLoading = ref(false)
const syncForm = reactive({
  syncType: 'time',
  timeRange: [] as string[],
  platformOrderIds: ''
})

/** 初始化 */
onMounted(async () => {
})

/** 获取列表 */
const getList = async () => {
  if (!queryParams.shopId) return
  loading.value = true
  try {
    const data = await OrderApi.queryCrossOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
    // 换页时清空选择或根据业务逻辑保留
  } finally {
    loading.value = false
  }
}

/** 搜索/重置 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  if (queryFormRef.value) queryFormRef.value.resetFields()
  selectedShopPath.value = []
  queryParams.platformId = undefined
  queryParams.shopId = undefined
  handleQuery()
}

/** 选择逻辑 */
const isAllSelected = computed(() => {
  return list.value.length > 0 && list.value.every(item => selectedIds.value.includes(item.id))
})
const isIndeterminate = computed(() => {
  return selectedIds.value.length > 0 && !isAllSelected.value
})

const handleItemSelection = (selected: boolean, order: any) => {
  if (selected) {
    if (!selectedIds.value.includes(order.id)) {
      selectedOrders.value.push(order)
    }
  } else {
    selectedOrders.value = selectedOrders.value.filter((o) => o.id !== order.id)
  }
}

const handleSelectAll = (val: boolean) => {
  if (val) {
    list.value.forEach((order) => {
      if (!selectedIds.value.includes(order.id)) {
        selectedOrders.value.push(order)
      }
    })
  } else {
    const currentIds = list.value.map((item) => item.id)
    selectedOrders.value = selectedOrders.value.filter((o) => !currentIds.includes(o.id))
  }
}

const clearSelection = () => {
  selectedOrders.value = []
}

/** 店铺与 Tab 切换 */
const handleShopChange = (node: any) => {
  if (selectedShopPath.value && selectedShopPath.value.length === 2) {
    queryParams.platformId = selectedShopPath.value[0]
    queryParams.shopId = selectedShopPath.value[1]
    handleQuery()
  } else {
    queryParams.platformId = undefined
    queryParams.shopId = undefined
  }
}

const handleTabChange = (name: any) => {
  const statusMap: any = {
    all: undefined,
    pending: 10,
    to_ship: 20,
    shipped: 40,
    completed: 40,
    cancelled: 50
  }
  queryParams.status = statusMap[name]
  handleQuery()
}

/** 业务操作 */
const handleSingleSync = () => {
  // 这里可以调用单个同步接口，或者复用同步弹窗
  handSync()
}

const handleBatchSync = async () => {
  if (selectedOrders.value.length === 0) return
  try {
    await message.confirm(`确定要同步选中的 ${selectedOrders.value.length} 个订单吗？`)
    loading.value = true
    const platformOrderIds = selectedOrders.value.map((o) => o.platformOrderId)
    await OrderApi.syncCrossOrders({
       platformId: queryParams.platformId,
       shopId: queryParams.shopId,
       syncType: 'order',
       platformOrderIds: platformOrderIds
    })
    message.success('批量同步提交成功')
    clearSelection()
    await getList()
  } catch {
    // catch cancel
  } finally {
    loading.value = false
  }
}

// 同步逻辑 (复用)

const handSync = () => {
  if (!queryParams.shopId) {
    message.warning('请选择店铺')
    return
  }
  syncForm.timeRange = [
    dayjs().subtract(7, 'day').format('YYYY-MM-DD HH:mm:ss'),
    dayjs().format('YYYY-MM-DD HH:mm:ss')
  ]
  syncDialogVisible.value = true
}

const handleDoSync = async () => {
  syncLoading.value = true
  try {
    await OrderApi.syncCrossOrders({
       platformId: queryParams.platformId,
       shopId: queryParams.shopId,
       syncType: syncForm.syncType,
       createTimeStart: syncForm.syncType === 'time' ? syncForm.timeRange[0] : undefined,
       createTimeEnd: syncForm.syncType === 'time' ? syncForm.timeRange[1] : undefined,
       platformOrderIds: syncForm.syncType === 'order' ? syncForm.platformOrderIds.split('\n').filter(i => i.trim()) : undefined
    })
    message.success('同步提交成功')
    syncDialogVisible.value = false
    getList()
  } finally {
    syncLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.app-container {
  padding: 8px 10px 0 10px;
  background-color: #f7f9fb;
  min-height: calc(100vh - 84px);
}

.batch-bar {
  position: sticky;
  top: 0;
  z-index: 10;
  
  :deep(.el-alert) {
    padding: 8px 16px;
    border: 1px solid #c2e7b0;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  }
}

.order-list-container {
  margin-top: 15px;
}

:deep(.el-tabs__header) {
  margin-bottom: 0px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
</style>
