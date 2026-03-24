<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"

    >
      <el-form-item label="名称" prop="name" >
        <el-input v-model="formData.name" placeholder="请输入名称" class="!w-240px"/>
      </el-form-item>
      <el-form-item label="平台" prop="platformId">
        <el-select v-model="formData.platformId" placeholder="请选择平台" class="!w-240px">
          <el-option
v-for="platform in sellplatformList" 
          :key="platform.id"
          :label="platform.name" 
          :value="platform.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="AppId" prop="appId">
        <el-input v-model="formData.appId" placeholder="请输入应用Id" />
      </el-form-item>
      <el-form-item label="应用Key" prop="appKey">
        <el-input v-model="formData.appKey" placeholder="请输入应用Key" />
      </el-form-item>
      <el-form-item label="应用密钥" prop="appSecret">
        <el-input type="password" show-password v-model="formData.appSecret" placeholder="请输入应用密钥" />
      </el-form-item>
            <el-form-item label="状态" prop="status">
        <el-select
          v-model="formData.status"
          class="!w-240px"
          clearable
          placeholder="请选择状态"
          
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { AppApi, AppVO } from '@/app/erp/api/app'
import {SellPlatformApi, SellPlatformVO} from '@/app/erp/api/sellplatform'

/** 应用注册信息 表单 */
defineOptions({ name: 'AppForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  name: undefined,
  platformId: undefined,
  appId: undefined,
  appKey: undefined,
  appSecret: undefined,
  status: undefined
})
const formRules = reactive({
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
  platformId: [{ required: true, message: '平台ID不能为空', trigger: 'change' }],
  appKey: [{ required: true, message: '应用Key不能为空', trigger: 'blur' }],
  appSecret: [{ required: true, message: '应用密钥不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await AppApi.getApp(id)
    } finally {
      formLoading.value = false
    }
  }
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
    const data = formData.value as unknown as AppVO
    if (formType.value === 'create') {
      await AppApi.createApp(data)
      message.success(t('common.createSuccess'))
    } else {
      await AppApi.updateApp(data)
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
    name: undefined,
    platformId: undefined,
    appId: undefined,
    appKey: undefined,
    appSecret: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}

const sellplatformList = ref<SellPlatformVO[]>([]) // 平台列表

const loadPlatformList = async () => {
  sellplatformList.value = await SellPlatformApi.getSellPlatformListCache() || []
}

onMounted(() => {
  loadPlatformList()
})


</script>