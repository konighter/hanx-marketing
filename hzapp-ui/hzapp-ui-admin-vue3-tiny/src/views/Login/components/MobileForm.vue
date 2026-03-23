<template>
  <el-form
    v-show="getShow"
    ref="formSmsLogin"
    :model="loginData.loginForm"
    :rules="rules"
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
      <!-- 手机号 -->
      <el-col :span="24" class="px-10px">
        <el-form-item prop="mobileNumber">
          <el-input
            v-model="loginData.loginForm.mobileNumber"
            :placeholder="t('login.mobileNumberPlaceholder')"
            :prefix-icon="iconCellphone"
          />
        </el-form-item>
      </el-col>
      <!-- 验证码 -->
      <el-col :span="24" class="px-10px">
        <el-form-item prop="code">
          <el-row :gutter="5" justify="space-between" style="width: 100%">
            <el-col :span="24">
              <el-input
                v-model="loginData.loginForm.code"
                :placeholder="t('login.codePlaceholder')"
                :prefix-icon="iconCircleCheck"
              >
                <!-- <el-button class="w-[100%]"> -->
                <template #append>
                  <span
                    v-if="mobileCodeTimer <= 0"
                    class="getMobileCode"
                    style="cursor: pointer"
                    @click="getSmsCode"
                  >
                    {{ t('login.getSmsCode') }}
                  </span>
                  <span v-if="mobileCodeTimer > 0" class="getMobileCode" style="cursor: pointer">
                    {{ mobileCodeTimer }}秒后可重新获取
                  </span>
                </template>
              </el-input>
              <!-- </el-button> -->
            </el-col>
          </el-row>
        </el-form-item>
      </el-col>
      <!-- 登录按钮 / 返回按钮 -->
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <XButton
            :loading="loginLoading"
            :title="t('login.login')"
            class="w-full"
            type="primary"
            @click="signIn()"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <XButton
            :loading="loginLoading"
            :title="t('login.backLogin')"
            class="w-full"
            @click="handleBackLogin()"
          />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
  <TenantSelectForm ref="tenantSelectRef" @success="routeDerect"/>
</template>
<script lang="ts" setup>
import type { RouteLocationNormalizedLoaded } from 'vue-router'

import { useIcon } from '@/hooks/web/useIcon'

import { setTenantId, setToken } from '@/utils/auth'
import { usePermissionStore } from '@/store/modules/permission'
import { sendSmsCode, smsLogin, getUserTenants } from '@/api/login'
import LoginFormTitle from './LoginFormTitle.vue'
import TenantSelectForm from './TenantSelectForm.vue'
import { LoginStateEnum, useFormValid, useLoginState } from './useLogin'
import { ElLoading } from 'element-plus'

defineOptions({ name: 'MobileForm' })

const { t } = useI18n()
const message = useMessage()
const permissionStore = usePermissionStore()
const { currentRoute, push } = useRouter()
const formSmsLogin = ref()
const loginLoading = ref(false)
const iconCellphone = useIcon({ icon: 'ep:cellphone' })
const iconCircleCheck = useIcon({ icon: 'ep:circle-check' })
const tenantSelectRef = ref()
const { validForm } = useFormValid(formSmsLogin)
const { handleBackLogin, getLoginState } = useLoginState()
const getShow = computed(() => unref(getLoginState) === LoginStateEnum.MOBILE)

const rules = {
  mobileNumber: [required],
  code: [required]
}
const loginData = reactive({
  codeImg: '',
  tenantEnable: import.meta.env.VITE_APP_TENANT_ENABLE,
  token: '',
  loading: {
    signIn: false
  },
  loginForm: {
    uuid: '',
    mobileNumber: '',
    code: ''
  }
})
const smsVO = reactive({
  smsCode: {
    mobile: '',
    scene: 21
  },
  loginSms: {
    mobile: '',
    code: ''
  }
})
const mobileCodeTimer = ref(0)
const redirect = ref<string>('')
const getSmsCode = async () => {
  smsVO.smsCode.mobile = loginData.loginForm.mobileNumber
  await sendSmsCode(smsVO.smsCode).then(async () => {
    message.success(t('login.SmsSendMsg'))
    // 设置倒计时
    mobileCodeTimer.value = 60
    let msgTimer = setInterval(() => {
      mobileCodeTimer.value = mobileCodeTimer.value - 1
      if (mobileCodeTimer.value <= 0) {
        clearInterval(msgTimer)
      }
    }, 1000)
  })
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

// 登录
const signIn = async () => {
  const data = await validForm()
  if (!data) return
  const loading = ElLoading.service({
    lock: true,
    text: '正在加载系统中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  loginLoading.value = true
  try {
    smsVO.loginSms.mobile = loginData.loginForm.mobileNumber
    smsVO.loginSms.code = loginData.loginForm.code
    const res = await smsLogin(smsVO.loginSms)
    setToken(res)

    // 如果用户有多个租户，则需要弹出选择租户的弹窗
    if (import.meta.env.VITE_APP_TENANT_ENABLE === 'true') {
      const tenants = await getUserTenants()
      if (tenants.length > 1) {
        tenantSelectRef.value.open(tenants)
      } else if (tenants.length === 1) {
        setTenantId(tenants[0].id)
        await routeDerect()
      } else {
        await routeDerect()
      }
    } else {
      await routeDerect()
    }
  } catch (e) {
  } finally {
    loginLoading.value = false
    loading.close()
  }
}

const routeDerect = async () => {
  if (!redirect.value) {
    redirect.value = '/'
  }
  push({ path: redirect.value || permissionStore.addRouters[0].path })
}
</script>

<style lang="scss" scoped>
:deep(.anticon) {
  &:hover {
    color: var(--el-color-primary) !important;
  }
}

.smsbtn {
  margin-top: 33px;
}
</style>
