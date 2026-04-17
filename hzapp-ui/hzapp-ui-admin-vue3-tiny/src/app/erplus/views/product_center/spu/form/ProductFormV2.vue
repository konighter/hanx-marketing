<template>
  <div class="product-form-v2 flex flex-col h-full bg-gray-50">
    <!-- 顶部模式切换区 -->
    <div class="form-header bg-white border-b px-8 py-4 flex items-center justify-between sticky top-0 z-20">
      <div class="header-left flex items-center gap-8">
        <div class="mode-selector flex items-center bg-gray-100 p-1 rounded-md">
          <div 
            class="mode-btn px-4 py-1.5 rounded cursor-pointer text-sm font-medium transition-all"
            :class="{ 'bg-white shadow-sm text-blue-600': entryMode === 'SKU' }"
            @click="entryMode = 'SKU'"
          >
            SKU 模式
          </div>
          <div 
            class="mode-btn px-4 py-1.5 rounded cursor-pointer text-sm font-medium transition-all"
            :class="{ 'bg-white shadow-sm text-blue-600': entryMode === 'SPU' }"
            @click="entryMode = 'SPU'"
          >
            SPU 模式
          </div>
        </div>

        <div class="flex items-center gap-4">
          <span class="text-xs text-gray-400 uppercase tracking-wider font-bold">详细类型</span>
          <el-radio-group v-model="specificType" size="default">
            <template v-if="entryMode === 'SKU'">
              <el-radio label="ORDINARY">普通商品</el-radio>
              <el-radio label="COMBO">产品组合</el-radio>
            </template>
            <template v-else>
              <el-radio label="SINGLE">单规格 SPU</el-radio>
              <el-radio label="MULTI">多规格 SPU</el-radio>
            </template>
          </el-radio-group>
        </div>
      </div>

      <div class="header-right flex items-center gap-3">
        <el-button @click="$emit('close')">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading" class="!bg-blue-600 border-none px-6">
          <Icon icon="ep:check" class="mr-1" /> 保存商品
        </el-button>
      </div>
    </div>

    <!-- 下方主体：侧边导航 + 内容 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 侧边导航 -->
      <div class="aside-nav w-44 border-r bg-white flex flex-col">
        <div
          v-for="item in navItems"
          :key="item.value"
          v-show="item.show === undefined || item.show"
          class="nav-item px-6 py-3.5 cursor-pointer transition-all border-l-4"
          :class="activeNav === item.value ? 'active bg-blue-50 border-blue-600 text-blue-600' : 'border-transparent text-gray-600 hover:bg-gray-50'"
          @click="activeNav = item.value"
        >
          <span class="text-sm font-semibold tracking-wide">{{ item.label }}</span>
        </div>
      </div>

      <!-- 主表单区域 -->
      <div class="main-content flex-1 overflow-y-auto bg-white p-8">
        <el-form ref="formRef" :model="formData" :rules="rules" label-position="right" label-width="80px" class="lingxing-form">
          <ProductBasicInfo
            v-show="activeNav === 'basic'"
            v-model="formData"
            :category-options="categoryOptions"
            :brand-options="brandOptions"
            :user-options="userOptions"
            :entry-mode="entryMode"
          />

          <ProductSalesInfo
            v-show="activeNav === 'variants'"
            ref="salesInfoRef"
            v-model="formData"
            :property-list="propertyList"
            :sku-rule-config="skuRuleConfig"
            @add-property="handleAddProperty"
            @property-success="handlePropertySuccess"
            @add-combo="handleAddComboItem"
          />

          <ProductPurchaseInfo
            v-show="activeNav === 'purchase'"
            v-model="formData"
          />

          <ProductLogisticsInfo
            v-show="activeNav === 'logistics'"
            v-model="formData"
          />

          <ProductQCInfo
            v-show="activeNav === 'qc'"
            v-model="formData"
          />

          <ProductMediaInfo
            v-show="activeNav === 'media'"
            v-model="formData.attributes.media"
          />

          <ProductMaterialInfo
            v-show="activeNav === 'materials'"
            v-model="formData.materialItems"
          />
        </el-form>
      </div>
    </div>

    <!-- 属性选择器 Dialog (用于多规格) -->
    <PropertyAndValuesAddForm ref="propertyAddFormRef" @success="onAddPropertySuccess" />

    <!-- SKU 选择器 Dialog (用于组合产品) -->
    <SkuTableSelect ref="skuSelectRef" @success="onSkuSelectSuccess" />
  </div>
</template>

