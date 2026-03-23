<template>
  <div>
    <el-divider content-position="left">变体信息</el-divider>

    <el-form-item label="变体属性">
      <el-checkbox-group v-model="selectedAttributes">
        <el-checkbox v-for="attr in salesAttributes" :value="attr" :key="attr.attrCode">
          {{ attr.attrName }}
        </el-checkbox>
      </el-checkbox-group>
    </el-form-item>

    <el-table :data="skus" border class="tabNumWidth" max-height="500" size="small"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column align="center" label="图片" min-width="65">
        <template #default="{ row }">
          <el-image style="width: 60px; height: 60px" :src="row.picUrl" :preview-src-list="[row.picUrl]" fit="cover"
            :preview-teleported="true" />
        </template>
      </el-table-column>
      <el-table-column label="属性" prop="id" width="100" :show-overflow-tooltip="true">
        <template #default="{ row: sku }">
          <el-space spacer="-">
            <span v-for="(p, i) in sku.properties" :key="i"> {{ p.valueName }}</span>
          </el-space>
        </template>
      </el-table-column>

      <el-table-column v-for="(attr, idx) in selectedAttributes" :key="idx" width="150" :label="getLabel(attr)"
        :show-overflow-tooltip="true">
        <template #default="{ row: sku, $index }">
          <el-form-item :error="errors[`${$index}-${attr.attrCode}`]" class="mb-0!">
            <el-input v-model="sku.attrValues[attr.attrCode]" class="w-100%" />
          </el-form-item>
        </template>
      </el-table-column>

      <el-table-column align="center" label="商品编码" min-width="168">
        <template #default="{ row }">
          <el-input v-model="row.barCode" class="w-100%" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="销售价(元)" min-width="168">
        <template #default="{ row, $index }">
          <el-form-item :error="errors[`${$index}-price`]" class="mb-0!">
            <el-input-number v-model="row.price" :min="0" :precision="2" :step="0.1" class="w-100%"
              controls-position="right" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column align="center" label="市场价(元)" min-width="168">
        <template #default="{ row }">
          <el-input-number v-model="row.marketPrice" :min="0" :precision="2" :step="0.1" class="w-100%"
            controls-position="right" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="成本价(元)" min-width="168">
        <template #default="{ row }">
          <el-input-number v-model="row.costPrice" :min="0" :precision="2" :step="0.1" class="w-100%"
            controls-position="right" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="库存" min-width="168">
        <template #default="{ row, $index }">
          <el-form-item :error="errors[`${$index}-stock`]" class="mb-0!">
            <el-input-number v-model="row.stock" :min="0" class="w-100%" controls-position="right" />
          </el-form-item>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'

const props = defineProps({
  attributes: {
    type: Array as PropType<any[]>,
    default: () => []
  },
  skus: {
    type: Array as PropType<any[]>,
    default: () => []
  },
  modelValue: {
    type: Array as PropType<any[]>,
    default: () => []
  },
  attributeValues: {
    type: Object as PropType<Record<string, any>>,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'selectionChange'])

const selectedAttributes = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const salesAttributes = computed(() => {
  return props.attributes.filter((i: any) => i.attrType === 'SALES_PROPERTY')
})

const selectedRows = ref<any[]>([])
const errors = ref<Record<string, string>>({})

const handleSelectionChange = (val: any) => {
  selectedRows.value = val
  emit('selectionChange', val)
}

const validate = () => {
  return new Promise((resolve, reject) => {
    errors.value = {} // Clear previous errors
    let hasError = false

    if (selectedRows.value.length === 0) {
      return resolve(true)
    }

    if (selectedAttributes.value.length === 0) {
      return reject('请选择变体属性')
    }

    for (const row of selectedRows.value) {
      const rowIndex = props.skus.indexOf(row)
      if (rowIndex === -1) continue

      // Check required attributes
      for (const attr of selectedAttributes.value) {
        if (!row.attrValues[attr.attrCode]) {
          errors.value[`${rowIndex}-${attr.attrCode}`] = `${attr.attrName}不能为空`
          hasError = true
        }
      }

      if (row.price === undefined || row.price === null || row.price === '') {
        errors.value[`${rowIndex}-price`] = '销售价不能为空'
        hasError = true
      }

      if (row.stock === undefined || row.stock === null || row.stock === '') {
        errors.value[`${rowIndex}-stock`] = '库存不能为空'
        hasError = true
      }

      if (row.originalStock !== undefined && row.stock > row.originalStock) {
        errors.value[`${rowIndex}-stock`] = `库存不能大于当前库存(${row.originalStock})`
        hasError = true
      }
    }

    if (hasError) {
      return reject('请完善变体信息')
    }

    resolve(true)
  })
}

const getLabel = (attr: any) => {
  return attr.isComposite ? attr.attrName + '(' + props.attributeValues[attr.attrCode] + ')' : attr.attrName
}

defineExpose({ validate })
</script>
