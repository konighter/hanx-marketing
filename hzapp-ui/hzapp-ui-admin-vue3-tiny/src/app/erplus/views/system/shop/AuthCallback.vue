<template>
  <div class="auth-callback-container h-full flex items-center justify-center">
    <el-card class="w-[400px] text-center" shadow="hover">
      <div v-if="loading" class="py-10">
        <el-icon class="is-loading text-4xl text-primary mb-4"><Loading /></el-icon>
        <div class="text-gray-500">正在处理授权回调，请稍候...</div>
      </div>
      <div v-else-if="success" class="py-8">
        <el-icon class="text-5xl text-success mb-4"><CircleCheckFilled /></el-icon>
        <div class="text-xl font-bold mb-2">授权成功</div>
        <div class="text-gray-500 mb-6">您已成功完成平台授权，可以关闭此窗口并刷新页面。</div>
        <el-button type="primary" @click="closeWindow">关闭窗口</el-button>
      </div>
      <div v-else class="py-8">
        <el-icon class="text-5xl text-danger mb-4"><CircleCloseFilled /></el-icon>
        <div class="text-xl font-bold mb-2">授权失败</div>
        <div class="text-gray-500 mb-6">{{ errorMessage || '处理授权回调时发生错误' }}</div>
        <el-button @click="closeWindow">关闭窗口</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { PlatformAuthApi } from '@/app/erplus/api/authorization'
import { Loading, CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const success = ref(false)
const errorMessage = ref('')

onMounted(async () => {
  const authType = route.params.authType as string
  const code = route.query.code as string
  const state = route.query.state as string

  if (!authType || !code || !state) {
    loading.value = false
    errorMessage.value = '回调参数缺失或不完整，请检查访问链接'
    return
  }

  try {
    loading.value = true
    await PlatformAuthApi.oauthCallback(authType, code, state)
    success.value = true
    ElMessage.success('授权回调处理成功')
  } catch (error: any) {
    success.value = false
    errorMessage.value = error.message || '网络请求或后端处理失败'
    ElMessage.error(errorMessage.value)
  } finally {
    loading.value = false
  }
})

const closeWindow = () => {
  // Try to close window if opened via popup or blank target
  if (window.opener && !window.opener.closed) {
    window.close()
  } else {
    // If we're unable to close the window (e.g., opened in same tab), just route to shop list
    router.replace('/system/shop')
  }
}
</script>

<style scoped>
.auth-callback-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}
.text-success {
  color: #67c23a;
}
.text-danger {
  color: #f56c6c;
}
.text-primary {
  color: #409eff;
}
</style>
