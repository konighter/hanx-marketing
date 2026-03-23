<template>
  <el-drawer
    v-model="visible"
    title="会话记录"
    direction="ltr"
    size="320px"
    class="mas-history-drawer"
  >
    <div class="flex flex-col h-full bg-slate-50/50 dark:bg-slate-900/50 rounded-xl overflow-hidden glass-effect">
      <div class="p-4 border-b border-slate-200 dark:border-slate-700">
        <el-input
          v-model="searchQuery"
          placeholder="搜索会话..."
          prefix-icon="Search"
          clearable
          class="custom-search"
        />
      </div>

      <el-scrollbar class="flex-1">
        <div v-if="loading" class="flex justify-center p-8">
          <el-skeleton :rows="5" animated />
        </div>
        
        <div v-else-if="filteredHistory.length === 0" class="flex flex-col items-center justify-center p-12 opacity-50">
          <Icon icon="solar:empty-box-linear" :size="48" />
          <span class="mt-2 text-sm">暂无历史记录</span>
        </div>

        <div v-else class="p-2 space-y-2">
          <div
            v-for="item in filteredHistory"
            :key="item.id"
            class="session-item group"
            :class="{ 'active': activeId === item.id }"
            @click="handleSelect(item)"
          >
            <div class="flex items-start gap-3">
              <div class="mt-1">
                <div 
                  class="w-2 h-2 rounded-full shadow-lg"
                  :class="statusClass(item.status)"
                ></div>
              </div>
              <div class="flex-1 min-width-0">
                <div class="text-sm font-medium truncate group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">
                  {{ item.goal }}
                </div>
                <div class="text-xs text-slate-400 mt-1 flex justify-between items-center">
                  <span>{{ formatDate(item.createTime) }}</span>
                  <el-tag size="small" :type="statusTagType(item.status)" effect="plain" class="scale-90 origin-right">
                    {{ item.status }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-scrollbar>

      <div class="p-4 border-t border-slate-200 dark:border-slate-700 bg-white/30 dark:bg-black/30">
        <el-button type="primary" class="w-full h-10 rounded-lg shadow-indigo-200 shadow-md group" @click="handleNewSession">
          <Icon icon="solar:add-circle-bold" class="mr-2 group-hover:rotate-90 transition-transform" />
          新会话
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { MasSession } from '../types'
import { MasApi } from '../../api'
import dayjs from 'dayjs'

const props = defineProps<{
  activeId?: string
}>()

const emit = defineEmits(['select', 'new'])

const visible = ref(false)
const searchQuery = ref('')
const history = ref<MasSession[]>([])
const loading = ref(false)

const open = () => {
  visible.value = true
  fetchHistory()
}

const fetchHistory = async () => {
  loading.value = true
  try {
    const res = await MasApi.getSessionPage({ pageNo: 1, pageSize: 50 })
    history.value = res.list || []
  } catch (error) {
    console.error('Failed to fetch history:', error)
  } finally {
    loading.value = false
  }
}

const filteredHistory = computed(() => {
  return history.value.filter(item => 
    item.goal.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const handleSelect = (item: MasSession) => {
  emit('select', item)
  visible.value = false
}

const handleNewSession = () => {
  emit('new')
  visible.value = false
}

const statusClass = (status: string) => {
  switch (status) {
    case 'EXECUTING': return 'bg-blue-500 shadow-blue-500/50 animate-pulse'
    case 'COMPLETED': return 'bg-emerald-500 shadow-emerald-500/50'
    case 'FAILED': return 'bg-rose-500 shadow-rose-500/50'
    default: return 'bg-slate-400'
  }
}

const statusTagType = (status: string) => {
  switch (status) {
    case 'EXECUTING': return 'primary'
    case 'COMPLETED': return 'success'
    case 'FAILED': return 'danger'
    default: return 'info'
  }
}

const formatDate = (date: string) => {
  return dayjs(date).format('MM-DD HH:mm')
}

defineExpose({ open })
</script>

<style scoped>
.glass-effect {
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.session-item {
  @apply p-3 rounded-xl cursor-pointer transition-all border border-transparent hover:bg-white dark:hover:bg-white/5 hover:border-slate-200 dark:hover:border-slate-700 hover:shadow-sm;
}

.session-item.active {
  @apply bg-indigo-50 dark:bg-indigo-950/30 border-indigo-200 dark:border-indigo-900 shadow-sm leading-relaxed;
}

.custom-search :deep(.el-input__wrapper) {
  @apply rounded-xl bg-slate-100 dark:bg-slate-800 border-none shadow-none ring-0;
}

:deep(.mas-history-drawer .el-drawer__body) {
  padding: 0;
}
</style>
