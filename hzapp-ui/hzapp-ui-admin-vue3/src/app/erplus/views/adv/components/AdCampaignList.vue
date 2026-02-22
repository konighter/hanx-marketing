<template>
  <!-- 列表 -->
  <ContentWrap>
    <el-table
        row-key="id"
        v-loading="loading"
        :data="list"
        :stripe="true"
        :show-overflow-tooltip="true"
        @selection-change="handleSelectionChange"
        style="width: 100%"
    >
      <el-table-column type="selection" width="55" reserve-selection />
      <el-table-column label="名称" align="center" prop="name" min-width="200" />
      <el-table-column label="外部ID" align="center" prop="externalId" width="150" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'warning'">
            {{ scope.row.status === 'ENABLED' ? '启用' : '暂停' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="每日预算" align="center" prop="dailyBudget" width="120">
        <template #default="scope">
          {{ scope.row.dailyBudget ? `$${scope.row.dailyBudget}` : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="同步时间" align="center" prop="syncedAt" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="150">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleUpdateStatus(scope.row)"
          >
            {{ scope.row.status === 'ENABLED' ? '暂停' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { AdsCampaignApi } from '@/app/erplus/api/adv/ads'
import { AdsCampaign } from '../types/ads'

defineOptions({ name: 'AdCampaignList' })

const props = defineProps<{
  accountId?: number
}>()

const emit = defineEmits<{
  select: [selection: any[]]
}>()

const loading = ref(true)
const list = ref<AdsCampaign[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: props.accountId
})

const handleSelectionChange = (selection: any[]) => {
  emit('select', selection)
}

const getList = async () => {
  if (!props.accountId) {
    list.value = []
    total.value = 0
    loading.value = false
    return
  }
  loading.value = true
  try {
    const data = await AdsCampaignApi.getCampaignPage({
      ...queryParams,
      accountId: props.accountId
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleUpdateStatus = async (row: AdsCampaign) => {
  const newStatus = row.status === 'ENABLED' ? 'PAUSED' : 'ENABLED'
  try {
    await AdsCampaignApi.updateCampaignStatus({ id: row.id, status: newStatus })
    ElMessage.success('操作成功')
    await getList()
  } catch (error) {}
}

watch(() => props.accountId, (val) => {
  queryParams.accountId = val
  queryParams.pageNo = 1
  getList()
})

onMounted(() => {
  getList()
})
</script>