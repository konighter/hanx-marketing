<template>
  <ContentWrap class="px-10">
    <el-divider content-position="left">平台信息</el-divider>
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="售卖平台" prop="platformId">
        <el-select v-model="formData.platformId" placeholder="请选择平台" @change="onPlatformChange" class="w-100!">
          <el-option v-for="p in platforms" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="店铺" prop="shopIds">
        <el-select v-model="formData.shopIds" multiple placeholder="请选择店铺" :disabled="!formData.platformId"
          @change="onShopChange" class="w-100!">
          <el-option v-for="s in shops" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="品类" prop="categoryId">

        <el-cascader v-model="formData.categoryId" :options="categories" :props="categoryCasOpt" class="w-100!"
          :disabled="!(formData.platformId && formData.shopIds && formData.shopIds.length > 0)" clearable filterable
          :filter-method="loadCategoriesDebounced" @change="onCategoryChange" placeholder="请选择分类" />

      </el-form-item>


      <div v-if="loadingAttributes" class="py-4">
        <el-skeleton :rows="5" animated />
      </div>
      <template v-else>
        <template v-if="formData.attributes && Object.keys(formData.attributes).length > 0">
          <el-divider content-position="left">商品属性</el-divider>

          <div>
            <template v-for="attr in attributes.filter(t => t.required)" :key="attr.id">
              <el-form-item :label="attr.attrName" :labelMessage="attr.attrDescription" :prop="`${attr.attrCode}`"
                :required="attr.required">
                <template #label>
                  <Tooltip :title="attr.attrName" :message="attr.attrDescription" icon="ep:info-filled" />

                </template>


                <component :is="getFieldComponent(attr.type)" v-model="attr.value" v-bind="fieldProps(attr)">
                  <template v-if="attr.type === 'select'" #default>
                    <el-option v-for="opt in attr.options" :key="opt.value" :label="opt.label" :value="opt.value" />
                  </template>
                </component>
              </el-form-item>
            </template>


          </div>

        </template>
      </template>


    </el-form>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { ref, reactive, watch, onMounted } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { SellPlatformApi } from '@/app/erp/api/sellplatform' // 已存在模块
// 假设存在下面的 API 模块，如不存在请替换为项目实际 API
import * as CategoryApi from '@/app/erplus/api/product/category' // getCategoriesByPlatform, getCategoryAttributes
import type { Spu } from '@/api/mall/product/spu'
import { ShopApi } from '@/app/erplus/api/system/shop'
import { defaultProps, handleTree } from '@/utils/tree'
import type { CascaderNode } from 'element-plus'
import { debounce } from 'lodash-es'

defineOptions({ name: 'ProductListingForm' })

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => ({})
  },
  isDetail: propTypes.bool.def(false)
})

// const { push } = useRouter()
// const { query } = useRoute()

const formRef = ref()
const platforms = ref<any[]>([])
const shops = ref<any[]>([])
const categories = ref<any[]>([])
const attributes = ref<any[]>([])
const loadingAttributes = ref(false)
const categoryCasOpt = Object.assign({}, defaultProps, {

  emitPath: true,
  value: 'categoryId',
  label: 'name',
  children: 'children'
})

// 表单数据：platformId, shopIds, categoryId, attributes 为 id->value 映射
const formData = reactive({
  platformId: undefined,
  shopIds: [],
  categoryId: undefined,
  attributes: {},
})

const rules = reactive({
  platformId: [{ required: true, message: '请选择平台', trigger: 'change' }],
  shopIds: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择品类', trigger: 'change' }]
})

/** 初始化平台列表、并把传入数据复制到本地表单 */
onMounted(async () => {
  await loadPlatforms()
})



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
  console.log('加载品类，节点', node, '名称过滤', name)
  const result = await CategoryApi.getCrossCategories({ platformId: formData.platformId, shopIds: formData.shopIds, name } as CategoryApi.PlatformCategoryReqVO) || {}
  if (result.categories && result.categories.length !== 0) {
    categories.value = handleTree(result.categories, 'categoryId', 'parentCategoryId')
  }
  console.log('加载品类', categories.value)
}

const loadCategoriesDebounced = debounce(loadCategories, 500);


/** 选择品类后拉取该类目的属性设置（动态表单） */
const loadCategoryAttributes = async (categoryId: string[], platformId: number, shopId: number) => {
  loadingAttributes.value = true
  try {
    // 返回格式示例： [{ id, name, type: 'text'|'number'|'select'|'checkbox', options: [{label, value}] }]
    attributes.value = await CategoryApi.getCategoryAttributes(categoryId, platformId, shopId) || []
    // 初始化 formData.attributes 对应字段，保留已有值
    attributes.value.forEach((a: any) => {
      if (formData.attributes[a.id] === undefined) {
        formData.attributes[a.id] = a.type === 'checkbox' ? false : (a.type === 'select' ? null : '')
      }
    })
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
  console.log('选择品类', categoryId)
  formData.attributes = {}
  attributes.value = []
  if (categoryId != null) {
    await loadCategoryAttributes(categoryId, formData.platformId, formData.shopIds[0])
  }
}

/** 表单暴露和校验 */
const emit = defineEmits(['update:activeName'])
const validate = async () => {
  await unref(formRef)?.validate()
  // 合并回父 propFormData
  Object.assign(props.propFormData, {
    platformId: formData.platformId,
    shopIds: [...formData.shopIds],
    categoryId: formData.categoryId,
    attributes: { ...formData.attributes }
  })
}
defineExpose({ validate })

/** 模板辅助函数 */
const getFieldComponent = (type: string) => {
  switch (type) {
    case 'number': return 'el-input-number'
    case 'select': return 'el-select'
    case 'checkbox': return 'el-checkbox'
    default: return 'el-input'
  }
}
const fieldProps = (attr: any) => {
  if (attr.type === 'select') return { placeholder: `请选择 ${attr.name}` }
  if (attr.type === 'number') return { min: 0 }
  return {}
}
</script>

<style scoped></style>