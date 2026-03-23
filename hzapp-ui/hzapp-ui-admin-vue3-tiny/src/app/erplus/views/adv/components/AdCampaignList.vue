<template>
  <!-- 列表 -->
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
            <div class="flex items-center text-12px text-gray-400 mt-2px">
              <span class="truncate">ID: {{ scope.row.externalId }}</span>
              <el-button
                link
                type="primary"
                class="ml-5px !p-0 h-auto"
                @click="handleDetail(scope.row)"
              >
                <Icon icon="ep:document" />
              </el-button>
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
      <el-table-column label="预算" align="center" width="150" fixed="left">
        <template #default="scope">
          <div class="flex items-center justify-center group">
            <template v-if="budgetEditId === scope.row.id">
              <el-input-number
                v-model="budgetEditValue"
                :precision="2"
                :step="1"
                size="small"
                controls-position="right"
                class="!w-90px"
              />
              <el-button link type="primary" class="ml-5px" @click="handleSaveBudget(scope.row)">
                <Icon icon="ep:check" />
              </el-button>
              <el-button link @click="budgetEditId = undefined">
                <Icon icon="ep:close" />
              </el-button>
            </template>
            <template v-else>
              <span class="text-13px">
                {{ getBudgetTypeLabel(scope.row.budgetType) }} / {{ scope.row.dailyBudget || scope.row.totalBudget ? `$${scope.row.dailyBudget || scope.row.totalBudget}` : '-' }}
              </span>
              <el-button
                link
                type="primary"
                class="ml-5px invisible group-hover:visible"
                @click="handleEditBudget(scope.row)"
              >
                <Icon icon="ep:edit" />
              </el-button>
            </template>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="投放时间" align="center" width="200">
        <template #default="scope">
          <div class="text-12px">
            <span>{{ formatPast(scope.row.startDate, 'YYYY-MM-DD') }}</span>
            
            <span v-if=scope.row.endDate>~
            <span class="text-gray-400">{{ scope.row.endDate ? formatPast(scope.row.endDate, 'YYYY-MM-dd') : '-' }}</span>
            </span>
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
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详情抽屉 -->
    <AdCampaignDetailDrawer ref="detailDrawerRef" @success="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { formatPast } from '@/utils/formatTime'
import { DICT_TYPE, ad_status } from '@/app/erplus/common/dict'
import { AdsCampaignApi } from '@/app/erplus/api/adv/ads'
import { AdsCampaign } from '../types/ads'
import AdCampaignDetailDrawer from './AdCampaignDetailDrawer.vue'

defineOptions({ name: 'AdCampaignList' })

const props = defineProps<{
  accountId?: number
  metricColumns: any[]
}>()

const emit = defineEmits<{
  select: [selection: AdsCampaign[]]
  'click-name': [row: AdsCampaign]
}>()

const loading = ref(true)
const list = ref<AdsCampaign[]>([])
const total = ref(0)
const tableRef = ref()
const detailDrawerRef = ref()

const budgetEditId = ref<number>()
const budgetEditValue = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: props.accountId,
  name: undefined,
  status: undefined
})

const visibleMetricColumns = computed(() => props.metricColumns.filter(c => c.visible))

const getBudgetTypeLabel = (type?: string) => {
  switch (type) {
    case 'DAILY': return '日'
    case 'LIFETIME': return '期'
    case 'CAMPAIGN_TOTAL': return '总'
    default: return '日'
  }
}

const handleSelectionChange = (selection: AdsCampaign[]) => {
  emit('select', selection)
}

const handleNameClick = (row: AdsCampaign) => {
  // 同步选中状态
  tableRef.value.clearSelection()
  tableRef.value.toggleRowSelection(row, true)
  emit('click-name', row)
}

const handleDetail = (row: AdsCampaign) => {
  detailDrawerRef.value.open(row.id)
}

const handleEditBudget = (row: AdsCampaign) => {
  budgetEditId.value = row.id
  budgetEditValue.value = row.dailyBudget || row.totalBudget || 0
}

const handleSaveBudget = async (row: AdsCampaign) => {
  try {
    await AdsCampaignApi.updateCampaignBudget({ id: row.id, budget: budgetEditValue.value })
    ElMessage.success('更新预算成功')
    budgetEditId.value = undefined
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