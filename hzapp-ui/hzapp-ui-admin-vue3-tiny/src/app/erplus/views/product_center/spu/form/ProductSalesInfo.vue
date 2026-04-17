<template>
  <div class="product-sales-info">
    <template v-if="modelValue.specType === 2">
      <div class="section-header mb-6 border-b pb-2 flex justify-between items-center">
        <span>规格属性配置</span>
        <el-button type="primary" link @click="$emit('add-property')">
          <Icon icon="ep:plus" class="mr-1" /> 添加规格项 (如颜色、尺寸)
        </el-button>
      </div>

      <div class="attribute-box bg-gray-50 p-6 rounded-md mb-8">
        <ProductAttributes 
          :property-list="propertyList" 
          @success="$emit('property-success', $event)"
        />
      </div>

      <div class="sku-table-wrapper">
        <SkuList
          ref="skuListRef"
          :prop-form-data="modelValue"
          :property-list="propertyList"
          :rule-config="skuRuleConfig"
        />
      </div>
    </template>

    <!-- 单规格 / SKU 模式下的价格与库存 -->
    <template v-else>
      <div class="section-header mb-6">销售信息</div>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="销售价" required>
            <el-input-number v-model="modelValue.price" :precision="2" :min="0" :controls="false" class="!w-full" placeholder="0.00" />
            <template #append>CNY</template>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="市场价">
            <el-input-number v-model="modelValue.marketPrice" :precision="2" :min="0" :controls="false" class="!w-full" placeholder="0.00" />
            <template #append>CNY</template>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="成本价">
            <el-input-number v-model="modelValue.costPrice" :precision="2" :min="0" :controls="false" class="!w-full" placeholder="0.00" />
            <template #append>CNY</template>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="当前库存">
            <el-input-number v-model="modelValue.stock" :min="0" :controls="false" class="!w-full" placeholder="0" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="条形码" prop="barCode">
            <el-input v-model="modelValue.barCode" placeholder="EAN / UPC" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 如果是组合产品，增加成分管理 -->
      <template v-if="modelValue.productType === 2">
        <div class="section-header mb-6 mt-8 border-b pb-2 flex justify-between items-center">
          <span>组合成分管理</span>
          <el-button type="primary" link @click="$emit('add-combo')">
            <Icon icon="ep:plus" class="mr-1" /> 添加成分商品
          </el-button>
        </div>
        <el-table :data="modelValue.comboItems" border size="small">
          <el-table-column label="成分 SKU" prop="code" min-width="150" />
          <el-table-column label="成分名称" prop="name" min-width="200" />
          <el-table-column label="组成数量" width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" size="small" class="!w-full" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link @click="modelValue.comboItems.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </template>
  </div>
</template>

<script setup lang="ts">
import ProductAttributes from './ProductAttributes.vue'
import { SkuList, PropertyAndValues, RuleConfig } from '@/app/erplus/views/product/spu/components/index'

const modelValue = defineModel<any>({ required: true })

defineProps({
  propertyList: {
    type: Array as () => PropertyAndValues[],
    default: () => []
  },
  skuRuleConfig: {
    type: Array as () => RuleConfig[],
    default: () => []
  }
})

const emit = defineEmits(['add-property', 'property-success', 'add-combo'])

// Expose internal refs if parent needs them (e.g. for validation)
const skuListRef = ref()
defineExpose({
  skuListRef
})
</script>
<style scoped lang="scss">
.product-sales-info {
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
