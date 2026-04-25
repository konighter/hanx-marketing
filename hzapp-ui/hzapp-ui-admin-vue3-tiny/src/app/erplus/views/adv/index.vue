<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <div class="flex justify-between items-center mb-15px">
      <!-- 左侧：搜索工作栏 -->
      <el-form
        class="!mb-0"
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="80px"
      >
        <el-form-item label="店铺" prop="shopId">
          <el-cascader
            v-model="selectedShopPath"
            :options="shopCascaderList"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择平台/店铺"
            clearable
            class="!w-280px"
            @change="handleShopChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <Icon icon="ep:search" class="mr-5px" /> 查询
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 右侧：同步组件 -->
      <AdSyncDialog :shop-id="queryParams.shopId" />
    </div>

    <!-- 展示当前广告效果 (账户级别分析) -->
    <AdAccountDataAnalysis 
      v-if="queryParams.shopId"
      :key="refreshKey"
      :shop-id="queryParams.shopId"
      v-model:date-range="dateRange"
    />
  </ContentWrap>

  <ContentWrap>
    <div class="mb-15px flex items-center justify-between">
      <div class="flex items-center">
        <div class="text-16px font-bold mr-15px">广告管理</div>
        <el-button v-if="queryParams.shopId" type="primary" size="small" @click="handleCreateCampaign">
          <Icon icon="ep:plus" class="mr-5px" /> 新建广告活动
        </el-button>
      </div>

      <!-- 全局列配置 -->
      <el-dropdown trigger="click" :hide-on-click="false">
        <el-button type="default">
          <Icon icon="ep:menu" class="mr-5px" /> 指标列配置
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="col in metricColumns" :key="col.prop">
              <el-checkbox v-model="col.visible">
                {{ col.label }}
              </el-checkbox>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-tabs v-model="activeTab" type="card" @tab-change="handleTabChange" :key="refreshKey">
      <el-tab-pane label="广告活动" name="campaign">
        <AdCampaignList 
          :shop-id="queryParams.shopId"
          :metric-columns="metricColumns"
          :date-range="dateRange"
          @select="handleCampaignSelect"
          @click-name="handleCampaignClick"
        />
      </el-tab-pane>
      <el-tab-pane label="广告组" name="adGroup">
        <AdGroupList 
          :shop-id="queryParams.shopId"
          :campaign-ids="filterContext.campaignId ? [filterContext.campaignId] : selectedCampaignIds"
          :metric-columns="metricColumns"
          :date-range="dateRange"
          @select="handleAdGroupSelect"
          @click-name="handleAdGroupClick"
        />
      </el-tab-pane>
      <el-tab-pane label="广告" name="ad">
        <AdList 
          :shop-id="queryParams.shopId"
          :campaign-ids="filterContext.campaignId ? [filterContext.campaignId] : selectedCampaignIds"
          :ad-group-ids="filterContext.adGroupId ? [filterContext.adGroupId] : selectedAdGroupIds"
          :metric-columns="metricColumns"
          :date-range="dateRange"
        />
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>
</template>

<script setup lang="ts">
import { ShopApi } from '@/app/erplus/api/system/shop'
import AdCampaignList from './components/AdCampaignList.vue'
import AdGroupList from './components/AdGroupList.vue'
import AdList from './components/AdList.vue'
import AdAccountDataAnalysis from './components/AdAccountDataAnalysis.vue'
import AdSyncDialog from './components/AdSyncDialog.vue'

defineOptions({ name: 'AdsAccountManager' })

const message = useMessage()
const activeTab = ref('campaign')
const shopCascaderList = ref<any[]>([])
const selectedShopPath = ref<any[]>([])
const selectedCampaignIds = ref<number[]>([])
const selectedAdGroupIds = ref<number[]>([])
const refreshKey = ref(0)

const handleQuery = () => {
  refreshKey.value++
}

const queryParams = reactive({
  shopId: undefined as number | undefined
})

// === 全局日期范围 (最近14天) ===
const getDefaultDateRange = (): [string, string] => {
  const endDate = new Date()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 14)
  return [
    startDate.toISOString().split('T')[0],
    endDate.toISOString().split('T')[0]
  ]
}
const dateRange = ref<[string, string]>(getDefaultDateRange())

// === 全局指标列配置 ===
const metricColumns = ref([
  { label: '曝光量(Imps)', prop: 'impressions', visible: true, width: 120 },
  { label: '点击量(Clicks)', prop: 'clicks', visible: true, width: 120 },
  { label: '花费(Spend)', prop: 'spend', visible: true, width: 120, isCurrency: true },
  { label: '销售额(Sales)', prop: 'sales', visible: true, width: 120, isCurrency: true },
  { label: '订单量(Orders)', prop: 'orders', visible: true, width: 120 },
  { label: 'ROAS', prop: 'roas', visible: true, width: 100 }
])
// === 全局指标列配置结束 ===

const filterContext = reactive({
  campaignId: undefined as number | undefined,
  campaignName: '',
  adGroupId: undefined as number | undefined,
  adGroupName: ''
})

// 移除 getAccountList，改为 getShopCascaderList
const getShopCascaderList = async () => {
  try {
    shopCascaderList.value = await ShopApi.getCascaderShopList()
  } catch (error) {}
}

const handleShopChange = (value: any[]) => {
  if (value && value.length === 2) {
    queryParams.shopId = value[1]
  } else {
    queryParams.shopId = undefined
  }
}

const handleCreateCampaign = () => {
  message.info('新建广告活动功能开发中...')
}

const handleTabChange = (tab: string) => {
  console.log('Tab changed to:', tab)
}

const handleCampaignSelect = (selection: any[]) => {
  selectedCampaignIds.value = selection.map(item => item.id)
  selectedAdGroupIds.value = [] // 级联清空
  
  // 同步 filterContext：如果只选了一个，则视为当前过滤上下文
  if (selection.length === 1) {
    filterContext.campaignId = selection[0].id
    filterContext.campaignName = selection[0].name
  } else {
    // 否则清除单选上下文，下级组件将使用 selectedCampaignIds 数组
    filterContext.campaignId = undefined
    filterContext.campaignName = ''
  }
}

const handleCampaignClick = (row: any) => {
  filterContext.campaignId = row.id
  filterContext.campaignName = row.name
  filterContext.adGroupId = undefined
  filterContext.adGroupName = ''
  activeTab.value = 'adGroup'
}

const handleAdGroupSelect = (selection: any[]) => {
  selectedAdGroupIds.value = selection.map(item => item.id)
  
  // 同步 filterContext
  if (selection.length === 1) {
    filterContext.adGroupId = selection[0].id
    filterContext.adGroupName = selection[0].name
  } else {
    filterContext.adGroupId = undefined
    filterContext.adGroupName = ''
  }
}

const handleAdGroupClick = (row: any) => {
  filterContext.adGroupId = row.id
  filterContext.adGroupName = row.name
  activeTab.value = 'ad'
}

const handleAdClick = (row: any) => {
  // 目前广告层级已经是底部之一，点击后可以考虑打开抽屉或者做别的处理
  console.log('Clicked ad:', row.name)
}

watch(() => queryParams.shopId, () => {
  filterContext.campaignId = undefined
  filterContext.campaignName = ''
  selectedCampaignIds.value = []
  filterContext.adGroupId = undefined
  filterContext.adGroupName = ''
  selectedAdGroupIds.value = []
  activeTab.value = 'campaign'
})

onMounted(() => {
  getShopCascaderList()
})
</script>
