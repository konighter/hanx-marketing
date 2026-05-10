<template>
  <el-form
    v-show="getShow"
    ref="formLogin"
    :model="loginData.loginForm"
    :rules="LoginRules"
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
            v-model="loginData.loginForm.username"
            :placeholder="t('login.usernamePlaceholder')"
            :prefix-icon="iconAvatar"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="password">
          <el-input
            v-model="loginData.loginForm.password"
            :placeholder="t('login.passwordPlaceholder')"
            :prefix-icon="iconLock"
            show-password
            type="password"
            @keyup.enter="getCode()"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px mt-[-20px] mb-[-20px]">
        <el-form-item>
          <el-row justify="space-between" style="width: 100%">
            <el-col :span="6">
              <el-checkbox v-model="loginData.loginForm.rememberMe">
                {{ t('login.remember') }}
              </el-checkbox>
            </el-col>
            <el-col :offset="6" :span="12">
              <el-link
                class="float-right"
                type="primary"
                @click="setLoginState(LoginStateEnum.RESET_PASSWORD)"
              >
                {{ t('login.forgetPassword') }}
              </el-link>
            </el-col>
          </el-row>
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <el-row :gutter="5" justify="space-between" style="width: 100%">  
<el-col :span="12">
<XButton
            :loading="loginLoading"
            :title="t('login.login')"
            class="w-full"
            type="primary"
            @click="getCode()"
          />
</el-col>
<el-col :span="12">
  <XButton
                :title="t('login.btnRegister')"
                class="w-full"
                @click="setLoginState(LoginStateEnum.REGISTER)"
              />
</el-col>


          </el-row>
          
        
        </el-form-item>
      </el-col>
      <Verify
        v-if="loginData.captchaEnable === 'true'"
        ref="verify"
        :captchaType="captchaType"
        :imgSize="{ width: '400px', height: '200px' }"
        mode="pop"
        @success="handleLogin"
      />
      <!-- <el-col :span="24" class="px-10px">
        <el-form-item>
          <el-row :gutter="5" justify="space-between" style="width: 100%">
            <el-col :span="8">
              <XButton
                :title="t('login.btnMobile')"
                class="w-full"
                @click="setLoginState(LoginStateEnum.MOBILE)"
              />
            </el-col>
            <el-col :span="8">
              <XButton
                :title="t('login.btnQRCode')"
                class="w-full"
                @click="setLoginState(LoginStateEnum.QR_CODE)"
              />
            </el-col>
            <el-col :span="8">
              <XButton
                :title="t('login.btnRegister')"
                class="w-full"
                @click="setLoginState(LoginStateEnum.REGISTER)"
              />
            </el-col>
          </el-row>
        </el-form-item>
      </el-col> -->
      <!-- <el-divider content-position="center">{{ t('login.otherLogin') }}</el-divider>
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <div class="w-full flex justify-between">
            <Icon
              v-for="(item, key) in socialList"
              :key="key"
              :icon="item.icon"
              :size="30"
              class="anticon cursor-pointer"
              color="#999"
              @click="doSocialLogin(item.type)"
            />
          </div>
        </el-form-item>
      </el-col> -->

    </el-row>
  </el-form>

</template>
<script lang="ts" setup>
import { ElLoading } from 'element-plus'
import LoginFormTitle from './LoginFormTitle.vue'

import type { RouteLocationNormalizedLoaded } from 'vue-router'

import { useIcon } from '@/hooks/web/useIcon'

import * as authUtil from '@/utils/auth'
import { usePermissionStore } from '@/store/modules/permission'
import * as LoginApi from '@/api/login'
import { LoginStateEnum, useFormValid, useLoginState } from './useLogin'

defineOptions({ name: 'LoginForm' })

const { t } = useI18n()
const message = useMessage()
const iconHouse = useIcon({ icon: 'ep:house' })
const iconAvatar = useIcon({ icon: 'ep:avatar' })
const iconLock = useIcon({ icon: 'ep:lock' })
const formLogin = ref()

const { validForm } = useFormValid(formLogin)
const { setLoginState, getLoginState } = useLoginState()
const { currentRoute, push } = useRouter()
const permissionStore = usePermissionStore()
const redirect = ref<string>('')
const loginLoading = ref(false)
const verify = ref()
const captchaType = ref('blockPuzzle') // blockPuzzle 滑块 clickWord 点击文字 pictureWord 文字验证码

const getShow = computed(() => unref(getLoginState) === LoginStateEnum.LOGIN)

