<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="店铺" prop="shopId">
        <el-cascader
          v-model="selectedShopPath"
          :options="shopCascaderList"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="请选择店铺"
          clearable
          class="!w-240px"
          @change="handleShopChange"
        />
      </el-form-item>
      <el-form-item label="商品" prop="sku">
        <el-input
          v-model="queryParams.sku"
          placeholder="请输入 SKU/ASIN"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option label="运行中" value="RUNNING" />
          <el-option label="已暂停" value="PAUSED" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('2024-01-01 00:00:00'), new Date('2024-01-01 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 查询
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button
          type="primary"
          plain
          @click="handleCreate"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 创建运营计划
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="商品信息" align="left" min-width="280">
        <template #default="scope">
          <div class="flex items-center gap-10px">
            <el-image 
              v-if="scope.row.productImage" 
              :src="scope.row.productImage" 
              class="w-40px h-40px rounded flex-shrink-0 border" 
              fit="cover" 
            />
            <div class="flex flex-col overflow-hidden">
              <div v-if="scope.row.productTitle" class="text-13px font-bold truncate mb-2px" :title="scope.row.productTitle">
                {{ scope.row.productTitle }}
              </div>
              <div class="flex gap-5px">
                <el-tag size="small" type="info" effect="plain">{{ scope.row.sku }}</el-tag>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="运营计划名称" align="left" prop="name" min-width="150">
        <template #default="scope">
          <div class="font-bold text-gray-700">{{ scope.row.name }}</div>
        </template>
      </el-table-column>
      <el-table-column label="所属店铺" align="center" prop="shopName" width="150" />
      <el-table-column label="平台" align="center" prop="platform" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'RUNNING' ? 'success' : 'info'" size="small">
            {{ scope.row.status === 'RUNNING' ? '运行中' : '已暂停' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最近执行" align="center" prop="lastRunAt" width="180">
        <template #default="scope">
          <span>{{ scope.row.lastRunAt ? formatDate(scope.row.lastRunAt) : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleToggleStatus(scope.row)"
          >
            {{ scope.row.status === 'RUNNING' ? '暂停' : '启动' }}
          </el-button>
          <el-button
            link
            type="primary"
            @click="handleViewLog(scope.row)"
          >
            运行日志
          </el-button>
          <el-dropdown class="ml-10px">
            <el-button link type="primary">更多<Icon icon="ep:arrow-down" class="ml-5px" /></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleInit(scope.row)">初始化结构</el-dropdown-item>
                <el-dropdown-item @click="handleExecute(scope.row)">立即检查</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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

  <!-- 日志弹窗 -->
  <AutomationLogDialog ref="logDialogRef" />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { AutomationApi } from '@/app/erplus/api/adv/automation'
import { ShopApi } from '@/app/erplus/api/system/shop'
import { formatDate } from '@/utils/formatTime'
import AutomationLogDialog from './components/AutomationLogDialog.vue'

defineOptions({ name: 'AdsAutomationPlanManager' })

const router = useRouter()
const message = useMessage()
const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryFormRef = ref()
const logDialogRef = ref()

const shopCascaderList = ref<any[]>([])
const selectedShopPath = ref<any[]>([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  shopId: undefined,
  sku: '',
  status: undefined,
  createTime: []
})

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AutomationApi.getPlanPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  selectedShopPath.value = []
  queryParams.shopId = undefined
  handleQuery()
}

/** 店铺变更 */
const handleShopChange = (value: any[]) => {
  if (value && value.length === 2) {
    queryParams.shopId = value[1]
  } else {
    queryParams.shopId = undefined
  }
}

/** 创建计划 */
const handleCreate = () => {
  router.push({ name: 'CreatePlanByTemplate' })
}

/** 切换状态 */
const handleToggleStatus = async (row: any) => {
  const newStatus = row.status === 'RUNNING' ? 'PAUSED' : 'RUNNING'
  const text = newStatus === 'RUNNING' ? '启动' : '暂停'
  try {
    await message.confirm(`确认要${text}计划 "${row.name}" 吗？`)
    await AutomationApi.updatePlanStatus(row.id, newStatus)
    message.success(`${text}成功`)
    getList()
  } catch {}
}

/** 查看日志 */
const handleViewLog = (row: any) => {
  logDialogRef.value.open(row.id, row.name)
}

/** 初始化结构 */
const handleInit = async (row: any) => {
  try {
    await message.confirm(`确认要初始化计划 "${row.name}" 的广告结构吗？`)
    await AutomationApi.initStructure(row.id)
    message.success('初始化成功')
    getList()
  } catch {}
}

/** 立即执行 */
const handleExecute = async (row: any) => {
  try {
    await message.confirm(`确认要立即执行计划 "${row.name}" 的自动化检查吗？`)
    await AutomationApi.executeCycle(row.id)
    message.success('已触发检查')
    getList()
  } catch {}
}

/** 初始化 **/
onMounted(async () => {
  shopCascaderList.value = await ShopApi.getCascaderShopList()
  getList()
})
</script>
