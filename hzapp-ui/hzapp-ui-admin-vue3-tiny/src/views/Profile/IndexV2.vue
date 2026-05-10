<template>
  <div class="profile-v2-wrapper p-20px animate-fade-in">
    <div class="profile-v2-container bg-white rounded-24px shadow-xl shadow-gray-100 flex min-h-800px overflow-hidden border border-gray-50">
      <!-- Left Side: Nav -->
      <div class="w-280px border-r border-gray-50 p-32px bg-[#fafbfc]/50">
        <h2 class="text-22px font-800 mb-40px text-[#1a1a1a] tracking-tight">个人中心管理</h2>
        <div class="nav-list">
          <div
            v-for="item in navItems"
            :key="item.id" 
            class="nav-item mb-12px p-14px rounded-16px flex items-center cursor-pointer transition-all duration-300"
            :class="{ 
              'bg-blue-600 text-white shadow-lg shadow-blue-200': activeNav === item.id, 
              'text-gray-500 hover:bg-gray-100 hover:text-[#1a1a1a]': activeNav !== item.id 
            }"
            @click="activeNav = item.id"
          >
            <div
              class="w-32px h-32px rounded-10px flex items-center justify-center mr-14px"
              :class="activeNav === item.id ? 'bg-white/20' : 'bg-gray-100'"
            >
              <Icon :icon="item.icon" :class="activeNav === item.id ? 'text-white' : 'text-gray-500'" class="text-16px" />
            </div>
            <span class="text-14px font-700">{{ item.name }}</span>
          </div>
        </div>
        <div class="mt-40px pt-32px border-t border-gray-100">
          <div
            class="nav-item p-14px rounded-16px flex items-center cursor-pointer transition-all duration-300 text-gray-500 hover:bg-gray-100 hover:text-blue-600 group"
            @click="push('/')"
          >
            <div class="w-32px h-32px rounded-10px flex items-center justify-center mr-14px bg-gray-100 group-hover:bg-blue-50 transition-colors">
              <Icon icon="ep:home-filled" class="text-16px text-gray-500 group-hover:text-blue-600" />
            </div>
            <span class="text-14px font-700">返回主页</span>
          </div>
        </div>
      </div>

      <!-- Right Side: Content -->
      <div class="flex-1 p-48px bg-white overflow-y-auto">
        <div v-if="activeNav === 'personal'" class="max-w-800px mx-auto">
          <div class="flex justify-end items-center mb-40px h-32px">
            <div v-if="isSaving" class="flex items-center text-green-500 text-13px font-600 bg-green-50 px-12px py-6px rounded-full">
              <Icon icon="ep:loading" class="mr-8px animate-spin" /> 正在保存...
            </div>
          </div>
          
          <!-- Avatar Section -->
          <div class="mb-48px flex items-center">
            <div class="relative group w-fit cursor-pointer mr-32px" @click="triggerAvatarUpload">
              <div class="w-120px h-120px rounded-full overflow-hidden border-4 border-white shadow-xl p-1 bg-gray-50">
                <img :src="userProfile.avatar || defaultAvatar" class="w-full h-full rounded-full object-cover" />
              </div>
              <div class="absolute inset-0 flex items-center justify-center bg-black/50 rounded-full opacity-0 group-hover:opacity-100 transition-all duration-300 backdrop-blur-sm">
                <div class="flex flex-col items-center">
                  <Icon icon="ep:camera" class="text-white text-24px mb-4px" />
                  <span class="text-white text-11px font-bold">修改头像</span>
                </div>
              </div>
              <div class="absolute -right-4px -bottom-4px w-36px h-36px bg-blue-600 rounded-full border-4 border-white flex items-center justify-center shadow-lg">
                 <Icon icon="ep:edit-pen" class="text-white text-14px" />
              </div>
              <input
                ref="avatarInputRef"
                type="file"
                class="hidden"
                accept="image/*"
                @change="handleAvatarFileChange"
              />
            </div>
            <div>
              <h1 class="text-32px font-900 text-[#1a1a1a] mb-8px tracking-tighter">{{ userProfile.username }}</h1>
              <div class="flex items-center text-gray-400 text-14px font-500">
                <Icon icon="ep:clock" class="mr-6px" />
                <span>最近登录: {{ userProfile.loginDate ? formatDate(userProfile.loginDate) : '暂无数据' }}</span>
                <span class="mx-12px text-gray-200">|</span>
                <Icon icon="ep:location" class="mr-6px" />
                <span>IP: {{ userProfile.loginIp || '未知' }}</span>
              </div>
            </div>
          </div>

          <!-- Form -->
          <div class="grid grid-cols-2 gap-x-32px gap-y-28px mb-48px">
            <div class="form-item">
              <label class="text-13px font-700 text-gray-500 mb-10px block uppercase tracking-wider">用户昵称</label>
              <el-input v-model="userProfile.nickname" placeholder="请输入昵称" class="custom-input" />
            </div>
            <div class="form-item">
              <label class="text-13px font-700 text-gray-500 mb-10px block uppercase tracking-wider">手机号码</label>
              <div class="flex items-center w-full">
                 <div class="w-90px mr-12px flex-shrink-0">
                    <el-select v-model="userProfile.countryCode" class="custom-select w-full">
                       <el-option label="+86" value="+86" />
                       <el-option label="+880" value="+880" />
                    </el-select>
                 </div>
                 <div class="flex-1 min-w-0">
                    <el-input v-model="userProfile.mobile" placeholder="请输入手机号" class="custom-input w-full" />
                 </div>
              </div>
            </div>
            <div class="form-item col-span-2">
              <label class="text-13px font-700 text-gray-500 mb-10px block uppercase tracking-wider">电子邮箱</label>
              <div class="relative">
                <el-input v-model="userProfile.email" placeholder="hello@example.com" class="custom-input" />
                <!-- <div class="mt-8px flex items-center text-orange-500 text-12px font-600">
                  <Icon icon="ep:warning" class="mr-4px" />
                  <span>邮箱修改需要进行身份验证（待实现）</span>
                </div> -->
              </div>
            </div>
          </div>

          <div class="flex justify-end mb-48px">
            <el-button type="primary" class="h-48px px-32px rounded-16px font-bold shadow-lg shadow-blue-100" :loading="isSaving" @click="handleSave">
              保存更改
            </el-button>
          </div>
          
          <!-- Delete Account Section -->
          <div class="mt-20px p-32px bg-gray-50 rounded-24px border border-gray-100 shadow-inner">
            <h3 class="text-18px font-800 mb-16px text-[#1a1a1a] tracking-tight">注销账号</h3>
            <div class="bg-white p-16px rounded-16px mb-24px flex items-center border border-blue-50 shadow-sm">
              <div class="w-32px h-32px rounded-full bg-blue-50 flex items-center justify-center mr-12px">
                <Icon icon="ep:info-filled" class="text-blue-500 text-16px" />
              </div>
              <span class="text-14px text-gray-600 font-500">提交注销申请后，您将有 <b class="text-[#1a1a1a] font-700">6 个月</b> 的缓冲期来恢复账号。</span>
            </div>
            <p class="text-14px text-gray-400 mb-24px leading-relaxed font-500">
              点击下方按钮将永久注销您的账号。注销后，您将无法访问关联的企业、财务及个人数据，且数据无法恢复。
            </p>
            <el-button type="danger" plain class="h-44px px-24px rounded-14px text-14px font-bold border-2 hover:bg-red-50 transition-all">注销我的账号</el-button>
          </div>
        </div>

        <!-- Account & Security Tab -->
        <div v-else-if="activeNav === 'email'" class="max-w-800px mx-auto pt-32px">
          
          <div class="bg-white rounded-24px border border-gray-100 p-32px mb-32px shadow-sm">
            <h3 class="text-18px font-800 mb-24px text-[#1a1a1a]">修改密码</h3>
            <el-form ref="passFormRef" :model="passwordForm" :rules="passRules" label-position="top">
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" class="custom-input" />
              </el-form-item>
              <div class="grid grid-cols-2 gap-24px">
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" class="custom-input" />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" class="custom-input" />
                </el-form-item>
              </div>
              <div class="flex justify-end mt-24px">
                <el-button type="primary" class="h-44px px-32px rounded-12px font-bold" @click="handleUpdatePassword">更新密码</el-button>
              </div>
            </el-form>
          </div>

          <div class="bg-blue-50 rounded-24px p-32px flex items-center justify-between border border-blue-100">
            <div>
              <h3 class="text-16px font-800 text-blue-900 mb-8px">找回密码</h3>
              <p class="text-14px text-blue-700/70">如果您忘记了密码，可以通过绑定的邮箱或手机号找回。</p>
            </div>
            <el-button type="primary" plain class="h-44px px-24px rounded-12px font-bold bg-white" @click="handleRetrievePassword">立即找回</el-button>
          </div>
        </div>

        <!-- My Businesses Tab -->
        <div v-else-if="activeNav === 'businesses'" class="max-w-900px mx-auto pt-32px">
          
          <div v-if="tenantList.length > 0" class="grid grid-cols-2 gap-24px">
            <div
              v-for="tenant in tenantList"
              :key="tenant.id" 
              class="bg-white p-24px rounded-24px border-2 border-gray-200 flex flex-col items-center text-center hover:shadow-2xl hover:border-blue-600 hover:-translate-y-4px transition-all duration-300 group cursor-pointer"
            >
              <div class="w-64px h-64px bg-gray-50 rounded-20px flex items-center justify-center mb-20px group-hover:bg-blue-600 transition-colors duration-300">
                <Icon icon="ep:office-building" class="text-32px text-blue-600 group-hover:text-white" />
              </div>
              
              <h3 class="text-18px font-800 text-[#1a1a1a] mb-8px tracking-tight line-clamp-1 px-10px w-full">{{ tenant.name }}</h3>
              <p class="text-13px text-gray-400 mb-20px">关联企业成员</p>
              
              <div class="w-full h-1px bg-gray-50 mb-20px"></div>
              
              <el-button type="primary" link class="font-bold text-14px">
                进入企业 <Icon icon="ep:arrow-right" class="ml-4px" />
              </el-button>
            </div>
          </div>
          <div v-else class="text-center py-80px bg-gray-50 rounded-24px border border-dashed border-gray-200">
             <div class="w-80px h-80px bg-white rounded-full flex items-center justify-center mx-auto mb-20px shadow-sm">
                <Icon icon="ep:office-building" class="text-32px text-gray-200" />
             </div>
             <h3 class="text-18px font-800 text-gray-400 mb-8px">暂无关联企业</h3>
             <p class="text-14px text-gray-300">您目前尚未加入或创建任何企业</p>
          </div>
        </div>

        <!-- Placeholder for other tabs -->
        <div v-else class="flex flex-col items-center justify-center h-full text-center py-100px animate-fade-in">
           <div class="w-120px h-120px bg-gray-50 rounded-full flex items-center justify-center mb-20px">
              <Icon :icon="navItems.find(n => n.id === activeNav)?.icon" class="text-40px text-gray-200" />
           </div>
           <h3 class="text-20px font-800 text-gray-400 mb-10px">{{ navItems.find(n => n.id === activeNav)?.name }}</h3>
           <p class="text-gray-400">内容即将上线...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserProfile, updateUserProfile, updateUserPassword } from '@/api/system/user/profile'
