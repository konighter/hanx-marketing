<template>
  <div class="ad-manager">
    <div class="flex justify-between items-center mb-10px w-full">
      <div class="flex items-center">
        <span class="text-14px font-bold mr-10px">广告列表:</span>
      </div>
      <el-button 
        size="small" 
        @click="addAd"
        class="add-btn"
      >
        <el-icon><Plus /></el-icon>
        <span class="ml-4px">添加商品广告</span>
      </el-button>
    </div>
 
    <el-dialog v-model="createDialogVisible" title="添加商品广告" width="500px" append-to-body>
      <el-form label-position="top">
        <el-form-item label="请输入 SKU (每行一个)">
          <el-input
            v-model="skusText"
            type="textarea"
            :rows="10"
            placeholder="请输入 SKU，多个 SKU 请换行输入"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false" size="small">取消</el-button>
        <el-button type="primary" @click="handleCreateAd" size="small">创建</el-button>
      </template>
    </el-dialog>

    <el-table :data="ads" border size="small" style="width: 100%">
      <el-table-column label="图片" width="70" align="center">
        <template #default="{ row }">
          <el-image 
            v-if="row.image"
            :src="row.image" 
            class="w-40px h-40px rounded-4px"
            :preview-src-list="[row.image]"
            preview-teleported
          />
          <div v-else class="w-40px h-40px bg-gray-100 rounded-4px flex items-center justify-center text-gray-400">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" min-width="240">
        <template #default="{ row }">
          <div class="flex flex-col py-4px">
            <div class="text-13px font-medium mb-4px" v-if="row.name">{{ row.name }}</div>
            <div class="text-12px text-gray-500">
              <span v-if="row.asin" class="mr-8px">ASIN: {{ row.asin }}</span>
              <span v-if="row.sku">SKU: {{ row.sku }}</span>
              <span v-if="!row.asin && !row.sku" class="text-gray-300">-</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default>
          <el-tag type="info" size="mini">NEW</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ $index }">
          <el-button 
            type="danger" 
            link 
            size="small" 
            @click="removeAd($index)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Picture, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const ads = defineModel<any[]>({ default: () => [] })

const createDialogVisible = ref(false)
const skusText = ref('')

const addAd = () => {
  skusText.value = ''
  createDialogVisible.value = true
}

const handleCreateAd = () => {
  if (!skusText.value.trim()) {
    ElMessage.warning('请输入 SKU')
    return
  }
  
  const skus = skusText.value.split('\n')
    .map(s => s.trim())
    .filter(s => s !== '')

  if (skus.length === 0) {
    ElMessage.warning('请输入有效的 SKU')
    return
  }

  const newAds = skus.map(sku => ({
    sku: sku,
    name: sku,
    status: 'ENABLED'
  }))

  ads.value = [...(ads.value || []), ...newAds]
  createDialogVisible.value = false
}

const removeAd = (index: number) => {
  ads.value = ads.value.filter((_, i) => i !== index)
}
</script>


<style scoped>
.ad-manager :deep(.el-table .cell) {
  padding: 4px 8px;
}

.add-btn {
  border: 1px solid var(--el-border-color);
  background: #fff;
  color: var(--el-text-color-regular);
  height: 24px;
  padding: 0 8px;
  font-size: 12px;
}
.add-btn:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}
</style>
