<template>
  <div class="product-basic-info">
    <div class="flex gap-10">
      <!-- 左侧图片 -->
      <div class="w-48 shrink-0">
        <div class="section-header mb-3">产品图片</div>
        <div class="image-uploader-wrapper">
          <UploadImg
            v-model="modelValue.picUrl"
            height="192px"
            width="192px"
            class="lingxing-uploader"
          />
          <div class="upload-tip text-center text-xs text-gray-400 mt-3">建议比例 1:1，支持 JPG/PNG</div>
        </div>
      </div>

      <!-- 右侧字段网格 (3列) -->
      <div class="flex-1">
        <div class="section-header mb-3">核心信息</div>
        <el-row :gutter="40">
          <el-col :span="24">
            <el-form-item label="品名" prop="name" required>
              <el-input v-model="modelValue.name" placeholder="请输入完整品名" />
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="SKU 编码" prop="skuCode" v-if="entryMode === 'SKU'">
              <el-input v-model="modelValue.skuCode" placeholder="自动生成" />
            </el-form-item>
            <el-form-item label="SPU 编码" prop="code" v-else>
              <el-input v-model="modelValue.code" placeholder="父体编码" />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="分类" prop="categoryId" required>
              <el-cascader
                v-model="modelValue.categoryId"
                :options="categoryOptions"
                :props="{ label: 'name', value: 'id', checkStrictly: true, emitPath: false }"
                class="w-full"
                placeholder="搜索分类"
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="品牌" prop="brandId">
              <el-select v-model="modelValue.brandId" class="w-full" filterable clearable placeholder="关联品牌">
                <el-option v-for="item in brandOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="型号" prop="model">
              <el-input v-model="modelValue.model" placeholder="Model" />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="modelValue.unit" placeholder="Pcs" />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="modelValue.status" class="w-full">
                <el-option label="在售" :value="1" />
                <el-option label="下架" :value="2" />
                <el-option label="草稿" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>


          <el-col :span="8">
            <el-form-item label="负责人" prop="personInCharge">
              <el-select v-model="modelValue.personInCharge" class="w-full" filterable>
                <el-option v-for="item in userOptions" :key="item.id" :label="item.nickname" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8" v-if="entryMode === 'SKU'">
            <el-form-item label="关联 SPU" prop="spuId">
              <el-select v-model="modelValue.spuId" class="w-full" placeholder="可选关联" />
            </el-form-item>
          </el-col>
        </el-row>
</div>
</div>

<div class="mt-8">
<div class="section-header mb-3">详细描述</div>
      <Editor v-model="modelValue.description" height="300px" />
</div>
</div>
</template>

<script setup lang="ts">
import { Editor } from '@/components/Editor'

const modelValue = defineModel<any>({ required: true })

defineProps({
  categoryOptions: {
    type: Array,
    default: () => []
  },
  brandOptions: {
    type: Array,
    default: () => []
  },
  userOptions: {
    type: Array,
    default: () => []
  },
  entryMode: {
    type: String,
    default: 'SKU'
  }
})
</script>
<style scoped lang="scss">
.product-basic-info {
  :deep(.el-form-item) {
    margin-bottom: 24px;
    
    .el-form-item__label {
      font-weight: 500;
    }
  }
}
.section-header {
  font-size: 14px;
  font-weight: 700;
  color: #1a202c;
  position: relative;
  padding-left: 10px;
  display: flex;
  align-items: center;
  &::before {
    content: "";
    position: absolute;
    left: 0;
    top: 2px;
    bottom: 2px;
    width: 3px;
    background: #3b82f6;
    border-radius: 2px;
  }
}
</style>
