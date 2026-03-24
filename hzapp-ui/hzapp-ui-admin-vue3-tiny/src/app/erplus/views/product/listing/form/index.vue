<template>
  <ContentWrap class="px-10 h-full!">

    <el-divider content-position="left">刊登设置</el-divider>
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="200px" :inline="true">
      <el-form-item label="售卖平台" prop="platformId">
        <el-select v-model="formData.platformId" placeholder="请选择平台" @change="onPlatformChange" class="w-100!">
          <el-option v-for="p in platforms" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="店铺" prop="shopIds">
        <el-select
v-model="formData.shopIds" multiple placeholder="请选择店铺" :disabled="!formData.platformId"
          @change="onShopChange" class="w-100!">
          <el-option v-for="s in shops" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="品类" prop="categoryId">

        <el-cascader
v-model="formData.categoryId" :options="categories" :props="categoryCasOpt" class="w-100!"
          :disabled="!(formData.platformId && formData.shopIds && formData.shopIds.length > 0)" clearable filterable
          @change="onCategoryChange" placeholder="请选择分类" />

      </el-form-item>

      <el-form-item label="是否翻译文本" prop="translateText">
        <el-switch v-model="formData.translateText" />
      </el-form-item>


      <div v-if="loadingAttributes" class="py-4">
        <el-skeleton :rows="5" animated />
      </div>
      <template v-else>
        <template v-if="formData.attributes && Object.keys(formData.attributes).length > 0">
          <el-divider content-position="left">商品属性</el-divider>

          <ExpendBox :collapsedHeight="180" class="no-border-collapse">
            <template v-for="attr in attributes.filter(t => t.isRequired)" :key="attr.id">
              <el-form-item
:label="attr.attrName" :labelMessage="attr.attrDescription"
                :prop="'attributes.' + attr.attrCode" :required="attr.isRequired">
                <template #label>
                  <Tooltip
:title="attr.attrName.substring(0, 10)" :message="attr.attrDescription"
                    icon="ep:info-filled" />

                </template>


                <component
:is="getFieldComponent(attr)" v-model="formData.attributes[attr.attrCode]"
                  v-bind="fieldProps(attr)" class="w-100!">
                  <template v-if="attr.compType === 'el-select'" #default>
                    <el-option v-for="opt in attr.options" :key="opt.value" :label="opt.valueName" :value="opt.value" />
                  </template>
                </component>
              </el-form-item>
            </template>


            <el-divider content-position="left" @click="showMore = !showMore">
              <div class="flex items-center cursor-pointer">
                <span>更多属性（可选）</span>
                <Icon :icon="showMore ? 'ep:arrow-down' : 'ep:arrow-right'" />
              </div>

            </el-divider>
            <div v-show="showMore">


              <template v-for="attr in attributes.filter(t => !t.isRequired)" :key="attr.id">

                <el-form-item
:label="attr.attrName" :labelMessage="attr.attrDescription"
                  :prop="formData.attributes[attr.attrCode]" :required="attr.isRequired">
                  <template #label>
                    <Tooltip
:title="attr.attrName.substring(0, 10)" :message="attr.attrDescription"
                      icon="ep:info-filled" />

                  </template>


                  <component
:is="getFieldComponent(attr)" v-model="formData.attributes[attr.attrCode]"
                    v-bind="fieldProps(attr)" class="w-100!">
                    <template v-if="attr.compType === 'el-select'" #default>
                      <el-option
v-for="opt in attr.options" :key="opt.value" :label="opt.valueName"
                        :value="opt.value" />
                    </template>
                  </component>
                </el-form-item>



              </template>

            </div>



          </ExpendBox>

          <VariationAttributes
ref="variationAttributesRef" :attributes="attributes" :skus="spuInfo.skus"
            v-model="formData.variationAttributes" :attributeValues="formData.attributes"
            @selection-change="handleSelectionChange" />









        </template>




      </template>

      <div class="flex justify-center mt-20px space-x-10px">
        <el-button type="primary" @click="handleSubmit">刊登</el-button>
        <el-button type="info" @click="$emit('update:activeName', 'spuInfo')">返回</el-button>
      </div>


    </el-form>
  </ContentWrap>
</template>

<script lang="ts" setup>

import { ref, reactive, watch, onMounted, unref } from 'vue'
import { ElMessage } from 'element-plus'

import { SellPlatformApi } from '@/app/erp/api/sellplatform' // 已存在模块
import { ShopApi } from '@/app/erplus/api/system/shop'
// 假设存在下面的 API 模块，如不存在请替换为项目实际 API
import * as CategoryApi from '@/app/erplus/api/product/category' // getCategoriesByPlatform, getCategoryAttributes
import * as SpuApi from '@/app/erplus/api/product/spu'

