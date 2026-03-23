<template>
  <div class="h-full flex flex-col glass-container rounded-2xl overflow-hidden border border-white/20 shadow-2xl bg-white/40 dark:bg-slate-900/40 backdrop-blur-xl">
    <!-- Header -->
    <div class="p-5 flex items-center gap-3 border-b border-white/30 dark:border-slate-800">
      <div class="p-2 bg-indigo-500/10 rounded-lg">
        <Icon icon="solar:chat-round-dots-bold-duotone" class="text-indigo-600 dark:text-indigo-400" :size="20"/>
      </div>
      <div>
        <h3 class="text-sm font-bold m-0 tracking-tight text-slate-800 dark:text-slate-200">交互对话</h3>
        <p class="text-[10px] text-slate-400 m-0 mt-0.5 uppercase tracking-widest font-semibold">User Interaction</p>
      </div>
    </div>

    <!-- Message List -->
    <el-scrollbar ref="scrollbarRef" class="flex-1 px-6 py-4">
      <div v-if="messages.length === 0" class="h-full flex flex-col items-center justify-center opacity-30 text-slate-500 py-20 mt-10">
        <Icon icon="solar:dialog-2-linear" :size="64" />
        <p class="text-sm font-medium mt-4">开始您的 MAS 之旅</p>
      </div>

      <div v-else class="space-y-6 pb-4">
        <div v-for="msg in messages" :key="msg.id" :class="['flex', msg.role === 'user' ? 'justify-end' : 'justify-start anim-fade-in']">
          <div :class="[
            'max-w-[85%] px-4 py-3 rounded-2xl shadow-sm border',
            msg.role === 'user' 
              ? 'bg-gradient-to-br from-indigo-600 to-indigo-700 text-white border-indigo-500/30 rounded-tr-none' 
              : 'bg-white/80 dark:bg-slate-800/80 text-slate-800 dark:text-slate-200 border-white/40 dark:border-slate-700 rounded-tl-none'
          ]">
            <div v-if="msg.role === 'assistant'" class="flex items-center gap-2 mb-1.5 opacity-50">
              <Icon icon="solar:magic-stick-3-bold-duotone" :size="14" />
              <span class="text-[10px] font-bold uppercase tracking-tighter">Assistant</span>
            </div>
            <div class="text-sm leading-relaxed whitespace-pre-wrap">{{ msg.content }}</div>
            <div :class="['mt-1 text-[10px] opacity-40 text-right', msg.role === 'user' ? 'text-white/70' : 'text-slate-400']">
              {{ formatDate(msg.createTime) }}
            </div>
          </div>
        </div>
        
        <!-- Thinking Placeholder -->
        <div v-if="loading" class="flex justify-start anim-fade-in">
          <div class="bg-white/50 dark:bg-slate-800/50 px-4 py-3 rounded-2xl border border-white/20 rounded-tl-none flex items-center gap-2">
            <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce [animation-delay:-0.3s]"></span>
            <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce [animation-delay:-0.15s]"></span>
            <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce"></span>
          </div>
        </div>
      </div>
    </el-scrollbar>

    <!-- Input Area -->
    <div class="p-5 bg-white/20 dark:bg-black/20 border-t border-white/30 dark:border-slate-800">
      <div class="relative group">
        <el-input
          v-model="input"
          type="textarea"
          :rows="3"
          placeholder="给智能体发送指令或反馈..."
          class="custom-input h-full"
          resize="none"
          @keydown.enter.prevent="handleSend"
          :disabled="loading"
        />
        <div class="absolute right-3 bottom-3 flex gap-2">
          <el-button 
            type="primary" 
            circle 
            class="shadow-lg shadow-indigo-500/40 hover:scale-110 !transition-all duration-300"
            :loading="loading"
            @click="handleSend"
          >
            <Icon icon="solar:plain-bold" :size="20" />
          </el-button>
        </div>
      </div>
      <div class="mt-2 flex justify-between items-center px-1">
        <span class="text-[10px] text-slate-400 font-medium tracking-tight">⌘ + Enter 发送</span>
        <div class="flex gap-4">
          <el-tooltip content="清除对话" placement="top">
            <Icon icon="solar:trash-bin-trash-linear" class="text-slate-400 hover:text-rose-500 cursor-pointer transition-colors" :size="16" @click="$emit('clear')"/>
          </el-tooltip>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUpdated, nextTick } from 'vue'
import { MasMessage } from '../types'
import dayjs from 'dayjs'

const props = defineProps<{
  messages: MasMessage[]
  loading?: boolean
}>()

const emit = defineEmits(['send', 'clear'])

const input = ref('')
const scrollbarRef = ref()

const handleSend = () => {
  if (!input.value.trim() || props.loading) return
  emit('send', input.value)
  input.value = ''
}

const scrollToBottom = async () => {
  await nextTick()
  if (scrollbarRef.value) {
    const wrap = scrollbarRef.value.wrapRef
    wrap.scrollTop = wrap.scrollHeight
  }
}

const formatDate = (date: string) => {
  return dayjs(date).format('HH:mm')
}

// Watch for new messages and scroll
onUpdated(() => {
  scrollToBottom()
})

defineExpose({ scrollToBottom })
</script>

<style scoped>
.glass-container {
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.custom-input :deep(.el-textarea__inner) {
  @apply rounded-xl bg-white/50 dark:bg-slate-800/50 border-white/40 dark:border-slate-700/50 backdrop-blur-md transition-all duration-300 p-4 pb-12 focus:border-indigo-500/50 focus:shadow-[0_0_15px_-5px_rgba(99,102,241,0.3)];
}

.anim-fade-in {
  animation: fadeIn 0.4s ease-out forwards;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
