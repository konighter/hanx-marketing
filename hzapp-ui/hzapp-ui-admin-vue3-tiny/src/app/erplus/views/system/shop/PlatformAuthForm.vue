<template>
  <el-dialog
    v-model="dialogVisible"
    title="新增平台授权"
    width="600px"
    class="auth-dialog"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="right"
      class="auth-form"
    >
      <el-form-item label="平台" prop="platform">
        <el-select v-model="formData.platform" placeholder="请选择平台" class="w-full">
          <el-option label="Amazon" value="AMAZON" />
          <el-option label="TikTok" value="TIKTOK" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="授权范围" prop="authScope">
        <el-select v-model="formData.authScope" placeholder="请选择授权范围" class="w-full">
          <template v-if="formData.platform === 'AMAZON'">
            <el-option label="Seller Partner (SP-API)" value="AMAZON_SP" />
            <el-option label="Advertising (ADV)" value="AMAZON_ADV" />
          </template>
          <template v-else-if="formData.platform === 'TIKTOK'">
            <el-option label="Shop" value="TTS_SHOP" />
          </template>
        </el-select>
      </el-form-item>

      <el-form-item label="区域" prop="region">
        <el-select v-model="formData.region" placeholder="请选择区域" class="w-full">
          <el-option label="北美 (NA)" value="NA" />
          <el-option label="欧洲 (EU)" value="EU" />
          <el-option label="远东 (FE)" value="FE" />
          <el-option label="全球 (GLOBAL)" value="GLOBAL" />
        </el-select>
      </el-form-item>

      <el-form-item label="授权模式" prop="selfAuth">
        <el-radio-group v-model="formData.selfAuth">
          <el-radio :label="false">平台 OAuth 授权</el-radio>
          <el-radio :label="true">开发者自授权</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="平台应用" prop="appId">
        <div class="flex items-center w-full">
          <el-select
            v-model="formData.appId"
            placeholder="请选择或新增平台应用"
            class="flex-1"
            clearable
          >
            <el-option
              v-for="item in appList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-button type="primary" class="ml-2" @click="handleOpenAppForm">新增</el-button>
        </div>
      </el-form-item>

      <template v-if="formData.selfAuth">
        <el-form-item label="刷新Token" prop="refreshToken">
          <el-input
            v-model="formData.refreshToken"
            type="textarea"
            :rows="3"
            placeholder="请输入 刷新Token"
          />
        </el-form-item>
        <el-form-item label="卖家ID" prop="sellerId">
          <el-input v-model="formData.sellerId" placeholder="请输入 Seller ID" />
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ formData.selfAuth ? '直接提交' : '前往授权' }}
        </el-button>
      </div>
    </template>
    
    <!-- 平台应用表单 -->
    <PlatformAppForm ref="appFormRef" @success="handleAppSuccess" />
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { PlatformAuthApi, PlatformAuthSubmitReqVO, PlatformAppApi, PlatformAppVO } from '@/app/erplus/api/authorization'
import { ElMessage } from 'element-plus'
import PlatformAppForm from './PlatformAppForm.vue'

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const appFormRef = ref()

const formData = reactive({
  platform: 'AMAZON',
  authScope: 'AMAZON_SP',
  region: 'NA',
  selfAuth: false,
  refreshToken: '',
  appId: undefined as number | undefined,
  sellerId: ''
})

const appList = ref<PlatformAppVO[]>([])

/** 加载应用列表 */
const loadAppList = async () => {
  try {
    appList.value = await PlatformAppApi.getPlatformAppList({ platform: formData.platform })
  } catch (error) {
    console.error('Failed to load platform apps:', error)
  }
}

/** 监听平台变化，刷新应用列表 */
watch(() => formData.platform, () => {
  formData.appId = undefined
  loadAppList()
})

/** 打开新增应用弹窗 */
const handleOpenAppForm = () => {
  appFormRef.value.open(formData.platform)
}

/** 新增应用成功 */
const handleAppSuccess = async (newId: number) => {
  await loadAppList()
  formData.appId = newId
}

const formRules = computed(() => {
  const rules: any = {
    platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
    authScope: [{ required: true, message: '请选择授权范围', trigger: 'change' }],
    region: [{ required: true, message: '请选择区域', trigger: 'change' }],
    appId: [{ required: true, message: '请选择平台应用', trigger: 'change' }]
  }

  if (formData.selfAuth) {
    rules.sellerId = [{ required: true, message: '请输入 Seller ID', trigger: 'blur' }]
    rules.refreshToken = [{ required: true, message: '请输入 Refresh Token', trigger: 'blur' }]
  }

  return rules
})

/** 打开弹窗 */
const open = () => {
  dialogVisible.value = true
  resetForm()
  loadAppList()
}

/** 重置表单 */
const resetForm = () => {
  formData.platform = 'AMAZON'
  formData.authScope = 'AMAZON_SP'
  formData.region = 'NA'
  formData.selfAuth = false
  formData.refreshToken = ''
  formData.appId = undefined
  formData.sellerId = ''
}


/** 提交处理 */
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  submitting.value = true
  try {
    if (formData.selfAuth) {
      // 自授权：直接提交到后端进行 Token 换取和保存
      await PlatformAuthApi.submitAuth(formData as PlatformAuthSubmitReqVO)
      ElMessage.success('自授权信息提交成功')
      dialogVisible.value = false
      emit('success')
    } else {
      // 平台授权：生成并跳转到 OAuth 页面
      if (!formData.appId) {
        ElMessage.warning('请选择平台应用')
        return
      }
      const url = await PlatformAuthApi.generateAuthUrl(formData as PlatformAuthGenerateReqVO)
      if (url) {
        window.open(url, '_blank')
        ElMessage.info('正在跳转至平台授权页面...')
        dialogVisible.value = false
      }
    }
  } catch (error) {
    console.error('Authorization failed:', error)
  } finally {
    submitting.value = false
  }
}

/** 监听回调页面的消息 */
const handleMessage = (event: MessageEvent) => {
  if (event.data?.type === 'PLATFORM_AUTH_SUCCESS') {
    ElMessage.success('平台授权成功')
    emit('success')
    // 授权成功后，如果窗口还在也可以刷新
    // 实际上 AuthCallback 会自动关闭
  }
}

onMounted(() => {
  window.addEventListener('message', handleMessage)
})

onUnmounted(() => {
  window.removeEventListener('message', handleMessage)
})

const emit = defineEmits(['success'])
defineExpose({ open })
</script>

<style scoped>
.auth-dialog :deep(.el-dialog) {
  /* Glassmorphism style */
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
}

.auth-form {
  padding-top: 10px;
}

.w-full {
  width: 100%;
}

.ml-2 {
  margin-left: 8px;
}
</style>
