<template>
  <div class="flex space-x-2">
    <!-- Left: Pending Items -->
    <el-card shadow="never" class="flex-1 todo-panel" :body-style="{ padding: '12px' }">
      <template #header>
        <div class="font-bold">待处理事项</div>
      </template>
      
      <div class="space-y-3">
        <!-- Operation Section -->
        <div>
          <div class="flex items-center space-x-2 mb-2 border-l-4 border-primary pl-2">
            <span class="font-bold text-sm">运营</span>
          </div>
          <div class="grid grid-cols-3 gap-2">
            <div v-for="item in opItems" :key="item.label" class="bg-gray-50 p-2 rounded hover:bg-gray-100 transition-colors">
              <div class="flex items-center text-xs text-gray-500 mb-1">
                <span>{{ item.label }}</span>
                <Icon icon="ep:info-filled" class="ml-1 text-gray-300" />
              </div>
              <div class="text-xl font-bold">{{ item.value }}</div>
            </div>
          </div>
        </div>

        <!-- Approval Section -->
        <div>
          <div class="flex items-center space-x-2 mb-2 border-l-4 border-primary pl-2">
            <span class="font-bold text-sm">审批</span>
          </div>
          <div class="grid grid-cols-3 gap-2">
            <div v-for="item in approveItems" :key="item.label" class="bg-gray-50 p-2 rounded hover:bg-gray-100 transition-colors">
              <div class="flex items-center text-xs text-gray-500 mb-1">
                <span>{{ item.label }}</span>
                <Icon icon="ep:info-filled" class="ml-1 text-gray-300" />
              </div>
              <div class="text-xl font-bold">{{ item.value }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Right: Operational To-dos -->
    <el-card shadow="never" class="flex-1 todo-panel" :body-style="{ padding: '12px' }">
      <template #header>
        <div class="flex justify-between items-center">
          <div class="flex items-center space-x-1 cursor-pointer hover:text-primary">
            <span class="font-bold">运营待办</span>
            <Icon icon="ep:arrow-right" />
          </div>
          <div class="flex items-center space-x-2">
            <el-checkbox v-model="onlyMe" label="仅看自己" />
            <el-date-picker
              v-model="weekValue"
              type="week"
              format="[2026 第] ww [周]"
              placeholder="选择周"
              size="small"
              style="width: 130px"
            />
            <el-button size="small" type="primary" link :icon="Plus">新建待办</el-button>
            <el-button size="small" type="primary" link :icon="Edit">写日志</el-button>
          </div>
        </div>
      </template>

      <!-- Calendar Strip -->
      <div class="flex justify-between items-center bg-blue-50/50 p-2 rounded mb-2">
        <Icon icon="ep:arrow-left" class="cursor-pointer text-gray-400" />
        <div v-for="(day, idx) in weekDays" :key="day.date" class="flex flex-col items-center cursor-pointer px-2" :class="idx === 3 ? 'active-day' : ''">
          <span class="text-[10px] text-gray-400">{{ day.label }}</span>
          <span class="text-xs font-bold" :class="idx === 3 ? 'text-primary' : ''">{{ day.date }}</span>
        </div>
        <Icon icon="ep:arrow-right" class="cursor-pointer text-gray-400" />
      </div>

      <!-- Empty State -->
      <div class="flex flex-col items-center justify-center py-4">
        <div class="text-gray-400 text-sm">暂无数据</div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus, Edit } from '@element-plus/icons-vue'

const onlyMe = ref(true)
const weekValue = ref(new Date())

const opItems = [
  { label: '库存不足', value: '31' },
  { label: '库存冗余', value: '1' },
  { label: '被跟卖', value: '0' },
  { label: '丢失购物车', value: '0' },
  { label: '待回复邮件', value: '0' },
  { label: '发票上传失败', value: '0' }
]

const approveItems = [
  { label: '待我处理', value: '0' },
  { label: '我发起', value: '0' },
  { label: '已驳回', value: '0' }
]

const weekDays = [
  { label: '日', date: '05-10' },
  { label: '一', date: '05-11' },
  { label: '二', date: '05-12' },
  { label: '三', date: '05-13' },
  { label: '四', date: '05-14' },
  { label: '五', date: '05-15' },
  { label: '六', date: '05-16' }
]
</script>

<style scoped>
.todo-panel :deep(.el-card__header) {
  padding: 8px 12px;
  border-bottom: 1px solid #f3f4f6;
  background-color: #f8fafc;
}
.active-day {
  background-color: white;
  border: 1px solid #3b82f6;
  border-radius: 6px;
  padding: 2px 8px !important;
}
</style>
