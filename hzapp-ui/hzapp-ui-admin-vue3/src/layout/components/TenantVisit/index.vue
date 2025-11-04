<template>
  <div>
    <el-select
      filterable
      placeholder="请选择租户"
      class="!w-180px"
      v-model="value"
      @change="handleChange"
      clearable
      v-if="tenants.length > 1"
    >
      <el-option v-for="item in tenants" :key="item.id" :label="item.name" :value="item.id" />
    </el-select>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
// import * as TenantApi from '@/api/system/tenant'
import * as LoginApi from '@/api/login/index'
import { getTenantId, setTenantId } from '@/utils/auth'
import { useMessage } from '@/hooks/web/useMessage'
import { useTagsView } from '@/hooks/web/useTagsView'
import { useUserStore } from '@/store/modules/user'
import { deleteUserCache } from '@/hooks/web/useCache'


const message = useMessage() // 消息弹窗
const tagsView = useTagsView() // 标签页操作

const value = ref(getTenantId()) // 当前选中的租户 ID
const tenants = ref<any[]>([]) // 租户列表

const { push } = useRouter()

const { setUserInfoAction } = useUserStore()


const handleChange = (id: number) => {
  // 设置访问租户 ID
  setTenantId(id)
  // 关闭其他标签页，只保留当前页
  tagsView.closeOther()
  // 刷新当前页面
  tagsView.refreshPage()

  deleteUserCache()
  // 重置用户信息
  setUserInfoAction()
  // 提示切换成功
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
