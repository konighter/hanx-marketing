<template>
  <div class="column-customizer">
    <div class="customizer-header">
      <div class="header-left">
        <h4>列定制</h4>
        <span class="tab-type">{{ getTabTypeLabel() }}</span>
      </div>
      <div class="header-right">
        <el-checkbox 
          v-model="checkAll" 
          :indeterminate="isIndeterminate"
          @change="handleCheckAllChange"
          size="small"
        >
          全选
        </el-checkbox>
      </div>
    </div>
    
    <div class="customizer-content">
      <el-input 
        :model-value="searchTerm" 
        @input="handleSearch"
        placeholder="搜索列名..."
        prefix-icon="Search"
        size="small"
        clearable
        class="search-input"
      />
      
      <el-scrollbar height="280px" class="column-list">
        <div class="column-list-content">
          <div 
            v-for="column in availableColumns" 
            :key="column.key"
            class="column-item"
            :class="{ 
              'column-required': column.required,
              'column-disabled': column.disabled 
            }"
          >
            <el-checkbox 
              :model-value="column.visible"
              @change="(checked: boolean) => handleCheckedChange(column.key)"
              :disabled="column.required || column.disabled"
              size="small"
            >
              <div class="column-info">
                <span class="column-label">{{ column.label }}</span>
                <div class="column-tags">
                  <el-tag v-if="column.required" size="small" type="info">必需</el-tag>
                  <el-tag v-if="column.fixed" size="small" type="warning">固定</el-tag>
                </div>
              </div>
            </el-checkbox>
          </div>
        </div>
      </el-scrollbar>
    </div>
    
    <div class="customizer-footer">
      <el-button size="small" @click="resetColumns">重置</el-button>
      <el-button size="small" type="primary" @click="confirmColumns">确定</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { useTableColumns } from '../composables/useTableColumns'
import type { TableColumn } from '../types/columns'

const props = defineProps<{
  tableType: string
  onUpdate?: (columns: string[]) => void
}>()
const emit = defineEmits<{
  update: [columns: string[]]
  close: []
}>()

// 使用列状态管理
const tableColumnsManager = useTableColumns({
  tableType: props.tableType as any,
  onUpdate: (visibleColumns) => {
    const visibleKeys = visibleColumns.map(col => col.key)
    emit('update', visibleKeys)
  }
})

// 从composable中解构所需的状态和方法
const {
  availableColumns,
  searchTerm,
  selectedCount,
  optionalCount,
  resetToDefault,
  toggleColumnVisibility,
  toggleAllColumns,
  setSearchTerm,
  initializeColumns
} = tableColumnsManager

// 监听 tableType 变化，重新初始化列配置
watch(() => props.tableType, (newType) => {
  // 先重置搜索词
  searchTerm.value = ''
  // 重新初始化列配置
  initializeColumns()
}, { immediate: true })

// 全选状态
const checkAll = computed(() => {
  const available = availableColumns.value.filter(col => !col.required)
  return available.length > 0 && selectedCount.value >= optionalCount.value
})

// 半选状态
const isIndeterminate = computed(() => {
  return selectedCount.value > 0 && selectedCount.value < optionalCount.value
})

// 获取tab类型标签
const getTabTypeLabel = () => {
  const labels = {
    campaign: '广告活动',
    adGroup: '广告组',
    ad: '广告'
  }
  return labels[props.tableType as keyof typeof labels] || ''
}

// 处理全选变化
const handleCheckAllChange = (checked: boolean) => {
  toggleAllColumns(checked)
}

// 处理选中变化
const handleCheckedChange = (columnKey: string) => {
  toggleColumnVisibility(columnKey)
}

// 处理搜索
const handleSearch = (value: string) => {
  setSearchTerm(value)
}

// 重置列
const resetColumns = () => {
  resetToDefault()
  ElMessage.success('已重置为默认配置')
}

// 确认设置
const confirmColumns = () => {
  ElMessage.success('列配置已保存')
  // 配置已通过composable的onUpdate自动保存和通知
  emit('close')
}
</script>

<style scoped>
.column-customizer {
  width: 100%;
  padding: 0;
  box-sizing: border-box;
  overflow: hidden;
}

.customizer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-light);
  background: var(--el-bg-color-page);
  box-sizing: border-box;
  min-width: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.header-left h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  flex-shrink: 0;
}

.tab-type {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background: var(--el-color-info-light-9);
  padding: 2px 8px;
  border-radius: 4px;
  white-space: nowrap;
  flex-shrink: 0;
}

.customizer-content {
  padding: 12px 16px;
  box-sizing: border-box;
  overflow: hidden;
}

.search-input {
  margin-bottom: 12px;
}

.column-list {
  max-height: 280px;
}

.column-item {
  padding: 8px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  box-sizing: border-box;
}

.column-item:last-child {
  border-bottom: none;
}

.column-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  min-width: 0;
}

.column-label {
  flex: 1;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.column-tags {
  display: flex;
  gap: 4px;
}

.customizer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid var(--el-border-color-light);
  background: var(--el-bg-color-page);
  box-sizing: border-box;
}

.column-required {
  opacity: 0.7;
}

.column-disabled {
  opacity: 0.5;
}

.column-list-content {
  padding: 4px 0;
  box-sizing: border-box;
}

/* 覆盖Element Plus的checkbox样式 */
:deep(.el-checkbox) {
  width: 100%;
}

:deep(.el-checkbox__label) {
  width: 100%;
}
</style>