<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="流程名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" class="!w-240px" />
      </el-form-item>
      <el-form-item label="流程标识" prop="key">
        <el-input v-model="queryParams.key" placeholder="请输入流程标识" clearable @keyup.enter="handleQuery" class="!w-240px" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 查询
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="primary" plain @click="openDeployForm">
          <Icon icon="ep:upload" class="mr-5px" /> 部署
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="流程标识" align="center" prop="key" min-width="120" />
      <el-table-column label="流程名称" align="center" prop="name" min-width="150" />
      <el-table-column label="版本" align="center" prop="version" width="80">
        <template #default="scope">
          <el-tag>v{{ scope.row.version }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="suspensionState" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.suspensionState === 1 ? 'success' : 'warning'">
            {{ scope.row.suspensionState === 1 ? '激活' : '暂停' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="部署ID" align="center" prop="deploymentId" width="300" />
      <el-table-column label="部署时间" align="center" prop="deployTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="操作" align="center" width="300" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleLoad(scope.row)">
            <Icon icon="ep:view" class="mr-3px" /> 加载
          </el-button>
          
          <el-button 
            v-if="scope.row.suspensionState === 1" 
            link 
            type="warning" 
            @click="handleSuspend(scope.row)"
          >
            <Icon icon="ep:video-pause" class="mr-3px" /> 暂停
          </el-button>
          <el-button 
            v-else 
            link 
            type="success" 
            @click="handleActivate(scope.row)"
          >
            <Icon icon="ep:video-play" class="mr-3px" /> 恢复
          </el-button>
          
          <el-button link type="danger" @click="handleDelete(scope.row)">
            <Icon icon="ep:delete" class="mr-3px" /> 移除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </ContentWrap>

  <!-- 加载/预览弹窗 -->
  <Dialog :title="'加载流程'" v-model="xmlDialogVisible" width="800px">
    <el-input type="textarea" :rows="20" v-model="xmlContent" readonly />
  </Dialog>

  <!-- 部署表单弹窗 -->
  <WorkflowDeployForm ref="deployFormRef" @success="getList" />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { WorkflowApi } from '@/app/system/api/workflow'
import WorkflowDeployForm from './WorkflowDeployForm.vue'
import { useMessage } from '@/hooks/web/useMessage'

defineOptions({ name: 'WorkflowProcess' })

const message = useMessage()

const loading = ref(true)
const list = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  key: undefined
})
const queryFormRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WorkflowApi.getPage(queryParams)
    list.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    // 错误在请求体拦截处理
  } finally {
    loading.value = false
  }
}

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置 */
const resetQuery = () => {
  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  handleQuery()
}

/** 部署弹窗 */
const deployFormRef = ref()
const openDeployForm = () => {
  deployFormRef.value.open()
}

/** 暂停 */
const handleSuspend = async (row: any) => {
  try {
    await message.confirm(`确认暂停流程定义【${row.name || row.key}】?`)
    await WorkflowApi.suspend(row.id)
    message.success('流程暂停成功')
    await getList()
  } catch {}
}

/** 恢复 */
const handleActivate = async (row: any) => {
  try {
    await message.confirm(`确认恢复流程定义【${row.name || row.key}】?`)
    await WorkflowApi.activate(row.id)
    message.success('流程恢复成功')
    await getList()
  } catch {}
}

/** 移除 */
const handleDelete = async (row: any) => {
  try {
    await message.confirm(`确认移除流程部署【${row.name || row.key}】及其所有实例吗?`)
    await WorkflowApi.delete(row.deploymentId)
    message.success('移除成功')
    await getList()
  } catch {}
}

/** 加载 */
const xmlDialogVisible = ref(false)
const xmlContent = ref('')

const handleLoad = async (row: any) => {
  try {
    const res = await WorkflowApi.load(row.id)
    xmlContent.value = typeof res === 'string' ? res : JSON.stringify(res, null, 2)
    xmlDialogVisible.value = true
    message.success('流程已加载')
  } catch {}
}

onMounted(() => {
  getList()
})
</script>
