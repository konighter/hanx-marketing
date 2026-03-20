<template>
  <Dialog :title="'部署流程'" v-model="dialogVisible" width="500px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="流程文件" prop="file">
        <el-upload
          ref="uploadRef"
          :limit="1"
          :auto-upload="false"
          drag
          action=""
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          accept=".xml,.bpmn,.bpmn20.xml"
        >
          <Icon icon="ep:upload-filled" class="el-icon--upload" />
          <div class="el-upload__text">
            将文件拖到此处，或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">只能上传 xml/bpmn 文件</div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :disabled="formLoading" @click="submitForm">确 定</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { WorkflowApi } from '@/app/system/api/workflow'
import { useMessage } from '@/hooks/web/useMessage'
import type { UploadFile, UploadInstance } from 'element-plus'

defineOptions({ name: 'WorkflowDeployForm' })

const message = useMessage()
const dialogVisible = ref(false)
const formLoading = ref(false)
const formRef = ref()
const uploadRef = ref<UploadInstance>()

const formData = reactive({
  file: null as any
})

const formRules = {
  file: [{ required: true, message: '请选择流程文件', trigger: 'change' }]
}

const open = () => {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open })

const emit = defineEmits(['success'])

const handleFileChange = (file: UploadFile) => {
  formData.file = file.raw
}

const handleFileRemove = () => {
  formData.file = null
}

const submitForm = async () => {
  if (!formData.file) {
    message.error('请选择需要部署的流程文件')
    return
  }
  
  // 校验表单
  await formRef.value.validate()
  
  formLoading.value = true
  try {
    const data = new FormData()
    data.append('file', formData.file)
    
    await WorkflowApi.deploy(data)
    message.success('部署成功')
    dialogVisible.value = false
    emit('success')
  } catch (error) {
    // 部署失败处理由 axios 拦截器处理
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.file = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>
