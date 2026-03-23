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
      <el-table-column label="操作" align="center" width="450" fixed="right">
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

          <el-button link type="primary" @click="handleExecute(scope.row)">
            <Icon icon="ep:caret-right" class="mr-3px" /> 执行
          </el-button>

          <el-button link type="success" @click="handleViewInstances(scope.row)">
            <Icon icon="ep:list" class="mr-3px" /> 实例
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

  <!-- 手动执行弹窗 -->
  <Dialog title="手动执行流程" v-model="executeDialogVisible" width="600px">
    <el-form label-width="100px">
      <el-form-item label="流程标识">
        <el-input v-model="executeForm.key" disabled />
      </el-form-item>
      <el-form-item label="流程变量 (JSON)">
        <el-input 
          type="textarea" 
          v-model="executeForm.variables" 
          :rows="10" 
          placeholder='例如：{"tenantId": 1, "skillCode": "ad-optimizer"}' 
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="executeDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="executeLoading" @click="handleStartProcess">启动</el-button>
    </template>
  </Dialog>

  <!-- 流程实例管理列表 (抽屉) -->
  <ProcessInstanceList ref="instanceListRef" />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { WorkflowApi } from '@/app/system/api/workflow'
import WorkflowDeployForm from './WorkflowDeployForm.vue'
import ProcessInstanceList from './ProcessInstanceList.vue'
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

/** 手动执行 */
const executeDialogVisible = ref(false)
const executeLoading = ref(false)
const executeForm = reactive({
  key: '',
  variables: '{}'
})

const handleExecute = (row: any) => {
  executeForm.key = row.key
  executeForm.variables = JSON.stringify({
    tenantId: 1,
    skillCode: row.key === 'skill-seq-tasks' ? 'ad-optimizer' : undefined,
    targetBizId: row.key === 'skill-seq-tasks' ? 'B00TEST001' : undefined,
    phaseConfigs: row.key === 'skill-seq-tasks' ? '[{"name":"数据收集","instruction":"收集广告数据","maxIterations":3,"interval":"P1D","tools":"[\"getAdCampaigns\",\"getAdMetrics\"]"}]' : undefined
  }, null, 2)
  executeDialogVisible.value = true
}

const handleStartProcess = async () => {
  try {
    let vars = {}
    if (executeForm.variables) {
      try {
        vars = JSON.parse(executeForm.variables)
      } catch (e) {
        message.error('流程变量 JSON 格式错误')
        return
      }
    }
    
    executeLoading.value = true
    const res = await WorkflowApi.startProcess(executeForm.key, vars)
    message.success('流程启动成功，实例ID: ' + res.processInstanceId)
    executeDialogVisible.value = false
  } catch (error) {
    console.error('Failed to start process:', error)
  } finally {
    executeLoading.value = false
  }
}

/** 实例管理 */
const instanceListRef = ref()
const handleViewInstances = (row: any) => {
  instanceListRef.value.open(row.key)
}

onMounted(() => {
  getList()
})
</script>
