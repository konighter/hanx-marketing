<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="500">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
      v-loading="formLoading"
    >
      <el-form-item label="受邀人手机号" prop="inviteeMobile">
        <el-input v-model="formData.inviteeMobile" placeholder="请输入受邀人手机号" />
      </el-form-item>
      <el-form-item label="受邀人邮箱" prop="inviteeEmail">
        <el-input v-model="formData.inviteeEmail" placeholder="请输入受邀人邮箱" />
      </el-form-item>
      <el-form-item label="有效期" prop="expireTime">
        <el-date-picker
          v-model="formData.expireTime"
          type="datetime"
          value-format="x"
          placeholder="选择过期时间"
          class="!w-full"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">生 成</el-button>
    </template>

    <!-- 结果展示 -->
    <el-divider v-if="inviteResult.inviteCode" />
    <div v-if="inviteResult.inviteCode" class="p-10px bg-gray-100 rounded">
      <div class="mb-10px">
        <span class="font-bold">邀请码：</span>
        <el-tag type="success" size="large">{{ inviteResult.inviteCode }}</el-tag>
        <el-button link type="primary" class="ml-10px" @click="copy(inviteResult.inviteCode)">复制</el-button>
      </div>
      <div>
        <div class="font-bold mb-5px">邀请链接：</div>
        <el-input :model-value="inviteLink" readonly>
          <template #append>
            <el-button @click="copy(inviteLink)">复制链接</el-button>
          </template>
        </el-input>
      </div>
    </div>
  </Dialog>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import * as TenantInviteApi from '@/api/system/tenant/invite'
import { useClipboard } from '@vueuse/core'
import dayjs from 'dayjs'

defineOptions({ name: 'UserInviteForm' })

const { copy } = useClipboard()
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('邀请用户加入组织') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formData = ref({
  inviteeMobile: '',
  inviteeEmail: '',
  expireTime: dayjs().add(7, 'day').valueOf()
})
const formRules = reactive({
  inviteeMobile: [{ required: true, message: '受邀人手机号不能为空', trigger: 'blur' }],
  inviteeEmail: [
    { required: true, message: '受邀人邮箱不能为空', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  expireTime: [{ required: true, message: '有效期不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

const inviteResult = ref({
  inviteCode: ''
})

const inviteLink = computed(() => {
  if (!inviteResult.value.inviteCode) return ''
  const url = window.location.origin + '/login?inviteCode=' + inviteResult.value.inviteCode
  return url
})

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const submitForm = async () => {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = {
      inviteeMobile: formData.value.inviteeMobile,
      inviteeEmail: formData.value.inviteeEmail,
      expireTime: Number(formData.value.expireTime)
    }
    const res = await TenantInviteApi.createTenantInvite(data)
    inviteResult.value.inviteCode = res
    message.success('生成成功')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    inviteeMobile: '',
    inviteeEmail: '',
    expireTime: dayjs().add(7, 'day').valueOf()
  }
  inviteResult.value = {
    inviteCode: ''
  }
  formRef.value?.resetFields()
}
</script>
