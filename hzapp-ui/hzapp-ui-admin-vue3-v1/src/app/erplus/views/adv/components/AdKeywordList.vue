<template>
  <ContentWrap>
    <el-table
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
      
      <!-- 固定列：关键词和状态 -->
      <el-table-column align="left" min-width="250" fixed="left">
        <template #header>
          <div class="flex items-center">
            <span>关键词</span>
            <el-popover placement="bottom" :width="200" trigger="click">
              <template #reference>
                <el-button link class="ml-5px" :type="queryParams.keywordText ? 'primary' : 'default'">
                  <Icon icon="ep:search" />
                </el-button>
              </template>
              <div class="p-2">
                <el-input
                  v-model="queryParams.keywordText"
                  placeholder="搜索关键词"
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
              <span class="truncate">{{ scope.row.keywordText }}</span>
            </el-link>
            <div class="flex items-center text-12px text-gray-400 mt-2px">
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
          <div class="flex items-center justify-center group">
            <template v-if="bidEditId === scope.row.id">
              <el-input-number
                v-model="bidEditValue"
                :precision="3"
                :step="0.01"
                size="small"
                controls-position="right"
                class="!w-90px"
              />
              <el-button link type="primary" class="ml-5px" @click="handleSaveBid(scope.row)">
                <Icon icon="ep:check" />
              </el-button>
              <el-button link @click="bidEditId = undefined">
                <Icon icon="ep:close" />
              </el-button>
            </template>
            <template v-else>
              <span class="text-13px">{{ scope.row.bid ? `$${scope.row.bid}` : '-' }}</span>
              <el-button
                link
                type="primary"
                class="ml-5px invisible group-hover:visible"
                @click="handleEditBid(scope.row)"
              >
                <Icon icon="ep:edit" />
              </el-button>
            </template>
          </div>
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

      <!-- 固有属性 -->
      <el-table-column label="匹配类型" align="center" prop="matchType" width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AD_MATCH_TYPE" :value="scope.row.matchType" />
        </template>
      </el-table-column>

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
import { AdsKeywordApi } from '@/app/erplus/api/adv/ads'
import { AdsKeyword } from '../types/ads'

defineOptions({ name: 'AdKeywordList' })

const props = defineProps<{
  accountId?: number
  campaignIds?: number[]
  adGroupIds?: number[]
  metricColumns: any[]
}>()

const emit = defineEmits<{
  select: [selection: any[]]
}>()

const loading = ref(true)
const list = ref<AdsKeyword[]>([])
const total = ref(0)
const tableRef = ref()

const bidEditId = ref<number>()
const bidEditValue = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: props.accountId,
  campaignIds: props.campaignIds && props.campaignIds.length > 0 ? props.campaignIds : undefined,
  adGroupIds: props.adGroupIds && props.adGroupIds.length > 0 ? props.adGroupIds : undefined,
  keywordText: undefined,
  status: undefined
})

const visibleMetricColumns = computed(() => props.metricColumns.filter(c => c.visible))

const handleSelectionChange = (selection: any[]) => {
  emit('select', selection)
}

const handleNameClick = (row: AdsKeyword) => {
  // 同步选中状态
  tableRef.value.clearSelection()
  tableRef.value.toggleRowSelection(row, true)
}

const handleEditBid = (row: AdsKeyword) => {
  bidEditId.value = row.id
  bidEditValue.value = row.bid || 0
}

const handleSaveBid = async (row: AdsKeyword) => {
  try {
    await AdsKeywordApi.updateKeywordBid({ id: row.id, bid: bidEditValue.value })
    ElMessage.success('更新出价成功')
    bidEditId.value = undefined
    await getList()
  } catch (error) {}
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
    const data = await AdsKeywordApi.getKeywordPage({
      ...queryParams,
      accountId: props.accountId,
      campaignIds: props.campaignIds && props.campaignIds.length > 0 ? props.campaignIds : undefined,
      adGroupIds: props.adGroupIds && props.adGroupIds.length > 0 ? props.adGroupIds : undefined
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleUpdateStatus = async (row: AdsKeyword) => {
  const newStatus = row.status === 'ENABLED' ? 'PAUSED' : 'ENABLED'
  try {
    await AdsKeywordApi.updateKeywordStatus({ id: row.id, status: newStatus })
    ElMessage.success('操作成功')
    await getList()
  } catch (error) {}
}

watch(() => [props.accountId, props.campaignIds, props.adGroupIds], () => {
  queryParams.pageNo = 1
  getList()
}, { deep: true })

onMounted(() => {
  getList()
})
</script>
