<template>
  <div class="amazon-campaign-config">
    <el-form :model="modelValue" label-width="140px" :disabled="disabled">
      <!-- 1. 竞价策略 -->
      <el-divider content-position="left">竞价策略</el-divider>
      <el-form-item label="竞价策略">
        <el-select 
          :model-value="modelValue.amz_dynamic_bidding?.strategy" 
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
      
      <el-form-item v-if="modelValue.amz_dynamic_bidding?.strategy === 'RULE_BASED_BIDDING'" label="目标 ROAS">
        <el-input-number 
          :model-value="modelValue.amz_dynamic_bidding?.rules?.[0]?.value" 
          :precision="2" :step="0.1" :min="0.1"
          @change="val => updateBiddingRule(val)"
        />
      </el-form-item>

      <el-form-item label="优化规则">
        <AdsOptimizationRuleSelect 
          :model-value="modelValue.amz_optimization_rules"
          :shop-id="shopId"
          :accountId="accountId"
          @update:model-value="val => updateModelValue('amz_optimization_rules', val)"
        />
      </el-form-item>

      <!-- 2. 分位置竞价调整 -->
      <el-divider content-position="left">分位置竞价调整</el-divider>
      <PlacementBiddingTable 
        :model-value="modelValue.amz_dynamic_bidding?.placementBidding || []" 
        :disabled="disabled"
        @update="updatePlacement"
      />

      <!-- 3. 否定投放 (活动级) -->
      <el-divider content-position="left">否定投放 (活动级)</el-divider>
      
      <div class="mb-8px">
        <!-- 否定类型选择 -->
        <el-radio-group v-model="activeNegativeType" size="small" class="mb-10px">
          <el-radio-button label="keyword">否定关键词 ({{ localNegativeKeywords.length }})</el-radio-button>
          <el-radio-button label="product">否定商品 ({{ localNegativeProducts.length }})</el-radio-button>
        </el-radio-group>
      </div>

      <div class="p-10px border-1 border-gray-200 border-solid rounded-4px bg-gray-50 min-h-80px">
        <!-- 否定关键词表格 -->
        <div v-if="activeNegativeType === 'keyword'">
          <div class="flex items-center mb-6px">
            <span class="mr-8px text-12px font-bold">否定关键词</span>
            <el-button type="primary" link size="small" :disabled="disabled" @click="addCampaignNegative" class="!text-12px">+ 添加</el-button>
          </div>
          <el-table :data="localNegativeKeywords" border size="small" empty-text="暂无否定关键词">
            <el-table-column label="关键词" prop="keywordText">
              <template #default="{ row }">
                <el-input v-model="row.keywordText" size="small" placeholder="请输入关键词" :disabled="disabled" @change="syncCampaignNegatives" />
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
            <el-table-column label="操作" width="70" align="center">
              <template #default="{ $index }">
                <el-button type="danger" link size="small" :disabled="disabled" @click="removeCampaignNegative($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 否定商品表格 -->
        <div v-if="activeNegativeType === 'product'">
          <div class="flex items-center mb-6px">
            <span class="mr-8px text-12px font-bold">否定商品</span>
            <el-button type="primary" link size="small" :disabled="disabled" @click="addCampaignNegativeProduct" class="!text-12px">+ 添加</el-button>
          </div>
          <el-table :data="localNegativeProducts" border size="small" empty-text="暂无否定商品">
            <el-table-column label="类型" width="120">
              <template #default="{ row }">
                <div v-if="row.targetId" class="text-13px py-4px">
                  {{ row.expression[0].type === 'ASIN_SAME_AS' ? '商品 (ASIN)' : '品牌' }}
                </div>
                <el-select v-else v-model="row.expression[0].type" size="small" :disabled="disabled" @change="syncCampaignNegativeProducts">
                  <el-option label="商品 (ASIN)" value="ASIN_SAME_AS" />
                  <el-option label="品牌" value="ASIN_BRAND_SAME_AS" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="值 (ASIN/品牌)" prop="expression">
              <template #default="{ row }">
                <div v-if="row.targetId" class="text-13px py-4px font-medium">
                  {{ row.resolvedExpression?.[0]?.value || row.expression[0].value }}
                </div>
                <el-input 
                  v-else
                  v-model="row.expression[0].value" 
                  size="small" 
                  :placeholder="row.expression[0].type === 'ASIN_SAME_AS' ? '请输入 ASIN' : '请输入品牌 ID'"
                  :disabled="disabled" 
                  @change="syncCampaignNegativeProducts" 
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" align="center">
              <template #default="{ $index }">
                <el-button type="danger" link size="small" :disabled="disabled" @click="removeCampaignNegativeProduct($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 4. 广告组配置 -->
      <el-divider content-position="left">广告组设置</el-divider>
      <div class="ad-group-section">
        <AdGroupManager 
          v-model:active-tab="activeAdGroupTab"
          :groups="adGroups"
          :loading="loadingGroups"
          :shop-id="shopId"
          :campaign-id="campaignId"
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
import AdsOptimizationRuleSelect from './AdsOptimizationRuleSelect.vue'
import PlacementBiddingTable from './PlacementBiddingTable.vue'
import AdGroupManager from './AdGroupManager.vue'

