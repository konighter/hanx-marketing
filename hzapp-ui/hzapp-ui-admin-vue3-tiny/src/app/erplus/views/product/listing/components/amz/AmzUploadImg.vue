<template>
  <div class="amz-upload-box" :style="{ width, height }">
    <div v-if="isMain" class="main-badge">主图</div>
    
    <el-upload
      ref="uploadRef"
      :accept="fileType.join(',')"
      :action="uploadUrl"
      :auto-upload="true"
      :show-file-list="false"
      :http-request="httpRequest"
      :before-upload="beforeUpload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      class="upload-trigger"
    >
      <template v-if="modelValue">
        <div class="image-preview-container">
          <img :src="modelValue" class="preview-img" />
          <div class="image-overlay">
            <div class="overlay-icons">
              <el-icon @click.stop="imagePreview(modelValue)"><ZoomIn /></el-icon>
              <el-icon v-if="!disabled" @click.stop="triggerLocalUpload"><Edit /></el-icon>
              <el-icon v-if="!disabled" @click.stop="handleRemove"><Delete /></el-icon>
            </div>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="upload-empty">
          <el-icon class="plus-icon"><Plus /></el-icon>
        </div>
      </template>
    </el-upload>

    <!-- URL Input bar at bottom -->
    <div class="url-action-bar" v-if="!disabled" title="通过 URL 上传" @click.stop="handleUrlPrompt">
      <el-icon><Link /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage, ElMessageBox, type UploadProps } from 'element-plus';
import { Plus, ZoomIn, Edit, Delete, Link } from '@element-plus/icons-vue';
import { createImageViewer } from '@/components/ImageViewer';
import { useUpload } from '@/components/UploadFile/src/useUpload';

const props = defineProps({
  modelValue: { type: String, default: '' },
  isMain: { type: Boolean, default: false },
  width: { type: String, default: '104px' },
  height: { type: String, default: '104px' },
  fileSize: { type: Number, default: 5 }, // MB
  fileType: { type: Array, default: () => ['image/jpeg', 'image/png', 'image/gif'] },
  disabled: { type: Boolean, default: false },
  directory: { type: String, default: undefined }
});

const emit = defineEmits(['update:modelValue', 'remove']);

const uploadRef = ref();
const { uploadUrl, httpRequest } = useUpload(props.directory);

const handleUploadSuccess = (res: any) => {
  emit('update:modelValue', res.data);
  ElMessage.success('上传成功');
};

const handleUploadError = () => {
  ElMessage.error('上传失败，请重试');
};

const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isLtSize = rawFile.size / 1024 / 1024 < props.fileSize;
  const isTypeMatched = props.fileType.includes(rawFile.type);
  
  if (!isTypeMatched) ElMessage.error('上传图片格式不符');
  if (!isLtSize) ElMessage.error(`图片大小不能超过 ${props.fileSize}MB`);
  
  return isTypeMatched && isLtSize;
};

const imagePreview = (url: string) => {
  createImageViewer({
    zIndex: 9999,
    urlList: [url]
  });
};

const triggerLocalUpload = () => {
  const input = uploadRef.value?.$el.querySelector('input');
  input?.click();
};

const handleRemove = () => {
  emit('update:modelValue', '');
  emit('remove');
};

const handleUrlPrompt = () => {
  ElMessageBox.prompt('请输入图片 URL 链接', 'URL 上传', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^https?:\/\/.+/,
    inputErrorMessage: '请输入有效的 HTTP/HTTPS 链接'
  }).then(({ value }) => {
    emit('update:modelValue', value);
    ElMessage.success('URL 已设置');
  }).catch(() => {});
};
</script>

<style scoped lang="scss">
.amz-upload-box {
  position: relative;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  transition: all 0.2s;
  overflow: hidden; // Ensure internal content doesn't push the box
  display: flex;
  flex-direction: column;
  box-sizing: border-box;

  &:hover {
    border-color: #409eff;
    .url-action-bar {
      border-top-color: #409eff;
      background-color: #f5f7fa;
    }
  }

  .main-badge {
    position: absolute;
    top: 0;
    right: 0;
    background-color: #67c23a;
    color: white;
    font-size: 10px;
    padding: 1px 4px;
    border-radius: 0 0 0 4px;
    z-index: 2;
  }

  .upload-trigger {
    flex: 1;
    display: flex;
    overflow: hidden; // Important to prevent inner content expansion
    cursor: pointer;
    
    :deep(.el-upload) {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .image-preview-container {
    width: 100%;
    height: 100%;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;

    .preview-img {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
    }

    .image-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      opacity: 0;
      transition: opacity 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 3;

      .overlay-icons {
        display: flex;
        gap: 12px;
        color: white;
        font-size: 16px;
        
        .el-icon {
          cursor: pointer;
          &:hover { color: #409eff; }
        }
      }
    }

    &:hover .image-overlay {
      opacity: 1;
    }
  }

  .upload-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    
    .plus-icon {
      font-size: 20px;
      color: #909399;
    }
  }

  .url-action-bar {
    height: 24px;
    border-top: 1px solid #dcdfe6;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    background-color: #fafafa;
    color: #606266;
    font-size: 14px;
    transition: all 0.2s;
    border-radius: 0 0 4px 4px;

    &:hover {
      color: #409eff;
    }
  }
}
</style>
