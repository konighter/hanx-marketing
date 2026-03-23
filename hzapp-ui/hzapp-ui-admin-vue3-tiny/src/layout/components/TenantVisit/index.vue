<template>
  <el-dropdown class="custom-hover" @command="handleCommand">
    <div class="flex items-center px-2 cursor-pointer">
      <Icon icon="ep:office-building" class="mr-1" />
      <span v-if="currentTenantName" class="text-14px">{{ currentTenantName }}</span>
      <span v-else class="text-14px">未选择组织</span>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item 
          v-for="item in tenants" 
          :key="item.id" 
          :command="item.id" 
          :disabled="item.id === value"
        >
          {{ item.name }}
        </el-dropdown-item>
        <el-dropdown-item divided command="manage">
          <Icon icon="ep:setting" class="mr-1" /> 组织管理中心
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import * as LoginApi from '@/api/login/index'
import { getTenantId, setTenantId } from '@/utils/auth'
import { useMessage } from '@/hooks/web/useMessage'
import { useTagsView } from '@/hooks/web/useTagsView'
import { useUserStore } from '@/store/modules/user'
import { deleteUserCache } from '@/hooks/web/useCache'
import { useRouter } from 'vue-router'

const message = useMessage()
const tagsView = useTagsView()
const userStore = useUserStore()
const router = useRouter()

const value = ref(getTenantId())
const tenants = ref<any[]>([])

const currentTenantName = computed(() => {
  const tenant = tenants.value.find(t => t.id === value.value)
  return tenant ? tenant.name : ''
})

const handleCommand = (command: number | string) => {
  if (command === 'manage') {
    router.push('/tenant/select')
    return
  }
  
  const id = command as number
  if (id === value.value) return

  setTenantId(id)
  tagsView.closeOther()
  tagsView.refreshPage()
  deleteUserCache()
  userStore.setUserInfoAction()
  
  const tenant = tenants.value.find((item) => item.id === id)
  if (tenant) {
    message.success(`切换当前租户为: ${tenant.name}`)
  }

  location.reload()
}

onMounted(async () => {
  tenants.value = await LoginApi.getUserTenants()
})
</script>

<style scoped>
.custom-hover {
  display: flex;
  height: 100%;
  padding: 0 10px;
  cursor: pointer;
  align-items: center;
  transition: background-color 0.3s;
}
.custom-hover:hover {
  background-color: var(--top-header-hover-color);
}
</style>