<script setup lang="ts">
import * as UserApi from '@/api/system/user'
import * as ProductCategoryApi from '@/app/erplus/api/product/category'
import * as ProductBrandApi from '@/app/erplus/api/product/brand'
import {
  getPropertyList,
  PropertyAndValues,
  RuleConfig
} from '@/app/erplus/views/product/spu/components/index'
import SkuTableSelect from '../components/SkuTableSelect.vue'
import PropertyAndValuesAddForm from './ProductPropertyAddForm.vue'
import { handleTree } from '@/utils/tree'

// Sub-components
import ProductBasicInfo from './ProductBasicInfo.vue'
import ProductSalesInfo from './ProductSalesInfo.vue'
import ProductPurchaseInfo from './ProductPurchaseInfo.vue'
import ProductLogisticsInfo from './ProductLogisticsInfo.vue'
import ProductQCInfo from './ProductQCInfo.vue'
import ProductMediaInfo from './ProductMediaInfo.vue'
import ProductMaterialInfo from './ProductMaterialInfo.vue'
import * as ProductSpuApi from '@/app/erplus/api/product/spu'

const message = useMessage()

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
})

const loading = ref(false)
const activeNav = ref('basic')
const entryMode = ref('SKU') // SKU | SPU
const specificType = ref('ORDINARY') // ORDINARY | COMBO | SINGLE | MULTI
const propertyAddFormRef = ref()
const skuSelectRef = ref()
const formRef = ref()
const salesInfoRef = ref()

const propertyList = ref<PropertyAndValues[]>([])

const formData = reactive({
  id: undefined,
  picUrl: '',
  name: '',
  code: '', // SPU Code
  skuCode: '', // SKU Code (for single spec)
  productType: 1, // ORDINARY=1, COMBO=2
  categoryId: undefined,
  status: 1,
  brandId: undefined,
  model: '',
  unit: 'pc',
  material: '',
  tags: [],
  developer: undefined,
  personInCharge: undefined,
  spuId: undefined,
  sort: 0,
  properties: '',
  description: '',
  comboItems: [] as any[], // 组合产品成分项
  materialItems: [] as any[], // 耗材/配件项 (BOM)
  
  // 规格 / 销售
  specType: 1, // SINGLE=1, MULTI=2
  skus: [] as any[],
  price: 0,
  marketPrice: 0,
  costPrice: 0,
  stock: 0,
  barCode: '',

  // 统一动态属性
  attributes: {
    purchase: {
      purchasePrice: 0,
      moq: 1,
      leadTime: 7
    },
    logistics: {
      itemDim: { length: 0, width: 0, height: 0, weight: 0, unit: 'cm', weightUnit: 'g' },
      dimUnit: 'cm',
      packingSchemes: [
        { 
          name: '默认箱规', 
          quantity: undefined, 
          outerBoxDim: { length: 0, width: 0, height: 0, unit: 'cm' },
          pkgDim: { length: 0, width: 0, height: 0, unit: 'cm' },
          boxWeight: undefined,
          boxWeightUnit: 'kg',
          grossWeight: undefined,
          grossWeightUnit: 'g'
        }
      ]
    },
    customs: {
      materialCn: '', materialEn: '', usageCn: '', usageEn: '', brandType: '', exportBenefit: '', internalCode: '',
      nature: [] as string[],
      declarationNameCn: '',
      declarationNameEn: '',
      declarationPrice: 0,
      declarationCurrency: 'USD',
      declarationHsCode: '',
      declarationModel: '',
      originCountry: '',
      domesticSource: '',
      declarationUnit: '',
      declarationElement: '',
      taxExemption: '',
      manufacturerName: '',
      manufacturerCode: '',
      clearanceModel: '',
      ingredientRemark: '',
      weavingMethod: '',
      clearancePicUrl: '',
      clearanceFees: [] as any[],
      taxInfos: [] as any[]
    },
    compliance: {
      qcStandards: '',
      qcRemark: ''
    },
    media: {
      mainImage: '',
      additionalImages: Array(9).fill('')
    }
  } as Record<string, any>
})

const navItems = computed(() => [
  { label: '基本信息', value: 'basic' },
  { 
    label: formData.specType === 2 ? '规格信息' : '销售信息', 
    value: 'variants' 
  },
  { label: '采购配置', value: 'purchase' },
  { label: '物流报关清关', value: 'logistics' },
  { label: '质检标准', value: 'qc' },
  { label: '图片信息', value: 'media' },
  { 
    label: '配件 & 耗材', 
    value: 'materials',
    show: entryMode.value === 'SKU'
  }
])

const skuRuleConfig: RuleConfig[] = [
  { name: 'price', rule: (arg) => arg >= 0, message: '价格必须大于等于0' }
]

