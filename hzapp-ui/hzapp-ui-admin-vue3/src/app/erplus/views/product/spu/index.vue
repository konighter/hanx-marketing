<!-- 商品中心 - 商品列表  -->
<template>
  <doc-alert title="【商品】商品 SPU 与 SKU" url="https://help.h2z.ltd/mall/product-spu-sku/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="68px">
      <el-form-item label="商品名称" prop="name">
        <el-input v-model="queryParams.name" class="!w-240px" clearable placeholder="请输入商品名称"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品分类" prop="categoryId">
        <el-cascader v-model="queryParams.categoryId" :options="categoryList" :props="defaultProps" class="w-1/1"
          clearable filterable placeholder="请选择商品分类" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" end-placeholder="结束日期"
          start-placeholder="开始日期" type="daterange" value-format="YYYY-MM-DD HH:mm:ss" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
        <el-button v-hasPermi="['product:spu:create']" plain type="primary" @click="openForm(undefined)">
          <Icon class="mr-5px" icon="ep:plus" />
          新增
        </el-button>
        <el-button v-hasPermi="['product:spu:export']" :loading="exportLoading" plain type="success"
          @click="handleExport">
          <Icon class="mr-5px" icon="ep:download" />
          导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="queryParams.tabType" @tab-click="handleTabClick">
      <el-tab-pane v-for="item in tabsData" :key="item.type" :label="item.name + '(' + item.count + ')'"
        :name="item.type" />
    </el-tabs>
    <el-table v-loading="loading" :data="list">
      <el-table-column type="expand">
        <template #default="{ row: spu }">
          <ContentWrap class="pl-9">


            <el-table :data="spu.skus" size="small" :stripe="true" @selection-change="handleListingSelect">
              <el-table-column type="selection" />


              <el-table-column label="图片" width="100">
                <template #default="{ row: sku }">
                  <el-image fit="cover" :src="sku.picUrl" class="h-50px w-50px flex-none" />
                </template>
              </el-table-column>
              <el-table-column label="属性" prop="id" width="100" :show-overflow-tooltip="true">
                <template #default="{ row: sku }">
                  <el-space spacer="-">
                    <span v-for="p, i in sku.properties" :key="i"> {{ p.valueName }}</span>
                  </el-space>

                </template>
              </el-table-column>
              <el-table-column label="编码" prop="barCode" width="200" />
              <el-table-column label="价格" prop="price" width="100">
                <template #default="{ row: sku }">
                  <el-tooltip placement="top">
                    <template #content>
                      <div>
                        市场价:<span>{{ fenToYuan(sku.marketPrice) }}</span>
                      </div>
                      <div>
                        成本价:<span>{{ fenToYuan(sku.costPrice) }}</span>
                      </div>
                    </template>
                    {{ fenToYuan(sku.price) }}
                  </el-tooltip>
                </template>

              </el-table-column>
              <el-table-column label="库存" prop="stock" width="100" />

              <el-table-column label="规格" width="200">
                <template #default="{ row: sku }">
                  <div v-if="sku.pkgDim">

                    <div>
                      长:{{ sku.pkgDim?.length }} 宽: {{ sku.pkgDim?.width }} 高: {{ sku.pkgDim?.height }}
                    </div>
                    <div>
                      体积: {{ sku.volume }} 重量: {{ sku.weight }}
                    </div>
                  </div>
                  <div v-else>
                    <div>
                      长:{{ spu.pkgDim?.length }} 宽: {{ spu.pkgDim?.width }} 高: {{ spu.pkgDim?.height }}
                    </div>
                    <div>
                      体积: {{ spu.pkgDim?.length * spu.pkgDim?.width * spu.pkgDim?.height }} 重量: {{ spu.pkgDim?.weight }}
                    </div>
                  </div>

                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">


                <template #header>
                  <div v-if="skus2Listing.length > 0">
                    <el-button type="primary" link size="small" @click="submitSkuListing([])">批量刊登</el-button>
                  </div>
                  <div v-else>
                    操作
                  </div>
                </template>

                <template #default="{ row: sku }">
                  <el-button type="primary" link size="small" @click="submitSkuListing([sku.id])">刊登</el-button>
                </template>


              </el-table-column>



            </el-table>

          </ContentWrap>


        </template>
      </el-table-column>
      <el-table-column label="图片" width="100">
        <template #default="{ row }">
          <el-image style="width: 60px; height: 60px" :src="row.picUrl"
            :preview-src-list="[row.picUrl, ...row.sliderPicUrls]" fit="cover" :preview-teleported=true />
        </template>
      </el-table-column>
      <el-table-column label="名称" min-width="300">
        <template #default="{ row }">
          <div class="flex">
            <div class="ml-1 overflow-hidden">
              <el-tooltip :content="row.name" placement="top">
                <div>
                  {{ row.name }}
                </div>
              </el-tooltip>
            </div>
            <div>
              {{ row.productCode }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="分类" min-width="90" prop="categoryId" show-overflow-tooltip="true">
        <template #default="{ row }">
          <span>{{ formatCategoryName(row.categoryId) }}</span>
        </template>


      </el-table-column>
      <el-table-column align="center" label="价格" min-width="160" prop="price">
        <template #default="{ row }">
          <div class="text-xs text-gray-500">
            <div>
              售价:<span>{{ fenToYuan(row.price) }}</span>
            </div>

            <div>
              市场价:<span>{{ fenToYuan(row.marketPrice) }}</span>
            </div>
            <div>
              成本价:<span>{{ fenToYuan(row.costPrice) }}</span>
            </div>
          </div>

        </template>
      </el-table-column>

      <el-table-column align="center" label="库存" min-width="90" prop="stock" />
      <el-table-column align="center" label="在售平台" min-width="100" prop="platforms" />
      <el-table-column align="center" label="状态" min-width="80">
        <template #default="{ row }">
          <div class="transform-none" v-if="row.status !== undefined">
            <el-switch v-show="Number(row.status) > 0" v-model="row.status" :active-value="1" :inactive-value="0"
              active-text="上架" inactive-text="下架" inline-prompt @change="handleStatusChange(row)" />
            <el-tag v-show="Number(row.status) === 0" type="info">草稿</el-tag>
            <el-tag v-show="Number(row.status) < 0" type="info">已归档</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column :formatter="dateFormatter" align="center" label="创建时间" prop="createTime" width="180" />
      <el-table-column align="center" fixed="right" label="操作" min-width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)"> 详情 </el-button>
          <el-button v-hasPermi="['product:spu:update']" link type="primary" @click="openForm(row.id)">
            修改
          </el-button>
          <template v-if="queryParams.tabType === 4">
            <el-button v-hasPermi="['product:spu:delete']" link type="danger" @click="handleDelete(row.id)">
              删除
            </el-button>
            <el-button v-hasPermi="['product:spu:update']" link type="primary"
              @click="handleStatus02Change(row, ProductSpuStatusEnum.DISABLE.status)">
              恢复
            </el-button>
          </template>

        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total"
      @pagination="getList" />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { TabsPaneContext } from 'element-plus'
