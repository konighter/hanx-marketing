<template>
  <el-dialog 
    :model-value="modelValue" 
    title="创建广告组" 
    width="900px" 
    append-to-body 
    destroy-on-close
    @update:model-value="val => $emit('update:modelValue', val)"
  >
    <el-form :model="form" label-width="100px" size="small">
      <el-form-item label="名称" required>
        <el-input v-model="form.name" placeholder="请输入广告组名称" class="w-400px" />
      </el-form-item>
      <el-form-item label="默认竞价" required>
        <el-input-number v-model="form.defaultBid" :precision="2" :step="0.01" :min="0.01" />
      </el-form-item>
      
      <el-divider content-position="left">定向策略</el-divider>
      <div class="p-10px border-1 border-gray-100 border-solid rounded-4px mb-20px">
        <AdGroupTargetingManager 
          :config="form.attributes" 
          :default-bid="form.defaultBid"
          @update="val => Object.assign(form.attributes, val)"
        />
      </div>
      
      <el-divider content-position="left">否定投放</el-divider>
      <div class="p-10px border-1 border-gray-100 border-solid rounded-4px">
        <AdGroupNegativeManager 
          :config="form.attributes"
          @update="val => Object.assign(form.attributes, val)"
        />
      </div>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import AdGroupTargetingManager from './AdGroupTargetingManager.vue'
import AdGroupNegativeManager from './AdGroupNegativeManager.vue'

const props = defineProps<{
  modelValue: boolean
  campaignId: number
}>()

const emit = defineEmits(['update:modelValue', 'success'])

const loading = ref(false)
const form = ref({
  name: '',
  defaultBid: 0.25,
  attributes: {
    amz_targeting_type: 'KEYWORD',
    amz_keyword: [],
    amz_target_clause: [],
    amz_negative_keyword: [],
    amz_negative_target_clause: []
  }
})

// 监听打开状态，重置表单
watch(() => props.modelValue, (val) => {
  if (val) {
    form.value = {
      name: '',
      defaultBid: 0.25,
      attributes: {
        amz_targeting_type: 'KEYWORD',
        amz_keyword: [],
        amz_target_clause: [],
        amz_negative_keyword: [],
        amz_negative_target_clause: []
      }
    }
  }
})

const handleConfirm = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入广告组名称')
    return
  }
  
  loading.value = true
  try {
    const data = {
      campaignId: props.campaignId,
      name: form.value.name,
      defaultBid: form.value.defaultBid,
      status: 'ENABLED',
      attributes: form.value.attributes
    }
    await AdsAdGroupApi.createAdGroup(data)
    ElMessage.success('广告组创建成功')
    emit('success')
    emit('update:modelValue', false)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>
