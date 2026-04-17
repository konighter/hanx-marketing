<template>
  <div class="integrated-search flex items-center gap-2">
    <el-input
      v-model="searchValue"
      placeholder="请输入搜索内容"
      class="search-input"
      clearable
      @keyup.enter="handleSearch"
    >
      <template #prepend>
        <el-select v-model="searchType" class="type-select" placeholder="请选择">
          <el-option label="SKU" value="SKU" />
          <el-option label="识别码" value="RECOG_CODE" />
          <el-option label="MSKU" value="MSKU" />
          <el-option label="品名" value="NAME" />
          <el-option label="型号" value="MODEL" />
          <el-option label="产品描述" value="DESCRIPTION" />
          <el-option label="内部编码" value="INTERNAL_CODE" />
        </el-select>
      </template>
      <template #append>
        <div class="flex items-center px-2 cursor-pointer hover:text-blue-500" @click="handleSearch">
          <Icon icon="ep:search" />
        </div>
      </template>
    </el-input>
    <el-button class="more-filter-btn" @click="$emit('more')">
      <span>更多筛选</span>
      <Icon icon="ep:arrow-down" class="ml-1" />
    </el-button>
    <el-button @click="handleReset">重置</el-button>
  </div>
</template>

<script setup lang="ts">
const searchType = ref('SKU')
const searchValue = ref('')

const emit = defineEmits(['search', 'reset', 'more'])

const handleSearch = () => {
  emit('search', {
    type: searchType.value,
    value: searchValue.value
  })
}

const handleReset = () => {
  searchValue.value = ''
  emit('reset')
}
</script>

<style scoped lang="scss">
.integrated-search {
  .search-input {
    width: 420px;
    
    :deep(.el-input-group__prepend) {
      background-color: transparent;
      box-shadow: none;
      padding: 0;
      
      .type-select {
        width: 100px;
        .el-input__wrapper {
          box-shadow: none !important;
          background-color: transparent;
          border-right: 1px solid var(--el-border-color);
          border-top-right-radius: 0;
          border-bottom-right-radius: 0;
        }
      }
    }

    :deep(.el-input-group__append) {
      background-color: transparent;
      box-shadow: none;
      padding: 0;
      border-left: 1px solid var(--el-border-color);
    }
    
    :deep(.el-input__wrapper) {
      box-shadow: 0 0 0 1px var(--el-border-color) inset;
      &.is-focus {
        box-shadow: 0 0 0 1px var(--el-color-primary) inset;
      }
    }
  }

  .more-filter-btn {
    border: 1px solid var(--el-border-color);
    background: white;
    &:hover {
      color: var(--el-color-primary);
      border-color: var(--el-color-primary);
    }
  }
}
</style>