import { createImageViewer } from '@/components/ImageViewer'
import { dateFormatter } from '@/utils/formatTime'
import { defaultProps, handleTree, treeToString } from '@/utils/tree'
import { ProductSpuStatusEnum } from '@/utils/constants'
import { fenToYuan } from '@/utils'
import download from '@/utils/download'
import * as ProductSpuApi from '@/app/erplus/api/product/spu'
import * as ProductCategoryApi from '@/app/erplus/api/product/category'

defineOptions({ name: 'ErplusProductSpu' })

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
const handleStatusChange = async (row: any) => {
  try {
    // 二次确认
    const text = row.status ? '上架' : '下架'
    await message.confirm(`确认要${text}"${row.name}"吗？`)
    // 发起修改
    await ProductSpuApi.updateStatus({ id: row.id, status: row.status })
    message.success(text + '成功')
    // 刷新 tabs 数据
    await getTabsCount()
    // 刷新列表
    await getList()
  } catch {
    // 异常时，需要重置回之前的值
    row.status =
      row.status === ProductSpuStatusEnum.DISABLE.status
        ? ProductSpuStatusEnum.ENABLE.status
        : ProductSpuStatusEnum.DISABLE.status
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

/** 商品图预览 */
const imagePreview = (imgUrl: string) => {
  createImageViewer({
    urlList: [imgUrl]
  })
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

/** 查看商品详情 */
const openDetail = (id: number) => {
  push({ name: 'ErplusProductSpuDetail', params: { id } })
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ProductSpuApi.exportSpu(queryParams)
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


// 列表项展开

const skus2Listing = ref<ProductSpuApi.Sku[]>([])

const handleListingSelect = async (val: ProductSpuApi.Sku[]) => {
  skus2Listing.value = val
}

const submitSkuListing = async (ids: number[]) => {
  if (ids.length === 0) {
    ids = skus2Listing.value.map(s => s.id) as number[]
  }
  message.info(`刊登成功-${ids}`)
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
</style>
