<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="店铺ID" prop="shopId">

        <ShopCascaderSelect v-model="queryParams.shopId"  class="!w-280px" clearable/>
        <!-- <span>店铺ID</span> -->
      </el-form-item>
      <el-form-item >
        <el-button 
          type="primary" 
          @click="openSyncDialog"
          :loading="syncLoading"
          :disabled="!queryParams.shopId"
        >
          <Icon icon="ep:refresh" class="mr-5px" /> 同步
        </el-button>

      </el-form-item>
     
    </el-form>

    <!-- 展示当前广告活动的效果 -->
    <AdDataChart 
      :shop-id="queryParams.shopId" 
      :active-tab="activeTab"
      :query-params="queryParams"
      :sync-loading="syncLoading"
      @data-loaded="handleChartDataLoaded"
    />






  </ContentWrap>


<ContentWrap>
<div class="tabs-with-actions">
  <el-tabs v-model="activeTab" type="card" class="adv-tabs" @tab-change="handleTabChange">
    <el-tab-pane label="广告活动" name="campaign">
      <CampaignList 
        :query-params="queryParams"
        :column-settings="campaignColumns"
        @select="handleCampaignSelect"
      />
    </el-tab-pane>
    <el-tab-pane label="广告组" name="adGroup">
      <AdGroupList 
        :query-params="queryParams"
        :column-settings="adGroupColumns"
        @select="handleAdGroupSelect"
      />
    </el-tab-pane>
    <el-tab-pane label="广告" name="ad">
      <AdList 
        :query-params="queryParams"
        :column-settings="adColumns"
        @select="handleAdSelect"
      />
    </el-tab-pane>
  </el-tabs>
  
  <div class="tabs-actions">
    <el-popover 
      v-model:visible="columnCustomizerVisible"
      placement="bottom-end" 
      :width="320" 
      trigger="click"
      :teleported="true"
      popper-class="column-customizer-popover"
    >
      <template #reference>
        <el-button 
          type="primary" 
          size="small"
          circle
          class="column-customizer-btn"
        >
          <el-icon><Setting /></el-icon>
        </el-button>
      </template>
      <ColumnCustomizer :table-type="activeTab" @close="columnCustomizerVisible = false" />
    </el-popover>
  </div>
</div>



</ContentWrap>



    <!-- 时间范围选择对话框 -->
  <el-dialog v-model="syncDialogVisible" title="选择同步时间范围" width="500px" append-to-body>
    <el-form :model="syncForm" label-width="100px">
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="syncForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
          value-format="YYYY-MM-DD HH:mm:ss"
          class="w-full"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSyncConfirm" :loading="syncLoading">确定同步</el-button>
      </span>
    </template>
  </el-dialog>


</template>



<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="广告账号" prop="accountId">
        <el-select v-model="queryParams.accountId" placeholder="请选择广告账号" clearable class="!w-240px">
          <el-option
            v-for="item in accountList"
            :key="item.id"
            :label="item.name + ' (' + item.platform + ')'"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button 
          type="primary" 
          @click="handleSyncAll"
          :loading="syncLoading"
          :disabled="!queryParams.accountId"
        >
          <Icon icon="ep:refresh" class="mr-5px" /> 同步全量
        </el-button>
        <el-button 
          type="success" 
          @click="handleSyncIncr"
          :loading="syncLoading"
          :disabled="!queryParams.accountId"
        >
          <Icon icon="ep:refresh" class="mr-5px" /> 同步增量
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 展示当前广告效果 (保留原有组件) -->
    <AdDataChart 
      v-if="queryParams.accountId"
      :account-id="queryParams.accountId" 
      :active-tab="activeTab"
      :query-params="queryParams"
    />
  </ContentWrap>

  <ContentWrap>
    <el-tabs v-model="activeTab" type="card" @tab-change="handleTabChange">
      <el-tab-pane label="广告活动" name="campaign">
        <AdCampaignList 
          :account-id="queryParams.accountId"
          @select="handleCampaignSelect"
        />
      </el-tab-pane>
      <el-tab-pane label="广告组" name="adGroup">
        <AdGroupList 
          :account-id="queryParams.accountId"
          :campaign-ids="selectedCampaignIds"
          @select="handleAdGroupSelect"
        />
      </el-tab-pane>
      <el-tab-pane label="广告" name="ad">
        <AdList 
          :account-id="queryParams.accountId"
          :ad-group-ids="selectedAdGroupIds"
        />
      </el-tab-pane>
      <el-tab-pane label="关键词" name="keyword">
        <AdKeywordList 
          :account-id="queryParams.accountId"
          :ad-group-ids="selectedAdGroupIds"
        />
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>
</template>

<script setup lang="ts">
import { AdsAuthApi, AdsSyncApi } from '@/app/erplus/api/adv/ads'
import { AdsAccount } from './types/ads'
import AdCampaignList from './components/AdCampaignList.vue'
import AdGroupList from './components/AdGroupList.vue'
import AdList from './components/AdList.vue'
import AdKeywordList from './components/AdKeywordList.vue'
import AdDataChart from './components/AdDataChart.vue'

defineOptions({ name: 'AdsManager' })

const message = useMessage()
const activeTab = ref('campaign')
const syncLoading = ref(false)

const accountList = ref<AdsAccount[]>([])
const selectedCampaignIds = ref<number[]>([])
const selectedAdGroupIds = ref<number[]>([])

const queryParams = reactive({
  accountId: undefined as number | undefined
})

const getAccountList = async () => {
  try {
    const data = await AdsAuthApi.getAccountPage({ pageNo: 1, pageSize: 100 })
    accountList.value = data.list
  } catch (error) {}
}

const handleTabChange = (tab: string) => {
  console.log('Tab changed to:', tab)
}

const handleCampaignSelect = (selection: any[]) => {
  selectedCampaignIds.value = selection.map(item => item.id)
  selectedAdGroupIds.value = [] // 级联清空
}

const handleAdGroupSelect = (selection: any[]) => {
  selectedAdGroupIds.value = selection.map(item => item.id)
}

const handleSyncAll = async () => {
  if (!queryParams.accountId) return
  syncLoading.value = true
  try {
    await AdsSyncApi.syncAllMetadata(queryParams.accountId)
    message.success('已触发全量同步任务')
  } finally {
    syncLoading.value = false
  }
}

const handleSyncIncr = async () => {
  if (!queryParams.accountId) return
  syncLoading.value = true
  try {
    await AdsSyncApi.syncIncrMetadata(queryParams.accountId)
    message.success('已触发增量同步任务')
  } finally {
    syncLoading.value = false
  }
}

onMounted(() => {
  getAccountList()
})
</script>

<style scoped>
.tabs-with-actions {
  position: relative;
  display: flex;
  align-items: flex-start;
  width: 100%;
  overflow: hidden;
}

.adv-tabs {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.tabs-actions {
  margin-left: 12px;
  margin-top: 4px;
  flex-shrink: 0;
}

.column-customizer-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.column-customizer-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.6);
}

.column-customizer-btn:active {
  transform: translateY(0);
}

/* 列定制弹窗样式 */
:deep(.column-customizer-popover) {
  padding: 0 !important;
  max-width: 320px;
  box-sizing: border-box;
}
</style>