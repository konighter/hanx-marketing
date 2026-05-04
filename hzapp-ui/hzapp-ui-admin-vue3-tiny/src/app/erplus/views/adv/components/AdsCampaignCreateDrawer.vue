<template>
  <el-drawer
    v-model="visible"
    title="新建广告活动"
    size="800px"
    destroy-on-close
  >
    <div v-loading="loading">
      <!-- 根据平台加载不同的创建组件 -->
      <component
        v-if="platform === 'amazon'"
        :is="AmazonCampaignCreateManager"
        :shop-id="shopId"
        @success="handleSuccess"
        @cancel="visible = false"
      />
      <div v-else-if="platform && platform !== 'amazon'" class="p-20px text-center">
        暂不支持该平台的广告活动创建
      </div>
      <div v-else-if="!loading" class="p-20px text-center text-gray-400">
        请选择有效的店铺
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, defineExpose } from 'vue'
import { ShopApi } from '@/app/erplus/api/system/shop'
import AmazonCampaignCreateManager from './amazon/AmazonCampaignCreateManager.vue'

const props = defineProps<{
  shopId?: number
}>()

const emit = defineEmits(['success'])

const visible = ref(false)
const loading = ref(false)
const platform = ref('')

const open = async (id?: number) => {
  const targetShopId = id || props.shopId
  if (!targetShopId) return
  
  visible.value = true
  loading.value = true
  platform.value = '' // Reset
  
  try {
    const shop = await ShopApi.getShop(targetShopId)
    const pName = shop.platformName || ''
    const sName = shop.name || ''
    const pCode = (shop.platformCode || '').toLowerCase()
    
    // 综合判断：platformCode, platformName, 或者 shopName 中包含关键词
    if (pCode === 'amazon' || 
        pName.toLowerCase().includes('amazon') || pName.includes('亚马逊') ||
        sName.toLowerCase().includes('amazon') || sName.includes('亚马逊')) {
      platform.value = 'amazon'
    } else if (pCode === 'meta' || pName.toLowerCase().includes('meta') || sName.toLowerCase().includes('meta')) {
      platform.value = 'meta'
    } else if (pCode === 'google' || pName.toLowerCase().includes('google') || sName.toLowerCase().includes('google')) {
      platform.value = 'google'
    } else {
      platform.value = pCode || pName.toLowerCase() || 'unknown'
    }
  } catch (e) {
    console.error('Failed to get shop info', e)
    platform.value = 'error'
  } finally {
    loading.value = false
  }
}

const handleSuccess = () => {
  visible.value = false
  emit('success')
}

defineExpose({ open })
</script>
