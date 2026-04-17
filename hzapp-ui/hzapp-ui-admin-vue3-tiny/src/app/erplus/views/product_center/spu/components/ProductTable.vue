<template>
  <ContentWrap class="erp-content">
    <el-table v-loading="loading" :data="list" size="small" class="erp-table">
      <el-table-column type="selection" width="40" />
      
      <!-- 图片 -->
      <el-table-column label="图片" width="70" align="center">
        <template #default="{ row }">
          <div class="flex items-center justify-center">
            <el-image
              fit="cover"
              :src="row.picUrl"
              class="h-10 w-10 rounded border shadow-sm cursor-pointer hover:scale-105 transition-transform"
              @click="handleImagePreview(row.picUrl)"
            />
          </div>
        </template>
      </el-table-column>

      <!-- 品名/SKU -->
      <el-table-column label="品名/SKU" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">
          <div class="sku-cell cursor-pointer" @click="$emit('edit', row)">
            <div class="product-name font-bold text-blue-600 mb-0.5 leading-tight hover:underline">
              {{ row.name || row.spuName || '-' }}
            </div>
            <div class="sku-code text-gray-400 text-[11px] leading-tight">
              {{ row.code || row.barCode || '-' }}
            </div>
          </div>
        </template>
      </el-table-column>

      <!-- 型号 -->
      <el-table-column label="型号" width="120" show-overflow-tooltip align="center">
        <template #default="{ row }">
          {{ row.model || '-' }}
        </template>
      </el-table-column>

      <!-- 单位 -->
      <el-table-column label="单位" width="60" align="center">
        <template #default="{ row }">
          {{ row.unit || '-' }}
        </template>
      </el-table-column>

      <!-- 分类 -->
      <el-table-column label="分类" width="120" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.categoryName || '-' }}
        </template>
      </el-table-column>

      <!-- 品牌 -->
      <el-table-column label="品牌" width="100" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.brandName || '-' }}
        </template>
      </el-table-column>

      <!-- 采购成本 -->
      <el-table-column label="采购成本" width="100" align="right">
        <template #default="{ row }">
          <span class="cost-price text-orange-600 font-medium">¥{{ fenToYuan(row.costPrice || row.price) }}</span>
        </template>
      </el-table-column>

      <!-- 组合情况 (仅 SKU 模式展示) -->
      <el-table-column v-if="viewMode === 'sku'" label="组合情况" width="80" align="center">
        <template #default="{ row }">
          <span :class="row.tabType === 6 ? 'text-purple-600' : 'text-gray-400'">
            {{ row.tabType === 6 ? '组合' : '单品' }}
          </span>
        </template>
      </el-table-column>

      <!-- 规格类型 (仅 SPU 模式展示) -->
      <el-table-column v-if="viewMode === 'spu'" label="规格类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.specType === 2 ? 'warning' : 'info'" size="small">
            {{ row.specType === 2 ? '多规格' : '单规格' }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 状态 -->
      <el-table-column label="状态" width="70" align="center">
        <template #default="{ row }">
          <el-tag 
            v-if="row.status === 1"
            type="success" 
            effect="plain"
            class="status-ghost-tag border-green-200 text-green-600 !bg-green-50"
            size="small"
          >
            在售
          </el-tag>
          <el-tag 
            v-else-if="row.status === 2"
            type="info" 
            effect="plain"
            class="status-ghost-tag border-gray-200 text-gray-500 !bg-gray-50"
            size="small"
          >
            下架
          </el-tag>
          <el-tag 
            v-else
            type="warning" 
            effect="plain"
            class="status-ghost-tag border-orange-200 text-orange-600 !bg-orange-50"
            size="small"
          >
            草稿
          </el-tag>
        </template>
      </el-table-column>

      <!-- 操作 -->
      <el-table-column fixed="right" label="操作" width="100" align="center">
        <template #default="{ row }">
          <div class="flex items-center justify-center gap-1">
            <el-button link type="primary" size="small" @click="$emit('edit', row)">
              编辑
            </el-button>
            <el-dropdown trigger="click">
              <el-button link type="primary" size="small">
                <Icon icon="ep:arrow-down" />
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$emit('detail', row)">数据分析</el-dropdown-item>
                  <el-dropdown-item divided @click="$emit('delete', row.id)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-footer flex justify-end p-2 bg-white sticky bottom-0 z-10 border-t border-gray-100 shadow-[0_-4px_10px_-5px_rgba(0,0,0,0.1)]">
      <Pagination
        v-model:limit="pageSizeModel"
        v-model:page="pageNoModel"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @pagination="$emit('pagination')"
      />
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { fenToYuan } from '@/utils'
import { createImageViewer } from '@/components/ImageViewer'

const props = defineProps({
  loading: Boolean,
  list: Array,
  total: Number,
  pageSize: Number,
  pageNo: Number,
  viewMode: {
    type: String,
    default: 'sku'
  }
})

const emit = defineEmits(['update:pageSize', 'update:pageNo', 'pagination', 'edit', 'detail', 'delete', 'formatCategoryName'])

const pageSizeModel = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

const pageNoModel = computed({
  get: () => props.pageNo,
  set: (val) => emit('update:pageNo', val)
})

const handleImagePreview = (url: string) => {
  createImageViewer({ urlList: [url] })
}
</script>

<style lang="scss" scoped>
.erp-content {
  margin-top: 8px !important;
  border: 1px solid #e5e7eb;
  border-radius: 0 !important;
  :deep(.el-card) {
    border-radius: 0 !important;
    border: none;
  }
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05) !important;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  
  :deep(.el-card__body) {
    padding: 0 20px 20px 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
  }
}

.erp-table {
  font-family: Inter, -apple-system, system-ui, sans-serif;
  font-size: 13px !important;
  border-radius: 0 !important;
  flex: 1;
  
  :deep(.el-table__header-wrapper) th {
    background-color: #f8fafc !important;
    color: #475569 !important;
    font-weight: 600;
    height: 38px;
    padding: 0;
    border-bottom: 1px solid #e5e7eb;
  }
  
  :deep(.el-table__cell) {
    padding: 10px 0 !important;
  }
}

.sku-cell {
  .sku-code {
    font-family: "Monaco", "Menlo", monospace;
    font-size: 13px;
  }
  .product-name {
    line-height: 1.4;
    color: #64748b;
  }
}

.status-ghost-tag {
  border-radius: 0 !important;
  padding: 0 4px;
  height: 20px;
  line-height: 18px;
}

.pagination-footer {
  background-color: #fff;
  margin: 0 -20px -20px -20px;
  padding: 12px 20px !important;
  border-top: 1px solid #f1f5f9;
}
</style>
