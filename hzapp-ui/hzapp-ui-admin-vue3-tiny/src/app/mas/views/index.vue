<template>
  <div class="mas-container h-full p-4 flex flex-col gap-4 bg-slate-50 dark:bg-[#0b0e14] overflow-hidden relative">
    <!-- Background Decor -->
    <div class="absolute inset-0 pointer-events-none overflow-hidden">
      <div class="absolute -top-[10%] -left-[10%] w-[40%] h-[40%] bg-indigo-500/10 blur-[120px] rounded-full"></div>
      <div class="absolute -bottom-[10%] -right-[10%] w-[30%] h-[30%] bg-blue-500/5 blur-[100px] rounded-full"></div>
    </div>

    <!-- Header / Navbar -->
    <div class="flex items-center justify-between px-2 z-10">
      <div class="flex items-center gap-3">
        <el-button circle class="!bg-white dark:!bg-slate-800 shadow-md" @click="historyDrawerRef?.open()">
          <Icon icon="solar:hamburger-menu-linear" />
        </el-button>
        <div class="flex flex-col">
          <h1 class="text-xl font-black tracking-tighter bg-gradient-to-r from-indigo-600 to-blue-500 bg-clip-text text-transparent m-0 uppercase">EntropyFlow <span class="text-slate-400 font-light ml-1">MAS</span></h1>
        </div>
      </div>
      
      <div v-if="activeSession" class="flex items-center gap-4 bg-white/50 dark:bg-slate-800/50 px-4 py-1.5 rounded-full border border-white/20 backdrop-blur-md shadow-sm">
        <div class="flex items-center gap-2">
          <div class="w-2 h-2 rounded-full" :class="statusClass(activeSession.status)"></div>
          <span class="text-xs font-bold text-slate-600 dark:text-slate-300 truncate max-w-[200px]">{{ activeSession.goal }}</span>
        </div>
        <el-divider direction="vertical" />
        <el-tag size="small" :type="statusType(activeSession.status)" effect="plain" class="font-bold border-none">{{ activeSession.status }}</el-tag>
      </div>

      <div class="flex items-center gap-2">
         <el-button 
           v-if="activeSession && !['COMPLETED', 'FAILED'].includes(activeSession.status)" 
           size="small" 
           type="danger" 
           plain 
           round 
           @click="handleStopSession"
           :loading="stopping"
         >
           <Icon icon="solar:stop-circle-linear" class="mr-1" />
           强制终止
         </el-button>
         <el-button size="small" plain round @click="handleNewSession">
           <Icon icon="solar:add-circle-linear" class="mr-1" />
           开启新任务
         </el-button>
      </div>
    </div>

    <!-- Main Content Area -->
    <div class="flex-1 min-h-0 relative z-10">
      <!-- Mode 1: New Session Initial State -->
      <div v-if="!activeSession" class="h-full flex items-center justify-center p-4">
        <div class="w-full max-w-2xl flex flex-col items-center text-center anim-fade-in">
          <div class="w-24 h-24 bg-gradient-to-br from-indigo-600 to-blue-500 rounded-[2rem] shadow-2xl flex items-center justify-center mb-8 rotate-3 hover:rotate-0 transition-transform duration-500">
            <Icon icon="solar:bolt-circle-bold-duotone" class="text-white" :size="48" />
          </div>
          <h2 class="text-3xl font-black text-slate-800 dark:text-white mb-2 tracking-tight">你好，我是您的多智能体助手</h2>
          <p class="text-slate-400 mb-10 max-w-md font-medium">只需输入您的目标，我们的专家智能体团队将协作为您完成任务。</p>
          
          <div class="w-full relative group">
            <div class="absolute -inset-1 bg-gradient-to-r from-indigo-500 to-blue-500 rounded-2xl blur opacity-25 group-focus-within:opacity-50 transition duration-500"></div>
            <div class="relative bg-white dark:bg-slate-800 rounded-2xl shadow-xl p-2 flex border border-white/20">
              <el-input
                v-model="newGoal"
                placeholder="例如：设计一个针对欧美市场的亚马逊新品推广计划..."
                class="main-goal-input"
                @keydown.enter="createSession"
              />
              <el-button 
                type="primary" 
                class="!h-14 !px-8 !rounded-xl !bg-indigo-600 hover:!bg-indigo-700 shadow-lg shadow-indigo-500/30 transition-all font-bold"
                @click="createSession"
                :loading="creating"
              >
                 GO
              </el-button>
            </div>
          </div>

          <div class="mt-12 grid grid-cols-3 gap-6 w-full opacity-60">
             <div v-for="role in ['Manager', 'PM', 'Executor']" :key="role" class="flex flex-col items-center gap-2">
                <div class="p-3 bg-slate-200 dark:bg-slate-700 rounded-xl">
                   <Icon :icon="roleIcon(role)" :size="20"/>
                </div>
                <span class="text-[10px] font-bold uppercase tracking-widest">{{ role }}</span>
             </div>
          </div>
        </div>
      </div>

      <!-- Mode 2: Active Session Split View -->
      <div v-else class="h-full flex gap-4 anim-fade-in">
        <!-- Left: Agent Process (New 3-layer layout) -->
        <div class="w-[55%] h-full flex flex-col gap-4 min-w-0">
          <MasManagerFlow 
            :session="activeSession" 
            :history="taskHistory" 
          />
          
          <div class="flex-1 min-h-0 relative">
             <MasAgentCardContainer :cards="agentCards" />
          </div>

          <MasArchivedTasks :tasks="archivedTasks" />
        </div>

        <!-- Right: Chat Interface -->
        <div class="flex-1 h-full min-w-0">
          <MasChat 
            :messages="chatMessages"
            :loading="feedbackLoading"
            @send="handleSendFeedback"
            @clear="clearChat"
          />
        </div>
      </div>
    </div>

    <!-- History Drawer -->
    <MasHistoryDrawer 
      ref="historyDrawerRef" 
      :active-id="activeSession?.id"
      @select="handleSessionSelect"
      @new="handleNewSession"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { MasSession, MasTaskHistory, MasMessage, AgentWorkCard } from './types'
