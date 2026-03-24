<template>
  <Dialog :title="title" v-model="dialogVisible" width="70%" :scroll="false" maxHeight="600px">
    <div class="flex flex-col h-550px">
      <div class="mb-10px">
        <el-input
v-model="queryParams.sellerSku" placeholder="请输入SKU" clearable class="!w-240px"
          @keyup.enter="handleQuery" />
        <el-button class="ml-10px" @click="handleQuery">
          <Icon icon="ep:search" />
          查询
        </el-button>
      </div>

      <el-table
v-loading="loading" :data="list" @selection-change="handleSelectionChange" ref="tableRef"
        row-key="sellerSku" class="flex-1" height="100%">
        <el-table-column type="selection" width="55" />
        <el-table-column label="SKU" align="center" prop="sellerSku" />
        <el-table-column label="可用库存" align="center" prop="availableCount" />
        <el-table-column label="总量" align="center" prop="totalCount" />
        <el-table-column label="预留量" align="center" prop="reservedCount" />
        <el-table-column label="挂起量" align="center" prop="blockCount" />
      </el-table>

      <Pagination
:total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
        @pagination="getList" class="mt-10px justify-end" />
    </div>

    <template #footer>
      <el-button @click="confirmSelect" type="primary" :disabled="selectedRows.length === 0">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { WarehouseApi } from '@/app/erplus/api/stock/warehouse'

defineOptions({ name: 'StockSkuSelect' })

const props = defineProps({
  warehouseId: {
    type: Number,
    required: false
  },
  selectedSkus: {
    type: Array,
    default: () => []
  },
  title: {
    type: String,
    default: '选择SKU'
  }
})

const emit = defineEmits(['selected'])

const dialogVisible = ref(false)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const selectedRows = ref([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  warehouseId: props.warehouseId,
  sellerSku: ''
})

/** 打开弹窗 */
const open = async () => {
  if (!props.warehouseId) {
    ElMessage.error('请选择仓库')
    return
  }
  dialogVisible.value = true
  queryParams.warehouseId = props.warehouseId
  handleQuery()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WarehouseApi.getWarehouseInventoryPage(queryParams)
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

/** 选择项发生变化 */
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

/** 确认选择 */
const confirmSelect = () => {
  emit('selected', selectedRows.value)
  dialogVisible.value = false
}

defineExpose({ open })
</script>

<style lang="scss" scoped>
:deep(.el-table__inner-wrapper::before) {
  display: none;
}
</style>
