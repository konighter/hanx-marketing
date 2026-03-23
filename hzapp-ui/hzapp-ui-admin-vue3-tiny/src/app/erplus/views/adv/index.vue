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
      :account-id="queryParams.shopId" 
      :entity-type="activeTab"
      :query-params="queryParams"
      :sync-loading="syncLoading"
      @data-loaded="handleChartDataLoaded"
    />






  </ContentWrap>


<ContentWrap>
<div class="tabs-with-actions">
  <el-tabs v-model="activeTab" type="card" class="adv-tabs" @tab-change="handleTabChange">
    <el-tab-pane label="广告活动" name="CAMPAIGN">
      <CampaignList 
        :query-params="queryParams"
        :column-settings="campaignColumns"
        @select="handleCampaignSelect"
      />
    </el-tab-pane>
    <el-tab-pane label="广告组" name="ADGROUP">
      <AdGroupList 
        :query-params="queryParams"
        :column-settings="adGroupColumns"
        @select="handleAdGroupSelect"
      />
    </el-tab-pane>
    <el-tab-pane label="广告" name="AD">
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

<script setup lang="ts">
import AdDataChart from './components/AdDataChart.vue'

defineOptions({ name: 'AdsManager' })

const activeTab = ref<'CAMPAIGN' | 'ADGROUP' | 'AD' | 'ACCOUNT'>('CAMPAIGN')
const syncLoading = ref(false)
const syncDialogVisible = ref(false)
const columnCustomizerVisible = ref(false)

const queryParams = reactive({
  shopId: undefined as number | undefined,
  campaignIds: [] as number[],
  adGroupIds: [] as number[],
  adIds: [] as number[]
})

// 模拟列定义
const campaignColumns = ref([])
const adGroupColumns = ref([])
const adColumns = ref([])

const syncForm = reactive({
  dateRange: [] as string[]
})

const handleTabChange = (tab: string) => {
  console.log('Tab changed to:', tab)
}

const handleCampaignSelect = (selection: any[]) => {
  console.log('Campaign selected:', selection)
}

const handleAdGroupSelect = (selection: any[]) => {
  console.log('AdGroup selected:', selection)
}

const handleAdSelect = (selection: any[]) => {
  console.log('Ad selected:', selection)
}

const handleChartDataLoaded = (data: any) => {
  console.log('Chart data loaded:', data)
}

const openSyncDialog = () => {
  syncDialogVisible.value = true
}

const handleSyncConfirm = async () => {
  syncDialogVisible.value = false
}
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