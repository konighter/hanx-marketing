<template>
  <div class="product-purchase-info">
    <div class="section-header mb-6">采购配置</div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-form-item label="默认采购价" label-width="100px">
          <el-input-number v-model="modelValue.attributes.purchase.purchasePrice" :precision="2" :controls="false" class="!w-full" placeholder="0.00" />
          <template #append>CNY</template>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="采购起订量" label-width="100px">
          <el-input-number v-model="modelValue.attributes.purchase.moq" :min="1" :controls="false" class="!w-full" placeholder="1" />
          <template #append>MOQ</template>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="默认采购周期" label-width="100px">
          <el-input-number v-model="modelValue.attributes.purchase.leadTime" :min="1" :controls="false" class="!w-full" placeholder="7" />
          <template #append>天</template>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 规格信息 (Net Dimensions) -->
    <div class="mt-8">
      <div class="flex items-center gap-4 mb-4">
        <div class="section-header">规格信息</div>
        <el-radio-group v-model="modelValue.attributes.logistics.dimUnit" size="small">
          <el-radio-button label="cm">公制</el-radio-button>
          <el-radio-button label="inch">英制</el-radio-button>
        </el-radio-group>
      </div>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="净品尺寸" label-width="100px">
            <div class="flex items-center gap-1">
              <el-input-number v-model="modelValue.attributes.logistics.itemDim.length" :precision="1" :controls="false" placeholder="长" />
              <span class="text-gray-400 px-1">×</span>
              <el-input-number v-model="modelValue.attributes.logistics.itemDim.width" :precision="1" :controls="false" placeholder="宽" />
              <span class="text-gray-400 px-1">×</span>
              <el-input-number v-model="modelValue.attributes.logistics.itemDim.height" :precision="1" :controls="false" placeholder="高" />
              <span class="ml-2 text-gray-400">{{ modelValue.attributes.logistics.dimUnit === 'cm' ? 'cm' : 'inch' }}</span>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="净品重量" label-width="100px">
            <div class="flex items-center gap-2">
              <el-input-number v-model="modelValue.attributes.logistics.itemDim.weight" :precision="2" :controls="false" class="!w-full" placeholder="0.00" />
              <span class="text-gray-400 whitespace-nowrap">{{ modelValue.attributes.logistics.dimUnit === 'cm' ? 'g' : 'oz' }}</span>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <div class="mt-8">
      <div class="flex items-center justify-between mb-4">
        <div class="section-header">包装 & 装箱方案</div>
        <el-button type="primary" size="small" @click="addScheme">
          <Icon icon="ep:plus" class="mr-1" />添加方案
        </el-button>
      </div>

      <el-table :data="modelValue.attributes.logistics.packingSchemes" border stripe size="small" class="packing-table">
        <el-table-column label="方案名称" min-width="120">
          <template #default="{ row }">
            <el-input v-model="row.name" placeholder="如: 标准装箱" />
          </template>
        </el-table-column>
        <el-table-column label="单箱数量" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" :controls="false" class="!w-full" />
          </template>
        </el-table-column>
        <el-table-column label="外箱规格 (长*宽*高)" min-width="220">
          <template #default="{ row }">
            <div class="flex items-center gap-1">
              <el-input-number v-model="row.outerBoxDim.length" :precision="1" :controls="false" placeholder="长" />
              <span>*</span>
              <el-input-number v-model="row.outerBoxDim.width" :precision="1" :controls="false" placeholder="宽" />
              <span>*</span>
              <el-input-number v-model="row.outerBoxDim.height" :precision="1" :controls="false" placeholder="高" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="包装规格 (长*宽*高)" min-width="220">
          <template #default="{ row }">
            <div class="flex items-center gap-1">
              <el-input-number v-model="row.pkgDim.length" :precision="1" :controls="false" placeholder="长" />
              <span>*</span>
              <el-input-number v-model="row.pkgDim.width" :precision="1" :controls="false" placeholder="宽" />
              <span>*</span>
              <el-input-number v-model="row.pkgDim.height" :precision="1" :controls="false" placeholder="高" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单箱重量" width="120">
          <template #header>
             <span>单箱重量 ({{ modelValue.attributes.logistics.dimUnit === 'cm' ? 'kg' : 'lb' }})</span>
          </template>
          <template #default="{ row }">
            <el-input-number v-model="row.boxWeight" :precision="2" :controls="false" class="!w-full" />
          </template>
        </el-table-column>
        <el-table-column label="单品毛重" width="120">
          <template #header>
             <span>单品毛重 ({{ modelValue.attributes.logistics.dimUnit === 'cm' ? 'kg' : 'lb' }})</span>
          </template>
          <template #default="{ row }">
            <el-input-number v-model="row.grossWeight" :precision="2" :controls="false" class="!w-full" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" fixed="right" align="center">
          <template #default="{ $index }">
            <el-button type="danger" link @click="removeScheme($index)">
              <Icon icon="ep:delete" />
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
const modelValue = defineModel<any>({ required: true })

const addScheme = () => {
  if (!modelValue.value.attributes.logistics.packingSchemes) {
    modelValue.value.attributes.logistics.packingSchemes = []
  }
  modelValue.value.attributes.logistics.packingSchemes.push({
    name: '',
    quantity: 1,
    outerBoxDim: { length: 0, width: 0, height: 0, unit: modelValue.value.attributes.logistics.dimUnit },
    pkgDim: { length: 0, width: 0, height: 0, unit: modelValue.value.attributes.logistics.dimUnit },
    boxWeight: 0,
    grossWeight: 0
  })
}

const removeScheme = (index: number) => {
  modelValue.value.attributes.logistics.packingSchemes.splice(index, 1)
}
</script>

<style scoped lang="scss">
.product-purchase-info {
  :deep(.el-form-item) {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    
    .el-form-item__label {
      margin-bottom: 0 !important;
      line-height: 32px;
      display: flex;
      align-items: center;
      justify-content: flex-start;
    }
    
    .el-form-item__content {
      flex: 1;
      margin-left: 0 !important;
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

.packing-table {
  :deep(.el-input-number .el-input__inner) {
    text-align: left;
    padding: 0 4px;
  }
}
</style>