import { defaultProps, handleTree } from '@/utils/tree'
import type { CascaderNode } from 'element-plus'

import ListingSkuList from './ListingSkuList.vue'
import ExpendBox from '@/app/erplus/compononts/ExpendBox.vue'
import VariationAttributes from './VariationAttributes.vue'

defineOptions({ name: 'ProductListingForm' })



// const { push } = useRouter()
const { query } = useRoute()

const formRef = ref()
const variationAttributesRef = ref()
const spuInfo = ref<SpuApi.Spu>({} as SpuApi.Spu)
const platforms = ref<any[]>([])
const shops = ref<any[]>([])
const categories = ref<any[]>([])
const attributes = ref<CategoryApi.CategoryAttributeModel[]>([])
const loadingAttributes = ref(false)
const showMore = ref(true)
const categoryCasOpt = Object.assign({}, defaultProps, {

  emitPath: true,
  value: 'categoryId',
  label: 'name',
  children: 'children'
})

// 表单数据：platformId, shopIds, categoryId, attributes 为 id->value 映射
const formData = reactive({
  platformId: undefined,
  shopIds: [] as any[],
  categoryId: undefined,
  attributes: {},
  skus: [] as any[],
  variationAttributes: [] as any[],
  translateText: false,
})

const rules = reactive({
  platformId: [{ required: true, message: '请选择平台', trigger: 'change' }],
  shopIds: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择品类', trigger: 'change' }]
})

/** 初始化平台列表、并把传入数据复制到本地表单 */
onMounted(async () => {
  await loadPlatforms()

  await loadSpuData()
})

const loadSpuData = async () => {
  spuInfo.value = await SpuApi.getSpu(query.id as number)
  if (spuInfo.value) {
    spuInfo.value.skus?.forEach((sku: any) => {
      sku.attrValues = {}
      sku.originalStock = sku.stock
    })
  }
}



/** 平台相关加载 */
const loadPlatforms = async () => {
  platforms.value = await SellPlatformApi.getSellPlatformListCache() || []
}

const loadShops = async (platformId: number) => {
  // 示例 API，替换为真实实现
  try {
    shops.value = await ShopApi.getPlatformShop(platformId) || []
  } catch {
    shops.value = []
  }
}

const loadCategories = async (node: CascaderNode, name: string) => {

  const result = await CategoryApi.getCrossCategories({ platformId: formData.platformId, shopIds: formData.shopIds, name } as unknown as CategoryApi.PlatformCategoryReqVO) || {}
  if (result.categories && result.categories.length !== 0) {
    categories.value = handleTree(result.categories, 'categoryId', 'parentCategoryId')
  }

}


/** 选择品类后拉取该类目的属性设置（动态表单） */
const loadCategoryAttributes = async (categoryId: string[], platformId: number, shopId: number, spuId: number) => {
  loadingAttributes.value = true
  try {
    // 返回格式示例： [{ id, name, type: 'text'|'number'|'select'|'checkbox', options: [{label, value}] }]
    attributes.value = await CategoryApi.renderCategoryAttributes(categoryId, platformId, shopId, spuId) || []
    // 初始化 formData.attributes 对应字段，保留已有值
    const newRules = []
    attributes.value.forEach((a: any) => {

      a.compType = getFieldComponent(a)
      if (formData.attributes[a.attrCode] === undefined && a.attrValue != null) {
        formData.attributes[a.attrCode] = a.attrValue.value
      } else {
        formData.attributes[a.attrCode] = ''
      }

      if (a.isRequired) {
        rules['attributes.' + a.attrCode] = { required: true, message: `请输入${a.attrName}`, trigger: 'change' }
      }

    })


    console.log('加载属性', formData.attributes)
    console.log('加载销售属性', attributes.value.filter(i => i.attrType === 'SALES_PROPERTY'))
  } finally {
    loadingAttributes.value = false
  }

}

/** 事件处理 */
const onPlatformChange = async (platformId: number) => {
  // 平台切换时清空店铺、品类与属性
  formData.shopIds = []
  formData.categoryId = undefined
  formData.attributes = {}
  shops.value = []
  categories.value = []
  attributes.value = []
  if (platformId != null) {
    await loadShops(platformId)
  }
}

