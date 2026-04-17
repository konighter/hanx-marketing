<!-- 商品中心 - 商品列表  -->
<template>
  <div class="apple-theme-mode">
    <doc-alert title="【商品】商品 SPU 与 SKU" url="https://help.h2z.ltd/mall/product-spu-sku/" />

    <div class="apple-hero-module">
      <h1 class="apple-display-hero">商品列表</h1>
      <p class="apple-sub-heading">管理和展示您的所有产品与变体</p>
    </div>

    <!-- 搜索工作栏 -->
    <ContentWrap class="apple-card">
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="68px">
      <el-form-item label="商品名称" prop="name">
        <el-input
v-model="queryParams.name" class="!w-240px" clearable placeholder="请输入商品名称"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品分类" prop="categoryId">
        <el-cascader
v-model="queryParams.categoryId" :options="categoryList" :props="defaultProps" class="w-1/1"
          clearable filterable placeholder="请选择商品分类" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
v-model="queryParams.createTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" end-placeholder="结束日期"
          start-placeholder="开始日期" type="daterange" value-format="YYYY-MM-DD HH:mm:ss" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          搜索
        </el-button>
        <el-button @click="resetQuery">
          重置
        </el-button>
        <el-button v-hasPermi="['product:spu:create']" type="primary" @click="openForm(undefined)">
          新增商品
        </el-button>
        <el-button
v-hasPermi="['product:spu:export']" :loading="exportLoading" @click="handleExport">
          导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>


  <!-- 列表层 -->
  <ContentWrap class="apple-card mt-5">
    <!-- Tab 切换 -->
    <el-tabs v-model="queryParams.tabType" class="apple-tabs" @tab-click="handleTabClick">
      <el-tab-pane
        v-for="item in tabsData" 
        :key="item.type" 
        :label="item.name + (item.count > 0 ? ' (' + item.count + ')' : '')"
        :name="item.type" 
      />
    </el-tabs>

    <!-- 批量操作工具栏 -->
    <transition name="el-zoom-in-top">
      <div v-if="selectedSpuIds.length > 0" class="apple-batch-bar mb-12px">
        <div class="flex justify-between items-center w-full px-16px py-8px bg-apple-blue/5 border border-apple-blue/20 rounded-8px">
          <div class="flex items-center text-13px">
            <el-checkbox
              :model-value="isAllSelected"
              :indeterminate="isIndeterminate"
              @change="handleSelectAll"
              class="mr-12px apple-checkbox"
            />
            <span class="text-gray-600">已选中 <b class="text-apple-blue">{{ selectedSpuIds.length }}</b> 个商品</span>
          </div>
          <div class="flex gap-8px">
            <el-button type="primary" size="small" @click="handleBatchStatusChange(1)">批量上架</el-button>
            <el-button size="small" @click="handleBatchStatusChange(2)">批量下架</el-button>
            <el-button type="danger" plain size="small" @click="handleBatchDelete">批量删除</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" @click="clearSelection">取消</el-button>
          </div>
        </div>
      </div>
    </transition>

    <div v-loading="loading" class="spu-list-container min-h-400px">
      <div v-if="list.length > 0">
        <SpuItem
          v-for="item in list"
          :key="item.id"
          :spu="item"
          :selected="selectedSpuIds.includes(item.id)"
          :tab-type="queryParams.tabType"
          @selection-change="handleItemSelection"
          @update-status="handleStatusChange"
          @delete="handleDelete"
          @edit="openForm"
          @restore="handleStatus02Change(item, ProductSpuStatusEnum.DISABLE.status)"
          @listing="submitSkuListing"
        />
      </div>
      <el-empty v-else description="暂无商品数据" />

      <!-- 分页 -->
      <Pagination
v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" class="apple-pagination mt-20px"
        @pagination="getList" />
    </div>
  </ContentWrap>
  </div>
</template>
<script lang="ts" setup>
import { TabsPaneContext } from 'element-plus'
import { createImageViewer } from '@/components/ImageViewer'
import { dateFormatter } from '@/utils/formatTime'
import { defaultProps, handleTree, treeToString } from '@/utils/tree'
import { ProductSpuStatusEnum } from '@/app/erplus/common/constants'
import { fenToYuan } from '@/utils'
import download from '@/utils/download'
import * as ProductSpuApi from '@/app/erplus/api/product/spu'
import * as ProductCategoryApi from '@/app/erplus/api/product/category'
import SpuItem from './components/SpuItem.vue'

defineOptions({ name: 'ErplusProductSpu' })

const selectedSpuOrders = ref<any[]>([])
const selectedSpuIds = computed(() => selectedSpuOrders.value.map((o) => o.id))
const isAllSelected = computed(() => {
  return list.value.length > 0 && list.value.every(item => selectedSpuIds.value.includes(item.id))
})
const isIndeterminate = computed(() => {
  return selectedSpuIds.value.length > 0 && !isAllSelected.value
})

