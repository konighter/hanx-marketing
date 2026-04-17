<template>
  <div class="product-media-info">
    <div class="section-header mb-6">
      <div class="section-title">图片信息</div>
    </div>

    <div class="media-container bg-white p-6 rounded-lg border border-gray-100">
      <!-- Toolbar -->
      <div class="toolbar-row flex items-center justify-between mb-8">
        <div class="flex items-center gap-3">
          <el-button type="primary" size="default" @click="triggerBatchUpload" class="!bg-blue-600 border-none">
            <Icon icon="ep:upload" class="mr-1" /> 本地上传
          </el-button>
          <el-button size="default" @click="handleBatchUrlUpload">
            <Icon icon="ep:link" class="mr-1" /> URL上传
          </el-button>
          <el-button size="default" @click="handleClearAll">
            <Icon icon="ep:delete" class="mr-1" /> 清空图片
          </el-button>
        </div>
        
        <div class="drag-info-bar flex items-center gap-2 px-4 py-2 bg-blue-50 border border-blue-100 rounded text-blue-700 text-sm">
          <Icon icon="ep:rank" class="text-blue-500" />
          <span>支持批量上传，自动按序填充槽位</span>
        </div>
      </div>

      <!-- Image Grid (10 Slots: 1 Main + 9 Additional) -->
      <div class="image-grid flex flex-wrap gap-5">
        <!-- Slot 1: Main Image -->
        <div class="grid-item">
          <AmzUploadImg
            v-model="modelValue.mainImage"
            is-main
            width="120px"
            height="120px"
          />
        </div>

        <!-- Slots 2-10: Additional Images (9 total) -->
        <div v-for="index in 9" :key="index" class="grid-item">
          <AmzUploadImg
            :model-value="modelValue.additionalImages[index - 1]"
            @update:model-value="(val) => handleUpdateAdditional(index - 1, val)"
            width="120px"
            height="120px"
          />
        </div>
      </div>

      <div class="mt-8 p-4 bg-gray-50 rounded border border-dashed border-gray-200">
        <div class="text-xs text-gray-500 space-y-1">
          <p>• 建议图片尺寸：800x800 像素以上，比例 1:1</p>
          <p>• 支持格式：JPG, PNG, GIF，单张不超过 5MB</p>
          <p>• 第一张为商品主图，其余为细节/场景图</p>
        </div>
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
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AmzUploadImg from '@/app/erplus/views/product/listing/components/amz/AmzUploadImg.vue'
import { useUpload } from '@/components/UploadFile/src/useUpload'

defineOptions({ name: 'ProductMediaInfo' })

const modelValue = defineModel<{
  mainImage: string
  additionalImages: string[]
}>({ 
  required: true,
  default: () => ({
    mainImage: '',
    additionalImages: Array(9).fill('')
  })
})

const batchInputRef = ref<HTMLInputElement>()
const { httpRequest } = useUpload()

const handleUpdateAdditional = (index: number, val: string) => {
  const newImages = [...modelValue.value.additionalImages]
  newImages[index] = val || ''
  modelValue.value.additionalImages = newImages
}

const triggerBatchUpload = () => {
  batchInputRef.value?.click()
}

const handleBatchFileChange = async (e: Event) => {
  const files = (e.target as HTMLInputElement).files
  if (!files || files.length === 0) return

  const fileList = Array.from(files)
  ElMessage.info(`正在批量上传 ${fileList.length} 张图片...`)

  let filledCount = 0
  for (const file of fileList) {
    try {
      const res = await httpRequest({ file } as any)
      if (res && res.data) {
        fillNextAvailableSlot(res.data)
        filledCount++
      }
    } catch (err) {
      console.error('Upload failed for file', file.name, err)
    }
  }
  ElMessage.success(`成功上传并填充 ${filledCount} 张图片`)
  if (batchInputRef.value) batchInputRef.value.value = ''
}

const fillNextAvailableSlot = (url: string) => {
  if (!modelValue.value.mainImage) {
    modelValue.value.mainImage = url
    return
  }
  
  const additional = [...modelValue.value.additionalImages]
  for (let i = 0; i < 9; i++) {
    if (!additional[i]) {
      additional[i] = url
      modelValue.value.additionalImages = additional
      return
    }
  }
}

const handleBatchUrlUpload = () => {
  ElMessageBox.prompt('请输入图片 URL (多条请换行)', '批量 URL 上传', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: 'https://example.com/1.jpg\nhttps://example.com/2.jpg'
  }).then(({ value }) => {
    if (!value) return
    const urls = value.split('\n').map(u => u.trim()).filter(u => u.startsWith('http'))
    urls.forEach(url => fillNextAvailableSlot(url))
    ElMessage.success(`已处理 ${urls.length} 条 URL`)
  }).catch(() => {})
}

const handleClearAll = () => {
  ElMessageBox.confirm('确定要清空所有图片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    modelValue.value.mainImage = ''
    modelValue.value.additionalImages = Array(9).fill('')
    ElMessage.success('已清空')
  }).catch(() => {})
}
</script>

<style scoped lang="scss">
.product-media-info {
  .section-header {
    display: flex;
    align-items: center;
    border-left: 3px solid #3b82f6;
    padding-left: 12px;
    height: 16px;
    
    .section-title {
      font-size: 14px;
      font-weight: 700;
      color: #1a202c;
    }
  }

  .image-grid {
    .grid-item {
      transition: transform 0.2s;
      &:hover {
        transform: translateY(-2px);
      }
    }
  }
}
</style>
