<template>
  <div class="amazon-campaign-config">
    <el-form :model="modelValue" label-width="140px" :disabled="disabled">
      <!-- 1. 竞价策略 -->
      <el-divider content-position="left">竞价策略</el-divider>
      <el-form-item label="竞价策略">
        <el-select 
          :model-value="modelValue.bidding?.strategy || modelValue.biddingStrategy" 
          placeholder="请选择竞价策略"
          class="w-300px"
          @update:model-value="val => updateBidding('strategy', val)"
        >
          <el-option label="基于规则 (ROAS)" value="RULE_BASED" />
          <el-option label="动态竞价 - 只降低" value="LEGACY_FOR_SALES" />
          <el-option label="动态竞价 - 提高和降低" value="AUTO_OPTIMIZED" />
          <el-option label="固定竞价" value="MANUAL" />
        </el-select>
      </el-form-item>
      
      <el-form-item v-if="modelValue.bidding?.strategy === 'RULE_BASED'" label="目标 ROAS">
        <el-input-number 
          :model-value="modelValue.bidding?.rules?.targetRoas" 
          :precision="2" 
          :step="0.1" 
          :min="0.1"
          @change="val => updateBiddingRule('targetRoas', val)"
        />
      </el-form-item>

      <!-- 2. 分位置竞价调整 -->
      <el-divider content-position="left">分位置竞价调整</el-divider>
      <div class="mb-20px">
        <el-table :data="placementData" border style="width: 100%">
          <el-table-column label="位置" prop="label" width="180" />
          <el-table-column label="竞价调整 (%)">
            <template #default="{ row }">
              <el-input-number 
                v-model="row.percentage" 
                :min="0" 
                :max="900" 
                :step="10" 
                size="small"
                @change="handlePercentageChange(row)"
              /> %
            </template>
          </el-table-column>
          <el-table-column label="目标出价 (参考 $1.00)">
            <template #default="{ row }">
              <el-input-number 
                v-model="row.targetBid"
                :precision="2" 
                :step="0.1" 
                :min="1.0" 
                size="small"
                @change="handleTargetBidChange(row)"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 3. 否定投放规则 (活动级) -->
      <el-divider content-position="left">否定投放 (活动级)</el-divider>
      <div class="mb-20px p-15px border-1 border-gray-200 border-solid rounded-4px bg-gray-50">
        <div class="flex items-center mb-10px">
          <span class="mr-10px">否定关键词</span>
          <el-button type="primary" link @click="addNegativeKeyword">添加</el-button>
        </div>
        <el-table :data="modelValue.negativeKeywords || []" border size="small">
          <el-table-column label="关键词" prop="keywordText">
            <template #default="{ row }">
              <el-input v-model="row.keywordText" size="small" @change="updateNegativeKeywords" />
            </template>
          </el-table-column>
          <el-table-column label="匹配类型" width="150">
            <template #default="{ row }">
              <el-select v-model="row.matchType" size="small" @change="updateNegativeKeywords">
                <el-option label="否定精准" value="NEGATIVE_EXACT" />
                <el-option label="否定短语" value="NEGATIVE_PHRASE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link @click="removeNegativeKeyword($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 4. 广告组配置 -->
      <el-divider content-position="left">广告组设置</el-divider>
      <div v-loading="loadingGroups">
        <el-tabs v-if="adGroups.length > 0" v-model="activeAdGroupTab" type="card">
          <el-tab-pane 
            v-for="group in adGroups" 
            :key="group.id" 
            :label="group.name" 
            :name="group.id.toString()"
          >
            <div class="p-15px border-1 border-gray-100 border-solid rounded-4px">
              <div class="flex items-center mb-15px">
                <span class="mr-20px">状态: 
                  <el-tag :type="group.status === 'ENABLED' ? 'success' : 'info'" size="small">
                    {{ group.status === 'ENABLED' ? '启用' : '暂停' }}
                  </el-tag>
                </span>
                <span class="mr-10px">默认竞价:</span>
                <el-input-number 
                  :model-value="groupBids[group.id]" 
                  :precision="2" 
                  :step="0.01" 
                  :min="0.02" 
                  size="small"
                  @change="val => handleGroupBidChange(group.id, val)"
                />
              </div>

              <el-tabs v-model="activeTargetingTab" class="mt-10px">
                <el-tab-pane label="关键词投放" name="targeting">
                  <el-table :data="[]" border size="small" style="width: 100%">
                    <el-table-column label="关键词" prop="keywordText" />
                    <el-table-column label="匹配类型" prop="matchType" width="120" />
                    <el-table-column label="建议出价" prop="suggestedBid" width="100" />
                    <el-table-column label="状态" prop="status" width="80" />
                  </el-table>
                  <el-empty description="此功能待对接后端逻辑" :image-size="40" />
                </el-tab-pane>
                <el-tab-pane label="否定投放" name="negative">
                  <div class="mb-10px">
                    <el-radio-group v-model="activeNegativeType" size="small">
                      <el-radio-button label="keyword">否定关键词</el-radio-button>
                      <el-radio-button label="product">否定商品</el-radio-button>
                    </el-radio-group>
                  </div>
                  <el-table v-if="activeNegativeType === 'keyword'" :data="[]" border size="small">
                    <el-table-column label="否定关键词" prop="keywordText" />
                    <el-table-column label="匹配类型" prop="matchType" width="120" />
                  </el-table>
                  <el-table v-else :data="[]" border size="small">
                    <el-table-column label="否定商品 (ASIN/SKU)" prop="value" />
                    <el-table-column label="匹配类型" prop="type" width="120" />
                  </el-table>
                  <el-empty description="此功能待对接后端逻辑" :image-size="40" />
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-tab-pane>
        </el-tabs>
        <el-empty v-if="adGroups.length === 0" description="暂无广告组" />
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import { AdsAdGroup } from '../types/ads'

const props = defineProps<{
  modelValue: any
  campaignId: number
  disabled?: boolean
}>()

const emit = defineEmits(['update:modelValue'])

const loadingGroups = ref(false)
const adGroups = ref<AdsAdGroup[]>([])
const groupBids = ref<Record<number, number>>({})
const activeAdGroupTab = ref('')
const activeTargetingTab = ref('targeting')
const activeNegativeType = ref('keyword')

const placementData = ref([
  { type: 'PLACEMENT_TOP', label: '首页 (搜索顶部)', percentage: 0, targetBid: 1.0 },
  { type: 'PLACEMENT_PRODUCT_PAGE', label: '商品详情页', percentage: 0, targetBid: 1.0 },
  { type: 'PLACEMENT_REST_OF_SEARCH', label: '搜索其他位置', percentage: 0, targetBid: 1.0 }
])

// 初始化分位置竞价数据
watch(() => props.modelValue.bidding?.adjustments, (adjustments) => {
  if (adjustments) {
    placementData.value.forEach(item => {
      const adj = adjustments.find(a => a.placement === item.type)
      item.percentage = adj ? adj.percentage : 0
      // Initialize target bid based on current percentage as a starting point
      item.targetBid = Number((1 * (1 + item.percentage / 100)).toFixed(2))
    })
  }
}, { immediate: true })

const handlePercentageChange = (row: any) => {
  // Percentage change only updates the model
  updatePlacement(row.type, row.percentage)
}

const handleTargetBidChange = () => {
  // Target bid is now just a standalone field for manual calculation/reference
  // No longer syncs back to percentage automatically
}

const updateBidding = (key: string, value: any) => {
  const bidding = { ...(props.modelValue.bidding || {}) }
  bidding[key] = value
  // 同步旧字段保持兼容
  if (key === 'strategy') {
    updateModelValue('biddingStrategy', value)
  }
  updateModelValue('bidding', bidding)
}

const updateBiddingRule = (key: string, value: any) => {
  const bidding = { ...(props.modelValue.bidding || {}) }
  const rules = { ...(bidding.rules || {}) }
  rules[key] = value
  bidding.rules = rules
  updateModelValue('bidding', bidding)
}

const updatePlacement = (type: string, val: number) => {
  const bidding = { ...(props.modelValue.bidding || {}) }
  const adjustments = [...(bidding.adjustments || [])]
  const index = adjustments.findIndex(a => a.placement === type)
  if (index > -1) {
    adjustments[index] = { placement: type, percentage: val }
  } else {
    adjustments.push({ placement: type, percentage: val })
  }
  bidding.adjustments = adjustments
  updateModelValue('bidding', bidding)
}

const addNegativeKeyword = () => {
  const list = [...(props.modelValue.negativeKeywords || [])]
  list.push({ keywordText: '', matchType: 'NEGATIVE_EXACT' })
  updateModelValue('negativeKeywords', list)
}

const removeNegativeKeyword = (index: number) => {
  const list = [...(props.modelValue.negativeKeywords || [])]
  list.splice(index, 1)
  updateModelValue('negativeKeywords', list)
}

const updateNegativeKeywords = () => {
  updateModelValue('negativeKeywords', props.modelValue.negativeKeywords)
}

const fetchAdGroups = async () => {
  if (!props.campaignId) return
  loadingGroups.value = true
  try {
    const res = await AdsAdGroupApi.getAdGroupPage({
      campaignIds: [props.campaignId],
      pageSize: 100
    })
    adGroups.value = res.list
    if (adGroups.value.length > 0 && !activeAdGroupTab.value) {
      activeAdGroupTab.value = adGroups.value[0].id.toString()
    }
    // Init bids
    adGroups.value.forEach(group => {
      groupBids.value[group.id] = group.defaultBid || 0
    })
    
    // Ensure amazonConfig has adGroups field
    if (!props.modelValue.adGroups) {
      updateModelValue('adGroups', [])
    }
  } finally {
    loadingGroups.value = false
  }
}

const handleGroupBidChange = (groupId: number, val: number) => {
  groupBids.value[groupId] = val
  const currentGroups = [...(props.modelValue.adGroups || [])]
  const index = currentGroups.findIndex(g => g.id === groupId)
  
  if (index > -1) {
    currentGroups[index] = { ...currentGroups[index], defaultBid: val }
  } else {
    currentGroups.push({ id: groupId, defaultBid: val })
  }
  
  updateModelValue('adGroups', currentGroups)
}

const updateModelValue = (key: string, value: any) => {
  emit('update:modelValue', {
    ...props.modelValue,
    [key]: value
  })
}

watch(() => props.campaignId, () => {
  fetchAdGroups()
}, { immediate: true })

onMounted(() => {
  fetchAdGroups()
})
</script>

<style scoped>
.amazon-campaign-config {
  width: 100%;
}
.ad-group-item {
  background-color: #f9f9f9;
}
</style>