const handleItemSelection = (selected: boolean, spu: any) => {
  if (selected) {
    if (!selectedSpuIds.value.includes(spu.id)) {
      selectedSpuOrders.value.push(spu)
    }
  } else {
    selectedSpuOrders.value = selectedSpuOrders.value.filter((o) => o.id !== spu.id)
  }
}

const handleSelectAll = (val: any) => {
  if (val) {
    list.value.forEach((spu) => {
      if (!selectedSpuIds.value.includes(spu.id)) {
        selectedSpuOrders.value.push(spu)
      }
    })
  } else {
    const currentIds = list.value.map((item) => item.id)
    selectedSpuOrders.value = selectedSpuOrders.value.filter((o) => !currentIds.includes(o.id))
  }
}

const clearSelection = () => {
  selectedSpuOrders.value = []
}

const handleBatchDelete = async () => {
  if (selectedSpuIds.value.length === 0) return
  try {
    await message.confirm(`确定要彻底删除选中的 ${selectedSpuIds.value.length} 个商品吗？`)
    loading.value = true
    for (const id of selectedSpuIds.value) {
      await ProductSpuApi.deleteSpu(id)
    }
    message.success('批量删除成功')
    clearSelection()
    await getList()
    await getTabsCount()
  } catch {
  } finally {
    loading.value = false
  }
}

const handleBatchStatusChange = async (status: number) => {
   if (selectedSpuIds.value.length === 0) return
  try {
    const text = status === 1 ? '上架' : '下架'
    await message.confirm(`确定要批量${text}选中的 ${selectedSpuIds.value.length} 个商品吗？`)
    loading.value = true
    for (const id of selectedSpuIds.value) {
      await ProductSpuApi.updateStatus({ id, status })
    }
    message.success(`批量${text}成功`)
    clearSelection()
    await getList()
    await getTabsCount()
  } catch {
  } finally {
    loading.value = false
  }
}

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { push } = useRouter() // 路由跳转

const loading = ref(false) // 列表的加载中
const exportLoading = ref(false) // 导出的加载中
const total = ref(0) // 列表的总页数
const list = ref<ProductSpuApi.Spu[]>([]) // 列表的数据
// tabs 数据
const tabsData = ref([
  {
    name: '草稿',
    type: 0,
    count: 0
  },
  {
    name: '出售中',
    type: 1,
    count: 0
  },
  {
    name: '下架',
    type: 2,
    count: 0
  },
  {
    name: '已售罄',
    type: 3,
    count: 0
  },
  {
    name: '警戒库存',
    type: 4,
    count: 0
  },
  {
    name: '已归档',
    type: 5,
    count: 0
  }
])

const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  tabType: 0,
  name: '',
  categoryId: undefined,
  createTime: undefined
}) // 查询参数
const queryFormRef = ref() // 搜索的表单Ref

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProductSpuApi.getSpuPage(queryParams.value)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 切换 Tab */
const handleTabClick = (tab: TabsPaneContext) => {
  queryParams.value.tabType = tab.paneName as number
  getList()
}

/** 获得每个 Tab 的数量 */
const getTabsCount = async () => {
  const res = await ProductSpuApi.getTabsCount()
  for (let objName in res) {
    tabsData.value[Number(objName)].count = res[objName]
  }
}

/** 添加到仓库 / 回收站的状态 */
const handleStatus02Change = async (row: any, newStatus: number) => {
  try {
    // 二次确认
    const text = newStatus === ProductSpuStatusEnum.RECYCLE.status ? '加入到回收站' : '恢复到仓库'
    await message.confirm(`确认要"${row.name}"${text}吗？`)
    // 发起修改
    await ProductSpuApi.updateStatus({ id: row.id, status: newStatus })
    message.success(text + '成功')
    // 刷新 tabs 数据
    await getTabsCount()
    // 刷新列表
    await getList()
  } catch { }
}

/** 更新上架/下架状态 */
const handleStatusChange = async (row: any, newStatus: number) => {
  try {
    // 二次确认
    const text = newStatus === 1 ? '上架' : '下架'
    await message.confirm(`确认要${text}"${row.name}"吗？`)
    // 发起修改
    await ProductSpuApi.updateStatus({ id: row.id, status: newStatus })
    message.success(text + '成功')
    // 刷新 tabs 数据
    await getTabsCount()
    // 刷新列表
    await getList()
  } catch {
    // 失败时无需重置，因为我们没有直接修改 row.status
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ProductSpuApi.deleteSpu(id)
    message.success(t('common.delSuccess'))
    // 刷新tabs数据
    await getTabsCount()
    // 刷新列表
    await getList()
  } catch { }
}



