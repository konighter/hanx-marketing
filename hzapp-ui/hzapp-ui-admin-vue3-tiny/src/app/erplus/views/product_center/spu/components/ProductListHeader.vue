<template>
  <ContentWrap class="erp-header">
    <!-- Row 1: Unified Filter Bar -->
    <div class="filter-bar flex items-center flex-wrap gap-2 mb-1">
      <!-- 视图切换 -->
      <el-radio-group v-model="viewMode" size="default" @change="handleViewModeChange">
        <el-radio-button label="sku">按SKU</el-radio-button>
        <el-radio-button label="spu">按SPU</el-radio-button>
      </el-radio-group>

      <!-- 产品类型切换 -->
      <el-radio-group v-model="queryParams.tabType" size="default" @change="emitSearch">
        <el-radio-button :label="undefined">全部</el-radio-button>
        <template v-if="viewMode === 'spu'">
        </template>
        <template v-else>
          <el-radio-button :label="8">普通产品</el-radio-button>
          <el-radio-button :label="9">组合产品</el-radio-button>
        </template>
      </el-radio-group>

      <!-- 下拉筛选系列 -->
      <div class="inline-filters flex items-center gap-2">
        <el-cascader
          v-model="queryParams.categoryId"
          :options="categoryList"
          :props="{ label: 'name', value: 'id', checkStrictly: true }"
          placeholder="分类"
          size="default"
          clearable
          class="w-24"
          @change="emitSearch"
        />
        <el-select v-model="queryParams.brandId" placeholder="品牌" size="default" clearable class="!w-22" @change="emitSearch">
          <el-option v-for="item in brandOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="状态" size="default" clearable class="!w-20" @change="emitSearch">
          <el-option label="在售" :value="1" />
          <el-option label="下架" :value="2" />
          <el-option label="草稿" :value="0" />
        </el-select>
        <el-select v-model="queryParams.productTag" placeholder="产品标签" size="default" clearable class="!w-22" @change="emitSearch">
          <el-option label="爆款" value="HOT" />
          <el-option label="新款" value="NEW" />
        </el-select>
        <el-date-picker
          v-model="queryParams.createTime"
          type="daterange"
          size="default"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          class="!w-64"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="emitSearch"
        />
      </div>

      <!-- 右侧搜索区 -->
      <div class="flex-1 flex justify-end items-center gap-2">
        <div class="compact-search flex items-center border rounded overflow-hidden">
          <el-select v-model="queryParams.searchType" size="default" class="!w-20 border-none no-shadow">
            <el-option label="SKU" value="SKU" />
            <el-option label="品名" value="NAME" />
          </el-select>
          <div class="w-px h-4 bg-gray-200"></div>
          <el-input
            v-model="queryParams.searchValue"
            placeholder="搜索内容"
            size="default"
            class=" border-none no-shadow flex-1"
            clearable
            @keyup.enter="emitSearch"
          />
          <div class="px-2 border-l cursor-pointer hover:bg-gray-50" @click="emitSearch">
            <Icon icon="ep:search" />
          </div>
          <div class="px-2 border-l cursor-pointer hover:bg-gray-50">
            <Icon icon="ep:menu" />
          </div>
        </div>
        <el-button size="default" @click="$emit('openAdvancedFilter')">更多筛选</el-button>
        <el-button size="default" class="!bg-gray-100 !border-gray-200" @click="$emit('reset')">重置</el-button>
      </div>
    </div>

    <!-- Row 2: Action Bar -->
    <div class="action-bar flex items-center justify-between border-t pt-1">
      <div class="flex items-center gap-2">
        <el-dropdown trigger="click" @command="handleCreateCommand">
          <el-button type="primary" size="default">
            添加产品 <Icon icon="ep:arrow-down" class="ml-1" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="sku">添加 SKU</el-dropdown-item>
              <el-dropdown-item command="spu">添加 SPU</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-dropdown size="default">
          <el-button size="default">
            导入<Icon icon="ep:arrow-down" class="ml-1" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>Excel导入</el-dropdown-item>
              <el-dropdown-item>同步1688</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-dropdown size="default">
          <el-button size="default">
            同步图片<Icon icon="ep:arrow-down" class="ml-1" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>从1688同步</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-button size="default">打印产品条码</el-button>
        
        <el-dropdown size="default">
          <el-button size="default">
            更多<Icon icon="ep:arrow-down" class="ml-1" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
               <el-dropdown-item @click="$emit('export')">导出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <div class="flex items-center gap-2 text-gray-500">
        <Icon icon="ep:refresh" class="cursor-pointer hover:text-blue-500" @click="emitSearch" />
        <Icon icon="ep:question-filled" class="cursor-pointer hover:text-blue-500" />
        <Icon icon="ep:download" class="cursor-pointer hover:text-blue-500" @click="$emit('export')" />
        <Icon icon="ep:setting" class="cursor-pointer hover:text-blue-500" />
      </div>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
const props = defineProps({
  categoryList: Array,
  brandOptions: Array
})

const viewMode = defineModel<string>('viewMode')
const queryParams = defineModel<any>('queryParams')

const emit = defineEmits(['search', 'openAdvancedFilter', 'reset', 'export', 'create'])

const handleViewModeChange = (val: string) => {
  emit('search')
}

const emitSearch = () => {
  emit('search')
}

const handleCreateCommand = (command: string) => {
  emit('create', command)
}
</script>

<style lang="scss" scoped>
.erp-header {
  padding: 8px 16px;
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 0 !important;
  :deep(.el-card) {
    border-radius: 0 !important;
    border: none;
  }
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05) !important;
}

.compact-search {
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 0 !important;
  height: 32px;
  transition: all 0.2s;
  &:hover {
    border-color: var(--el-color-primary);
  }
}

.no-shadow {
  :deep(.el-input__wrapper), :deep(.el-select__wrapper) {
    box-shadow: none !important;
    background: transparent !important;
    border-radius: 2px !important;
  }
}

:deep(.el-button) {
  border-radius: 0 !important;
}

:deep(.el-input__wrapper), :deep(.el-select__wrapper) {
  border-radius: 0 !important;
}

:deep(.el-radio-button) {
  &:first-child .el-radio-button__inner {
    border-top-left-radius: 2px;
    border-bottom-left-radius: 2px;
  }
  &:last-child .el-radio-button__inner {
    border-top-right-radius: 2px;
    border-bottom-right-radius: 2px;
  }
}
:deep(.el-radio-button__inner) {
  border-radius: 0;
}
</style>
