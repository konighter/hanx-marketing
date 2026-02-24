<template>
  <div class="delivery-schedule">
    <div class="schedule-header">
      <div class="cell day-label"></div>
      <div v-for="h in 24" :key="h-1" class="cell hour-label">
        {{ h - 1 }}
      </div>
    </div>
    <div v-for="(day, dayIdx) in days" :key="dayIdx" class="schedule-row">
      <div class="cell day-label">{{ day }}</div>
      <div
        v-for="h in 24"
        :key="h-1"
        class="cell hour-cell"
        :class="{ active: schedule[dayIdx][h-1] }"
        @mousedown="handleMouseDown(dayIdx, h-1)"
        @mouseenter="handleMouseEnter(dayIdx, h-1)"
      ></div>
    </div>
    <div class="schedule-footer mt-10px flex justify-between items-center">
      <div class="text-12px text-gray-400">
        点击或拖拽来选择投放时间段 (当前选中: {{ totalSelected }} 小时)
      </div>
      <div class="btns">
        <el-button size="small" @click="selectAll">全选</el-button>
        <el-button size="small" @click="clearAll">清空</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

const props = defineProps<{
  modelValue: boolean[][]
}>()

const emit = defineEmits(['update:modelValue'])

const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

// Local copy of schedule
const schedule = ref<boolean[][]>(
  props.modelValue && props.modelValue.length === 7 
    ? JSON.parse(JSON.stringify(props.modelValue))
    : Array.from({ length: 7 }, () => Array(24).fill(true))
)

const isDrawing = ref(false)
const drawValue = ref(true)

const totalSelected = computed(() => {
  return schedule.value.reduce((acc, row) => acc + row.filter(Boolean).length, 0)
})

const handleMouseDown = (d: number, h: number) => {
  isDrawing.value = true
  drawValue.value = !schedule.value[d][h]
  schedule.value[d][h] = drawValue.value
  emitUpdate()
  
  window.addEventListener('mouseup', handleMouseUp)
}

const handleMouseEnter = (d: number, h: number) => {
  if (isDrawing.value) {
    schedule.value[d][h] = drawValue.value
    emitUpdate()
  }
}

const handleMouseUp = () => {
  isDrawing.value = false
  window.removeEventListener('mouseup', handleMouseUp)
}

const emitUpdate = () => {
  emit('update:modelValue', JSON.parse(JSON.stringify(schedule.value)))
}

const selectAll = () => {
  schedule.value = Array.from({ length: 7 }, () => Array(24).fill(true))
  emitUpdate()
}

const clearAll = () => {
  schedule.value = Array.from({ length: 7 }, () => Array(24).fill(false))
  emitUpdate()
}

// Sync from props
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.length === 7) {
    schedule.value = JSON.parse(JSON.stringify(newVal))
  }
}, { deep: true })

</script>

<style scoped>
.delivery-schedule {
  user-select: none;
  border: 1px solid #ebeef5;
  padding: 10px;
  background: #fff;
}

.schedule-header, .schedule-row {
  display: flex;
}

.cell {
  width: 25px;
  height: 25px;
  border: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
}

.day-label {
  width: 50px;
  background: #f5f7fa;
  font-weight: bold;
}

.hour-label {
  background: #f5f7fa;
}

.hour-cell {
  cursor: pointer;
}

.hour-cell.active {
  background-color: #409eff;
}

.hour-cell:hover {
  border-color: #409eff;
}
</style>
