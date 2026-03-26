<template>
  <ContentWrap>
    <el-table
        ref="tableRef"
        row-key="id"
        v-loading="loading"
        :data="list"
        :stripe="true"
        :show-overflow-tooltip="true"
        @selection-change="handleSelectionChange"
        @filter-change="handleFilterChange"
        style="width: 100%"
    >
      <el-table-column type="selection" width="55" reserve-selection fixed="left" />
      
      <!-- 固定列：名称和状态 -->
      <el-table-column align="left" min-width="250" fixed="left">
        <template #header>
          <div class="flex items-center">
            <span>名称</span>
            <el-popover placement="bottom" :width="200" trigger="click">
              <template #reference>
                <el-button link class="ml-5px" :type="queryParams.name ? 'primary' : 'default'">
                  <Icon icon="ep:search" />
                </el-button>
              </template>
              <div class="p-2">
                <el-input
                  v-model="queryParams.name"
                  placeholder="搜索名称"
                  size="small"
                  clearable
                  @change="getList"
                />
              </div>
            </el-popover>
          </div>
        </template>
        <template #default="scope">
          <div class="flex flex-col max-w-400px">
            <el-link 
              type="primary" 
              :underline="false" 
              @click="handleNameClick(scope.row)" 
              class="!justify-start"
            >
              <span class="truncate">{{ scope.row.name }}</span>
            </el-link>
            <div class="flex items-center text-12px text-[var(--el-text-color-placeholder)] mt-2px">
              <span class="truncate">ID: {{ scope.row.externalId }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column 
        label="状态" 
        align="center" 
        width="100" 
        fixed="left"
        prop="status"
        :column-key="'status'"
        :filters="ad_status.map(s => ({ text: s.label, value: s.value }))"
      >
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AD_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="出价" align="center" width="120" fixed="left">
        <template #default="scope">
          <span class="text-13px">默认 / {{ scope.row.defaultBid ? `$${scope.row.defaultBid}` : '-' }}</span>
        </template>
      </el-table-column>

      <!-- 动态指标列 -->
      <template v-for="col in visibleMetricColumns" :key="col.prop">
        <el-table-column :prop="col.prop" align="center" :width="col.width" sortable="custom">
          <template #header>
            <el-tooltip :content="col.prop.toUpperCase()" placement="top">
              <span>{{ col.label.split('(')[0] }}</span>
            </el-tooltip>
          </template>
          <template #default="scope">
            <template v-if="col.isCurrency">
              {{ scope.row[col.prop] != null ? `$${scope.row[col.prop]}` : '-' }}
            </template>
            <template v-else-if="col.prop === 'roas'">
              {{ scope.row[col.prop] != null ? scope.row[col.prop].toFixed(2) : '-' }}
            </template>
            <template v-else>
              {{ scope.row[col.prop] != null ? scope.row[col.prop] : '-' }}
            </template>
          </template>
        </el-table-column>
      </template>

      <!-- 操作列 -->
      <el-table-column label="操作" align="center" fixed="right" width="80">
        <template #default="scope">
          <el-button
            link
            type="primary"
            :disabled="scope.row.status === 'ARCHIVED'"
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
import { DICT_TYPE, ad_status } from '@/app/erplus/common/dict'
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import { AdsAdGroup } from '../types/ads'

defineOptions({ name: 'AdGroupList' })

const props = defineProps<{
  accountId?: number
  campaignIds?: number[]
  metricColumns: any[]
}>()

const emit = defineEmits<{
  select: [selection: any[]]
  'click-name': [row: AdsAdGroup]
}>()

const loading = ref(true)
const list = ref<AdsAdGroup[]>([])
const total = ref(0)
const tableRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: props.accountId,
  campaignIds: props.campaignIds && props.campaignIds.length > 0 ? props.campaignIds : undefined,
  name: undefined,
  status: undefined
})

const visibleMetricColumns = computed(() => props.metricColumns.filter(c => c.visible))

const handleSelectionChange = (selection: any[]) => {
  emit('select', selection)
}

const handleNameClick = (row: AdsAdGroup) => {
  // 同步选中状态
  tableRef.value.clearSelection()
  tableRef.value.toggleRowSelection(row, true)
  emit('click-name', row)
}

const handleFilterChange = (filters: any) => {
  if (filters.status) {
    queryParams.status = filters.status[0]
  } else {
    queryParams.status = undefined
  }
  getList()
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
      campaignIds: props.campaignIds && props.campaignIds.length > 0 ? props.campaignIds : undefined
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