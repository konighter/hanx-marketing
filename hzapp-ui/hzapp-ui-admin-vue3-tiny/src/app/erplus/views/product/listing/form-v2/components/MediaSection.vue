<template>
  <div class="media-section-v2">
    <!-- Toolbar -->
    <div class="toolbar-row">
      <div class="btn-group">
        <el-button type="primary" size="small" @click="triggerBatchUpload">
          <el-icon><Upload /></el-icon> 本地上传
        </el-button>
        <el-button size="small" @click="handleBatchUrlUpload">
          <el-icon><Link /></el-icon> URL上传
        </el-button>
        <el-button size="small" @click="handleProductImageSelect">
          <el-icon><Picture /></el-icon> 商品图片
        </el-button>
        <el-button size="small" @click="handleClearAll">
          <el-icon><Delete /></el-icon> 清空图片
        </el-button>
      </div>
      
      <div class="drag-info-bar">
        <el-icon class="drag-icon"><Rank /></el-icon>
        <span>支持拖拽上传 <span class="sub-text">(将图片拖拽到图片区域即可)</span></span>
      </div>

      <el-dropdown trigger="click" class="guide-dropdown">
        <span class="dropdown-link">
          图片建议 <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>主图建议：白色背景，1000x1000px以上</el-dropdown-item>
            <el-dropdown-item>辅图建议：展示产品细节、使用场景</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- Image Grid (9 Slots: 1 Main + 8 Additional) -->
    <div class="image-grid">
      <!-- Slot 1: Main Image -->
      <div class="grid-item">
        <AmzUploadImg
          v-model="modelValue.mainImage"
          is-main
          width="104px"
          height="104px"
        />
      </div>

      <!-- Slots 2-9: Additional Images -->
      <div v-for="index in 8" :key="index" class="grid-item">
        <AmzUploadImg
          :model-value="modelValue.additionalImages[index - 1]"
          @update:model-value="(val) => handleUpdateAdditional(index - 1, val)"
          width="104px"
          height="104px"
        />
      </div>
    </div>

    <!-- Hidden multi-file input for batch upload -->
    <input
      ref="batchInputRef"
      type="file"
      multiple
      accept="image/*"
      style="display: none"
      @change="handleBatchFileChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Upload, Link, Picture, Delete, Rank, ArrowDown } from '@element-plus/icons-vue';
import AmzUploadImg from '../../components/amz/AmzUploadImg.vue';
import { useUpload } from '@/components/UploadFile/src/useUpload';

const modelValue = defineModel<{
  mainImage: string;
  additionalImages: string[];
}>({ required: true });

const batchInputRef = ref<HTMLInputElement>();
const { httpRequest } = useUpload();

const handleUpdateAdditional = (index: number, val: string) => {
  const newImages = [...modelValue.value.additionalImages];
  if (val) {
    newImages[index] = val;
  } else {
    // If clearing, we can either leave empty or splice. 
    // Usually in index-fixed grid, we just clear the value at that index.
    newImages[index] = '';
  }
  modelValue.value.additionalImages = newImages;
};

const triggerBatchUpload = () => {
  batchInputRef.value?.click();
};

const handleBatchFileChange = async (e: Event) => {
  const files = (e.target as HTMLInputElement).files;
  if (!files || files.length === 0) return;

  const fileList = Array.from(files);
  ElMessage.info(`正在批量上传 ${fileList.length} 张图片...`);

  let filledCount = 0;
  for (const file of fileList) {
    try {
      // Mocking/Using httpRequest logic
      const res = await httpRequest({ file } as any);
      if (res && res.data) {
        fillNextAvailableSlot(res.data);
        filledCount++;
      }
    } catch (err) {
      console.error('Upload failed for file', file.name, err);
    }
  }
  ElMessage.success(`成功上传并填充 ${filledCount} 张图片`);
  if (batchInputRef.value) batchInputRef.value.value = '';
};

const fillNextAvailableSlot = (url: string) => {
  if (!modelValue.value.mainImage) {
    modelValue.value.mainImage = url;
    return;
  }
  
  const additional = [...modelValue.value.additionalImages];
  for (let i = 0; i < 8; i++) {
    if (!additional[i]) {
      additional[i] = url;
      modelValue.value.additionalImages = additional;
      return;
    }
  }
  ElMessage.warning('图片位已满');
};

const handleBatchUrlUpload = () => {
  ElMessageBox.prompt('请输入图片 URL (多条请换行)', '批量 URL 上传', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: 'https://example.com/1.jpg\nhttps://example.com/2.jpg'
  }).then(({ value }) => {
    const urls = value.split('\n').map(u => u.trim()).filter(u => u.startsWith('http'));
    urls.forEach(url => fillNextAvailableSlot(url));
    ElMessage.success(`已添加 ${urls.length} 条 URL`);
  }).catch(() => {});
};

const handleProductImageSelect = () => {
  ElMessage.info('商品图片选择器开发中...');
};

const handleClearAll = () => {
  ElMessageBox.confirm('确定要清空所有图片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    modelValue.value.mainImage = '';
    modelValue.value.additionalImages = Array(8).fill('');
    ElMessage.success('已清空');
  }).catch(() => {});
};
</script>

<style scoped lang="scss">
.media-section-v2 {
  padding: 8px 0;

  .toolbar-row {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;

    .btn-group {
      display: flex;
      gap: 8px;
    }

    .drag-info-bar {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 0 16px;
      height: 32px;
      background-color: #fffaf0; // Light orange/yellow
      border: 1px solid #ffe4b5;
      border-radius: 4px;
      color: #8a6d3b;
      font-size: 13px;
      flex: 1;

      .drag-icon {
        font-size: 16px;
        color: #f39c12;
      }

      .sub-text {
        color: #999;
        font-size: 12px;
        margin-left: 4px;
      }
    }

    .guide-dropdown {
      .dropdown-link {
        cursor: pointer;
        color: #909399;
        font-size: 13px;
        display: flex;
        align-items: center;
        gap: 4px;
        &:hover { color: #409eff; }
      }
    }
  }

  .image-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    
    .grid-item {
      flex-shrink: 0;
    }
  }
}
</style>