/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 新增或修改 */
const openForm = (id?: number) => {
  // 修改
  if (typeof id === 'number') {
    push({ path: '/erplusv2/product/spu-add', query: { id } })
    return
  }
  // 新增
  push({ path: '/erplusv2/product/spu-add' })
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ProductSpuApi.exportSpu(queryParams.value)
    download.excel(data, '商品列表.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 获取分类的节点的完整结构 */
const categoryList = ref() // 分类树
const formatCategoryName = (categoryId: number) => {
  return treeToString(categoryList.value, categoryId)
}

/** 激活时 */
onActivated(() => {
  getList()
})

/** 初始化 **/
onMounted(async () => {
  await getTabsCount()
  await getList()
  // 获得分类树
  const data = await ProductCategoryApi.getCategoryList({})
  categoryList.value = handleTree(data, 'id', 'parentId')
})



const submitSkuListing = async (ids: number[]) => {
  message.info(`模拟刊登请求：SKU ID [${ids.join(', ')}]`)
}



</script>
<style lang="scss" scoped>
.spu-table-expand {
  padding-left: 42px;

  :deep(.el-form-item__label) {
    width: 82px;
    font-weight: bold;
    color: #99a9bf;
  }
}

/* Apple Theme Styling */
.apple-theme-mode {
  --apple-blue: #0071e3;
  --apple-bg: #f5f5f7;
  --apple-card-bg: #ffffff;
  --apple-text-main: #1d1d1f;
  --apple-text-muted: rgba(0, 0, 0, 0.48);
  --apple-border: rgba(0, 0, 0, 0.04);
  --apple-radius-card: 12px;
  --apple-radius-input: 11px;
  --apple-radius-btn: 8px;
  --apple-radius-pill: 980px;

  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Arial, sans-serif;
  color: var(--apple-text-main);
  background-color: var(--apple-bg);
  min-height: 100%;
  padding-bottom: 40px;

  /* Reset global Element Plus styles within this wrapper to match Apple aesthetic */
  :deep() {
    --el-color-primary: var(--apple-blue);
    --el-bg-color: var(--apple-card-bg);
    --el-text-color-primary: var(--apple-text-main);
    --el-border-radius-base: var(--apple-radius-btn);
    --el-border-color-lighter: rgba(0, 0, 0, 0.06);
    
    .el-card {
      border: none;
      border-radius: var(--apple-radius-card);
      box-shadow: rgba(0, 0, 0, 0.05) 0px 4px 20px 0px; /* extremely soft shadow */
      background: var(--apple-card-bg);
      margin-bottom: 24px;
    }

    /* Buttons */
    .el-button--primary {
      background-color: var(--apple-blue);
      border-color: var(--apple-blue);
      color: #ffffff;
      padding: 8px 15px;
      height: 36px;
      font-weight: 400;
      letter-spacing: -0.224px;
      &:hover {
        background-color: #0077F3; /* slightly brighter */
      }
      &:active {
        background-color: #ededf2;
        color: var(--apple-blue);
      }
    }
    
    .el-button {
      border-radius: var(--apple-radius-btn);
      letter-spacing: -0.12px;
    }

    .el-button.is-link {
      color: #0066cc;
      &:hover {
        text-decoration: underline;
      }
    }

    /* Inputs */
    .el-input__wrapper, .el-textarea__inner {
      background-color: #fafafc;
      border-radius: var(--apple-radius-input);
      box-shadow: 0 0 0 1px var(--apple-border) inset;
      &:hover {
        box-shadow: 0 0 0 1px rgba(0,0,0,0.1) inset;
      }
      &.is-focus {
        box-shadow: 0 0 0 2px var(--apple-blue) inset !important;
      }
    }

    /* Tabs */
    .el-tabs__item {
      font-size: 15px;
      font-weight: 500;
      color: rgba(0,0,0,0.6);
      &.is-active {
        color: var(--apple-blue);
        font-weight: 600;
      }
    }
    .el-tabs__active-bar {
      background-color: var(--apple-blue);
    }
    .el-tabs__nav-wrap::after {
      height: 1px;
      background-color: rgba(0,0,0,0.05);
    }

    /* Table (Transitioned to Cards) */
    .el-table {
       display: none;
    }
  }
}

.apple-batch-bar {
  position: sticky;
  top: 0;
  z-index: 20;
  background: white;
  padding: 8px 0;
}

.apple-pagination {
  padding: 24px 0;
}

/* Custom Header */
.apple-hero-module {
  padding: 48px 24px 32px 32px;
  background-color: var(--apple-bg);
  
  .apple-display-hero {
    font-size: 40px;
    font-weight: 600;
    line-height: 1.1;
    letter-spacing: -0.28px;
    margin: 0 0 8px 0;
    color: var(--apple-text-main);
  }
  
  .apple-sub-heading {
    font-size: 21px;
    font-weight: 400;
    line-height: 1.19;
    letter-spacing: 0.231px;
    color: rgba(0,0,0,0.6);
    margin: 0;
  }
}
</style>
