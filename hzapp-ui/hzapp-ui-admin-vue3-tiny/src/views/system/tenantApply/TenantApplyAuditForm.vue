<template>
  <Dialog v-model="dialogVisible" title="审批" width="500px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item label="审批状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :value="1">通过</el-radio>
          <el-radio :value="2">拒绝</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="租户套餐" prop="packageId" v-if="formData.status === 1">
        <el-select v-model="formData.packageId" placeholder="请选择租户套餐" class="w-full">
          <el-option
            v-for="item in packageList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审批备注" prop="auditRemark">
        <el-input v-model="formData.auditRemark" type="textarea" placeholder="请输入审批备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as TenantApplyApi from '@/api/system/tenantApply'
import * as TenantPackageApi from '@/api/system/tenantPackage'

defineOptions({ name: 'SystemTenantApplyAuditForm' })

const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref({
  id: undefined,
  status: 1,
  auditRemark: undefined,
  packageId: undefined
})
const formRules = reactive({
  status: [{ required: true, message: '审批状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref
const packageList = ref<any[]>([]) // 套餐列表

onMounted(async () => {
  packageList.value = await TenantPackageApi.getTenantPackageList()
})

/** 打开弹窗 */
const open = async (id: number, packageId?: number) => {
  dialogVisible.value = true
  resetForm()
  formData.value.id = id
  formData.value.packageId = packageId
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as TenantApplyApi.TenantApplyAuditReqVO
    await TenantApplyApi.auditTenantApply(data)
    message.success('审批成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    status: 1,
    auditRemark: undefined
  }
  formRef.value?.resetFields()
}
</script>
