<template>
  <div v-loading="loading" class="ad-group-manager">
    <el-tabs 
      v-if="groups && groups.length > 0" 
      :model-value="activeTab" 
      type="card" 
      :addable="!disabled"
      :closable="!disabled"
      @update:model-value="val => $emit('update:activeTab', val)"
      @tab-add="showCreateGroupDialog"
      @tab-remove="handleTabRemove"
    >
      <el-tab-pane 
        v-for="group in groups" 
        :key="group.id || group.externalId" 
        :label="group.name || '未命名广告组'" 
        :name="String(group.id || group.externalId)"
      >
        <div class="p-10px border-1 border-gray-100 border-solid rounded-4px">
          <div class="flex items-center gap-15px mb-12px">
            <div class="flex items-center">
              <span class="mr-10px text-14px">状态:</span>
              <el-switch 
                v-model="group.status" 
                active-value="ENABLED" 
                inactive-value="PAUSED" 
                inline-prompt
                active-text="启用"
                inactive-text="停用"
                size="small"
                :disabled="disabled"
                @change="val => handleAdGroupStateChange(group, val as string)"
              />
            </div>
            <div class="flex items-center">
              <span class="mr-10px text-14px">默认竞价:</span>
              <el-input-number 
                v-model="group.defaultBid" 
                :precision="2" :step="0.01" :min="0.01" 
                size="small" 
                :disabled="disabled" 
                @change="val => handleBidChange(group, val)"
              />
            </div>
          </div>

          <el-tabs v-model="activeInnerTab" class="inner-tabs">
            <el-tab-pane label="定向策略" name="targeting">
              <AdGroupTargetingManager 
                v-if="isActive(group)"
                :config="group.attributes || {}" 
                :default-bid="group.defaultBid || 0"
                :ad-group-id="group.id"
                :external-id="group.externalId"
                :shop-id="shopId"
                :targeting-type="targetingType"
                :disabled="disabled"
                @update="val => syncAdGroupAttributes(group, val)"
                @refresh="$emit('refresh')"
              />
            </el-tab-pane>
            <el-tab-pane label="否定投放" name="negative">
              <AdGroupNegativeManager 
                v-if="isActive(group)"
                :config="group.attributes || {}"
                :ad-group-id="group.id"
                :shop-id="shopId"
                :disabled="disabled"
                @update="val => syncAdGroupAttributes(group, val)"
                @refresh="$emit('refresh')"
              />
            </el-tab-pane>
            <el-tab-pane label="广告列表" name="ad">
              <AdGroupAdManager 
                v-if="isActive(group)"
                :ad-group-id="group.id"
                :campaign-id="campaignId"
                :disabled="disabled"
              />
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-tab-pane>
    </el-tabs>
    <el-empty v-else description="暂无广告组">
      <el-button type="primary" :disabled="disabled" @click="showCreateGroupDialog">立即添加广告组</el-button>
    </el-empty>

    <!-- 创建广告组对话框 -->
    <AdsAdGroupCreateDialog 
      v-model="createGroupDialogVisible"
      :campaign-id="campaignId"
      :shop-id="shopId"
      :targeting-type="targetingType"
      @success="$emit('refresh')"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, provide, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { AdsAdGroup } from '../../types/ads'
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import AdGroupTargetingManager from './AdGroupTargetingManager.vue'
import AdGroupNegativeManager from './AdGroupNegativeManager.vue'
import AdGroupAdManager from './AdGroupAdManager.vue'
import AdsAdGroupCreateDialog from './AdsAdGroupCreateDialog.vue'

const props = defineProps<{
  groups: AdsAdGroup[]
  activeTab: string
  shopId: number
  campaignId: number
  loading?: boolean
  targetingType?: string
  disabled?: boolean
}>()

const emit = defineEmits(['update:activeTab', 'refresh', 'update-attributes'])

provide('shopId', computed(() => props.shopId))

const activeInnerTab = ref('targeting')
const createGroupDialogVisible = ref(false)

const isActive = (group: AdsAdGroup) => {
  return props.activeTab === String(group.id || group.externalId)
}

const showCreateGroupDialog = () => {
  createGroupDialogVisible.value = true
}

const handleAdGroupStateChange = async (group: AdsAdGroup, val: string) => {
  if (!group.id) return
  try {
    await AdsAdGroupApi.updateAdGroupStatus({ id: group.id, status: val })
    ElMessage.success('广告组状态已更新')
    emit('refresh')
  } catch (e) {
    // 失败时回滚
    group.status = val === 'ENABLED' ? 'PAUSED' : 'ENABLED'
    ElMessage.error('更新状态失败')
  }
}

const handleBidChange = (group: AdsAdGroup, val: number | undefined) => {
  if (val === undefined) return
  emit('update-attributes', { ...group, defaultBid: val })
}

const syncAdGroupAttributes = (group: AdsAdGroup, newAttributes: any) => {
  emit('update-attributes', { ...group, attributes: newAttributes })
}

const handleTabRemove = async (name: string) => {
  const group = props.groups.find(g => String(g.id || g.externalId) === name)
  if (!group || !group.id) return
  
  try {
    await ElMessageBox.confirm(`确定要归档广告组 "${group.name}" 吗？归档后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await AdsAdGroupApi.updateAdGroupStatus({ id: group.id, status: 'ARCHIVED' })
    ElMessage.success('广告组已归档')
    emit('refresh')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('归档失败')
    }
  }
}
</script>

<style scoped>
.inner-tabs :deep(.el-tabs__content) {
  padding: 15px 0 0 0;
}

:deep(.el-tabs__new-tab) {
  width: auto !important;
  padding: 0 12px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  border: 1px solid var(--el-border-color);
  background: #fff;
  height: 28px;
  margin-top: 4px;
}

:deep(.el-tabs__new-tab)::after {
  content: '添加广告组';
  margin-left: 4px;
  font-size: 12px;
  font-weight: normal;
  color: var(--el-text-color-regular);
}

:deep(.el-tabs__new-tab:hover) {
  color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

:deep(.el-tabs__new-tab:hover)::after {
  color: var(--el-color-primary);
}
</style>
