<template>
  <el-drawer
    v-model="visible"
    :title="'[' + processKey + '] 运行实例管理'"
    size="60%"
    destroy-on-close
  >
    <div v-loading="loading" class="p-20px">
      <el-table :data="list" stripe border @row-click="handleRowClick">
        <el-table-column label="实例ID" align="center" prop="processInstanceId" min-width="120" />
        <el-table-column label="业务标识" align="center" prop="businessKey" min-width="120" />
        <el-table-column label="启动时间" align="center" prop="startTime" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.suspended ? 'warning' : 'success'">
              {{ scope.row.suspended ? '已挂起' : '运行中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="260" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click.stop="handleViewVariables(scope.row)">
              变量
            </el-button>
            <el-button link type="warning" @click.stop="handleViewTimers(scope.row)">
              定时器
            </el-button>
            <el-button link type="danger" @click.stop="handleTerminate(scope.row)">
              终止
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 变量查看对话框 -->
    <el-dialog title="运行时变量" v-model="varVisible" width="600px" append-to-body>
      <pre class="bg-gray-100 p-10px rounded">{{ JSON.stringify(currentVars, null, 2) }}</pre>
    </el-dialog>

    <!-- 定时器管理对话框 -->
    <el-dialog title="待执行定时器 (Timer Jobs)" v-model="timerVisible" width="700px" append-to-body>
      <el-table :data="timerList" border>
        <el-table-column label="Job ID" prop="jobId" width="100" />
        <el-table-column label="节点名称" prop="elementName" min-width="120" />
        <el-table-column label="过期时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleTriggerTimer(scope.row)">
              立即触发
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="timerList.length === 0" class="text-center py-20px text-gray-400">
        暂无等待中的定时器
      </div>
    </el-dialog>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { WorkflowApi } from '@/app/system/api/workflow'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'

const visible = ref(false)
const loading = ref(false)
const processKey = ref('')
const list = ref<any[]>([])

/** 打开抽屉 */
const open = async (key: string) => {
  processKey.value = key
  visible.value = true
  await getList()
}

defineExpose({ open })

const getList = async () => {
  loading.value = true
  try {
    const res = await WorkflowApi.listInstances({ processKey: processKey.value })
    list.value = res || []
  } catch (error) {
    console.error('Failed to list instances', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date: any) => {
  if (!date) return '--'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

/** 变量查看 */
const varVisible = ref(false)
const currentVars = ref({})
const handleViewVariables = async (row: any) => {
  try {
    const res = await WorkflowApi.getInstanceVariables(row.processInstanceId)
    currentVars.value = res || {}
    varVisible.value = true
  } catch (error) {
    ElMessage.error('获取变量失败')
  }
}

/** 定时器管理 */
const timerVisible = ref(false)
const timerList = ref<any[]>([])
const selectedInstanceId = ref('')

const handleViewTimers = async (row: any) => {
  selectedInstanceId.value = row.processInstanceId
  try {
    const res = await WorkflowApi.listTimers(row.processInstanceId)
    timerList.value = res || []
    timerVisible.value = true
  } catch (error) {
    ElMessage.error('获取定时器失败')
  }
}

const handleTriggerTimer = async (timer: any) => {
  try {
    await ElMessageBox.confirm('确认立即触发此任务，跳过等待时间？', '确认操作')
    await WorkflowApi.triggerTimer(selectedInstanceId.value, timer.jobId)
    ElMessage.success('触发成功')
    // 延迟刷新
    setTimeout(async () => {
      const res = await WorkflowApi.listTimers(selectedInstanceId.value)
      timerList.value = res || []
    }, 1000)
  } catch {}
}

/** 终止实例 */
const handleTerminate = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认强制终止此流程实例？', '警示', { type: 'error' })
    await WorkflowApi.deleteInstance(row.processInstanceId, '手动终止')
    ElMessage.success('已终止')
    getList()
  } catch {}
}

const handleRowClick = () => {
  // 可以根据需要扩展
}
</script>

<style scoped>
pre {
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>
