<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="产品ID" prop="productId">
        <el-input
v-model="queryParams.productId" placeholder="请输入产品ID" clearable @keyup.enter="handleQuery"
          class="!w-240px" />
      </el-form-item>
      <el-form-item label="平台" prop="platformId">
        <el-select v-model="queryParams.platformId" placeholder="请选择平台" clearable class="!w-240px">
          <el-option
v-for="SellPlatform in sellplatformList" :key="SellPlatform.id" :label="SellPlatform.name"
            :value="SellPlatform.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
v-model="queryParams.createTime" value-format="YYYY-MM-DD HH:mm:ss" type="daterange"
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
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['erplus:product-monitor:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
type="success" plain @click="handleExport" :loading="exportLoading"
          v-hasPermi="['erplus:product-monitor:export']">
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <!-- <el-table-column label="ID" align="center" prop="id" /> -->
      <el-table-column label="商品图片" align="center" width="100">
        <template #default="scope">
          <el-image
style="width: 60px; height: 60px" :src="scope.row.imageLink"
            :preview-src-list="[scope.row.imageUrl]" fit="cover" :preview-teleported=true />
        </template>
      </el-table-column>
      <el-table-column label="产品ID" align="center" prop="productId">
        <template #default="scope">

          <el-link :href="scope.row.productLink" target="_blank">{{ scope.row.productId }}</el-link>

        </template>

      </el-table-column>
      <el-table-column label="平台" align="center" prop="platformId" />
      <el-table-column label="品类ID" align="center" prop="categoryId" />
      <el-table-column label="周期" align="center" prop="crone" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="操作" align="center">
        <template #default="scope">

          <el-button
link type="primary" @click="openForm('data', scope.row)"
            v-hasPermi="['erplus:product-monitor:update']">
            数据
          </el-button>

          <el-button
link type="primary" @click="openForm('update', scope.row)"
            v-hasPermi="['erplus:product-monitor:update']">
            编辑
          </el-button>
          <el-button
link type="danger" @click="handleDelete(scope.row.id)"
            v-hasPermi="['erplus:product-monitor:delete']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
:total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <ProductMonitorForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { ProductMonitorApi, ProductMonitorVO } from '@/app/erplus/api/product/productMonitor'
import ProductMonitorForm from './ProductMonitorForm.vue'
import { SellPlatformApi, SellPlatformVO } from '@/app/erp/api/sellplatform'

/** 产品监控 列表 */
defineOptions({ name: 'ProductMonitor' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { push } = useRouter() // 路由跳转

const loading = ref(true) // 列表的加载中
const list = ref<ProductMonitorVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  productId: undefined,
  platformId: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProductMonitorApi.getProductMonitorPage(queryParams)
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
const openForm = (type: string, row?: ProductMonitorVO) => {
  if( type == 'data') {
    push({ path: '/erplusv2/product_box/monitor_data', query: { 'monitorId': row?.id , 'productId': row?.productId} })
  } else {
    formRef.value.open(type, row?.id)
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ProductMonitorApi.deleteProductMonitor(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch { }
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ProductMonitorApi.exportProductMonitor(queryParams)
    download.excel(data, '产品监控.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
  loadPlatformList()
})

const sellplatformList = ref<SellPlatformVO[]>([]) // 平台列表
const loadPlatformList = async () => {
  sellplatformList.value = await SellPlatformApi.getSellPlatformListCache() || []
}
</script>