<template>
  <doc-alert title="【商品】商品 SPU 与 SKU" url="https://help.h2z.ltd/mall/product-spu-sku/" />

  <ProductListHeader
    v-model:viewMode="viewMode"
    v-model:queryParams="queryParams"
    :category-list="categoryList"
    :brand-options="brandOptions"
    @search="getList"
    @open-advanced-filter="openAdvancedFilter"
    @reset="resetQuery"
    @export="handleExport"
    @create="handleCreate"
  />

  <ProductFormDrawer ref="productFormDrawerRef" @success="getList" />
  
  <ProductTable
    v-model:pageNo="queryParams.pageNo"
    v-model:pageSize="queryParams.pageSize"
    :loading="loading"
    :list="list"
    :total="total"
    :view-mode="viewMode"
    @pagination="getList"
    @edit="handleEdit"
    @detail="openDetail"
    @delete="handleDelete"
    @format-category-name="formatCategoryName"
  />

  <AdvancedFilter ref="advancedFilterRef" @submit="handleAdvancedFilter" />
</template>

<script lang="ts" setup>
import ProductListHeader from './components/ProductListHeader.vue'
import ProductTable from './components/ProductTable.vue'
import AdvancedFilter from './components/AdvancedFilter.vue'
import ProductFormDrawer from './components/ProductFormDrawer.vue'
import { handleTree, treeToString } from '@/utils/tree'
import download from '@/utils/download'
import * as ProductSpuApi from '@/app/erplus/api/product/spu'
import * as ProductCategoryApi from '@/app/erplus/api/product/category'
import * as ProductBrandApi from '@/app/erplus/api/product/brand'

defineOptions({ name: 'ErplusProductSpu' })

const message = useMessage()
const { t } = useI18n()
const { push } = useRouter()
const productFormDrawerRef = ref()

const loading = ref(false)
const total = ref(0)
const list = ref<any[]>([])
const viewMode = ref('sku') // 'spu' | 'sku'
const advancedFilterRef = ref()
const brandOptions = ref([])
const categoryList = ref()

const queryParams = ref<any>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  categoryId: undefined,
  brandId: undefined,
  status: undefined,
  productTag: undefined,
  createTime: undefined,
  searchType: 'SKU',
  searchValue: ''
})

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const api = viewMode.value === 'spu' ? ProductSpuApi.getSpuPage : ProductSpuApi.getSkuPage
    const data = await api({
      ...queryParams.value,
      viewMode: viewMode.value.toUpperCase()
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 处理视图切换 */
watch(viewMode, () => {
  queryParams.value.pageNo = 1
  getList()
})

/** 打开高级筛选 */
const openAdvancedFilter = () => {
  advancedFilterRef.value?.open()
}

/** 处理高级筛选提交 */
const handleAdvancedFilter = (data: any) => {
  Object.assign(queryParams.value, data)
  queryParams.value.pageNo = 1
  getList()
}

/** 删除操作 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await ProductSpuApi.deleteSpu(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 重置搜索 */
const resetQuery = () => {
  queryParams.value = {
    pageNo: 1,
    pageSize: 10,
    name: '',
    categoryId: undefined,
    brandId: undefined,
    status: undefined,
    productTag: undefined,
    createTime: undefined,
    searchType: 'SKU',
    searchValue: ''
  }
  getList()
}

/** 添加操作 */
const handleCreate = (type: string) => {
  if (type === 'spu') {
    productFormDrawerRef.value.open('SPU', 'MULTI')
  } else if (type === 'spu_single') {
    productFormDrawerRef.value.open('SPU', 'SINGLE')
  } else if (type === 'sku_combo') {
    productFormDrawerRef.value.open('SKU', 'COMBO')
  } else {
    productFormDrawerRef.value.open('SKU', 'ORDINARY')
  }
}

/** 处理编辑 */
const handleEdit = (row: any) => {
  const id = viewMode.value === 'spu' ? row.id : row.spuId
  
  // 识别当前点击的是什么角色进行编辑
  const entryMode = viewMode.value.toUpperCase() // SPU | SKU
  const specificType = entryMode === 'SPU' 
    ? (row.specType === 2 ? 'MULTI' : 'SINGLE')
    : (row.tabType === 6 ? 'COMBO' : 'ORDINARY')

  // 打开抽屉，传入对应的模式
  productFormDrawerRef.value.open(entryMode, specificType, id)
}

/** 查看详情 */
const openDetail = (row: any) => {
  const id = viewMode.value === 'spu' ? row.id : row.spuId
  push({ name: 'ErplusProductSpuDetail', params: { id } })
}

/** 导出操作 */
const handleExport = async () => {
  try {
    await message.exportConfirm()
    const data = await ProductSpuApi.exportSpu(queryParams.value)
    download.excel(data, '商品列表.xls')
  } catch {}
}

const formatCategoryName = (categoryId: number) => {
  return treeToString(categoryList.value, categoryId)
}

onActivated(() => {
  getList()
})

onMounted(async () => {
  loading.value = true
  try {
    const [categoryData, brandData] = await Promise.all([
      ProductCategoryApi.getCategoryList({}),
      ProductBrandApi.getSimpleBrandList()
    ])
    categoryList.value = handleTree(categoryData, 'id', 'parentId')
    brandOptions.value = brandData
    await getList()
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
/* Main styles are now contained in child components */
</style>
