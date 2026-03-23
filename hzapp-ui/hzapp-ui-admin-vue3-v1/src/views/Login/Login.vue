<template>
  <div
    :class="prefixCls"
    class="relative h-[100%] w-full overflow-hidden bg-[#f8fafc] dark:bg-[#0f172a] flex items-center justify-center p-4 sm:p-8"
  >
    <!-- CSS Mesh Gradient Background Orbs -->
    <div class="fixed inset-0 overflow-hidden pointer-events-none z-0">
      <div class="absolute -top-[10%] -left-[10%] w-[50%] h-[50%] rounded-full mix-blend-multiply filter blur-[120px] opacity-70 animate-blob bg-indigo-200 dark:bg-indigo-900/40"></div>
      <div class="absolute top-[20%] -right-[10%] w-[45%] h-[45%] rounded-full mix-blend-multiply filter blur-[120px] opacity-70 animate-blob animation-delay-2000 bg-cyan-200 dark:bg-cyan-900/30"></div>
      <div class="absolute -bottom-[20%] left-[20%] w-[60%] h-[60%] rounded-full mix-blend-multiply filter blur-[120px] opacity-70 animate-blob animation-delay-4000 bg-blue-200 dark:bg-blue-900/40"></div>
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
      class="relative z-10 w-full max-w-[440px] bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl rounded-2xl md:rounded-3xl shadow-2xl shadow-indigo-500/10 dark:shadow-none border border-white/50 dark:border-slate-700/50 p-6 sm:p-10 transition-all duration-300"
    >
      <!-- Logo & System Title -->
      <div class="flex flex-col items-center justify-center mb-8">
        <img alt="Logo" class="h-14 w-14 mb-4 drop-shadow-sm" src="@/assets/imgs/logo.png" />
        <h1 class="text-2xl font-bold text-slate-900 dark:text-white tracking-tight text-center">
          {{ underlineToHump(appStore.getTitle) }}
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

import { useDesign } from '@/hooks/web/useDesign'
import { useAppStore } from '@/store/modules/app'
import { ThemeSwitch } from '@/layout/components/ThemeSwitch'
import { LocaleDropdown } from '@/layout/components/LocaleDropdown'
import { LoginStateEnum, useLoginState } from './components/useLogin'

import { LoginForm, MobileForm, QrCodeForm, RegisterForm, SSOLoginVue, ForgetPasswordForm } from './components'

defineOptions({ name: 'Login' })

const { t } = useI18n()
const appStore = useAppStore()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('login')
const { getLoginState } = useLoginState()
</script>

<style lang="scss" scoped>
/* 简单的呼吸流动关键帧给背景光晕使用 */
@keyframes blob {
  0% { transform: translate(0px, 0px) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0px, 0px) scale(1); }
}

.animate-blob {
  animation: blob 15s infinite alternate;
}

.animation-delay-2000 {
  animation-delay: 2s;
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
