<template>
  <el-form
    v-show="getShow"
    ref="formLogin"
    :model="registerData.registerForm"
    :rules="registerRules"
    class="login-form"
    label-position="top"
    label-width="120px"
    size="large"
  >
    <el-row class="mx-[-10px]">
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <LoginFormTitle class="w-full" />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="username">
          <el-input
            v-model="registerData.registerForm.username"
            :placeholder="t('login.username')"
            size="large"
            :prefix-icon="iconAvatar"
          >
            <template #suffix>
              <Icon v-if="registerData.isUsernameAvailable" icon="ep:circle-check" color="#67c23a" />
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="email">
          <el-input
            v-model="registerData.registerForm.email"
            :placeholder="t('login.emailPlaceholder')"
            size="large"
            :prefix-icon="iconMessage"
          >
            <template #suffix>
              <Icon v-if="registerData.isEmailAvailable" icon="ep:circle-check" color="#67c23a" />
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="password">
          <InputPassword
            v-model="registerData.registerForm.password"
            :placeholder="t('login.password')"
            size="large"
            strength
            class="w-full"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="confirmPassword">
          <InputPassword
            v-model="registerData.registerForm.confirmPassword"
            size="large"
            :placeholder="t('login.checkPassword')"
            class="w-full"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <XButton
            :loading="loginLoading"
            :title="t('login.register')"
            class="w-full"
            type="primary"
            @click="getCode()"
          />
        </el-form-item>
      </el-col>
      <Verify
        v-if="registerData.captchaEnable === 'true'"
        ref="verify"
        :captchaType="captchaType"
        :imgSize="{ width: '400px', height: '200px' }"
        mode="pop"
        @success="handleRegister"
      />
    </el-row>
    <XButton :title="t('login.hasUser')" class="w-full" @click="handleBackLogin()" />
  </el-form>
</template>
<script lang="ts" setup>
import { ElLoading } from 'element-plus'
import LoginFormTitle from './LoginFormTitle.vue'
import { InputPassword } from '@/components/InputPassword'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import { useIcon } from '@/hooks/web/useIcon'
import * as authUtil from '@/utils/auth'
import { usePermissionStore } from '@/store/modules/permission'
import * as LoginApi from '@/api/login'
import { LoginStateEnum, useLoginState, useFormValid } from './useLogin'

import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'

defineOptions({ name: 'RegisterForm' })

const { t } = useI18n()
const message = useMessage()
const iconHouse = useIcon({ icon: 'ep:house' })
const iconAvatar = useIcon({ icon: 'ep:avatar' })
const iconLock = useIcon({ icon: 'ep:lock' })
const iconMessage = useIcon({ icon: 'ep:message' })
const formLogin = ref()
const {validForm} = useFormValid(formLogin)
const { handleBackLogin, getLoginState, setLoginState } = useLoginState()
const { currentRoute, push } = useRouter()
const permissionStore = usePermissionStore()
const redirect = ref<string>('')
const loginLoading = ref(false)
const verify = ref()
const captchaType = ref('blockPuzzle') // blockPuzzle 滑块 clickWord 点击文字 pictureWord 文字验证码

const getShow = computed(() => unref(getLoginState) === LoginStateEnum.REGISTER)

