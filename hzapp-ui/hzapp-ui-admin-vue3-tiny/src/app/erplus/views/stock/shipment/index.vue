<template>

  <div class="shipment-container h-[calc(100vh-160px)] overflow-hidden">
    <ContentWrap class="!mb-0 h-full">
      <div class="h-full flex flex-col">
        <!-- 头部固定区域 (Tabs + Form) -->
        <div class="flex-shrink-0">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane v-for="tab in statusTabs" :key="tab.value" :label="tab.label" :name="tab.name" />
          </el-tabs>

          <el-form :model="queryForm" ref="queryFormRef" class="mb-2" :inline="true">
            <el-form-item prop="shopId" class="!mr-10px">
              <ShopCascaderSelect v-model="queryForm.shopId" @change="handleShopChange" class="!w-280px" />
            </el-form-item>
            <!-- <el-form-item prop="marketId" class="!mr-10px">
              <el-select v-model="queryForm.marketId" placeholder="请选择市场" clearable class="!w-180px">
                <el-option v-for="r in markets" :key="r.id" :label="r.zoneName" :value="r.id" />
              </el-select>
            </el-form-item> -->
            <el-form-item prop="createTime" class="!mr-10px">
              <el-date-picker
v-model="queryForm.createTimeRange" type="daterange" range-separator="至"
                start-placeholder="开始时间" end-placeholder="结束时间" class="!w-280px" />
            </el-form-item>

            <el-form-item class="!mr-0">
              <el-button type="primary" @click="handleQuery">
                <Icon icon="ep:search" class="mr-5px" /> 搜索
              </el-button>
              <el-button @click="resetQuery">
                <Icon icon="ep:refresh" class="mr-5px" /> 重置
              </el-button>
              <el-button type="primary" @click="handleAdd">
                <Icon icon="ep:plus" class="mr-5px" /> 创建货件
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 列表 (自动填充剩余空间) -->
        <el-table
row-key="id" v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
          class="flex-1 mt-2" height="100%" :header-cell-style="{ fontSize: '12px' }">
          <template #empty>
            <div class="flex items-center justify-center h-full">
              <el-empty description="没有找到相关数据" />
            </div>
          </template>


          <el-table-column label="名称" min-width="180">
            <template #default="scope">
              <div class="flex flex-col">
                <span class="font-bold text-gray-600">{{ scope.row.name }}</span>
                <span class="text-xs text-gray-400 font-mono">{{ scope.row.code }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="店铺" align="center">
            <template #default="scope">
              {{ scope.row.shopName }}
            </template>
          </el-table-column>

          <el-table-column label="发货仓库" align="center">
            <template #default="scope">
              {{ scope.row.warehouseName }}
            </template>
          </el-table-column>

          <el-table-column label="发货数量" align="center">
            <template #default="scope">
              <el-tooltip placement="top" effect="light">
                <template #content>
                  <div class="w-200px">
                    <el-table
:data="scope.row.items" size="small" border
                      :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }">
                      <el-table-column prop="sellerSku" label="SKU" min-width="120" />
                      <el-table-column prop="quantity" label="数量" width="60" align="center" />
                    </el-table>
                  </div>
                </template>
                <div class="flex flex-col cursor-pointer">
                  <span class="font-bold text-gray-600">{{ scope.row.totalCount }}</span>
                  <span class="text-xs text-gray-400 font-mono">{{ scope.row.skuCount }} SKU</span>
                </div>
              </el-tooltip>
            </template>
          </el-table-column>

          <el-table-column label="状态" align="center">
            <template #default="scope">
              <el-tag type="primary">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" />


          <el-table-column label="操作" align="center" width="200">
            <template #default="{ row }">
              <template v-if="row.status !== ShipmentStatus.CANCELED">
                <el-button type="text" size="small" @click="handleDetail(row)">详情</el-button>



                <el-button
type="text" size="small" v-if="row.status === ShipmentStatus.INIT"
                  @click="handleEdit(row)">编辑</el-button>
                <el-button
type="text" size="small" v-if="row.status === ShipmentStatus.AUDITING"
                  @click="handleAudit(row)">审核</el-button>
                <el-button
type="text" size="small"
                  v-if="row.status !== ShipmentStatus.AUDITING && row.status !== ShipmentStatus.INIT"
                  @click="handleConfig(row)">配置</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 (固定在底部) -->
        <Pagination
