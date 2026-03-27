<template>
  <div class="callback-container">
    <div class="glass-card">
      <div v-if="loading" class="loading-state">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>正在处理授权结果...</p>
      </div>
      <div v-else-if="status === 'success'" class="result-state success">
        <el-icon class="result-icon"><Check /></el-icon>
        <h2>授权成功</h2>
        <p>您的平台账号已成功绑定，正在自动关闭窗口...</p>
      </div>
      <div v-else class="result-state error">
        <el-icon class="result-icon"><Close /></el-icon>
        <h2>授权失败</h2>
        <p>{{ message || '未知错误，请重试' }}</p>
        <el-button type="primary" class="mt-4" @click="handleClose">关闭窗口</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Check, Close, Loading } from '@element-plus/icons-vue'

const route = useRoute()
const loading = ref(true)
const status = ref('')
const message = ref('')

const handleClose = () => {
  window.close()
}

onMounted(() => {
  // 从 URL 参数中获取状态信息
  // 后端重定向时应携带 status 和 message
  status.value = (route.query.status as string) || 'success'
  message.value = (route.query.message as string) || ''
  
  loading.value = false
  
  if (status.value === 'success') {
    // 通知父窗口刷新列表
    if (window.opener) {
      window.opener.postMessage({ type: 'PLATFORM_AUTH_SUCCESS' }, '*')
    }
    
    // 3秒后自动关闭
    setTimeout(() => {
      handleClose()
    }, 3000)
  }
})
</script>

<style scoped>
.callback-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.glass-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
  text-align: center;
}

.result-state {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.result-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.success .result-icon {
  color: #788c5d; /* Sage Green */
}

.error .result-icon {
  color: #d97757; /* Orangey-Red */
}

h2 {
  font-family: 'Poppins', sans-serif;
  margin-bottom: 12px;
  color: #333;
}

p {
  font-family: 'Lora', serif;
  color: #666;
  line-height: 1.6;
}

.mt-4 {
  margin-top: 16px;
}
</style>
