<template>
  <div class="campaign-bidding-manager">
    <el-divider content-position="left">
      竞价管理
    </el-divider>

    <el-form 
      ref="formRef" 
      :model="localBidding" 
      label-width="140px" 
      :disabled="disabled"
      class="bidding-form"
    >
      <!-- 1. 竞价策略 -->
      <el-form-item label="竞价策略" prop="strategy">
        <div class="flex items-center gap-20px">
          <el-select 
            v-model="localBidding.strategy" 
            placeholder="请选择竞价策略"
            style="width: 240px"
          >
            <el-option label="基于规则 (ROAS)" value="RULE_BASED" />
            <el-option label="动态竞价 - 只降低" value="LEGACY_FOR_SALES" />
            <el-option label="动态竞价 - 提高和降低" value="AUTO_FOR_SALES" />
            <el-option label="固定竞价" value="MANUAL" />
          </el-select>
          <div class="strategy-desc text-gray-400 text-12px">
            {{ strategyDescriptions[localBidding.strategy] }}
          </div>
        </div>
      </el-form-item>

      <el-form-item 
        v-if="localBidding.strategy === 'RULE_BASED'" 
        label="目标 ROAS" 
        prop="targetRoas"
        :rules="[{ required: true, message: '请输入目标 ROAS', trigger: 'blur' }]"
      >
        <el-input-number 
          v-model="localBidding.targetRoas" 
          :precision="2" :step="0.1" :min="0.1"
          style="width: 300px"
        />
      </el-form-item>

      <!-- 2. 出价调整 (位置 & 受众) -->
      <el-form-item label="出价调整">
        <div class="w-full max-w-600px">
          <el-tabs v-model="activeAdjustmentTab" type="card" class="adjustment-tabs">
            <el-tab-pane label="分位置调整" name="placement">
              <div class="p-10px border-1 border-gray-100 border-solid rounded-4px">
                <el-table :data="placementList" border size="small">
                  <el-table-column label="位置" prop="label" width="180" />
                  <el-table-column label="竞价调整 (%)">
                    <template #default="{ row }">
                      <div class="flex items-center gap-10px">
                        <el-input-number 
                          v-model="row.percentage" 
                          :min="0" :max="900" :step="10" 
                          size="small"
                        />
                        <span class="text-12px text-gray-500">%</span>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="mt-8px text-gray-400 text-12px leading-relaxed">
                  说明：亚马逊将根据您设置的百分比，在相应位置提高您的竞价。
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="分受众调整" name="audience">
              <div class="p-10px border-1 border-gray-100 border-solid rounded-4px">
                <div class="flex justify-end mb-8px">
                  <el-button type="primary" link size="small" icon="Plus" disabled>添加受众段 (即将支持)</el-button>
                </div>
                <el-table :data="audienceList" border size="small" empty-text="暂无受众调整">
                  <el-table-column label="受众类型" prop="label" width="180" />
                  <el-table-column label="竞价调整 (%)">
                    <template #default="{ row }">
                      <div class="flex items-center gap-10px">
                        <el-input-number 
                          v-model="row.percentage" 
                          :min="0" :max="900" :step="10" 
                          size="small"
                        />
                        <span class="text-12px text-gray-500">%</span>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-form-item>

      <div v-if="hasChanged && !disabled" class="flex justify-start mt-20px pb-20px ml-140px">
        <el-button type="primary" size="small" :loading="saving" @click="saveDynamicBidding">保存</el-button>
        <el-button size="small" @click="resetBidding">取消</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { AmzAdvCampaignManagerApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  shopId: number
  campaignId: number 
  externalId?: string
  bidding: any
  disabled?: boolean
}>()

const emit = defineEmits(['update:bidding', 'refresh'])

const formRef = ref()
const activeAdjustmentTab = ref('placement')
const saving = ref(false)
const snapshot = ref<string>('')

