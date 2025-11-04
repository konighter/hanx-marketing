<script lang="ts" setup>
import { Icon } from '@/components/Icon'
import { useDesign } from '@/hooks/web/useDesign'
import {getTenantId, getTenantName, getUserTenant, setTenantId} from "@/utils/auth";
import {useUserStore} from "@/store/modules/user";

defineOptions({ name: 'TenantSelect' })

const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('tenant-dropdown')

const userStore = useUserStore()

const loginTenant = computed(() => getTenantId())

console.log(loginTenant.value)

const userTenants = getUserTenant()

const currentTenant = ref()

onMounted(() => {
  currentTenant.value = loginTenant.value
}
)


const toggleTenant = () => {
  setTenantId(currentTenant.value)
  userStore.resetTenant()
  // 刷新页面
  location.reload()
}
</script>

<template>
  <div v-if="userTenants.length > 1" class="custom-hover" @click.stop="showTopSearch = !showTopSearch">
    <el-select
      v-model="currentTenant"
      class="overflow-hidden transition-all-600 w-220px ml2"
      @change="toggleTenant"
    >
      <el-option
        v-for="item in userTenants"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>
  </div>

</template>