import { MasApi } from '../api'
import MasHistoryDrawer from './components/MasHistoryDrawer.vue'
import MasChat from './components/MasChat.vue'
import MasManagerFlow from './components/MasManagerFlow.vue'
import MasAgentCardContainer from './components/MasAgentCardContainer.vue'
import MasArchivedTasks from './components/MasArchivedTasks.vue'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'MasDashboard' })

const activeSession = ref<MasSession | null>(null)
const newGoal = ref('')
const creating = ref(false)
const stopping = ref(false)
const taskHistory = ref<MasTaskHistory[]>([])
const chatMessages = ref<MasMessage[]>([])
const historyLoading = ref(false)
const feedbackLoading = ref(false)
const historyDrawerRef = ref()

// Polling for updates when session is active and not completed
let pollTimer: any = null

const handleStopSession = async () => {
  if (!activeSession.value) return
  stopping.value = true
  try {
    await MasApi.stopSession(activeSession.value.id)
    ElMessage.success('已发送强制终止指令')
    // Trigger an immediate sync
    const session = await MasApi.getSession(activeSession.value.id)
    activeSession.value = session
    await loadTaskHistory()
    stopPolling()
  } catch (error) {
    console.error('Failed to stop session:', error)
    ElMessage.error('强制终止失败')
  } finally {
    stopping.value = false
  }
}

const createSession = async () => {
  if (!newGoal.value.trim()) return
  creating.value = true
  try {
    const sessionId = await MasApi.createSession(newGoal.value)
    // 立即使用本地数据构建会话对象，切换到分栏 UI
    const now = new Date().toISOString()
    activeSession.value = {
      id: sessionId,
      goal: newGoal.value,
      status: 'INIT',
      createTime: now,
      updateTime: now
    }
    // 添加初始用户消息
    chatMessages.value = [{
      id: 'init',
      role: 'user',
      content: newGoal.value,
      createTime: now
    }]
    newGoal.value = ''
    ElMessage.success('会话已成功创建')
    // 启动轮询，后台同步最新状态和任务进展
    startPolling()
  } catch (error) {
    console.error('Failed to create session:', error)
    ElMessage.error('创建会话失败')
  } finally {
    creating.value = false
  }
}

const loadSession = async (id: string) => {
  try {
    const session = await MasApi.getSession(id)
    activeSession.value = session
    
    // Convert session goal into first message if messages empty
    if (chatMessages.value.length === 0) {
      chatMessages.value.push({
        id: 'init',
        role: 'user',
        content: session.goal,
        createTime: session.createTime
      })
    }

    await loadTaskHistory()
    startPolling()
  } catch (error) {
    console.error('Failed to load session:', error)
  }
}

const loadTaskHistory = async () => {
  if (!activeSession.value) return
  historyLoading.value = true
  try {
    const history = await MasApi.getTaskHistoryList(activeSession.value.id)
    taskHistory.value = history
    
    // Auto-update session status if changed in history
    if (history.length > 0 && activeSession.value.status !== 'COMPLETED' && activeSession.value.status !== 'FAILED') {
       // Ideally the backend session call returns latest, but we sync if needed
    }
  } catch (error) {
    console.error('Failed to load task history:', error)
  } finally {
    historyLoading.value = false
  }
}

const handleSendFeedback = async (text: string) => {
  if (!activeSession.value) return
  feedbackLoading.value = true
  
  // Add user message locally for responsiveness
  const userMsg: MasMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: text,
    createTime: new Date().toISOString()
  }
  chatMessages.value.push(userMsg)

  try {
    await MasApi.sendFeedback(activeSession.value.id, text)
    // After feedback, the agent will react, so we poll for new history
    await loadTaskHistory()
  } catch (error) {
    console.error('Failed to send feedback:', error)
    ElMessage.error('发送反馈失败')
  } finally {
    feedbackLoading.value = false
  }
}