/** 当店铺变更后，只有在已选平台且至少选中一个店铺时才加载品类 */
const onShopChange = async (shopIds: number[]) => {
  formData.categoryId = undefined
  formData.attributes = {}
  attributes.value = []
  categories.value = []
  if (formData.platformId != null && shopIds && shopIds.length > 0) {
    await loadCategories(undefined, '')
  }
}

const onCategoryChange = async () => {
  const categoryId = formData.categoryId

  formData.attributes = {}
  attributes.value = []
  if (categoryId != null) {
    await loadCategoryAttributes(categoryId, formData.platformId, formData.shopIds[0], query.id as number)
  }
}

/** 表单暴露和校验 */
const emit = defineEmits(['update:activeName'])
const validate = async () => {
  await unref(formRef)?.validate()
  console.log('formValidate passed')
  await unref(variationAttributesRef)?.validate()
  console.log('variationAttributesRef passed')
  // // 合并回父 propFormData
  // Object.assign(props.propFormData, {
  //   platformId: formData.platformId,
  //   shopIds: [...formData.shopIds],
  //   categoryId: formData.categoryId,
  //   attributes: { ...formData.attributes }
  // })
}

const handleSubmit = async () => {
  try {
    await validate()
    const data = {
      spuId: spuInfo.value.id,
      ...formData,
      skus: formData.skus // Use selected SKUs
    }
    await SpuApi.spuListing(data)
    ElMessage.success('刊登成功')
    emit('update:activeName', 'spuInfo')
  } catch (e: any) {
    console.error(e)
    // Check if e is a string (custom rejection) or object (Element Plus validation error)
    if (typeof e === 'string') {
      ElMessage.warning(e)
    } else {
      ElMessage.warning('请完善表单信息')
    }
  }
}

defineExpose({ validate })

/** 模板辅助函数 */
const getFieldComponent = (a) => {
  if (a.options && a.options.length > 0) return 'el-select'
  else {
    return 'el-input'
  }
  // switch (type) {
  //   case 'number': return 'el-input-number'
  //   case 'select': return 'el-select'
  //   case 'checkbox': return 'el-checkbox'
  //   default: return 'el-input'
  // }
}
const fieldProps = (attr: any) => {
  const props = {}

  if (attr.compType === 'el-select') {
    Object.assign(props, { placeholder: `请选择${attr.attrName}`, clearable: true, filterable: true, })

    if (attr.mulSelection) {
      Object.assign(props, { multiple: true })
    }

    if (attr.customizable) {
      Object.assign(props, { allowCreate: true })
    }


  } else if (attr.compType === 'el-input') {
    Object.assign(props, { placeholder: `请输入${attr.attrName}` })
    if (attr.isEditable === false) {
      Object.assign(props, { disabled: true })
    }
  }

  return props
}


const handleSelectionChange = (val: any[]) => {
  formData.skus = val
}





</script>

<style scoped>
/* 注意：如果使用 <style scoped>，可能需要使用深度选择器 (如 ::v-deep 或 :deep())
   以确保样式能作用到 el-form-item 内部的 Label 元素。
*/
:deep(.el-form-item__label) {
  /* 覆盖默认的省略号和不换行设置 */
  white-space: nowrap !important;

  text-overflow: ellipsis !important;
  overflow: hidden !important;
  /* 允许换行 */
  word-break: break-all;
  /* 强制长单词或无空格文本换行 */
  line-height: normal;
  /* 确保多行文本的行高适应 */
  padding-top: 0;
  /* 移除顶部默认填充 */
  padding-bottom: 0;
  /* 移除底部默认填充 */
}

/* 可选：调整整个 form item 的布局，使其与多行 label 更好地对齐。
   通常会调整 el-form-item 的对齐方式。
*/
:deep(.el-form-item) {
  /* 默认为 flex-start，对于多行 label 保持顶部对齐最好 */
  align-items: flex-start;
}

/* 使用深度选择器覆盖 Element Plus 的默认样式 */
.no-border-collapse {

  /* 针对整个 el-collapse 组件的外部边框 */
  :deep(.el-collapse) {
    border-top: none;
    /* 移除顶部边框 */
    border-bottom: none;
    /* 移除底部边框 */
  }

  /* 针对每个 el-collapse-item 之间的分隔线和边框 */
  :deep(.el-collapse-item__header) {
    border-bottom: none;
    /* 移除头部（标题）下的分隔线 */
  }

  :deep(.el-collapse-item__wrap) {
    /* 移除内容包裹区域的边框 */
    border-bottom: none;
  }

  /* 确保整个 item 底部没有边框（特别是最后一项） */
  :deep(.el-collapse-item) {
    border-bottom: none;
  }

}
</style>