<template>
  <el-drawer v-model="visiable" direction="rtl" size="90%" :with-header="true" @close="resetQuery">
    <template #title>
      <span class="text-lg font-medium">{{ title }}</span>
    </template>


    <el-tabs tab-position="right" active-name="inventory-detail">
      <el-tab-pane label="库存详情" name="inventory-detail" class="mr-15">
        <ContentWrap>
          <el-table :data="data" :stripe="true" :show-overflow-tooltip="true">
            <el-table-column label="SKU" align="center" prop="sellerSku" />
            <el-table-column label="总量" align="center" prop="totalCount" />
            <el-table-column label="可用库存" align="center" prop="availableCount" />
            <el-table-column label="预留量" align="center" prop="reservedCount" />
            <el-table-column label="挂起量" align="center" prop="blockCount" />
          </el-table>
        </ContentWrap>
      </el-tab-pane>

      <el-tab-pane label="出/入库记录" name="other-info">
        <ContentWrap>
          <!-- 其他信息内容 -->
          <div class="text-center text-gray-400">暂无其他信息</div>
        </ContentWrap>

      </el-tab-pane>




    </el-tabs>




  </el-drawer>


</template>



<script setup lang="ts">

defineOptions({ name: "WarehouseInventoryDetail" })

const props = defineProps({

  title: {
    type: String,
    default: '库存详情'
  },

})

const warehouseId = ref<number | null>(null)

const visiable = ref(false)

const data = ref([])
const getList = async () => {
  // 获取库存详情数据
  if (warehouseId.value === null) {
    return
  }
}



const openDetail = async (id: number) => {
  visiable.value = true
  warehouseId.value = id
  await getList()
}

defineExpose({ openDetail })

</script>