const equalToPassword = (_rule, value, callback) => {
  if (registerData.registerForm.password !== value) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateUsername = async (_rule, value, callback) => {
  registerData.isUsernameAvailable = false
  if (!value || value.length < 4 || value.length > 30) {
    callback(new Error(t('login.accountMsg')))
    return
  }
  try {
    const res = await LoginApi.checkUsername(value)
    if (res === true) {
      callback(new Error(t('login.accountExists')))
    } else {
      registerData.isUsernameAvailable = true
      callback()
    }
  } catch (error) {
    callback()
  }
}

const validateEmail = async (_rule, value, callback) => {
  registerData.isEmailAvailable = false
  if (!value) {
    callback(new Error(t('login.emailMsg')))
    return
  }
  // 简单的邮箱格式正则
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (!emailReg.test(value)) {
    callback(new Error(t('login.emailInvalid')))
    return
  }
  
  // 校验邮箱是否已存在
  try {
    const res = await LoginApi.checkUsername(value)
    if (res === true) {
      callback(new Error(t('login.emailRegistered')))
    } else {
      registerData.isEmailAvailable = true
      callback()
    }
  } catch (error) {
    callback()
  }
}

const registerRules = computed(() => ({
  username: [
    { required: true, trigger: 'blur', message: t('login.accountMsg') },
    { min: 4, max: 30, message: t('login.usernameLengthMsg'), trigger: 'blur' },
    { validator: validateUsername, trigger: 'blur' }
  ],
  email: [
    { required: true, trigger: 'blur', message: t('login.emailMsg') },
    { validator: validateEmail, trigger: 'blur' }
  ],
  password: [
    { required: true, trigger: 'blur', message: t('login.passwordMsg') },
    { min: 5, max: 20, message: t('login.passwordLengthMsg'), trigger: 'blur' },
    { pattern: /^[^<>"'|\\]+$/, message: t('login.passwordInvalid'), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, trigger: 'blur', message: t('login.confirmPasswordMsg') },
    { required: true, validator: equalToPassword, trigger: 'blur' }
  ]
}))

const registerData = reactive({
  isShowPassword: false,
  isUsernameAvailable: false,
  isEmailAvailable: false,
  captchaEnable: import.meta.env.VITE_APP_CAPTCHA_ENABLE,
  tenantEnable: import.meta.env.VITE_APP_TENANT_ENABLE,
  registerForm: {
    username: '',
    email: '',
    tenantId: 1, // 默认租户ID
    password: '',
    confirmPassword: '',
    captchaVerification: ''
  }
})

const loading = ref() // ElLoading.service 返回的实例
// 提交注册
const handleRegister = async (params: any) => {
  loginLoading.value = true
  try {
    const data = await validForm()
    if (!data) {
      return
    }

    if (registerData.captchaEnable) {
      registerData.registerForm.captchaVerification = params.captchaVerification
    }

    const res = await LoginApi.register(registerData.registerForm)
    if (!res) {
      return
    }
    
    message.success(t('login.registerSuccess'))
    // 跳转到登录页面
    setLoginState(LoginStateEnum.LOGIN)
  } finally {
    loginLoading.value = false
  }
}

// 获取验证码
const getCode = async () => {
  // 情况一，未开启：则直接注册
  if (registerData.captchaEnable === 'false') {
    await handleRegister({})
  } else {
    // 情况二，已开启：则展示验证码；只有完成验证码的情况，才进行注册
    // 弹出验证码
    verify.value.show()
  }
}

// 获取租户 ID
const getTenantId = async () => {
  if (registerData.tenantEnable === 'true') {
    const res = await LoginApi.getTenantIdByName(registerData.registerForm.tenantName)
    authUtil.setTenantId(res)
  }
}

// 根据域名，获得租户信息
const getTenantByWebsite = async () => {
  if (registerData.tenantEnable === 'true') {
    const website = location.host
    const res = await LoginApi.getTenantByWebsite(website)
    if (res) {
      registerData.registerForm.tenantName = res.name
      authUtil.setTenantId(res.id)
    }
  }
}

watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string
  },
  {
    immediate: true
  }
)
onMounted(() => {
  // getCookie()
  getTenantByWebsite()
})
</script>

<style lang="scss" scoped>
:deep(.anticon) {
  &:hover {
    color: var(--el-color-primary) !important;
  }
}

.login-code {
  float: right;
  width: 100%;
  height: 38px;

  img {
    width: 100%;
    height: auto;
    max-width: 100px;
    vertical-align: middle;
    cursor: pointer;
  }
}
</style>
