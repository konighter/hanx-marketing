<template>
  <div class="amazon-campaign-config">
    <el-form :model="modelValue" label-width="140px" :disabled="disabled">
      <!-- 1. 竞价策略 -->
      <el-divider content-position="left">竞价策略</el-divider>
      <el-form-item label="竞价策略">
        <el-select 
          :model-value="modelValue.dynamicBidding?.strategy" 
          placeholder="请选择竞价策略"
          class="w-300px"
          @update:model-value="val => updateBidding('strategy', val)"
        >
          <el-option label="基于规则 (ROAS)" value="RULE_BASED_BIDDING" />
          <el-option label="动态竞价 - 只降低" value="LEGACY_FOR_SALES" />
          <el-option label="动态竞价 - 提高和降低" value="AUTO_FOR_SALES" />
          <el-option label="固定竞价" value="MANUAL" />
        </el-select>
      </el-form-item>
      
      <el-form-item v-if="modelValue.dynamicBidding?.strategy === 'RULE_BASED_BIDDING'" label="目标 ROAS">
        <el-input-number 
          :model-value="modelValue.dynamicBidding?.rules?.[0]?.value" 
          :precision="2" :step="0.1" :min="0.1"
          @change="val => updateBiddingRule(val)"
        />
      </el-form-item>

      <el-form-item label="优化规则">
        <AdsOptimizationRuleSelect 
          :model-value="modelValue.optimizationRuleId"
          :shop-id="modelValue.shopId"
          :account-id="modelValue.accountId"
          :profile-id="modelValue.profileId"
          @update:model-value="val => updateModelValue('optimizationRuleId', val)"
        />
      </el-form-item>

      <!-- 2. 分位置竞价调整 -->
      <el-divider content-position="left">分位置竞价调整</el-divider>
      <PlacementBiddingTable 
        :model-value="modelValue.dynamicBidding?.placementBidding || []" 
        :disabled="disabled"
        @update="updatePlacement"
      />

      <!-- 3. 否定投放 (活动级) -->
      <el-divider content-position="left">否定投放 (活动级)</el-divider>
      <div class="mb-20px p-15px border-1 border-gray-200 border-solid rounded-4px bg-gray-50">
        <div class="flex items-center mb-10px">
          <span class="mr-10px text-14px font-bold">否定关键词</span>
          <el-button type="primary" link :disabled="disabled" @click="addCampaignNegative">添加</el-button>
        </div>
        <el-table :data="localNegativeKeywords" border size="small">
          <el-table-column label="关键词" prop="keywordText">
            <template #default="{ row }">
              <el-input v-model="row.keywordText" size="small" :disabled="disabled" @change="syncCampaignNegatives" />
            </template>
          </el-table-column>
          <el-table-column label="匹配类型" width="150">
            <template #default="{ row }">
              <el-select v-model="row.matchType" size="small" :disabled="disabled" @change="syncCampaignNegatives">
                <el-option label="否定精准" value="NEGATIVE_EXACT" />
                <el-option label="否定短语" value="NEGATIVE_PHRASE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link :disabled="disabled" @click="removeCampaignNegative($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 4. 广告组配置 -->
      <el-divider content-position="left">广告组设置</el-divider>
      <div v-loading="loadingGroups">
        <el-tabs v-if="adGroups.length > 0" v-model="activeAdGroupTab" type="card">
          <el-tab-pane v-for="group in adGroups" :key="group.id" :label="group.name" :name="group.id.toString()">
            <div class="p-15px border-1 border-gray-100 border-solid rounded-4px">
              <div class="flex items-center mb-15px">
                <span class="mr-20px text-14px">状态: 
                  <el-tag :type="group.status === 'ENABLED' ? 'success' : 'info'" size="small">{{ group.status === 'ENABLED' ? '启用' : '暂停' }}</el-tag>
                </span>
                <span class="mr-10px text-14px">默认竞价:</span>
                <el-input-number v-model="groupBids[group.id]" :precision="2" :step="0.01" :min="0.02" size="small" :disabled="disabled" @change="val => handleGroupBidChange(group.id, val)" />
              </div>

              <el-tabs v-model="activeInnerTab">
                <el-tab-pane label="投放数据" name="targeting">
                  <AdGroupTargetingManager 
                    v-if="activeAdGroup"
                    :config="ensureActiveGroupConfig()" 
                    :default-bid="activeAdGroup.defaultBid || 0"
                    :disabled="disabled"
                    @update="val => syncActiveGroupToModel(val)"
                  />
                </el-tab-pane>
                <el-tab-pane label="否定数据" name="negative">
                  <AdGroupNegativeManager 
                    v-if="activeAdGroup"
                    :config="ensureActiveGroupConfig()"
                    :disabled="disabled"
                    @update="val => syncActiveGroupToModel(val)"
                  />
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-tab-pane>
        </el-tabs>
        <el-empty v-else description="暂无广告组" />
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import { AdsAdGroup } from '../types/ads'
import AdsOptimizationRuleSelect from './AdsOptimizationRuleSelect.vue'
import PlacementBiddingTable from './PlacementBiddingTable.vue'
import AdGroupTargetingManager from './AdGroupTargetingManager.vue'
import AdGroupNegativeManager from './AdGroupNegativeManager.vue'

const props = defineProps<{ modelValue: any; campaignId: number; disabled?: boolean }>()
const emit = defineEmits(['update:modelValue'])

const loadingGroups = ref(false)
const adGroups = ref<AdsAdGroup[]>([])
const groupBids = ref<Record<number, number>>({})
const activeAdGroupTab = ref('')
const activeInnerTab = ref('targeting')
const localNegativeKeywords = ref<any[]>([])

const activeAdGroup = computed(() => adGroups.value.find(g => g.id.toString() === activeAdGroupTab.value))

const ensureActiveGroupConfig = () => {
  const group = activeAdGroup.value
  if (!group) return {}
  if (!group.extData) group.extData = { platformConfig: {} }
  if (!group.extData.platformConfig) group.extData.platformConfig = {}
  return group.extData.platformConfig
}

const syncActiveGroupToModel = (newConfig?: any) => {
  if (!activeAdGroup.value) return
  const config = newConfig || ensureActiveGroupConfig()
  const currentGroups = [...(props.modelValue.adGroups || [])]
  const index = currentGroups.findIndex(g => g.id === activeAdGroup.value!.id)
  const groupData = { id: activeAdGroup.value!.id, extData: { platformConfig: config } }
  
  if (index > -1) currentGroups[index] = { ...currentGroups[index], ...groupData }
  else currentGroups.push(groupData)
  updateModelValue('adGroups', currentGroups)
}

const updateBidding = (key: string, value: any) => {
  const dynamicBidding = { ...(props.modelValue.dynamicBidding || {}), [key]: value }
  updateModelValue('dynamicBidding', dynamicBidding)
}

const updateBiddingRule = (val: any) => {
  const dynamicBidding = { ...(props.modelValue.dynamicBidding || {}) }
  const rules = [...(dynamicBidding.rules || [])]
  if (rules.length === 0) rules.push({ ruleType: 'BID', value: val })
  else rules[0] = { ...rules[0], value: val }
  updateModelValue('dynamicBidding', { ...dynamicBidding, rules })
}

const updatePlacement = (type: string, val: number) => {
  const dynamicBidding = { ...(props.modelValue.dynamicBidding || {}) }
  const adjustments = [...(dynamicBidding.placementBidding || [])]
  const index = adjustments.findIndex(a => a.placement === type)
  if (index > -1) adjustments[index] = { placement: type, percentage: val }
  else adjustments.push({ placement: type, percentage: val })
  updateModelValue('dynamicBidding', { ...dynamicBidding, placementBidding: adjustments })
}

const syncCampaignNegatives = () => updateModelValue('negativeKeywords', [...localNegativeKeywords.value])
const addCampaignNegative = () => { localNegativeKeywords.value.push({ keywordText: '', matchType: 'NEGATIVE_EXACT' }); syncCampaignNegatives() }
const removeCampaignNegative = (i: number) => { localNegativeKeywords.value.splice(i, 1); syncCampaignNegatives() }

const fetchAdGroups = async () => {
  if (!props.campaignId) return
  loadingGroups.value = true
  try {
    const res = await AdsAdGroupApi.getAdGroupPage({ campaignIds: [props.campaignId], pageSize: 100 })
    adGroups.value = res.list
    if (adGroups.value.length > 0 && !activeAdGroupTab.value) activeAdGroupTab.value = adGroups.value[0].id.toString()
    adGroups.value.forEach(g => { groupBids.value[g.id] = g.defaultBid || 0 })
  } finally { loadingGroups.value = false }
}

const handleGroupBidChange = (groupId: number, val: number | undefined) => {
  if (val === undefined) return
  groupBids.value[groupId] = val
  const currentGroups = [...(props.modelValue.adGroups || [])]
  const index = currentGroups.findIndex(g => g.id === groupId)
  const data = { id: groupId, defaultBid: val }
  if (index > -1) currentGroups[index] = { ...currentGroups[index], ...data }
  else currentGroups.push(data)
  updateModelValue('adGroups', currentGroups)
}

const updateModelValue = (key: string, value: any) => emit('update:modelValue', { ...props.modelValue, [key]: value })

watch(() => props.campaignId, fetchAdGroups, { immediate: true })
watch(() => props.modelValue.negativeKeywords, (val) => { localNegativeKeywords.value = JSON.parse(JSON.stringify(val || [])) }, { immediate: true })

onMounted(fetchAdGroups)
</script>
