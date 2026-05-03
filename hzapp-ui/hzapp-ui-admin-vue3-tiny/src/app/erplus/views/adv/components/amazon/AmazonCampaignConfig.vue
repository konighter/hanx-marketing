<template>
  <div class="amazon-campaign-config">
    <el-form :model="modelValue" label-width="140px" :disabled="disabled">
      <!-- 1. 竞价策略 -->
      <CampaignBiddingManager 
        :shop-id="shopId!"
        :campaign-id="campaignId"
        :external-id="modelValue.externalId"
        :bidding="modelValue.amz_dynamic_bidding"
        :disabled="disabled"
        @refresh="emit('refresh')"
      />


      <!-- 2. 否定投放 (活动级) -->
      <el-divider content-position="left" class="mt-24px">否定投放 (活动级)</el-divider>
      <div class="px-20px">
        <CampaignNegativeManager 
          :shop-id="shopId!"
          :campaign-id="campaignId"
          :external-id="modelValue.externalId"
          :negative-keywords="modelValue.amz_negative_keywords"
          :negative-targets="modelValue.amz_negative_target"
          :disabled="disabled"
          @refresh="emit('refresh')"
        />
      </div>

      <!-- 4. 广告组配置 -->
      <el-divider content-position="left" class="mt-24px">广告组设置</el-divider>
      <div class="ad-group-section px-20px">
        <AdGroupManager 
          v-model:active-tab="activeAdGroupTab"
          :groups="adGroups"
          :loading="loadingGroups"
          :shop-id="shopId"
          :campaign-id="campaignId"
          :targeting-type="targetingType"
          :disabled="disabled"
          @refresh="fetchAdGroups"
          @update-attributes="val => syncAdGroupAttributes(val)"
        />
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import { AdsAdGroup } from '../../types/ads'
import AdGroupManager from './AdGroupManager.vue'
import CampaignBiddingManager from './CampaignBiddingManager.vue'
import CampaignNegativeManager from './CampaignNegativeManager.vue'

const props = defineProps<{ 
  modelValue: any; 
  campaignId: number; 
  shopId?: number; 
  accountId?: number; 
  targetingType?: string;
  disabled?: boolean 
}>()
const emit = defineEmits(['update:modelValue', 'refresh'])

const loadingGroups = ref(false)
const adGroups = ref<AdsAdGroup[]>([])
const activeAdGroupTab = ref('')
 
const syncAdGroupAttributes = (updatedGroup: AdsAdGroup) => {
  if (!updatedGroup.id) return
  const currentGroups = [...(props.modelValue.adGroups || [])]
  const index = currentGroups.findIndex(g => g.id === updatedGroup.id)
  
  const groupData = { 
    id: updatedGroup.id, 
    attributes: updatedGroup.attributes,
    defaultBid: updatedGroup.defaultBid
  }
  
  if (index > -1) currentGroups[index] = { ...currentGroups[index], ...groupData }
  else currentGroups.push(groupData)
  updateModelValue('adGroups', currentGroups)
}

const fetchAdGroups = async () => {
  if (!props.campaignId) return
  loadingGroups.value = true
  try {
    const res = await AdsAdGroupApi.getAdGroupPage({ campaignIds: [props.campaignId], pageSize: 100 })
    const list = res.list || []
    adGroups.value = list
    if (list.length > 0 && !activeAdGroupTab.value) {
      activeAdGroupTab.value = String(list[0].id || list[0].externalId)
    }
  } finally { loadingGroups.value = false }
}

const updateModelValue = (key: string, value: any) => emit('update:modelValue', { ...props.modelValue, [key]: value })

watch(() => props.campaignId, fetchAdGroups, { immediate: true })
onMounted(fetchAdGroups)
</script>

<style scoped>
.amazon-campaign-config {
  padding: 0 10px;
}
:deep(.el-divider--horizontal) {
  margin: 16px 0;
}
.mt-24px {
  margin-top: 24px !important;
}
</style>
