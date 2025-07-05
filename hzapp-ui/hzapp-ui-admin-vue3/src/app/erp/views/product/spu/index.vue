<!-- 商品中心 - 商品列表  -->
<template>
  <doc-alert title="【商品】商品 SPU 与 SKU" url="https://help.h2z.ltd/mall/product-spu-sku/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="商品名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入商品名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品分类" prop="categoryId">
        <el-cascader
          v-model="queryParams.categoryId"
          :options="categoryList"
          :props="defaultProps"
          class="w-1/1"
          clearable
          filterable
          placeholder="请选择商品分类"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
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
        <el-button
          v-hasPermi="['product:spu:create']"
          plain
          type="primary"
          @click="openForm(undefined)"
        >
          <Icon class="mr-5px" icon="ep:plus" />
          新增
        </el-button>
        <el-button
          v-hasPermi="['product:spu:export']"
          :loading="exportLoading"
          plain
          type="success"
          @click="handleExport"
        >
          <Icon class="mr-5px" icon="ep:download" />
          导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-tabs v-model="queryParams.tabType" @tab-click="handleTabClick">
      <el-tab-pane
        v-for="item in tabsData"
        :key="item.type"
        :label="item.name + '(' + item.count + ')'"
        :name="item.type"
      />
    </el-tabs>
    <el-table v-loading="loading" :data="list">
      <el-table-column type="expand">
        <template #default="{ row }">
          <el-form class="spu-table-expand" label-position="left">
            <el-row>
              <el-col :span="24">
                <el-row>
                  <el-col :span="8">
                    <el-form-item label="商品分类:">
                      <span>{{ formatCategoryName(row.categoryId) }}</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="市场价:">
                      <span>{{ fenToYuan(row.marketPrice) }}</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="成本价:">
                      <span>{{ fenToYuan(row.costPrice) }}</span>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-row>
                  <el-col :span="8">
                    <el-form-item label="浏览量:">
                      <span>{{ row.browseCount }}</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="虚拟销量:">
                      <span>{{ row.virtualSalesCount }}</span>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column label="编号" min-width="50" prop="id" show-overflow-tooltip/>
      <el-table-column label="商品信息" min-width="200">
        <template #default="{ row }">
          <div class="flex">
            <el-image
              fit="cover"
              :src="row.picUrl"
              class="h-50px w-50px flex-none"
              @click="imagePreview(row.picUrl)"
            />
            <div class="ml-4 overflow-hidden">
              <el-tooltip effect="dark" :content="row.name" placement="top">
                <div>
                  {{ row.name }}
                </div>
              </el-tooltip>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="价格" min-width="90" prop="price">
        <template #default="{ row }"> ¥ {{ fenToYuan(row.price) }}</template>
      </el-table-column>
      <el-table-column align="center" label="销量" min-width="90" prop="salesCount" />
      <el-table-column align="center" label="库存" min-width="90" prop="stock" />
      <el-table-column align="center" label="状态" min-width="80">
        <template #default="{ row }">
          <template v-if="row.status >= 0">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="已认领"
              inactive-text="已采集"
              inline-prompt
              @change="handleStatusChange(row)"
            />
          </template>
          <template v-else>
            <el-tag type="info">回收站</el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" fixed="right" label="操作" min-width="200">
        <template #default="{ row }">
<!--          <el-button link type="primary" @click="openDetail(row.id)"> 详情 </el-button>-->
          <el-button
            v-hasPermi="['product:spu:update']"
            link
            type="primary"
            @click="openForm(row.id)"
          >
            修改
          </el-button>

          <template v-if="queryParams.tabType === 4">
            <el-button
              v-hasPermi="['product:spu:delete']"
              link
              type="danger"
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
            <el-button
              v-hasPermi="['product:spu:update']"
              link
              type="primary"
              @click="handleStatus02Change(row, ProductSpuStatusEnum.DISABLE.status)"
            >
              恢复
            </el-button>
          </template>
          <template v-else>

            <el-button
              v-hasPermi="['product:spu:claim']"
              link
              type="primary"
              @click="handleClaim(row)"
            >
              认领
            </el-button>

            <el-button
              v-hasPermi="['product:spu:update']"
              link
              type="danger"
              @click="handleStatus02Change(row, ProductSpuStatusEnum.RECYCLE.status)"
            >
              回收
            </el-button>

          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
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
import * as ProductSpuApi from '@/app/erp/api/spu'
import { ProductCategoryApi } from '@/app/erp/api/product/category'

defineOptions({ name: 'ErpPlusProductSpu' })

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
    name: '已采集',
    type: 1,
    count: 0
  },
  {
    name: '已认领',
    type: 0,
    count: 0
  },
  {
    name: '已售罄',
    type: 2,
    count: 0
  },
  {
    name: '警戒库存',
    type: 3,
    count: 0
  },
  {
    name: '回收站',
    type: 4,
    count: 0
  },

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

    // ProductSpuApi.getSkuPage({name: "123"})


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
  console.log(queryParams.value.tabType)
}

/** 获得每个 Tab 的数量 */
const getTabsCount = async () => {
  const res = await ProductSpuApi.getTabsCount()
  // for (let objName in res) {
  //   tabsData.value[Number(objName)].count = res[objName]
  //
  // }
  tabsData.value.map(tab => tab.count = res[tab.type])

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
  } catch {}
}

/** 认领产品 */
const handleClaim = async (row: any) => {

  const id = row.id
  console.log(row.id, row.status)
  // 非认领状态, 先认领，后跳转认领页面
  if (row.status !== ProductSpuStatusEnum.ENABLE.status) {
    await ProductSpuApi.updateStatus({ id: row.id, status: ProductSpuStatusEnum.ENABLE.status })

    message.success('认领成功')
    // 刷新 tabs 数据
    await getTabsCount()
    // 刷新列表
    await getList()
  }

  //
  push({name: 'ErpProductSpuClaim', params: {id: row.id}})

}



/** 更新上架/下架状态 */
const handleStatusChange = async (row: any) => {
  try {
    // 二次确认
    const text = row.status ? '认领' : '取消认领'
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
  } catch {}
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
    push({ name: 'ErpProductSpuEdit', params: { id } })
    return
  }
  // 新增
  push({ name: 'ErpProductSpuAdd' })
}

/** 查看商品详情 */
const openDetail = (id: number) => {
  push({ name: 'ErpProductSpuDetail', params: { id } })
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
  const data = await ProductCategoryApi.getProductCategoryList({})
  categoryList.value = handleTree(data, 'id', 'parentId')
})
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
