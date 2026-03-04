<template>
  <el-drawer v-model="drawerVisible" title="亚马逊站点列表 (Profiles)" size="800px">
    <div class="mb-15px flex justify-between">
      <div class="text-16px font-bold">账号ID: {{ accountId }}</div>
      <el-button type="primary" :loading="initLoading" @click="handleInitStream">
        <Icon icon="ep:refresh" class="mr-5px" /> 初始化 Stream 订阅
      </el-button>
    </div>
    <el-table :data="list" v-loading="loading">
      <el-table-column label="Profile ID" align="center" prop="profileId" width="180px" />
      <el-table-column label="国家" align="center" prop="countryCode" width="80px" />
      <el-table-column label="区域" align="center" prop="region" width="80px" />
      <el-table-column label="币种" align="center" prop="currencyCode" width="80px" />
      <el-table-column label="时区" align="center" prop="timezone" />
    </el-table>
  </el-drawer>
</template>

<script setup lang="ts">
import { AdsAuthApi } from '@/app/erplus/api/adv/ads'
import { AmazonProfile } from '../../types/ads'

defineOptions({ name: 'AmazonProfileDrawer' })

const props = defineProps<{
  modelValue?: boolean
}>()

const emit = defineEmits(['update:modelValue'])

const message = useMessage()
const loading = ref(false)
const initLoading = ref(false)
const list = ref<AmazonProfile[]>([])
const drawerVisible = ref(false)
const accountId = ref<number>()


const getList = async () => {
  if (!accountId.value) return
  loading.value = true
  try {
    list.value = await AdsAuthApi.getAmazonProfileList(accountId.value)
  } finally {
    loading.value = false
  }
}

/** 初始化 Stream 订阅 */
const handleInitStream = async () => {
  if (!accountId.value) return
  try {
    await message.confirm('确认初始化该账号下所有站点的 Amazon Marketing Stream 订阅吗？')
    initLoading.value = true
    await AdsAuthApi.initStream(accountId.value)

    message.success('初始化指令已发送，订阅正在创建中...')
  } catch (error) {
    // 处理取消确认或 API 错误
  } finally {
    initLoading.value = false
  }
}

/** 暴露 open 方法 */
const open = async (id: number) => {
  accountId.value = id
  drawerVisible.value = true
}

defineExpose({ open })


watch(
  () => drawerVisible.value,
  (val) => {
    if (val && accountId.value) {
      getList()
    }
  }
)

watch(
  () => accountId.value,
  (val) => {
    if (drawerVisible.value && val) {
      getList()
    }
  }
)
</script>
