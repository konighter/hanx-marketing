<template>

  <Dialog :title="props.title" v-model="dialogVisible" width="60%">


    <el-table :data="data" @selection-change="onSelectChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="SKU" align="center" prop="sellerSku" />
      <el-table-column label="总量" align="center" prop="totalCount" />
      <el-table-column label="可用库存" align="center" prop="availableCount" />
      <el-table-column label="预留量" align="center" prop="reservedCount" />
      <el-table-column label="挂起量" align="center" prop="blockCount" />

    </el-table>


    <template #footer>
      <el-button @click="confirmSelect" type="primary">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>

  </Dialog>




</template>


<script setup lang="ts">

import * as StockApi from '@/app/erplus/api/stock/stock'
import { emit } from 'process'
defineOptions({ name: "WarehouseInventorySelectList" })

const dialogVisible = ref(false)
const props = defineProps({

  transferData: {
    type: Object as PropType<StockApi.WarehouseInventoryQuery>,
  },

  title: {
    type: String,
    default: '选择库存'
  }

})

const openForm = async () => {
  await getList()
  dialogVisible.value = true
}

defineExpose({ openForm })

const data = ref([])

const getList = async () => {
  const result = await StockApi.getTransferAvailablInventory(props.transferData as StockApi.WarehouseInventoryQuery)
  data.value = result
}

onMounted(async () => {
  // await getList()
})

const emits = defineEmits(['onSelected'])

const selected = ref([])
const onSelectChange = (data) => {
  console.log('selected rows:', data)
  selected.value = data
}


const confirmSelect = () => {
  dialogVisible.value = false
  console.log('confirmSelect:', selected.value)
  emits("onSelected", selected.value)
}



</script>
