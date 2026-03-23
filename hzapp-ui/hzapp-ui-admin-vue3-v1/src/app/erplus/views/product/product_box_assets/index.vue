<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="产品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable @keyup.enter="handleQuery"
          class="!w-240px" />
      </el-form-item>
      <el-form-item label="标签" prop="tags">
        <el-input v-model="queryParams.tags" placeholder="请输入标签" clearable @keyup.enter="handleQuery"
          class="!w-240px" />
      </el-form-item>
      <el-form-item label="素材类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择素材类型" clearable class="!w-240px">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" value-format="YYYY-MM-DD HH:mm:ss" type="daterange"
          start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['erplus:product-assets:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>

        <!-- <a-button
          type="primary"
          status="success"

          :loading="exportLoading"
          @click="handleExport"
          class="ml-10px"
          v-hasPermi="['erplus:product-assets:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </a-button> -->


      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>

    <!-- <el-card
    class="wh-full border-none"
    :body-style="{ margin: 0, padding: 0, height: '100%', position: 'relative' }"
    shadow="never"
  > -->
    <el-skeleton :loading="loading" class="wh-full" animated>
      <div
        class=" flex flex-row flex-wrap content-start justify-center h-full overflow-auto p-5 pb-[140px] box-border [&>div]:mr-3 [&>div]:mb-5"
        ref="imageListRef">

        <ProductAssetImageCard v-for="(item, index) in list" :key="index" :detail="item"
          @onBtnClick="handleImageClick" />
      </div>
    </el-skeleton>
    <!-- </el-card> -->


    <!-- <el-table
        row-key="id"
        v-loading="loading"
        :data="list"
        :stripe="true"
        :show-overflow-tooltip="true"
        @selection-change="handleRowCheckboxChange"
    >
    <el-table-column type="selection" width="55" />
      <el-table-column label="素材" align="center" prop="assetLink" >
        <template #default="scope">
          <el-image
            v-if="scope.row.assetLink"
            :src="scope.row.assetLink"
            style="width: 40px; height: 40px; margin-left: 10px;"
            fit="cover"
            preview-teleported />
        </template>
</el-table-column>
<el-table-column label="产品" align="center" prop="productId">
  <template #default="scope">
          <span>{{ scope.row.productName }}</span> <span v-if="scope.row.productId">({{ scope.row.productId }})</span>
        </template>
</el-table-column>
<el-table-column label="标签" align="center" prop="tags">
  <template #default="scope">
          <el-tag
            v-for="tag in scope.row.tags?.split(',')"
            :key="tag"
            class="m-2px"
            type="info"
            effect="light"
            >{{ tag }}</el-tag
          >
        </template>

</el-table-column>
<el-table-column label="素材类型" align="center" prop="type" />


<el-table-column label="状态" align="center" prop="status">
  <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
</el-table-column>
<el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
<el-table-column label="操作" align="center" min-width="120px">
  <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['erplus:product-assets:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['erplus:product-assets:delete']"
          >
            删除
          </el-button>
        </template>
</el-table-column>
</el-table> -->
    <!-- 分页 -->
    <div
      class="absolute bottom-[60px] h-[50px] leading-[90px] w-full z-[999] bg-white flex flex-row justify-center items-center">
      <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
        @pagination="getList" />
    </div>
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <ProductAssetsForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { ProductAssetsApi, ProductAssets, AssetSourceEnum } from '@/app/erplus/api/product/productAsset'
import ProductAssetsForm from './ProductAssetsForm.vue'
import ProductAssetImageCard from './components/ImageCard.vue'

/** 商品素材 列表 */
defineOptions({ name: 'ProductBoxAssets' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<ProductAssets[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  productName: undefined,
  tags: undefined,
  type: undefined,
  assetInfo: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProductAssetsApi.getProductAssetsPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleImageClick = (type: string, detail: ProductAssets) => {
  console.log('handleImageClick', type, detail)
  if (type === 'delete') {
    handleDelete(detail.id!)
    return
  } else if (type === 'download') {
    download.image({ url: detail.assetLink })
    return
  } else if (type === 'edit') {
    openForm('update', detail.id)
    return
  }
}


/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ProductAssetsApi.deleteProductAssets(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch { }
}

/** 批量删除商品素材 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await ProductAssetsApi.deleteProductAssetsList(checkedIds.value);
    checkedIds.value = [];
    message.success(t('common.delSuccess'))
    await getList();
  } catch { }
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: ProductAssets[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ProductAssetsApi.exportProductAssets(queryParams)
    download.excel(data, '商品素材.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>