:total="total" v-model:page="queryForm.pageNo" v-model:limit="queryForm.pageSize"
          class="flex-shrink-0 justify-end" @pagination="handleQuery" />
      </div>
    </ContentWrap>

    <!-- 表单视图 -->
    <!-- <ShipmentForm v-if="showForm" @close="showForm = false" @success="handleFormSuccess" /> -->


    <!-- 配置侧滑 -->
    <ShipmentConfig ref="configRef" @close="handleConfigClose" />
  </div>

</template>



<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ContentWrap } from '@/components/ContentWrap'
import { Icon } from '@/components/Icon'

import { SellPlatformApi } from '@/app/erp/api/sellplatform' // 已存在模块

import { shipmentApi, ShipmentStatus } from '@/app/erplus/api/stock/shipment'
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue'
import { dateFormatter } from '@/utils/formatTime'
import ShipmentConfig from './components/ShipmentConfig.vue'


const { push } = useRouter() // 路由跳转

const showForm = ref(false)

/** 新建成功回调 */
const handleFormSuccess = () => {
  showForm.value = false
  handleQuery()
}

const handleConfigClose = () => {
  handleQuery()
}

const queryFormRef = ref()
const queryForm = reactive<{
  pageNo: number
  pageSize: number
  platformId?: number
  shopId?: number
  marketId?: number
  status?: number
  createTimeRange?: [Date, Date]
}>({
  pageNo: 1,
  pageSize: 10,
  platformId: undefined,
  shopId: undefined,
  marketId: undefined,
  status: undefined,
  createTimeRange: undefined
})

const activeTab = ref('all')
const statusTabs = [
  { label: '全部', name: 'all', value: undefined },
  { label: '已保存', name: 'init', value: ShipmentStatus.INIT },
  { label: '待审核', name: 'pending_review', value: ShipmentStatus.AUDITING },
  { label: '待配货', name: 'pending_allocation', value: ShipmentStatus.PENDING_SHIPMENT },
  { label: '待装箱', name: 'pending_packing', value: ShipmentStatus.PENDING_BOXING },
  { label: '待贴标', name: 'pending_labeling', value: ShipmentStatus.PENDING_LABEL },
  { label: '待发货', name: 'pending_shipment', value: ShipmentStatus.PENDING_DELIVERY },
  { label: '已发货', name: 'shipped', value: ShipmentStatus.SHIPPED },
  { label: '已取消', name: 'canceled', value: ShipmentStatus.CANCELED, show: false }
]

const loading = ref(false)
const list = ref([])
const total = ref(0) // 列表的总页数

const configRef = ref()

const platforms = ref([])



const handleQuery = async () => {

  loading.value = true
  try {
    queryForm.status = activeTab.value === 'all' ? undefined : statusTabs.find((t) => t.name === activeTab.value)?.value
    const data = await shipmentApi.getShipmentPage(queryForm) || []
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryFormRef.value.resetFields()
}


onMounted(async () => {
  await loadPlatforms()
})


/** 平台相关加载 */
const loadPlatforms = async () => {
  platforms.value = await SellPlatformApi.getSellPlatformListCache() || []
}



/** 标签切换 */
const handleTabChange = (name: string) => {
  const tab = statusTabs.find((t) => t.name === name)
  queryForm.status = tab?.value
  handleQuery()
}



/** 获取状态标签 */
const getStatusLabel = (status: number) => {
  const tab = statusTabs.find((t) => t.value === status)
  return tab ? tab.label : '未知'
}

/** 当店铺变更后，只有在已选平台且至少选中一个店铺时才加载品类 */
const handleShopChange = async (shopIds: number[]) => {
  // 重置区域
  queryForm.marketId = undefined

}

/** 新建发货计划 */
const handleAdd = () => {
  // push({ path: '/erplusv2/stock/shipment-add' })
  push({name: 'ShipmentForm'})
}

/** 编辑发货计划 */
const handleEdit = (row: any) => {
  // push({ path: '/erplusv2/stock/shipment-add', query: { id: row.id } })
  push({name: 'ShipmentForm', query: { id: row.id } })
}

/** 审核发货计划 */
const handleAudit = (row: any) => {
  configRef.value.open(row)
}

/** 配置发货计划 */
const handleConfig = (row: any) => {
  configRef.value.open(row)
}

/** 配置发货计划 */
const handleDetail = (row: any) => {
  configRef.value.open(row, true)
}


onMounted(() => {
  handleQuery()
})






</script>

<style scoped>
:deep(.el-card) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>