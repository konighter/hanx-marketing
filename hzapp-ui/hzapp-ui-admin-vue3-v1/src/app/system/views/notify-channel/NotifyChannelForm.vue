<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="680px">
    <el-form ref="formRef" v-loading="formLoading" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="渠道名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入渠道名称，如：飞书-运营群" />
      </el-form-item>
      <el-form-item label="渠道类型" prop="channelType">
        <el-select v-model="formData.channelType" placeholder="请选择渠道类型" :disabled="formType === 'update'"
          @change="handleChannelTypeChange" class="w-full">
          <el-option v-for="item in ChannelTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="Webhook 地址" prop="webhookUrl">
        <el-input v-model="formData.webhookUrl" type="textarea" :rows="2"
          placeholder="请输入 Webhook 地址" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)" :key="dict.value"
            :value="dict.value">{{ dict.label }}</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 渠道个性化配置 -->
      <el-divider content-position="left">渠道配置</el-divider>
      <FeishuConfig v-if="formData.channelType === 1" :modelValue="channelConfig" @update:modelValue="onConfigUpdate" />
      <DingtalkConfig v-else-if="formData.channelType === 2" :modelValue="channelConfig" @update:modelValue="onConfigUpdate" />
      <WecomConfig v-else-if="formData.channelType === 3" :modelValue="channelConfig" @update:modelValue="onConfigUpdate" />
      <el-empty v-else description="请先选择渠道类型" :image-size="60" />
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitForm" :loading="formLoading">确 定</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { NotifyChannelApi, NotifyChannelVO, ChannelTypeOptions } from '@/app/system/api/notifyChannel'
import FeishuConfig from './config/FeishuConfig.vue'
import DingtalkConfig from './config/DingtalkConfig.vue'
import WecomConfig from './config/WecomConfig.vue'

defineOptions({ name: 'NotifyChannelForm' })

const emit = defineEmits(['success'])
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formRef = ref()

const formData = ref<Partial<NotifyChannelVO>>({
  id: undefined,
  name: '',
  channelType: undefined,
  webhookUrl: '',
  config: '',
  status: 0
})

const channelConfig = ref<Record<string, any>>({})

const formRules = reactive({
  name: [{ required: true, message: '请输入渠道名称', trigger: 'blur' }],
  channelType: [{ required: true, message: '请选择渠道类型', trigger: 'change' }],
  webhookUrl: [{ required: true, message: '请输入 Webhook 地址', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增通知渠道' : '编辑通知渠道'
  formType.value = type
  resetForm()

  if (id) {
    formLoading.value = true
    try {
      const data = await NotifyChannelApi.get(id)
      formData.value = data
      // 解析 config JSON
      if (data.config) {
        try {
          channelConfig.value = JSON.parse(data.config)
        } catch {
          channelConfig.value = {}
        }
      }
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

/** 渠道类型切换 */
const handleChannelTypeChange = () => {
  channelConfig.value = {}
}

/** 接收子组件配置更新 (用 Object.assign 原地更新，避免 ElForm 检测到新对象触发循环) */
const onConfigUpdate = (val: Record<string, any>) => {
  Object.keys(channelConfig.value).forEach(k => delete channelConfig.value[k])
  Object.assign(channelConfig.value, val)
}

/** 提交 */
const submitForm = async () => {
  await formRef.value?.validate()
  formLoading.value = true
  try {
    // 将 channelConfig 序列化到 config 字段
    const data = {
      ...formData.value,
      config: Object.keys(channelConfig.value).length > 0 ? JSON.stringify(channelConfig.value) : ''
    } as NotifyChannelVO

    if (formType.value === 'create') {
      await NotifyChannelApi.create(data)
      message.success('创建成功')
    } else {
      await NotifyChannelApi.update(data)
      message.success('修改成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: '',
    channelType: undefined,
    webhookUrl: '',
    config: '',
    status: 0
  }
  channelConfig.value = {}
  formRef.value?.resetFields()
}
</script>
