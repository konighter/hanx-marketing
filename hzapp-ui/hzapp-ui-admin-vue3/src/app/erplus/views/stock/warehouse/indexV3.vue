<template>
  <div class="warehouse-container h-[calc(100vh-160px)] overflow-hidden">
    <el-row :gutter="10" class="h-full">
      <!-- 左侧：仓库列表 -->
      <el-col :span="6" :xs="24" class="h-full">
        <WarehouseSidebar v-model="selectedWarehouseId" @select="handleWarehouseSelect" class="h-full" />
      </el-col>

      <!-- 右侧：仓库详情 (Tabbed) -->
      <el-col :span="18" :xs="24" class="h-full">
        <ContentWrap :title="selectedWarehouse?.name || '仓库详情'" class="!mb-0 h-full">
          <template #header>
            <div class="flex items-center justify-end w-full h-32px">

              <el-tabs v-model="activeTab" class="header-tabs">
                <el-tab-pane label="库存列表" name="stock" />
                <el-tab-pane label="库存记录" name="history" />
              </el-tabs>
            </div>
          </template>

          <div class="flex-1 flex flex-col overflow-hidden pt-10px">
            <template v-if="selectedWarehouseId">
              <WarehouseStockTable v-if="activeTab === 'stock'" :warehouse-id="selectedWarehouseId"
                :warehouse-type="selectedWarehouse?.type" />
              <WarehouseHistoryTable v-else-if="activeTab === 'history'" :warehouse-id="selectedWarehouseId" />
            </template>
            <el-empty v-else description="请从左侧选择一个仓库以查看详情" class="flex-1" />
          </div>
        </ContentWrap>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ContentWrap } from '@/components/ContentWrap'
import WarehouseSidebar from './components/WarehouseSidebar.vue'
import WarehouseStockTable from './components/WarehouseStockTable.vue'
import WarehouseHistoryTable from './components/WarehouseHistoryTable.vue'

defineOptions({ name: 'WarehouseIndexV3' })

const activeTab = ref('stock')
const selectedWarehouseId = ref<number | null>(null)
const selectedWarehouse = ref<any>(null)

const handleWarehouseSelect = (warehouse: any) => {
  selectedWarehouse.value = warehouse
}
</script>

<style scoped>
.warehouse-container {
  padding: 0;
}

/* Tab Header 样式自定义，使其更紧凑并靠右 */
:deep(.header-tabs) {
  height: 32px;
}

:deep(.header-tabs .el-tabs__header) {
  margin: 0;
  border-bottom: none;
}

:deep(.header-tabs .el-tabs__nav-wrap::after) {
  display: none;
}

:deep(.header-tabs .el-tabs__item) {
  height: 32px;
  line-height: 32px;
  padding: 0 15px !important;
  font-size: 14px;
}

:deep(.el-card) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 10px !important;
}
</style>
