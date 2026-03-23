<template>
  <el-drawer
    v-model="visible"
    title="广告计划详情"
    size="90%"
    @closed="handleClosed"
    destroy-on-close
  >
    <template #header>
      <div class="flex items-center">
        <span class="mr-10px font-bold text-18px">广告计划详情</span>
        <el-divider direction="vertical" />
        <div v-if="!isEditingName" class="flex items-center">
          <span class="mr-10px text-16px color-primary">{{ form.name }}</span>
          <el-button 
            v-if="!isArchived"
            link 
            type="primary" 
            @click="isEditingName = true"
          >
            <Icon icon="ep:edit" />
          </el-button>
        </div>
        <div v-else class="flex items-center" style="max-width: 50%">
          <el-input 
            v-model="form.name" 
            placeholder="请输入计划名称" 
            autofocus
            @blur="isEditingName = false"
            @keyup.enter="isEditingName = false"
          />
          <el-button 
            link 
            type="success" 
            class="ml-5px"
            @click="isEditingName = false"
          >
            <Icon icon="ep:check" />
          </el-button>
        </div>
      </div>
    </template>
    <div v-loading="loading" class="drawer-content">
      <el-tabs v-model="activeTab" tab-position="right" class="detail-tabs">
        <el-tab-pane label="基本信息" name="basic">
          <div class="p-20px">
            <el-form :model="form" label-width="100px" :disabled="isArchived">
              <el-form-item label="状态">
                <el-radio-group v-model="form.status">
                  <el-radio-button 
                    v-for="dict in ad_status" 
                    :key="dict.value" 
                    :label="dict.value"
                  >
                    {{ dict.label }}
                  </el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="计划类型">
                <dict-tag :type="DICT_TYPE.AD_CAMPAIGN_TYPE" :value="detail.campaignType" />
              </el-form-item>
              <el-form-item label="预算类型">
                <el-tag type="info">{{ detail.budgetType === 'DAILY' ? '日预算' : '总预算' }}</el-tag>
              </el-form-item>
              <el-form-item :label="detail.budgetType === 'DAILY' ? '每日预算' : '总预算'">
                <el-input-number v-model="form.budget" :precision="2" :step="1" :min="0" />
              </el-form-item>
              <el-form-item label="投放时间">
                <el-date-picker
                  v-model="startDate"
                  type="date"
                  placeholder="开始日期"
                  value-format="YYYY-MM-DD"
                  clearable
                  style="width: 140px"
                />
                <span class="mx-10px">至</span>
                <el-date-picker
                  v-model="endDate"
                  type="date"
                  placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  clearable
                  style="width: 140px"
                />
              </el-form-item>
              <el-form-item label="分时投放">
                <DeliverySchedule v-model="schedule" :disabled="isArchived" />
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <el-tab-pane v-if="detail.platform === 'AMAZON'" label="亚马逊配置" name="platform-config">
          <div class="p-20px">
            <AmazonCampaignConfig 
              v-if="detail.id"
              v-model="platformConfig"
              :campaign-id="detail.id"
              :disabled="isArchived"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="数据分析" name="data">
          <div class="p-20px">
            <AdCampaignDataAnalysis 
              v-if="visible && activeTab === 'data'"
              :account-id="detail.accountId"
              :campaign-id="detail.id!"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <div class="flex justify-end p-20px">
        <el-button @click="visible = false">取消</el-button>
        <el-button 
          v-if="['basic', 'platform-config'].includes(activeTab) && !isArchived"
          type="primary" 
          :loading="saving" 
          @click="handleSave"
        >保存</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'

import { DICT_TYPE, ad_status } from '@/app/erplus/common/dict'
import { AdsCampaignApi } from '@/app/erplus/api/adv/ads'
import { AdsCampaign } from '../types/ads'
import AdCampaignDataAnalysis from './AdCampaignDataAnalysis.vue'
import DeliverySchedule from './DeliverySchedule.vue'
import AmazonCampaignConfig from './AmazonCampaignConfig.vue'

const visible = ref(false)
const loading = ref(false)
const saving = ref(false)
const isEditingName = ref(false)
const activeTab = ref('basic')
const detail = ref<Partial<AdsCampaign>>({})

const form = reactive({
  name: '',
  status: '',
  budget: 0,
})

const startDate = ref<string | null>(null)
const endDate = ref<string | null>(null)
const schedule = ref<boolean[][]>([])
const platformConfig = ref<any>({})

const isArchived = computed(() => detail.value.status === 'ARCHIVED' || form.status === 'ARCHIVED')

const open = async (id: number) => {
  visible.value = true
  loading.value = true
  activeTab.value = 'basic'
  isEditingName.value = false
  try {
    const res = await AdsCampaignApi.getCampaign(id)
    detail.value = res
    // Init form
    form.name = res.name
    form.status = res.status
    form.budget = res.budgetType === 'DAILY' ? res.dailyBudget : res.totalBudget
    startDate.value = res.startDate || null
    endDate.value = res.endDate || null

    // Init schedule from standalone field
    if (res.deliverySchedule) {
      if (typeof res.deliverySchedule === 'string') {
        try {
          schedule.value = JSON.parse(res.deliverySchedule)
        } catch (e) {
          console.error('Parse deliverySchedule failed:', e)
          schedule.value = Array.from({ length: 7 }, () => Array(24).fill(true))
        }
      } else {
        schedule.value = res.deliverySchedule
      }
    } else {
      schedule.value = Array.from({ length: 7 }, () => Array(24).fill(true))
    }

    // Init platform config from extData
    const extData = res.extData || {}
    platformConfig.value = {
      ...(extData.platformConfig || {}),
      accountId: res.accountId
    }
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (!detail.value.id) return
  
  saving.value = true
  try {
    const { adGroups, accountId, ...cleanPlatformConfig } = platformConfig.value || {}

    const updateData: any = {
      id: detail.value.id,
      name: form.name,
      status: form.status,
      startDate: startDate.value,
      endDate: endDate.value,
      deliverySchedule: JSON.stringify(schedule.value),
      adGroups: adGroups,
      extData: {
        ...(detail.value.extData || {}),
        platformConfig: cleanPlatformConfig
      }
    }
    
    if (detail.value.budgetType === 'DAILY') {
      updateData.dailyBudget = form.budget
    } else {
      updateData.totalBudget = form.budget
    }
    
    await AdsCampaignApi.updateCampaign(updateData)
    ElMessage.success('保存成功')
    visible.value = false
    emit('success')
  } catch (error) {
    console.error('Save campaign failed:', error)
  } finally {
    saving.value = false
  }
}

const handleClosed = () => {
  detail.value = {}
  activeTab.value = 'basic'
  platformConfig.value = {}
}

const emit = defineEmits(['success'])

defineExpose({ open })
</script>

<style scoped>
.drawer-content {
  height: 100%;
}
.detail-tabs {
  height: 100%;
}
.detail-tabs :deep(.el-tabs__content) {
  height: 100%;
  overflow-y: auto;
}
</style>