const strategyDescriptions: Record<string, string> = {
  'RULE_BASED': '亚马逊将自动调整您的竞价，以达到您的目标广告支出回报率 (ROAS)。',
  'LEGACY_FOR_SALES': '当您的广告不太可能带来销售时，亚马逊将实时降低您的竞价。',
  'AUTO_FOR_SALES': '当您的广告很有可能带来销售时，亚马逊将实时提高您的竞价（最高可提高 100%），并在不太可能带来销售时降低竞价。',
  'MANUAL': '亚马逊将使用您的精确竞价，而不会根据销售可能性进行调整。'
}

const localBidding = reactive({
  strategy: '',
  targetRoas: 1.0,
})

const placementList = ref([
  { type: 'PLACEMENT_TOP', label: '搜索顶部 (首页)', percentage: 0 },
  { type: 'PLACEMENT_PRODUCT_PAGE', label: '商品详情页', percentage: 0 },
  { type: 'PLACEMENT_REST_OF_SEARCH', label: '搜索其他位置', percentage: 0 }
])

const audienceList = ref<any[]>([])

// 同步初始值
const getCurrentStateString = () => {
  return JSON.stringify({
    localBidding,
    placementList: placementList.value,
    audienceList: audienceList.value
  })
}

const syncBidding = (val: any) => {
  if (!val) return
  localBidding.strategy = val.strategy || 'LEGACY_FOR_SALES'
  localBidding.targetRoas = val.rules?.[0]?.value || 1.0
  
  // 位置调整
  const placements = val.placementBidding || []
  placementList.value.forEach(item => {
    const found = placements.find((p: any) => p.placement === item.type)
    item.percentage = found ? found.percentage : 0
  })
  
  // 受众调整
  const cohorts = val.shopperCohortBidding || []
  audienceList.value = cohorts.map((c: any) => ({
    type: c.shopperCohortType,
    label: c.shopperCohortType === 'AUDIENCE_SEGMENT' ? '特定受众段' : c.shopperCohortType,
    percentage: c.percentage,
    audienceSegments: c.audienceSegments
  }))

  snapshot.value = getCurrentStateString()
}

watch(() => props.bidding, (val) => {
  syncBidding(val)
}, { immediate: true, deep: true })



const hasChanged = computed(() => {
  if (!snapshot.value) return false
  return getCurrentStateString() !== snapshot.value
})

const resetBidding = () => {
  if (props.bidding) {
    syncBidding(props.bidding)
  }
}

const saveDynamicBidding = async () => {
  const campaignId = props.campaignId
  
  if (!props.shopId || !campaignId) {
    ElMessage.error('缺少必要参数: shopId 或 campaignId')
    return
  }
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
  } catch (err) {
    ElMessage.warning('请检查输入项')
    return
  }

  saving.value = true
  try {
    const dynamicBidding: any = {
      strategy: localBidding.strategy,
      placementBidding: placementList.value.map(p => ({
        placement: p.type,
        percentage: p.percentage
      })),
      shopperCohortBidding: audienceList.value.map(a => ({
        shopperCohortType: a.type,
        percentage: a.percentage,
        audienceSegments: a.audienceSegments
      }))
    }
    
    // 处理 ROAS 规则
    if (localBidding.strategy === 'RULE_BASED') {
      dynamicBidding.rules = [{ ruleType: 'BID', value: localBidding.targetRoas }]
    }

    const res = await AmzAdvCampaignManagerApi.updateDynamicBidding({
      shopId: props.shopId,
      campaignId: campaignId,
      dynamicBidding
    })
    
    if (res) {
      ElMessage.success('竞价管理更新成功')
      snapshot.value = getCurrentStateString()
      emit('refresh')
    } else {
      ElMessage.error('更新失败：后端未返回成功标识')
    }
  } catch (error: any) {
    console.error('Failed to update dynamic bidding', error)
    ElMessage.error('更新失败: ' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.campaign-bidding-manager {
  margin-bottom: 24px;
}
.adjustment-tabs {
  margin-top: 5px;
  border-radius: 4px;
  overflow: hidden;
}
.adjustment-tabs :deep(.el-tabs__content) {
  padding: 0;
}
.adjustment-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}
.strategy-desc {
  line-height: 1.4;
  max-width: 500px;
}
.max-w-600px {
  max-width: 600px;
}
</style>
