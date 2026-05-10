<template>
  <div class="custom-hover mr-25px" @click="handleSubscribe">
    <el-badge value="Hot" type="danger" :offset="[15, 5]" class="badge-item">
      <div class="flex items-center px-2 cursor-pointer">
        <Icon icon="ep:promotion" class="mr-1" color="#ff4949" />
        <span class="text-14px font-bold text-[#ff4949]">套餐订阅</span>
      </div>
    </el-badge>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'

const { push } = useRouter()
const handleSubscribe = () => {
  push('/system/tenant/subscribe')
}
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
:deep(.badge-item .el-badge__content) {
  scale: 0.8;
}
</style>
