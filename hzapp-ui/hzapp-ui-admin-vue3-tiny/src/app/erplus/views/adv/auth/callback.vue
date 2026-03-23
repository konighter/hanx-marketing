<template>
  <div v-loading="loading" element-loading-text="正在处理授权回调..." class="callback-container">
    <div v-if="success" class="result-message">
      <el-result icon="success" title="授权成功" sub-title="正在为您跳转回账号管理页面...">
        <template #extra>
          <el-button type="primary" @click="handleBack">立即返回</el-button>
        </template>
      </el-result>
    </div>
    <div v-if="error" class="result-message">
      <el-result icon="error" title="授权失败" :sub-title="errorMsg">
        <template #extra>
          <el-button type="primary" @click="handleBack">返回重试</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { AdsAuthApi } from '@/app/erplus/api/adv/ads'

defineOptions({ name: 'AdsAuthCallback' })

const route = useRoute()
const router = useRouter()
const message = useMessage()

const loading = ref(true)
const success = ref(false)
const error = ref(false)
const errorMsg = ref('')

/** 处理回调逻辑 */
const handleCallback = async () => {
  const { code, state, platform } = route.query
  
  if (!code) {
    error.value = true
    errorMsg.value = '未获取到授权码 (code)'
    loading.value = false
    return
  }

  // 如果 URL 中没传 platform，后端会从 Redis 中的 state 元数据获取
  const finalPlatform = platform as string
  const finalState = state as string

  if (!finalState) {
    error.value = true
    errorMsg.value = '未获取到状态值 (state)'
    loading.value = false
    return
  }

  try {
    await AdsAuthApi.handleCallback({
      platform: finalPlatform,
      code: code as string,
      state: state as string
    })
    success.value = true
    loading.value = false
    // 3秒后自动返回
    setTimeout(() => {
      handleBack()
    }, 3000)
  } catch (err: any) {
    error.value = true
    errorMsg.value = err.message || '系统处理异常'
    loading.value = false
  }
}

const handleBack = () => {
  router.push('/erplusv2/sys/adv-auth')
}

onMounted(() => {
  handleCallback()
})
</script>

<style scoped>
.callback-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}
.result-message {
  width: 100%;
}
</style>