const props = defineProps<{ 
  modelValue: any; 
  campaignId: number; 
  shopId?: number; 
  accountId?: number; 
  disabled?: boolean 
}>()
const emit = defineEmits(['update:modelValue'])

const loadingGroups = ref(false)
const adGroups = ref<AdsAdGroup[]>([])
const activeAdGroupTab = ref('')
const activeNegativeType = ref('keyword')
const localNegativeKeywords = ref<any[]>([])
const localNegativeProducts = ref<any[]>([])
 
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

const updateBidding = (key: string, value: any) => {
  const dynamicBidding = { ...(props.modelValue.amz_dynamic_bidding || {}), [key]: value }
  updateModelValue('amz_dynamic_bidding', dynamicBidding)
}

const updateBiddingRule = (val: any) => {
  const dynamicBidding = { ...(props.modelValue.amz_dynamic_bidding || {}) }
  const rules = [...(dynamicBidding.rules || [])]
  if (rules.length === 0) rules.push({ ruleType: 'BID', value: val })
  else rules[0] = { ...rules[0], value: val }
  updateModelValue('amz_dynamic_bidding', { ...dynamicBidding, rules })
}

const updatePlacement = (type: string, val: number) => {
  const dynamicBidding = { ...(props.modelValue.amz_dynamic_bidding || {}) }
  const adjustments = [...(dynamicBidding.placementBidding || [])]
  const index = adjustments.findIndex(a => a.placement === type)
  if (index > -1) adjustments[index] = { placement: type, percentage: val }
  else adjustments.push({ placement: type, percentage: val })
  updateModelValue('amz_dynamic_bidding', { ...dynamicBidding, placementBidding: adjustments })
}

const syncCampaignNegatives = () => {
  // 过滤为空的关键词
  const validKeywords = localNegativeKeywords.value.filter(k => k.keywordText && k.keywordText.trim())
  updateModelValue('amz_negative_keywords', validKeywords)
}
const addCampaignNegative = () => { localNegativeKeywords.value.push({ keywordText: '', matchType: 'NEGATIVE_EXACT' }) }
const removeCampaignNegative = (i: number) => { localNegativeKeywords.value.splice(i, 1); syncCampaignNegatives() }

const syncCampaignNegativeProducts = () => {
  // 过滤为空的商品
  const validProducts = localNegativeProducts.value.filter(p => p.expression?.[0]?.value && p.expression[0].value.trim())
  updateModelValue('amz_negative_target', validProducts)
}
const addCampaignNegativeProduct = () => {
  localNegativeProducts.value.push({
    state: 'ENABLED',
    expression: [{ type: 'ASIN_SAME_AS', value: '' }]
  })
}
const removeCampaignNegativeProduct = (i: number) => { localNegativeProducts.value.splice(i, 1); syncCampaignNegativeProducts() }

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
watch(() => props.modelValue.amz_negative_keywords, (val) => { 
  localNegativeKeywords.value = JSON.parse(JSON.stringify(val || []))
  if (localNegativeKeywords.value.length > 0) activeNegativeType.value = 'keyword'
}, { immediate: true })
watch(() => props.modelValue.amz_negative_target, (val) => { 
  localNegativeProducts.value = JSON.parse(JSON.stringify(val || []))
  if (localNegativeProducts.value.length > 0) activeNegativeType.value = 'product'
}, { immediate: true })

onMounted(fetchAdGroups)
</script>

<style scoped>
.compact-radio :deep(.el-radio-button__inner) {
  padding: 5px 12px;
  font-size: 12px;
}

.inner-tabs :deep(.el-tabs__item) {
  font-size: 13px;
  padding: 0 12px;
  height: 30px;
  line-height: 30px;
}

.inner-tabs :deep(.el-tabs__header) {
  margin-bottom: 8px;
}

:deep(.el-table--small .el-table__cell) {
  padding: 4px 0;
}

:deep(.el-table .cell) {
  padding-left: 8px;
  padding-right: 8px;
  font-size: 12px;
}

:deep(.el-divider--horizontal) {
  margin: 16px 0;
}
</style>
