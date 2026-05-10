<template>
  <div
    :class="prefixCls"
    class="relative h-[100%] w-full overflow-hidden bg-[#f8fafc] dark:bg-[#0f172a] flex items-center justify-center p-4 sm:p-8"
  >
    <!-- CSS Mesh Gradient Background Orbs -->
    <div class="fixed inset-0 overflow-hidden pointer-events-none z-0">
      <div class="absolute -top-[10%] -left-[10%] w-[60%] h-[60%] opacity-50 animate-blob-slow blob-1"></div>
      <div class="absolute top-[30%] -right-[15%] w-[50%] h-[50%] opacity-50 animate-blob-slow animation-delay-4000 blob-2"></div>
    </div>

    <!-- Top Right Tools (Theme & Locale) -->
    <div
      class="absolute top-4 right-4 sm:top-8 sm:right-8 flex items-center space-x-4 z-20 text-slate-500 dark:text-slate-400"
    >
      <ThemeSwitch />
      <LocaleDropdown />
    </div>

    <!-- Centered Login Card -->
    <div
      class="relative z-10 w-full max-w-[440px] bg-white dark:bg-slate-800 rounded-2xl md:rounded-3xl shadow-2xl shadow-indigo-500/10 dark:shadow-none border border-slate-100 dark:border-slate-700/50 p-6 sm:p-10 transition-all duration-300"
    >
      <!-- Logo & System Title -->
      <div class="flex flex-col items-center justify-center mb-8">
        <img alt="Logo" class="h-14 w-14 mb-4" src="@/assets/imgs/logo.png" />
        <h1 class="text-2xl font-bold text-slate-900 dark:text-white tracking-tight text-center">
          {{ appTitle }}
        </h1>
        <p class="text-sm text-slate-500 dark:text-slate-400 mt-2 text-center">
          {{ t('login.welcome') }}
        </p>
      </div>

      <!-- Forms Container -->
      <Transition appear enter-active-class="animate__animated animate__fadeInUp animate__faster">
        <div class="w-full relative">
          <!-- 账号登录 -->
          <LoginForm v-show="getLoginState === LoginStateEnum.LOGIN" class="w-full" />
          <!-- 手机登录 -->
          <MobileForm v-show="getLoginState === LoginStateEnum.MOBILE" class="w-full" />
          <!-- 二维码登录 -->
          <QrCodeForm v-show="getLoginState === LoginStateEnum.QR_CODE" class="w-full" />
          <!-- 注册 -->
          <RegisterForm v-show="getLoginState === LoginStateEnum.REGISTER" class="w-full" />
          <!-- 三方登录 -->
          <SSOLoginVue v-show="getLoginState === LoginStateEnum.SSO" class="w-full" />
          <!-- 忘记密码 -->
          <ForgetPasswordForm v-show="getLoginState === LoginStateEnum.RESET_PASSWORD" class="w-full" />
        </div>
      </Transition>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { underlineToHump } from '@/utils'
import { onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'

import { useDesign } from '@/hooks/web/useDesign'
import { useAppStore } from '@/store/modules/app'
import { ThemeSwitch } from '@/layout/components/ThemeSwitch'
import { LocaleDropdown } from '@/layout/components/LocaleDropdown'
import { LoginStateEnum, useLoginState } from './components/useLogin'

import { LoginForm, MobileForm, QrCodeForm, RegisterForm, SSOLoginVue, ForgetPasswordForm } from './components'

defineOptions({ name: 'Login' })

const { t } = useI18n()
const route = useRoute()
const appStore = useAppStore()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('login')
const { getLoginState, setLoginState } = useLoginState()

const appTitle = computed(() => underlineToHump(appStore.getTitle))

onMounted(() => {
  if (route.query.login_state === 'reset_password') {
    setLoginState(LoginStateEnum.RESET_PASSWORD)
  }
})
</script>

<style lang="scss" scoped>
/* 使用 Radial Gradient 模拟模糊效果，避开昂贵的 filter: blur */
.blob-1 {
  background: radial-gradient(circle, rgba(165, 180, 252, 0.4) 0%, rgba(165, 180, 252, 0) 70%);
  .dark & {
    background: radial-gradient(circle, rgba(49, 46, 129, 0.4) 0%, rgba(49, 46, 129, 0) 70%);
  }
}
.blob-2 {
  background: radial-gradient(circle, rgba(165, 243, 252, 0.4) 0%, rgba(165, 243, 252, 0) 70%);
  .dark & {
    background: radial-gradient(circle, rgba(22, 78, 99, 0.3) 0%, rgba(22, 78, 99, 0) 70%);
  }
}

@keyframes blob-slow {
  0% { transform: translate(0px, 0px) scale(1); opacity: 0.4; }
  50% { transform: translate(20px, -20px) scale(1.02); opacity: 0.6; }
  100% { transform: translate(0px, 0px) scale(1); opacity: 0.4; }
}

.animate-blob-slow {
  animation: blob-slow 25s infinite ease-in-out;
  will-change: transform, opacity;
}

.animation-delay-4000 {
  animation-delay: 4s;
}
</style>

<style lang="scss">
/* 针对表单内部组件的微调，让它们在卡片内更贴合 */
.dark .login-form {
  .el-divider__text {
    background-color: transparent !important;
  }
}
.login-form {
  .el-divider__text {
    background-color: transparent !important;
  }
  .el-form-item--large {
    margin-bottom: 22px;
  }
}
</style>