const rules = reactive({
  name: [{ required: true, message: '品名必填', trigger: 'blur' }],
  categoryId: [{ required: true, message: '分类必填', trigger: 'change' }],
  brandId: [{ required: true, message: '品牌必填', trigger: 'change' }],
  picUrl: [{ required: true, message: '封面图必填', trigger: 'change' }],
  description: [{ required: true, message: '详情描述必填', trigger: 'blur' }],
  sort: [{ required: true, message: '排序必填', trigger: 'blur' }]
})

const categoryOptions = ref([])
const brandOptions = ref([])
const userOptions = ref([])

/** 监听模式变化，同步到后端逻辑字段 */
watch([entryMode, specificType], ([newMode, newSpec]) => {
  if (newMode === 'SKU') {
    formData.specType = 1 // SKU模式默认单规格（或是变体中的一个）
    formData.productType = newSpec === 'COMBO' ? 2 : 1
  } else {
    formData.specType = newSpec === 'MULTI' ? 2 : 1
    formData.productType = 1
  }
})

/** 添加属性项 */
const handleAddProperty = () => {
  propertyAddFormRef.value.open()
}

const onAddPropertySuccess = (property: PropertyAndValues) => {
  propertyList.value.push(property)
}

const handlePropertySuccess = (list: PropertyAndValues[]) => {
  propertyList.value = list
}

/** 添加商品成分 (组合产品) */
const handleAddComboItem = () => {
  skuSelectRef.value.open()
}

/** 成分选择回调 */
const onSkuSelectSuccess = (selection: any[]) => {
  selection.forEach((sku: any) => {
    if (!formData.comboItems.some(item => item.childSkuId === sku.id)) {
      formData.comboItems.push({
        childSkuId: sku.id,
        code: sku.code,
        name: sku.name || '未命名商品',
        quantity: 1
      })
    }
  })
}

/** 属性清理：移除空模块，避免保存无意义的默认对象 */
const pruneAttributes = (attrs: any) => {
  if (!attrs) return {}
  const pruned = JSON.parse(JSON.stringify(attrs))
  
  // 1. 清理图片信息 (Media)
  if (pruned.media) {
    const hasMain = !!pruned.media.mainImage
    const hasAdditional = pruned.media.additionalImages && pruned.media.additionalImages.some((img: string) => !!img)
    if (!hasMain && !hasAdditional) {
      delete pruned.media
    }
  }
  
  // 2. 清理质检标准 (Compliance)
  if (pruned.compliance) {
    if (!pruned.compliance.qcStandards && !pruned.compliance.qcRemark) {
      delete pruned.compliance
    }
  }

  // 3. 清理采购信息 (Purchase) - 如果都是默认值且未修改
  if (pruned.purchase) {
    if (pruned.purchase.purchasePrice === 0 && (pruned.purchase.moq === 1 || pruned.purchase.moq === undefined) && (pruned.purchase.leadTime === 7 || pruned.purchase.leadTime === undefined)) {
      delete pruned.purchase
    }
  }

  // 4. 清理物流信息 (Logistics)
  if (pruned.logistics) {
    const dim = pruned.logistics.itemDim
    const isDimEmpty = !dim.length && !dim.width && !dim.height && !dim.weight
    const isSchemeEmpty = pruned.logistics.packingSchemes && pruned.logistics.packingSchemes.length === 1 && 
                          !pruned.logistics.packingSchemes[0].quantity && 
                          !pruned.logistics.packingSchemes[0].boxWeight
    if (isDimEmpty && isSchemeEmpty) {
      delete pruned.logistics
    }
  }
  
  // 5. 清理报关信息 (Customs)
  if (pruned.customs) {
    // 如果关键报关字段都为空，则清理
    const crucialFields = ['materialCn', 'materialEn', 'usageCn', 'usageEn', 'declarationNameCn', 'declarationHsCode']
    const isEmpty = crucialFields.every(f => !pruned.customs[f])
    if (isEmpty) {
      delete pruned.customs
    }
  }

  return pruned
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
  } catch (fields: any) {
    message.error('表单校验未通过，请检查必填项')
    // 自动跳转到第一个错误字段所在的页签
    const firstField = Object.keys(fields)[0]
    const basicFields = ['name', 'categoryId', 'brandId', 'picUrl', 'description', 'sort', 'code', 'unit']
    const variantsFields = ['price', 'marketPrice', 'costPrice', 'stock', 'barCode', 'skuCode']
    
    if (basicFields.includes(firstField)) {
      activeNav.value = 'basic'
    } else if (variantsFields.some(f => firstField.startsWith(f) || firstField.includes('skus'))) {
      activeNav.value = 'variants'
    } else if (firstField.includes('purchase')) {
      activeNav.value = 'purchase'
    } else if (firstField.includes('logistics') || firstField.includes('customs')) {
      activeNav.value = 'logistics'
    } else if (firstField.includes('compliance')) {
      activeNav.value = 'qc'
    } else if (firstField.includes('media')) {
      activeNav.value = 'media'
    } else if (firstField.includes('comboItems') || firstField.includes('materialItems')) {
      activeNav.value = 'materials'
    }
    return
  }

  try {
    loading.value = true
    
    // 创建提交数据的深拷贝，避免修改响应式数据导致 UI 崩溃或数据错误
    const submitData = JSON.parse(JSON.stringify(formData))
    
    // 同步 SKU 列表 (对于单规格，创建一个默认 SKU)
    if (submitData.specType === 1) {
      const skuData = {
        price: Math.round(formData.price * 100), // 元转分
        marketPrice: Math.round(formData.marketPrice * 100),
        costPrice: Math.round(formData.costPrice * 100),
        stock: formData.stock,
        barCode: formData.barCode,
        code: entryMode.value === 'SKU' ? formData.skuCode : formData.code,
        picUrl: formData.picUrl,
        name: formData.name,
        type: formData.productType, // 1-普通, 2-组合
        comboItems: formData.productType === 2 ? formData.comboItems.map((item: any) => ({
          id: item.childSkuId,
          quantity: item.quantity
        })) : [],
        // SKU 也携带属性
        attributes: pruneAttributes(formData.attributes),
        materialItems: formData.materialItems
      }

      if (!submitData.skus || submitData.skus.length === 0) {
        submitData.skus = [skuData]
      } else {
        Object.assign(submitData.skus[0], skuData)
      }
    } else {
      // 多规格模式：价格转换 (元 -> 分)
      if (submitData.skus && submitData.skus.length > 0) {
        submitData.skus.forEach((sku: any) => {
          sku.price = Math.round(sku.price * 100)
          sku.marketPrice = Math.round((sku.marketPrice || 0) * 100)
          sku.costPrice = Math.round((sku.costPrice || 0) * 100)
        })
      }
    }
    
    // SPU 级属性也进行清理
    submitData.attributes = pruneAttributes(formData.attributes)
    
    emit('save', submitData)
  } finally {
    loading.value = false
  }
}