import { getUserTenants } from '@/api/login'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { useI18n } from 'vue-i18n'
import { useMessage } from '@/hooks/web/useMessage'
import { useUpload } from '@/components/UploadFile/src/useUpload'
import { formatDate } from '@/utils/formatTime'
import { ElMessageBox, type FormInstance } from 'element-plus'
import { LoginStateEnum, useLoginState } from '@/views/Login/components/useLogin'
import type { UploadRequestOptions } from 'element-plus/es/components/upload/src/upload'
import defaultAvatar from '@/assets/imgs/avatar.gif'

const { t } = useI18n()
const message = useMessage()
const appStore = useAppStore()
const userStore = useUserStore()
const { setLoginState } = useLoginState()
const { push } = useRouter()
const { httpRequest } = useUpload()

const avatarInputRef = ref<HTMLInputElement | null>(null)
const activeNav = ref('personal')
const isSaving = ref(false)
const isUploadingAvatar = ref(false)
const navItems = [
  { id: 'personal', name: '个人信息', icon: 'ep:user' },
  { id: 'email', name: '账号与安全', icon: 'ep:lock' },
  { id: 'businesses', name: '我的企业', icon: 'ep:office-building' },
  { id: 'integration', name: '应用集成', icon: 'ep:link' }
]

const tenantList = ref<any[]>([])

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passRules = reactive({
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 4, max: 16, message: '密码长度需在 4-16 位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

const userProfile = reactive({
  username: '',
  nickname: '',
  email: '',
  mobile: '',
  countryCode: '+86',
  avatar: '',
  loginIp: '',
  loginDate: ''
})

const getList = async () => {
  const res = await getUserProfile()
  userProfile.username = res.username || ''
  userProfile.nickname = res.nickname || ''
  userProfile.email = res.email || ''
  userProfile.mobile = res.mobile || ''
  userProfile.avatar = res.avatar || ''
  userProfile.loginIp = res.loginIp || ''
  userProfile.loginDate = res.loginDate || ''
  
  // Get tenant list
  const tenants = await getUserTenants()
  tenantList.value = tenants || []
}

const passFormRef = ref<FormInstance>()
const handleUpdatePassword = async () => {
  if (!passFormRef.value) return
  await passFormRef.value.validate(async (valid) => {
    if (valid) {
      await updateUserPassword(passwordForm.oldPassword, passwordForm.newPassword)
      ElMessageBox.alert('密码修改成功，请重新登录', '提示', {
        confirmButtonText: '确定',
        type: 'success',
        showClose: false,
        callback: async () => {
          await userStore.loginOut()
          window.location.href = '/login'
        }
      })
    }
  })
}

const handleRetrievePassword = () => {
  message.confirm('找回密码需要退出当前登录，是否继续？', '提示').then(async () => {
    setLoginState(LoginStateEnum.RESET_PASSWORD)
    await userStore.loginOut()
    window.location.href = '/login?login_state=reset_password'
  })
}

const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

const handleAvatarFileChange = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return

  const file = input.files[0]
  isUploadingAvatar.value = true
  
  try {
    const uploadRes = await httpRequest({
      file: file,
      filename: file.name
    } as UploadRequestOptions) as any
    
    const avatarUrl = uploadRes.data
    
    // 更新后端头像
    await updateUserProfile({
      ...userProfile,
      avatar: avatarUrl
    })
    
    // 更新本地状态
    userProfile.avatar = avatarUrl
    await userStore.setUserAvatarAction(avatarUrl)
    
    message.success('头像上传成功')
  } catch (error) {
    console.error('Avatar upload failed:', error)
    message.error('头像上传失败')
  } finally {
    isUploadingAvatar.value = false
    // 清除 input 值，允许重复上传同一文件
    if (avatarInputRef.value) avatarInputRef.value.value = ''
  }
}

const handleSave = async () => {
  isSaving.value = true
  try {
    await updateUserProfile({
      nickname: userProfile.nickname,
      email: userProfile.email,
      mobile: userProfile.mobile,
      avatar: userProfile.avatar
    })
    setTimeout(() => {
      isSaving.value = false
      message.success('个人信息更新成功')
    }, 1000)
  } catch (error) {
    isSaving.value = false
  }
}

// Sidebar collapse handling
let originalCollapse = false

onMounted(() => {
  originalCollapse = appStore.getCollapse
  appStore.setCollapse(true)
  getList()
})

onUnmounted(() => {
  // Restore sidebar state when leaving
  appStore.setCollapse(originalCollapse)
})
</script>

<style scoped>
.profile-v2-wrapper {
  background-color: #f8fafc;
  min-height: calc(100vh - 84px);
}

.custom-input :deep(.el-input__wrapper) {
  background-color: #fff;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 4px 12px;
  height: 40px;
  transition: all 0.2s;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e1;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 1px #3b82f6;
}

.custom-select :deep(.el-input__wrapper) {
  background-color: #fff;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05) !important;
  border: 1px solid #e2e8f0 !important;
  border-radius: 8px;
  padding: 4px 12px;
  height: 40px;
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
