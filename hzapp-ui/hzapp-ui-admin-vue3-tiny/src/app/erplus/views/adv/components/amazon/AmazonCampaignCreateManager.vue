<template>
  <div class="amazon-campaign-create-manager">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="活动名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入广告活动名称" class="!w-400px" />
      </el-form-item>

      <el-form-item label="投放类型" prop="targetingType">
        <el-radio-group v-model="form.targetingType">
          <el-radio label="AUTO">自动投放</el-radio>
          <el-radio label="MANUAL">手动投放</el-radio>
        </el-radio-group>
        <div class="text-12px text-gray-400 ml-20px">
          {{ form.targetingType === 'AUTO' ? '亚马逊将自动将您的广告与搜索词和商品进行匹配。' : '您需要选择关键词或商品来定位您的广告。' }}
        </div>
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="ENABLED">启用</el-radio>
          <el-radio label="PAUSED">暂停</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="每日预算" prop="dailyBudget">
        <div class="flex items-center gap-10px">
          <el-input-number v-model="form.dailyBudget" :precision="2" :min="1" />
          <span class="text-gray-400 text-12px">美元</span>
        </div>
      </el-form-item>

      <el-form-item label="起止时间">
        <div class="flex items-center gap-10px">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
          />
          <span>至</span>
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="结束日期 (可选)"
            value-format="YYYY-MM-DD"
            clearable
          />
        </div>
      </el-form-item>

      <el-divider content-position="left">竞价策略 (Dynamic Bidding)</el-divider>
      <div class="p-15px border-1 border-gray-100 border-solid rounded-4px mb-20px bg-gray-50/30">
        <el-form-item label="策略" prop="dynamicBidding.strategy">
          <el-select v-model="form.dynamicBidding.strategy" style="width: 300px">
            <el-option label="动态竞价 - 只降低" value="LEGACY_FOR_SALES" />
            <el-option label="动态竞价 - 提高和降低" value="AUTO_FOR_SALES" />
            <el-option label="固定竞价" value="MANUAL" />
            <el-option label="基于规则 (ROAS)" value="RULE_BASED" />
          </el-select>
          <div class="mt-8px text-12px text-gray-400 leading-relaxed max-w-500px">
            {{ strategyDescriptions[form.dynamicBidding.strategy] }}
          </div>
        </el-form-item>

        <el-form-item 
          v-if="form.dynamicBidding.strategy === 'RULE_BASED'" 
          label="目标 ROAS" 
          prop="dynamicBidding.targetRoas"
          required
        >
          <div class="flex items-center gap-10px">
            <el-input-number 
              v-model="form.dynamicBidding.targetRoas" 
              :precision="2" :step="0.1" :min="0.1"
              style="width: 180px"
            />
            <span class="text-12px text-gray-500">建议范围: 1.0 - 5.0</span>
          </div>
        </el-form-item>

        <el-form-item label="竞价调整">
          <div class="w-full max-w-700px">
            <el-tabs v-model="activeAdjustmentTab" type="border-card">
              <el-tab-pane label="位置调整 (Placement)" name="placement">
                <div class="mb-10px text-12px text-gray-400">
                  针对特定位置提高竞价（最高 900%）。例如，$1.00 的竞价调整 50% 后将变为 $1.50。
                </div>
                <el-table :data="placementList" border size="small">
                  <el-table-column label="位置" prop="label" width="220" />
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
              </el-tab-pane>
              <el-tab-pane label="受众调整 (Audience)" name="audience">
                <div class="mb-10px text-12px text-gray-400">
                  针对特定受众群体进行竞价调整（SP v3 新特性）。
                </div>
                <el-table :data="audienceList" border size="small">
                  <el-table-column label="受众群体" prop="label" width="220" />
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
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-form-item>
      </div>
    </el-form>

    <div class="flex justify-end mt-20px pt-15px border-t-1 border-t-gray-100 border-t-solid">
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确认并创建活动</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { AdsCampaignApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  shopId: number
}>()

const emit = defineEmits(['success', 'cancel'])

const formRef = ref()
const loading = ref(false)
const activeAdjustmentTab = ref('placement')

const strategyDescriptions: Record<string, string> = {
  'RULE_BASED': '亚马逊将自动调整您的竞价，以达到您的目标广告支出回报率 (ROAS)。',
  'LEGACY_FOR_SALES': '当您的广告不太可能带来销售时，亚马逊将实时降低您的竞价。',
  'AUTO_FOR_SALES': '当您的广告很有可能带来销售时，亚马逊将实时提高您的竞价（最高可提高 100%），并在不太可能带来销售时降低竞价。',
  'MANUAL': '亚马逊将使用您的精确竞价，而不会根据销售可能性进行调整。'
}

const placementList = ref([
  { type: 'PLACEMENT_TOP', label: '搜索顶部 (首页)', percentage: 0 },
  { type: 'PLACEMENT_PRODUCT_PAGE', label: '商品详情页', percentage: 0 },
  { type: 'PLACEMENT_REST_OF_SEARCH', label: '搜索其他位置', percentage: 0 }
])

const audienceList = ref([
  { type: 'NEW_TO_BRAND', label: '品牌新客 (New-to-brand)', percentage: 0 },
  { type: 'BRAND_LOYAL', label: '品牌忠实客 (Brand Loyalists)', percentage: 0 }
])

const form = reactive({
  name: '',
  targetingType: 'AUTO',
  status: 'ENABLED',
  dailyBudget: 10.0,
  startDate: new Date().toISOString().split('T')[0],
  endDate: undefined,
  dynamicBidding: {
    strategy: 'LEGACY_FOR_SALES',
    targetRoas: 1.0,
    placementBidding: [] as any[]
  }
})

const rules = {
  name: [{ required: true, message: '请输入广告活动名称', trigger: 'blur' }],
  targetingType: [{ required: true, message: '请选择投放类型', trigger: 'change' }],
  dailyBudget: [{ required: true, message: '请输入每日预算', trigger: 'blur' }]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    
    // 构造最终的 dynamicBidding 数据
    const dynamicBidding: any = {
      strategy: form.dynamicBidding.strategy,
      placementBidding: placementList.value
        .filter(p => p.percentage > 0)
        .map(p => ({
          placement: p.type,
          percentage: p.percentage
        })),
      shopperCohortBidding: audienceList.value
        .filter(a => a.percentage > 0)
        .map(a => ({
          shopperCohortType: 'AUDIENCE_SEGMENT',
          percentage: a.percentage,
          audienceSegments: [{ type: a.type }]
        }))
    }
    
    if (form.dynamicBidding.strategy === 'RULE_BASED') {
      // 在 attributes 中带上 ROAS，后端可能需要单独处理这个规则
      dynamicBidding.rules = [{ ruleType: 'BID', value: form.dynamicBidding.targetRoas }]
    }

    const data = {
      shopId: props.shopId,
      name: form.name,
      campaignType: 'sp',
      targetingType: form.targetingType,
      status: form.status,
      dailyBudget: form.dailyBudget,
      startDate: form.startDate,
      endDate: form.endDate,
      attributes: {
        amz_dynamic_bidding: dynamicBidding
      }
    }
    
    await AdsCampaignApi.createCampaign(data)
    ElMessage.success('广告活动创建成功')
    emit('success')
  } catch (e: any) {
    console.error('Failed to create campaign:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.amazon-campaign-create-manager {
  padding: 10px;
}
.max-w-600px {
  max-width: 600px;
}
</style>
