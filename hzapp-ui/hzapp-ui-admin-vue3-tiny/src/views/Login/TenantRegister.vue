<template>
  <div class="relative h-[100vh] w-full bg-gray-50 flex items-center justify-center p-4">
    <div class="w-full max-w-2xl bg-white rounded-2xl shadow-xl overflow-hidden">
      <div class="bg-blue-600 p-6 text-white flex items-center justify-between">
        <div class="flex items-center">
          <el-button link @click="goBack" class="!text-white mr-4">
            <Icon icon="ep:arrow-left" :size="20" />
          </el-button>
          <h2 class="text-xl font-bold">创建新组织</h2>
        </div>
        <Icon icon="ep:office-building" :size="24" />
      </div>

      <div class="p-8">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-position="top"
          v-loading="loading"
          class="max-w-md mx-auto"
        >
          <el-form-item label="企业名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入企业名称" size="large">
              <template #prefix>
                <Icon icon="ep:office-building" class="text-gray-400" />
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="联系人姓名" prop="contactName">
            <el-input v-model="formData.contactName" placeholder="请输入联系人姓名" size="large">
              <template #prefix>
                <Icon icon="ep:user" class="text-gray-400" />
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="联系电话" prop="contactMobile">
            <el-input v-model="formData.contactMobile" placeholder="请输入联系电话" size="large">
              <template #prefix>
                <Icon icon="ep:iphone" class="text-gray-400" />
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="申请备注" prop="remark">
            <el-input
              v-model="formData.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入申请备注，例如公司简介、申请目的等..."
            />
          </el-form-item>

          <div class="mt-8 flex flex-col space-y-4">
            <el-button
              type="primary"
              size="large"
              class="w-full h-12 text-lg font-bold"
              @click="submitForm"
              :loading="submitting"
            >
              提交申请
            </el-button>
            <p class="text-center text-xs text-gray-400">
              提交后请耐心等待管理员审批。审批通过后，您即可在“选择已有组织”中看到该组织。
            </p>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as TenantApplyApi from '@/api/system/tenantApply'

defineOptions({ name: 'TenantRegister' })

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const formRef = ref()

const formData = reactive({
  name: '',
  contactName: '',
  contactMobile: '',
  remark: ''
})

const formRules = reactive({
  name: [{ required: true, message: '企业名称不能为空', trigger: 'blur' }],
  contactName: [{ required: true, message: '联系人姓名不能为空', trigger: 'blur' }],
  contactMobile: [
    { required: true, message: '联系电话不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

const goBack = () => {
  router.push('/tenant/select')
}

const submitForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    await TenantApplyApi.createTenantApply(formData)
    ElMessage.success('提交申请成功，请等待管理员审批')
    router.push('/tenant/select')
  } catch (error) {
    console.error('提交申请失败', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #4b5563;
}
</style>
