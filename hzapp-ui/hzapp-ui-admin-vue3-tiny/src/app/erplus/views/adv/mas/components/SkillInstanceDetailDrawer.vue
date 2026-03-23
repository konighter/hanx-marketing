<template>
  <el-drawer
    v-model="visible"
    title="运行详情"
    size="70%"
    destroy-on-close
    @close="handleClose"
  >
    <div v-if="instance" class="details-layout" v-loading="loading">
      <!-- 左侧: 时间轴 -->
      <div class="details-left">
        <div class="section-title">运行历史</div>
        <el-scrollbar>
          <el-timeline v-if="logs.length > 0">
            <el-timeline-item
              v-for="(log, index) in logs"
              :key="index"
              :timestamp="formatDate(log.createTime)"
              :type="getLogType(log.type)"
            >
              <h4>{{ log.title }}</h4>
              <p>{{ log.content }}</p>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无运行历史" :image-size="60" />
        </el-scrollbar>
      </div>
      
      <!-- 右侧: Chat 页面 -->
      <div class="details-right">
        <div class="section-title">对话与思考</div>
        <div class="chat-container">
          <el-scrollbar ref="chatScrollbar">
            <div class="message-list" v-if="messages.length > 0">
              <div 
                v-for="(msg, index) in messages" 
                :key="index" 
                :class="['message-item', msg.role]"
              >
                <div class="message-role">
                  <el-avatar :size="24" :src="getAvatar(msg.role)" />
                  <span class="role-name">{{ getRoleName(msg.role) }}</span>
                  <span class="msg-time">{{ formatDate(msg.createTime) }}</span>
                </div>
                <div class="message-content">
                  <pre v-if="msg.role === 'thought'" class="thought-content">{{ msg.content }}</pre>
                  <div v-else class="text-content">{{ msg.content }}</div>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无对话记录" :image-size="60" />
          </el-scrollbar>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { MasSkillApi } from '@/app/erplus/api/adv/mas/skill'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  modelValue: boolean
  instance: any
}>()

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const logs = ref<any[]>([])
const messages = ref<any[]>([])
const loading = ref(false)
const chatScrollbar = ref<any>(null)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.instance) {
    fetchDetails()
  }
})

watch(() => visible.value, (val) => {
  emit('update:modelValue', val)
})

const fetchDetails = async () => {
  if (!props.instance) return
  loading.value = true
  try {
    const [logsRes, msgRes] = await Promise.all([
      MasSkillApi.getSkillInstanceLogs(props.instance.id),
      MasSkillApi.getSkillInstanceMessages(props.instance.processInstanceId)
    ])
    logs.value = logsRes || []
    messages.value = msgRes || []
    
    // 自动滚动到底部
    nextTick(() => {
      if (chatScrollbar.value) {
        chatScrollbar.value.setScrollTop(99999)
      }
    })
  } catch (error) {
    console.error('Failed to fetch details:', error)
    ElMessage.error('无法获取详情数据')
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  logs.value = []
  messages.value = []
}

const formatDate = (date: any) => {
  if (!date) return '--'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const getLogType = (type: string) => {
  switch (type) {
    case 'SUCCESS': return 'success'
    case 'ERROR': return 'danger'
    case 'WARNING': return 'warning'
    default: return 'primary'
  }
}

const getRoleName = (role: string) => {
  switch (role) {
    case 'user': return '用户'
    case 'assistant': return '技能助理'
    case 'thought': return '系统思考'
    default: return role
  }
}

const getAvatar = (role: string) => {
  if (role === 'thought') return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  if (role === 'assistant') return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  return 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png'
}
</script>

<style scoped lang="scss">
.details-layout {
  display: flex;
  height: 100%;
  gap: 24px;
  padding: 0 10px 20px;

  .details-left {
    flex: 1;
    border-right: 1px solid #ebeef5;
    display: flex;
    flex-direction: column;
    padding-right: 20px;
  }

  .details-right {
    flex: 1.5;
    display: flex;
    flex-direction: column;
  }

  .section-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 2px solid #409eff;
    width: fit-content;
  }
}

.chat-container {
  flex: 1;
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  overflow: hidden;

  .message-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .message-item {
    display: flex;
    flex-direction: column;
    gap: 8px;

    &.user {
      align-items: flex-end;
      .message-content {
        background-color: #95ec69;
        color: #000;
      }
      .message-role {
        flex-direction: row-reverse;
      }
    }

    &.thought {
      .message-content {
        background-color: #fdf6ec;
        border: 1px dashed #e6a23c;
        color: #e6a23c;
      }
    }

    &.assistant {
      .message-content {
        background-color: white;
        color: #303133;
      }
    }
  }

  .message-role {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #909399;

    .role-name {
      font-weight: 600;
    }
  }

  .message-content {
    max-width: 90%;
    padding: 10px 14px;
    border-radius: 8px;
    font-size: 14px;
    line-height: 1.5;
    box-shadow: 0 2px 4px 0 rgba(0,0,0,0.05);

    .thought-content {
      margin: 0;
      white-space: pre-wrap;
      font-family: inherit;
    }
  }
}
</style>
