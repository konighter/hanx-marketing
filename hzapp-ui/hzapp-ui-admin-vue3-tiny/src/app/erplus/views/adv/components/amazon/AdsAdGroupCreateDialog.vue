<template>
  <el-dialog 
    :model-value="modelValue" 
    title="创建广告组" 
    fullscreen
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
      
      <el-divider content-position="left">商品广告</el-divider>
      <div class="p-10px border-1 border-gray-100 border-solid rounded-4px mb-20px">
        <AdGroupAdCreateManager v-model="form.ads" />
      </div>

      <el-divider content-position="left">定向策略</el-divider>
      <div class="p-10px border-1 border-gray-100 border-solid rounded-4px mb-20px">
        <AdGroupTargetingCreateManager 
          v-model:config="form.attributes" 
          :default-bid="form.defaultBid"
          :targeting-type="targetingType"
          :ad-asins="adAsins"
        />
      </div>
      
      <el-divider content-position="left">否定投放</el-divider>
      <div class="p-10px border-1 border-gray-100 border-solid rounded-4px">
        <AdGroupNegativeCreateManager 
          v-model:config="form.attributes"
        />
      </div>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="$emit('update:modelValue', false)">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleConfirm">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed, provide } from 'vue'
import { ElMessage } from 'element-plus'
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import AdGroupAdCreateManager from './AdGroupAdCreateManager.vue'
import AdGroupTargetingCreateManager from './AdGroupTargetingCreateManager.vue'
import AdGroupNegativeCreateManager from './AdGroupNegativeCreateManager.vue'

const props = defineProps<{
  modelValue: boolean
  campaignId: number
  shopId: number
  targetingType?: string
}>()

const emit = defineEmits(['update:modelValue', 'success'])

// 提供 shopId 给子组件
provide('shopId', computed(() => props.shopId))

const loading = ref(false)
const form = ref({
  name: '',
  defaultBid: 0.25,
  ads: [],
  attributes: {
    amz_targeting_type: props.targetingType === 'AUTO' ? 'AUTO' : 'KEYWORD',
    amz_keyword: [],
    amz_target_clause: [],
    amz_negative_keyword: [],
    amz_negative_target_clause: []
  }
})

// 计算当前已选商品的 ASIN，用于获取推荐
const adAsins = computed(() => {
  return form.value.ads.map((ad: any) => ad.asin).filter((asin: any) => !!asin)
})

// 监听打开状态，重置表单
watch(() => props.modelValue, (val) => {
  if (val) {
    form.value = {
      name: '',
      defaultBid: 0.25,
      ads: [],
      attributes: {
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
  
  if (form.value.ads.length === 0) {
    ElMessage.warning('请添加至少一个商品广告')
    return
  }

  const attrs = form.value.attributes
  const hasKeywords = attrs.amz_targeting_type === 'KEYWORD' && attrs.amz_keyword?.length > 0
  const hasProducts = attrs.amz_targeting_type === 'PRODUCT' && attrs.amz_target_clause?.length > 0
  
  if (!hasKeywords && !hasProducts) {
    ElMessage.warning('请添加至少一个定向策略（关键词或商品投放）')
    return
  }

  loading.value = true
  try {
    const data = {
      shopId: props.shopId,
      campaignId: props.campaignId,
      name: form.value.name,
      defaultBid: form.value.defaultBid,
      status: 'ENABLED',
      ads: form.value.ads,
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

