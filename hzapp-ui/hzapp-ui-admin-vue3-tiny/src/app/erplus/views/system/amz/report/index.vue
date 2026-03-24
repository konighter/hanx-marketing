<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">

      <!-- <el-form-item label="店铺" prop="shopIds">
        <el-select v-model="formData.shopIds" multiple placeholder="请选择店铺" class="w-100!">
          <el-option v-for="s in shops" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item> -->



      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['erplus:amz-report-task:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>

      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table row-key="id" v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column type="selection" width="55" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="报告ID" align="center" prop="reportId" />
      <el-table-column label="报告类型" align="center" prop="reportType">
        <template #default="{ row }">
          {{reportTypes.find(item => item.value === row.reportType)?.label || ''}}
        </template>
      </el-table-column>

      <el-table-column label="查询状态" align="center" prop="status">
        <template #default="{ row }">
          {{reportStatus.find(item => item.value === row.status)?.label || ''}}
        </template>

      </el-table-column>
      <el-table-column label="报告结果" align="center" prop="reportResult" />
      <el-table-column label="上次检索时间" align="center" prop="lastCheckTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="结果是否已归档" align="center" prop="isArchive" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="结果是否已归档" align="center" prop="isArchive">
        <template #default="{ row }">
          <el-button link type="primary" @click="checkStatus(row.id)" v-hasPermi="['ov:shop:update']">
            检查状态
          </el-button>
        </template>


      </el-table-column>


    </el-table>
    <!-- 分页 -->
    <Pagination
:total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <AmzReportTaskForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">

import { dateFormatter } from '@/utils/formatTime'
import { AmzReportTaskApi, AmzReportTask, periods, reportTypes, reportStatus } from '@/app/erplus/api/system/report'
import AmzReportTaskForm from './AmzReportTaskForm.vue'

/** 亚马逊报告任务 列表 */
defineOptions({ name: 'AmzReportTask' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<AmzReportTask[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  query: undefined,
  status: undefined,
  reportResult: undefined,
  lastCheckTime: [],
  isArchive: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AmzReportTaskApi.getAmzReportTaskPage(queryParams)
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
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 检查状态 */
const checkStatus = async (id: number) => {
  loading.value = true
  try {
    await AmzReportTaskApi.checkAmzReportTaskStatus(id)
    message.success('操作成功, 请稍后刷新查看最新状态')
    await getList()
  } finally {
    loading.value = false
  }
}



/** 初始化 **/
onMounted(() => {
  getList()
})
</script>