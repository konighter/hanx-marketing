<template>
  <div class="warehouse-container">
    <ContentWrap class="!mb-0">
      <el-tabs v-model="activeTab" class="page-tabs">
        <el-tab-pane label="当前库存" name="stock">
          <WarehouseStock v-if="activeTab === 'stock'" class="tab-content" />
        </el-tab-pane>
        <el-tab-pane label="库存历史" name="history">
          <WarehouseHistory v-if="activeTab === 'history'" class="tab-content" />
        </el-tab-pane>
      </el-tabs>
    </ContentWrap>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ContentWrap } from '@/components/ContentWrap'
import WarehouseStock from './components/WarehouseStock.vue'
import WarehouseHistory from './components/WarehouseHistory.vue'

defineOptions({ name: 'WarehouseIndexV2' })

const activeTab = ref('stock')
</script>

<style scoped>
.warehouse-container {
  padding: 0;
  height: calc(100vh - 110px);
  overflow: hidden;
}

:deep(.page-tabs) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.page-tabs .el-tabs__header) {
  margin: 0 0 15px 0;
  padding: 0 20px;
  background: #fff;
}

:deep(.page-tabs .el-tabs__content) {
  flex: 1;
  overflow: hidden;
}

:deep(.page-tabs .el-tab-pane) {
  height: 100%;
}

.tab-content {
  height: 100%;
}

/* Ensure the cards inside components fill the space */
:deep(.el-card) {
  height: 100%;
  border: none;
  box-shadow: none !important;
}

:deep(.el-card__body) {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
