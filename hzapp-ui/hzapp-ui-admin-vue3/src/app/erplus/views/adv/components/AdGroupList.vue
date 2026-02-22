<template>
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
      <el-table-column label="默认出价" align="center" prop="defaultBid" width="120">
        <template #default="scope">
          {{ scope.row.defaultBid ? `$${scope.row.defaultBid}` : '-' }}
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
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import { AdsAdGroup } from '../types/ads'

defineOptions({ name: 'AdGroupList' })

const props = defineProps<{
  accountId?: number
  campaignIds?: number[]
}>()

const emit = defineEmits<{
  select: [selection: any[]]
}>()

const loading = ref(true)
const list = ref<AdsAdGroup[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: props.accountId,
  campaignId: props.campaignIds && props.campaignIds.length > 0 ? props.campaignIds[0] : undefined
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
    const data = await AdsAdGroupApi.getAdGroupPage({
      ...queryParams,
      accountId: props.accountId,
      campaignId: props.campaignIds && props.campaignIds.length > 0 ? props.campaignIds[0] : undefined
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleUpdateStatus = async (row: AdsAdGroup) => {
  const newStatus = row.status === 'ENABLED' ? 'PAUSED' : 'ENABLED'
  try {
    await AdsAdGroupApi.updateAdGroupStatus({ id: row.id, status: newStatus })
    ElMessage.success('操作成功')
    await getList()
  } catch (error) {}
}

watch(() => [props.accountId, props.campaignIds], () => {
  queryParams.pageNo = 1
  getList()
}, { deep: true })

onMounted(() => {
  getList()
})
</script>