<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="套餐ID" prop="tenantPackageId">
        <el-select v-model="formData.tenantPackageId" placeholder="请选择套餐ID">
          <el-option v-for="( item ) in tenantPackages"
            :key="item.id"
            :label="item.name"
            :value="item.id" />
        </el-select>

      </el-form-item>
      <el-form-item label="过期时间" prop="expireTime">
        <el-date-picker
          v-model="formData.expireTime"
          type="date"
          value-format="x"
          placeholder="选择过期时间"
        />
      </el-form-item>
      <el-form-item label="订购人数" prop="accountCount">
        <el-input-number
          v-model="formData.accountCount"
          :min="0"
          controls-position="right"
          placeholder="请输入订购人数"
        />
      </el-form-item>
      <el-form-item label="订购状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>

      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import * as  TenantApi from '@/api/system/tenant'
import * as TenantPackageApi from "@/api/system/tenantPackage";
import {DICT_TYPE, getIntDictOptions} from "@/utils/dict";

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  tenantPackageId: undefined,
  expireTime: undefined,
  accountCount: undefined,
  status: undefined
})
const formRules = reactive({
  tenantPackageId: [{ required: true, message: '套餐ID不能为空', trigger: 'blur' }],
  expireTime: [{ required: true, message: '过期时间不能为空', trigger: 'blur' }],
  accountCount: [{ required: true, message: '订购人数不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '订购状态不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

const tenantPackages = ref([] as TenantPackageApi.TenantPackageVO[]) // 租户套餐


/** 打开弹窗 */
const open = async (type: string, id?: number, tenantId: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  formData.value.tenantId = tenantId
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await TenantApi.getTenantPackageSubscription(id)
    } finally {
      formLoading.value = false
    }
  }

  // 加载套餐列表
  tenantPackages.value = await TenantPackageApi.getTenantPackageList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value
    if (formType.value === 'create') {
      await TenantApi.createTenantPackageSubscription(data)
      message.success(t('common.createSuccess'))
    } else {
      await TenantApi.updateTenantPackageSubscription(data)
      message.success(t('common.updateSuccess'))
    }
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
    tenantPackageId: undefined,
    expireTime: undefined,
    accountCount: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}
</script>
