<template>
  <el-card shadow="never">
    <el-tabs v-model="activeName" class="demo-tabs">
      <el-tab-pane label="基础信息" name="spuInfo">
        <el-form :model="spuInfo" label-width="120px">
          <el-form-item label="商品名称">
            <el-input v-model="spuInfo.name" disabled />
          </el-form-item>
          <el-form-item label="商品编码">
            <el-input v-model="spuInfo.code" disabled />
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="spuInfo.categoryName" disabled />
          </el-form-item>
          <el-form-item label="品牌">
            <el-input v-model="spuInfo.brandName" disabled />
          </el-form-item>
        </el-form>

        <el-divider content-position="left">平台信息</el-divider>
        <el-form ref="formRef" :model="formData" label-width="120px" :inline="true">
          <el-form-item label="平台" prop="platformId" :rules="[{ required: true, message: '请选择平台', trigger: 'change' }]">
            <el-select
v-model="formData.platformId" placeholder="请选择平台" clearable filterable
              @change="handlePlatformChange">
              <el-option v-for="item in platformList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="店铺" prop="shopIds" :rules="[{ required: true, message: '请选择店铺', trigger: 'change' }]">
            <el-select
v-model="formData.shopIds" placeholder="请选择店铺" multiple clearable filterable
              @change="handleShopChange">
              <el-option v-for="item in shopList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item
label="站点分类" prop="categoryId"
            :rules="[{ required: true, message: '请选择站点分类', trigger: 'change' }]">
            <el-cascader
v-model="formData.categoryId" :options="categoryList"
              :props="{ label: 'categoryName', value: 'categoryId', checkStrictly: true, emitPath: true }" clearable
              filterable placeholder="请选择站点分类" class="w-full" @change="handleCategoryChange" />
          </el-form-item>

          <el-divider content-position="left">产品属性</el-divider>
          <el-row :gutter="20">
            <el-col v-for="attr in productAttributes" :key="attr.field" :span="12">
              <el-form-item :label="attr.label" :prop="`attributes.${attr.field}`" :rules="attr.rules">
                <AttributeRenderer :schema="attr" v-model="formData.attributes[attr.field]" />
                <div v-if="attr.description" class="text-gray-400 text-xs mt-1">{{ attr.description }}</div>
              </el-form-item>
            </el-col>
          </el-row>

          <VariationAttributes
ref="variationRef" v-model="formData.variationAttributes" :attributes="attributeList"
            :skus="spuInfo.skus" :attribute-values="formData.attributes" @selection-change="handleSkuSelection" />

          <el-form-item class="mt-4">
            <el-button type="primary" @click="handleSubmit">刊登</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as SpuApi from '@/app/erplus/api/product/spu'
import * as CategoryApi from '@/app/erplus/api/product/category'
import { ShopApi } from '@/app/erplus/api/system/shop'
import { SellPlatformApi } from '@/app/erp/api/sellplatform'
import { handleTree } from '@/utils/tree'
import AttributeRenderer from './components/AttributeRenderer.vue'
import VariationAttributes from './components/VariationAttributes.vue'
import { normalizeAttribute, type AttributeSchema } from './adapter'

const route = useRoute()
const activeName = ref('spuInfo')
const formRef = ref()
const variationRef = ref()

const spuInfo = ref<any>({})
const platformList = ref<any[]>([])
const shopList = ref<any[]>([])
const categoryList = ref<any[]>([])
const attributeList = ref<any[]>([])

const formData = reactive({
  platformId: undefined,
  shopIds: [] as any[],
  categoryId: undefined,
  attributes: {} as Record<string, any>,
  skus: [] as any[],
  variationAttributes: [] as any[]
})

// Computed property for product attributes (non-sales attributes)
const productAttributes = computed<AttributeSchema[]>(() => {
  return attributeList.value
    .filter((attr: any) => attr.attrType !== 0) // 0 is SALES_PROPERTY
    .map(normalizeAttribute)
})

const loadData = async () => {
  const id = route.params.id
  if (id) {
    spuInfo.value = await SpuApi.getSpu(Number(id))
    // Initialize originalStock for validation
    if (spuInfo.value.skus) {
      spuInfo.value.skus.forEach((sku: any) => {
        sku.originalStock = sku.stock
      })
    }
  }
  platformList.value = await SellPlatformApi.getSellPlatformListCache() || []
}

const handlePlatformChange = async () => {
  formData.shopIds = []
  formData.categoryId = undefined
  attributeList.value = []
  shopList.value = []
  if (formData.platformId) {
    try {
      shopList.value = await ShopApi.getPlatformShop(formData.platformId) || []
    } catch (e) {
      console.error(e)
    }
  }
}

const handleShopChange = async () => {
  formData.categoryId = undefined
  attributeList.value = []
  if (formData.shopIds.length > 0) {
    const name = spuInfo.value.categoryName
    const result = await CategoryApi.getCrossCategories({
      platformId: formData.platformId,
      shopIds: formData.shopIds,
      name
    } as any) || {}

    if (result.categories && result.categories.length > 0) {
      categoryList.value = handleTree(result.categories, 'categoryId', 'parentCategoryId')
    }
  }
}

const handleCategoryChange = async () => {
  attributeList.value = []
  // Keep existing attribute values if possible, or reset? v1 keeps them.
  // formData.attributes = {} 
  if (formData.categoryId) {
    // Using renderCategoryAttributes to get attributes with values if available (though here we are loading fresh for category)
    // v1 uses renderCategoryAttributes(categoryId, platformId, shopId, spuId)
    // Note: categoryId is array in v1 cascader, but here tree-select might be single value?
    // v1: const loadCategoryAttributes = async (categoryId: string[], ...)
    // formData.categoryId in v1 is cascader array.
    // In v2 template I used tree-select with emitPath (default false usually unless props set).
    // Let's assume we need to pass array.

    const catIds = Array.isArray(formData.categoryId) ? formData.categoryId : [formData.categoryId]
    const shopId = formData.shopIds[0] // v1 uses first shopId? v1 passes shopId (singular) to renderCategoryAttributes but shopIds (plural) to getCrossCategories.
    // v1 call: renderCategoryAttributes(categoryId, platformId, shopId, spuId)

    try {
      attributeList.value = await CategoryApi.renderCategoryAttributes(
        catIds,
        formData.platformId!,
        shopId,
        spuInfo.value.id
      ) || []

      // Initialize default values
      attributeList.value.forEach((a: any) => {
        if (formData.attributes[a.attrCode] === undefined && a.attrValue != null) {
          formData.attributes[a.attrCode] = a.attrValue.value
        }
      })
    } catch (e) {
      console.error(e)
    }
  }
}

const handleSkuSelection = (val: any[]) => {
  formData.skus = val
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    await variationRef.value?.validate()

    const data = {
      ...spuInfo.value,
      ...formData,
      skus: formData.skus
    }
    await SpuApi.spuListing(data)
    ElMessage.success('刊登成功')
    // Navigate back or reset
  } catch (e: any) {
    console.error(e)
    if (typeof e === 'string') {
      ElMessage.warning(e)
    } else if (typeof e === 'object' && Object.keys(e).length > 0) {
      // Element Plus validation error object { field: [{message: '...', ...}] }
      const firstField = Object.keys(e)[0]
      const firstError = e[firstField][0]?.message || '请完善表单信息'
      ElMessage.warning(firstError)
    } else {
      ElMessage.warning('请完善表单信息')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
:deep(.el-form-item__label) {
  white-space: nowrap !important;
  text-overflow: ellipsis !important;
  overflow: hidden !important;
}
</style>
