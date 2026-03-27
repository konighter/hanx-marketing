<template>
  <el-dialog
    v-model="dialogVisible"
    :title="id ? '编辑平台应用' : '新增平台应用'"
    width="500px"
    append-to-body
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      label-position="right"
    >
      <el-form-item label="应用名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入应用名称" />
      </el-form-item>
      <el-form-item label="平台" prop="platform">
        <el-select v-model="formData.platform" placeholder="请选择平台" class="w-full" disabled>
          <el-option label="Amazon" value="AMAZON" />
          <el-option label="TikTok" value="TIKTOK" />
        </el-select>
      </el-form-item>
      <el-form-item label="应用Key" prop="appKey">
        <el-input v-model="formData.appKey" placeholder="请输入 App Key / Client ID" />
      </el-form-item>
      <el-form-item label="应用Secret" prop="appSecret">
        <el-input v-model="formData.appSecret" type="password" show-password placeholder="请输入 Client Secret" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { PlatformAppApi, PlatformAppVO, PlatformAppSaveReqVO } from '@/app/erplus/api/authorization'
import { ElMessage } from 'element-plus'

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const id = ref<number>()

const formData = reactive<PlatformAppSaveReqVO>({
  name: '',
  platform: 'AMAZON',
  appKey: '',
  appSecret: ''
})

const formRules = {
  name: [{ required: true, message: '请输入应用名称', trigger: 'blur' }],
  platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
  appKey: [{ required: true, message: '请输入 App Key', trigger: 'blur' }],
  appSecret: [{ required: true, message: '请输入 App Secret', trigger: 'blur' }]
}

const open = (platform: string, item?: PlatformAppVO) => {
  dialogVisible.value = true
  id.value = item?.id
  formData.platform = platform
  // Reset form
  formData.name = ''
  formData.appKey = ''
  formData.appSecret = ''
  
  if (item) {
    formData.name = item.name
    // Note: appKey and appSecret are not returned by the list API for security
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  submitting.value = true
  try {
    if (id.value) {
      await PlatformAppApi.updatePlatformApp({ ...formData, id: id.value })
      ElMessage.success('修改成功')
    } else {
      const newId = await PlatformAppApi.createPlatformApp(formData)
      ElMessage.success('新增成功')
      emit('success', newId)
    }
    dialogVisible.value = false
  } catch (error) {
    console.error('Failed to save platform app:', error)
  } finally {
    submitting.value = false
  }
}

const emit = defineEmits(['success'])
defineExpose({ open })
</script>

<style scoped>
.w-full {
  width: 100%;
}
</style>