const handleSessionSelect = (session: MasSession) => {
  chatMessages.value = [] // Reset chat for new session
  loadSession(session.id)
}

const handleNewSession = () => {
  activeSession.value = null
  taskHistory.value = []
  chatMessages.value = []
  stopPolling()
}

const agentCards = computed<AgentWorkCard[]>(() => {
  if (!activeSession.value) return []
  if (['COMPLETED', 'FAILED'].includes(activeSession.value.status)) return [] 

  const history = taskHistory.value
  const managerTasks = history.filter(t => t.role === 'MANAGER' && t.isInternal && t.status === 'SUCCESS')
  const lastManagerRunId = managerTasks.length > 0 ? Math.max(...managerTasks.map(t => t.id)) : 0

  const currentRoundTasks = history.filter(t => !t.isInternal && t.id > lastManagerRunId)
  const activeRoles = new Set(currentRoundTasks.map(t => t.role))
  
  if (currentRoundTasks.length === 0) {
    if (activeSession.value.status === 'PLANNING') activeRoles.add('PM')
    if (activeSession.value.status === 'EXECUTING') activeRoles.add('EXECUTOR')
  }

  const cards: AgentWorkCard[] = []
  Array.from(activeRoles).forEach(role => {
    const roleTasks = currentRoundTasks.filter(t => t.role === role)
    const completed = roleTasks.filter(t => t.status === 'SUCCESS' || t.status === 'FAILED')
    const recentResults = [...completed].sort((a, b) => b.id - a.id)
    
    // In our event-driven system, Tasks in DB are already finished.
    // If we're not COMPLETED, it means system is either evaluating or working on the next task.
    // We treat the active agent card as "working" while the session is running.
    const status = 'working'
    
    // Use the latest result as an approximation of the "current" task context
    const currentTask = recentResults.length > 0 ? recentResults[0] : undefined

    cards.push({
      role,
      agentName: role === 'PM' ? '项目经理 (PM)' : '执行者 (Executor)',
      status,
      currentTask,
      recentResults,
      totalTasks: Math.max(roleTasks.length, 1),
      completedTasks: completed.length
    })
  })

  return cards
})

const archivedTasks = computed<MasTaskHistory[]>(() => {
  if (!activeSession.value) return []
  const history = taskHistory.value
  const businessTasks = history.filter(t => !t.isInternal)

  if (['COMPLETED', 'FAILED'].includes(activeSession.value.status)) {
    return businessTasks
  }

  const managerTasks = history.filter(t => t.role === 'MANAGER' && t.isInternal && t.status === 'SUCCESS')
  const lastManagerRunId = managerTasks.length > 0 ? Math.max(...managerTasks.map(t => t.id)) : 0

  return businessTasks.filter(t => t.id <= lastManagerRunId)
})

const clearChat = () => {
  chatMessages.value = []
}

const startPolling = () => {
  stopPolling()
  pollTimer = setInterval(async () => {
    if (activeSession.value && !['COMPLETED', 'FAILED'].includes(activeSession.value.status)) {
       // Refresh both session and task history
       const session = await MasApi.getSession(activeSession.value.id)
       activeSession.value = session
       await loadTaskHistory()
       
       if (['COMPLETED', 'FAILED'].includes(session.status)) {
         stopPolling()
       }
    }
  }, 5000)
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

const statusClass = (status: string) => {
  switch (status) {
    case 'EXECUTING': return 'bg-blue-500 shadow-blue-500/50 animate-pulse'
    case 'COMPLETED': return 'bg-emerald-500 shadow-emerald-500/50'
    case 'FAILED': return 'bg-rose-500 shadow-rose-500/50'
    default: return 'bg-slate-400'
  }
}

const statusType = (status: string) => {
  switch (status) {
    case 'EXECUTING': return 'primary'
    case 'COMPLETED': return 'success'
    case 'FAILED': return 'danger'
    default: return 'info'
  }
}

const roleIcon = (role: string) => {
  switch (role) {
    case 'Manager': return 'solar:user-speak-bold-duotone'
    case 'PM': return 'solar:clipboard-list-bold-duotone'
    case 'Executor': return 'solar:delivery-bold-duotone'
    default: return 'solar:user-bold'
  }
}

onBeforeUnmount(() => {
  stopPolling()
})

onMounted(() => {
  // Check if we have an active session in local storage or via route query (optional)
})
</script>

<style scoped>
.mas-container {
  height: calc(100vh - 84px); /* Assuming header takes 84px */
}

.main-goal-input :deep(.el-input__wrapper) {
  @apply border-none shadow-none bg-transparent !text-lg p-4 font-medium;
}

.anim-fade-in {
  animation: fadeIn 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.98); }
  to { opacity: 1; transform: scale(1); }
}

:deep(.el-tag--plain) {
  @apply bg-white/20 dark:bg-black/20 border-white/30 dark:border-slate-700;
}
</style>
