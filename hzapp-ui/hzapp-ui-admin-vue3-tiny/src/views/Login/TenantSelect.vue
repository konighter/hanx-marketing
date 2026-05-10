<template>
  <div class="relative h-[100vh] w-full bg-gray-50 flex items-center justify-center p-4">
    <div class="w-full max-w-3xl bg-white rounded-2xl shadow-xl overflow-hidden flex flex-col md:flex-row">
      <!-- Left side (Welcome/Branding) -->
      <div class="md:w-5/12 bg-blue-600 p-8 text-white flex flex-col justify-center">
        <h2 class="text-3xl font-bold mb-4">欢迎来到系统</h2>
        <p class="text-blue-100 mb-8">
          您当前尚未选择进入的组织。您可以选择一个已存在的组织，或者创建一个新的组织。
        </p>
        <div class="mt-auto">
          <el-button 
            type="primary" 
            class="w-full !bg-white !text-blue-600 hover:!bg-blue-50 border-none h-11 font-bold" 
            @click="goToProfile"
          >
            <Icon icon="ep:user" class="mr-2" />
            前往个人中心
          </el-button>
        </div>
      </div>

      <!-- Right side (Actions) -->
      <div class="md:w-7/12 p-8 flex flex-col h-[500px]">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-xl font-bold text-gray-800">请选择操作</h3>
          <el-button text @click="handleLogout">
            <Icon icon="ep:switch-button" class="mr-1" />
            退出登录
          </el-button>
        </div>

        <el-tabs v-model="activeTab" class="flex-1 flex flex-col">
          <el-tab-pane label="选择已有组织" name="select" class="h-full">
            <div v-if="loading" class="h-64 flex items-center justify-center" v-loading="loading"></div>
            
            <div v-else-if="tenants.length === 0" class="h-64 flex flex-col items-center justify-center text-gray-400">
              <Icon icon="ep:warning" :size="48" class="mb-4 text-gray-300" />
              <p>暂无相关组织，请联系管理员为您添加组织，<br/>或在右侧点击创建新组织。</p>
            </div>

            <div v-else class="h-[300px] overflow-y-auto pr-2 space-y-3">
              <div 
                v-for="tenant in tenants" 
                :key="tenant.id"
                @click="enterTenant(tenant.id)"
                class="p-4 border border-gray-100 rounded-xl cursor-pointer hover:border-blue-500 hover:shadow-md transition-all flex items-center justify-between group"
              >
                <div class="flex items-center space-x-3">
                  <div class="w-10 h-10 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center font-bold text-lg">
                    {{ tenant.name.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <h4 class="font-medium text-gray-800">{{ tenant.name }}</h4>
                  </div>
                </div>
                <Icon icon="ep:arrow-right" class="text-gray-300 group-hover:text-blue-500 transition-colors" />
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="创建新组织" name="create">
            <div class="pt-4 pb-4 px-2 min-h-64 flex flex-col justify-center">
              <div v-if="pendingApply" class="w-full bg-blue-50 p-6 rounded-2xl border border-blue-100 flex flex-col items-center">
                <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
                  <Icon icon="ep:clock" class="text-blue-600" :size="32" />
                </div>
                <h4 class="text-lg font-bold text-gray-800 mb-2">申请审批中</h4>
                <p class="text-sm text-gray-500 text-center mb-2">
                  您提交的企业申请 <span class="text-blue-600 font-semibold">“{{ pendingApply.name }}”</span> 正在审批中。
                </p>
                <el-tag type="info">待审批</el-tag>
              </div>
              
              <template v-else>
                <el-alert
                  title="创建组织需要相关资质与信息，该功能暂未对所有用户全量开放。相关管理员需审批通过后生效。"
                  type="info"
                  show-icon
                  class="mb-6"
                  :closable="false"
                />
                
                <div class="text-center py-6">
                  <el-button type="primary" size="large" @click="goToCreateTenant" class="w-full max-w-[200px]">
                    开始创建组织
                  </el-button>
                </div>
              </template>
            </div>
          </el-tab-pane>

          <el-tab-pane label="通过邀请码加入" name="join">
            <div class="pt-4 pb-4 px-2">
              <p class="text-sm text-gray-500 mb-4">如果您收到了组织的邀请码，可以在下方输入并加入组织。</p>
              <el-form :model="joinForm" label-position="top">
                <el-form-item label="邀请码">
                  <el-input 
                    v-model="joinForm.inviteCode" 
                    placeholder="请输入 8 位邀请码" 
                    size="large"
                    maxlength="8"
                    show-word-limit
                  >
                    <template #prefix>
                      <Icon icon="ep:key" class="text-gray-400" />
                    </template>
                  </el-input>
                </el-form-item>
                <div class="mt-6">
                  <el-button 
                    type="primary" 
                    size="large" 
                    @click="handleJoin" 
                    :loading="joining" 
                    class="w-full h-12"
                    :disabled="!joinForm.inviteCode"
                  >
                    确认加入
                  </el-button>
                </div>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { setTenantId } from '@/utils/auth'
import { deleteUserCache } from '@/hooks/web/useCache'
import { ElMessage } from 'element-plus'
import * as TenantInviteApi from '@/api/system/tenant/invite'
import * as TenantApplyApi from '@/api/system/tenantApply'

defineOptions({ name: 'TenantSelect' })

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref('select')
const loading = ref(false)
const tenants = ref<any[]>([])

const joining = ref(false)
const joinForm = reactive({
  inviteCode: ''
})

const pendingApply = ref<any>(null)

const fetchTenants = async () => {
  loading.value = true
  try {
    // 重新获取租户列表，防止过期
    const { getUserTenants } = await import('@/api/login')
    const list = await getUserTenants()
    tenants.value = list || []
    userStore.tenants = list || []
  } catch (error) {
    console.error('获取租户列表失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  fetchTenants()
  // 检查 URL 中是否有邀请码
  if (route.query.inviteCode) {
    joinForm.inviteCode = route.query.inviteCode as string
    activeTab.value = 'join'
  }
  // 检查是否需要激活“创建组织” Tab
  if (route.query.type === 'create') {
    activeTab.value = 'create'
  }
  // 获取申请状态
  try {
    const applyList = await TenantApplyApi.getMyTenantApplyList()
    if (applyList && applyList.length > 0) {
      // 找到待审批的记录
      pendingApply.value = applyList.find((a: any) => a.status === 0)
    }
  } catch (e) {
    console.error('获取申请列表失败', e)
  }
})

const handleJoin = async () => {
  if (!joinForm.inviteCode) return
  joining.value = true
  try {
    await TenantInviteApi.acceptTenantInvite(joinForm.inviteCode)
    ElMessage.success('成功加入组织')
    // 加入成功后，引导用户回到选择列表
    activeTab.value = 'select'
    await fetchTenants()
  } catch (error) {
    console.error('加入组织失败', error)
  } finally {
    joining.value = false
  }
}

const enterTenant = async (id: number) => {
  try {
    setTenantId(id)
    ElMessage.success('成功进入组织')
    
    // 清空现有路由权限缓存，让 permission.ts 重新拉取该租户下的路由和信息
    deleteUserCache()
    userStore.resetState()
    userStore.isSetUser = false
    
    // 强制跳转主页从而经过拦截器
    const redirect = route.query.redirect as string || '/'
    window.location.href = redirect
  } catch (err) {
    ElMessage.error('进入失败，请重试')
  }
}

const handleLogout = async () => {
  await userStore.loginOut()
  router.push('/login')
}

const goToProfile = () => {
  router.push({name: 'UserProfile'})
}

const goToCreateTenant = () => {
  // 可以设计独立页面或者弹窗
  router.push('/tenant/create')
}
</script>

<style scoped>
:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
}
</style>