/** 初始化表单方法，供外部调用 */
const initForm = async (mode: string, type: string, id?: number) => {
  // 重置表单
  Object.assign(formData, {
    id: undefined,
    picUrl: '',
    name: '',
    code: '',
    skuCode: '',
    productType: 1,
    categoryId: undefined,
    status: 1,
    brandId: undefined,
    model: '',
    unit: 'pc',
    material: '',
    description: '',
    sort: 0,
    specType: 1,
    price: 0,
    marketPrice: 0,
    costPrice: 0,
    stock: 0,
    barCode: '',
    skus: [],
    comboItems: [],
    materialItems: [],
    attributes: {
      purchase: { purchasePrice: 0, moq: 1, leadTime: 7 },
      logistics: {
        itemDim: { length: 0, width: 0, height: 0, weight: 0, unit: 'cm', weightUnit: 'g' },
        dimUnit: 'cm',
        packingSchemes: [{ 
          name: '默认箱规', quantity: undefined, 
          outerBoxDim: { length: 0, width: 0, height: 0, unit: 'cm' },
          pkgDim: { length: 0, width: 0, height: 0, unit: 'cm' },
          boxWeight: undefined, boxWeightUnit: 'kg', grossWeight: undefined, grossWeightUnit: 'g'
        }]
      },
      customs: {
        materialCn: '', materialEn: '', usageCn: '', usageEn: '', brandType: '', exportBenefit: '', internalCode: '',
        nature: [], declarationNameCn: '', declarationNameEn: '',
        declarationPrice: 0, declarationCurrency: 'USD', declarationHsCode: '', declarationModel: '',
        originCountry: '', domesticSource: '', declarationUnit: '', declarationElement: '', taxExemption: '',
        manufacturerName: '', manufacturerCode: '', clearanceModel: '', ingredientRemark: '',
        weavingMethod: '', clearancePicUrl: '', clearanceFees: [], taxInfos: []
      },
      compliance: { qcStandards: '', qcRemark: '' },
      media: { mainImage: '', additionalImages: Array(9).fill('') }
    }
  })
  
  propertyList.value = []
  activeNav.value = 'basic'
  
  if (id) {
    try {
      loading.value = true
      const data = await ProductSpuApi.getSpu(id)
      
      // 注意：使用 Object.assign 会覆盖掉某些嵌套对象的初始结构，需要小心合并
      const attributes = { ...formData.attributes, ...(data.attributes || {}) }
      Object.assign(formData, data)
      formData.attributes = attributes
      
      // 数据回显适配
      if (formData.specType === 2) {
        entryMode.value = 'SPU'
        specificType.value = 'MULTI'
        propertyList.value = getPropertyList(formData)
        
        // 多规格价格转换 (分 -> 元)
        if (formData.skus && formData.skus.length > 0) {
          formData.skus.forEach((sku: any) => {
            sku.price = sku.price / 100
            sku.marketPrice = sku.marketPrice ? sku.marketPrice / 100 : 0
            sku.costPrice = sku.costPrice ? sku.costPrice / 100 : 0
          })
        }
      } else {
        entryMode.value = 'SKU'
        const sku = formData.skus?.[0]
        if (sku) {
          formData.price = sku.price / 100
          formData.marketPrice = sku.marketPrice ? sku.marketPrice / 100 : 0
          formData.costPrice = sku.costPrice ? sku.costPrice / 100 : 0
          formData.stock = sku.stock
          formData.barCode = sku.barCode
          formData.skuCode = sku.code
          
          if (sku.type === 2) {
            specificType.value = 'COMBO'
            formData.comboItems = sku.comboItems || []
          } else {
            specificType.value = 'ORDINARY'
          }

          // 如果单规格 SKU 有属性，合并到 SPU 级编辑区
          if (sku.attributes) {
            Object.assign(formData.attributes, sku.attributes)
          }

          // 回显耗材/配件信息
          formData.materialItems = sku.materialItems || []
        }
      }
      
      // 物流信息回显 (如果 SKU 层面有物流信息，同步到 SPU 级编辑区)
      if (formData.skus?.[0]?.attributes?.logistics) {
        Object.assign(formData.attributes.logistics, formData.skus[0].attributes.logistics)
      }
    } finally {
      loading.value = false
    }
  } else {
    entryMode.value = mode
    specificType.value = type
  }
}

