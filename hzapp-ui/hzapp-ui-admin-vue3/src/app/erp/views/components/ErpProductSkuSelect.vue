<!-- 可入库的订单列表 -->
<template>
  <Dialog
    title="选择产品"
    v-model="dialogVisible"
    :appendToBody="true"
    :scroll="true"
    width="1080"
  >
    <ContentWrap>
      <!-- 搜索工作栏 -->
      <el-form
        class="-mb-15px"
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
      >
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="产品名称"
            clearable
            @keyup.enter="handleQuery"
            class="!w-240px"
          />
        </el-form-item>
        <el-form-item label="产品ID" prop="spuId">
          <el-input
            v-model="queryParams.spuId"
            placeholder="请输入SPU编号"
            clearable
            @keyup.enter="handleQuery"
            class="!w-240px"
          />
        </el-form-item>
        <el-form-item label="品类" prop="categoryId">
          <el-cascader
            v-model="queryParams.categoryId"
            placeholder="请选择品类"
            clearable
            @keyup.enter="handleQuery"
            class="!w-240px"

          />


        </el-form-item>



        <el-form-item>
          <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
          <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <ContentWrap>
      <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
        <el-table-column type="selection" align="center" width="65" />
        <el-table-column min-width="180" label="产品ID" align="center" prop="spuId" />
        <el-table-column min-width="180" label="产品信息" align="center" prop="skuId" >
            <template scope="{row}" >



            </template>
        </el-table-column>
        <el-table-column label="库存" align="center" prop="stock" />

      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getList"
      />
    </ContentWrap>
    <template #footer>
      <el-button :disabled="!currentRow" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { ElTable } from 'element-plus'
import { PurchaseOrderApi, PurchaseOrderVO } from '@/app/erp/api/purchase/order'
import { dateFormatter2 } from '@/utils/formatTime'
import { erpCountTableColumnFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { ProductApi, ProductVO } from '@/app/erp/api/product/product'

defineOptions({ name: 'ErpProductSkuSelect' })

const list = ref<PurchaseOrderVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const loading = ref(false) // 列表的加载中
const dialogVisible = ref(false) // 弹窗的是否展示
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  spuId: undefined,
  categoryId: undefined,
  status: 1, // 默认查已认领商品
})
const queryFormRef = ref() // 搜索的表单
const productList = ref<ProductVO[]>([]) // 产品列表

/** 选中行 */
const currentRowValue = ref(undefined) // 选中行的 value
const currentRow = ref(undefined) // 选中行
const handleCurrentChange = (row) => {
  currentRow.value = row
}

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  await nextTick() // 等待，避免 queryFormRef 为空
  // 加载可入库的订单列表
  await resetQuery()
  // 加载产品列表
  productList.value = await ProductApi.getProductSimpleList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交选择 */
const emits = defineEmits<{
  (e: 'success', value: PurchaseOrderVO): void
}>()
const submitForm = () => {
  try {
    emits('success', currentRow.value)
  } finally {
    // 关闭弹窗
    dialogVisible.value = false
  }
}

/** 加载列表  */
const getList = async () => {
  loading.value = true
  try {
    const data = await PurchaseOrderApi.getPurchaseOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  currentRowValue.value = undefined
  currentRow.value = undefined
  getList()
}
</script>