const LoginRules = computed(() => ({
  username: [{ required: true, trigger: 'blur', message: t('login.accountMsg') }],
  password: [{ required: true, trigger: 'blur', message: t('login.passwordMsg') }]
}))
const loginData = reactive({
  isShowPassword: false,
  captchaEnable: import.meta.env.VITE_APP_CAPTCHA_ENABLE,
  tenantEnable: import.meta.env.VITE_APP_TENANT_ENABLE,
  loginForm: {
    username: import.meta.env.VITE_APP_DEFAULT_LOGIN_USERNAME || '',
    password: import.meta.env.VITE_APP_DEFAULT_LOGIN_PASSWORD || '',
    captchaVerification: '',
    rememberMe: true // 默认记录我。如果不需要，可手动修改
  }
})

const socialList = [
  { icon: 'ant-design:wechat-filled', type: 30 },
  { icon: 'ant-design:dingtalk-circle-filled', type: 20 },
  { icon: 'ant-design:github-filled', type: 0 },
  { icon: 'ant-design:alipay-circle-filled', type: 0 }
]

// 获取验证码
const getCode = async () => {
  // 情况一，未开启：则直接登录
  if (loginData.captchaEnable === 'false') {
    await handleLogin({})
  } else {
    // 情况二，已开启：则展示验证码；只有完成验证码的情况，才进行登录
    // 弹出验证码
    verify.value.show()
  }
}
// 获取租户 ID
const getTenantId = async () => {
  if (loginData.tenantEnable === 'true') {
    const res = await LoginApi.getTenantIdByName(loginData.loginForm.tenantName)
    authUtil.setTenantId(res)
  }
}
// 记住我
const getLoginFormCache = () => {
  const loginForm = authUtil.getLoginForm()
  if (loginForm) {
    loginData.loginForm = {
      ...loginData.loginForm,
      username: loginForm.username ? loginForm.username : loginData.loginForm.username,
      password: loginForm.password ? loginForm.password : loginData.loginForm.password,
      rememberMe: loginForm.rememberMe,
    }
  }
}
// 根据域名，获得租户信息
const getTenantByWebsite = async () => {
  if (loginData.tenantEnable === 'true') {
    const website = location.host
    const res = await LoginApi.getTenantByWebsite(website)
    if (res) {
      authUtil.setTenantId(res.id)
    }
  }
}
const loading = ref() // ElLoading.service 返回的实例
// 登录
const handleLogin = async (params: any) => {
  loginLoading.value = true
  try {

    const data = await validForm()
    if (!data) {
      return
    }
    const loginDataLoginForm = { ...loginData.loginForm }
    loginDataLoginForm.captchaVerification = params.captchaVerification
    const res = await LoginApi.login(loginDataLoginForm)
    if (!res) {
      return
    }
    loading.value = ElLoading.service({
      lock: true,
      text: '正在加载系统中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    if (loginDataLoginForm.rememberMe) {
      authUtil.setLoginForm(loginDataLoginForm)
    } else {
      authUtil.removeLoginForm()
    }
    authUtil.setToken(res)


    // 如果用户有多个租户，则需要跳转选择租户的页面
    if (import.meta.env.VITE_APP_TENANT_ENABLE === 'true') {
      const tenants = await LoginApi.getUserTenants()
      if (tenants.length > 1 || tenants.length === 0) {
        await push({ path: '/tenant/select', query: { redirect: redirect.value, type: tenants.length === 0 ? 'create' : undefined } })
      } else {
        authUtil.setTenantId(tenants[0].id)
        await routeDerect()
      }
    } else {
      await routeDerect()
    }


    
  } finally {
    loginLoading.value = false
    loading.value.close()
  }
}

const routeDerect = async () => {
    if (!redirect.value) {
      redirect.value = '/'
    }
    // 判断是否为SSO登录
    if (redirect.value.indexOf('sso') !== -1) {
      window.location.href = window.location.href.replace('/login?redirect=', '')
    } else {
      await push({ path: redirect.value || permissionStore.addRouters[0].path })
    }
}

// 社交登录
const doSocialLogin = async (type: number) => {
  if (type === 0) {
    message.error('此方式未配置')
  } else {
    // 计算 redirectUri
    // 注意: type、redirect 需要先 encode 一次，否则钉钉回调会丢失。
    // 配合 social-login.vue#getUrlValue() 使用
    const redirectUri =
      location.origin +
      '/social-login?' +
      encodeURIComponent(`type=${type}&redirect=${redirect.value || '/'}`)

    // 进行跳转
    window.location.href = await LoginApi.socialAuthRedirect(type, encodeURIComponent(redirectUri))
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
  getLoginFormCache()
  // getTenantByWebsite()
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