defineExpose({ initForm })
const emit = defineEmits(['close', 'save'])

onMounted(async () => {
  const categories = await ProductCategoryApi.getCategoryList()
  categoryOptions.value = handleTree(categories)
  brandOptions.value = await ProductBrandApi.getSimpleBrandList()
  userOptions.value = await UserApi.getSimpleUserList()
  
  if (props.initialData?.id) {
    Object.assign(formData, props.initialData)
    propertyList.value = getPropertyList(props.initialData)
    
    if (formData.skus && formData.skus.length > 0 && formData.skus[0].attributes?.logistics) {
      Object.assign(formData.attributes.logistics, formData.skus[0].attributes.logistics)
    }

      if (formData.specType === 2) {
        entryMode.value = 'SPU'
        specificType.value = 'MULTI'
      } else {
        entryMode.value = 'SKU'
        specificType.value = formData.productType === 2 ? 'COMBO' : 'ORDINARY'
        
        // 单规格回显耗材信息
        const sku = formData.skus?.[0]
        if (sku) {
          formData.materialItems = sku.materialItems || []
        }
      }
    }
  })
</script>

<style scoped lang="scss">
.product-form-v2 {
  min-height: 100%;

  .mode-btn {
    border: 1px solid transparent;
    &.bg-white {
      border-color: #e5e7eb;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 24px;
    
    .el-form-item__label {
      font-weight: 500;
      color: #4a5568;
    }

    .el-form-item__content {
      position: relative;
    }

    .el-form-item__error {
      padding-top: 4px;
      position: absolute;
      top: 100%;
      left: 0;
    }
  }

  .nav-item {
    &.active {
      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 4px;
        background: var(--el-color-primary);
      }
    }
  }

  :deep(.el-form-item__label) {
    font-weight: 600;
    font-size: 12px;
    color: #718096;
    margin-bottom: 2px !important;
  }

  :deep(.el-input__wrapper), 
  :deep(.el-select__wrapper), 
  :deep(.el-input-number__wrapper) {
    box-shadow: none !important;
    border: 1px solid #e2e8f0 !important;
    background: #fff !important;
    border-radius: 4px !important;
    &:hover, &.is-focus {
      border-color: #3b82f6 !important;
    }
  }
  
  :deep(.el-radio-button__inner) {
    border-radius: 4px !important;
    border: none !important;
    box-shadow: none !important;
    margin: 0 4px;
  }
}
</style>
