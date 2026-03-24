<template>
  <el-drawer v-model="visiable" direction="rtl" size="90%" :with-header="true" @close="resetQuery">
    <template #title>
      <span class="text-lg font-medium">{{ title }}</span>
    </template>
    <ContentWrap>
      <!-- 搜索工作栏 -->
      <el-form
class="-mb-15px" :model="inboundSubmit" ref="queryFormRef" :inline="true" label-width="100px"
        :rules="rules">
        <el-form-item label="入库仓" prop="name">
          <el-select
v-model="inboundSubmit.inboundWarehouseId" placeholder="请选择入库仓"
            :disabled="props.warehouse ? true : false" class="!w-240px">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="来源" prop="srcType">
          <el-select v-model="inboundSubmit.srcType" placeholder="请选择入库来源" class="!w-240px" clearable>
            <el-option v-for="w in inboundSrcTypes" :key="w.value" :label="w.label" :value="w.value" />
          </el-select>
        </el-form-item>


        <el-form-item label="出库仓" prop="srcWarehouseId" v-if="inboundSubmit.srcType === 1">
          <el-select
v-model="inboundSubmit.srcWarehouseId" placeholder="请选择出库仓" @change="onOutboundWarehouseChange"
            class="!w-240px" clearable>
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>


        <el-form-item label="采购单" prop="purchaseOrderId" v-else-if="inboundSubmit.srcType === 0" clearable>
          <el-select v-model="inboundSubmit.purchaseOrderId" placeholder="请输入采购单号" class="!w-240px">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="入库业务号" prop="srcBizId" v-else>
          <el-input v-model="inboundSubmit.srcBizId" placeholder="请输入入库业务号" class="!w-240px" clearable />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" plain @click="saveTransferInbound">
            提 交
          </el-button>
          <el-button type="primary" plain @click="openStockSelect">
            导 入
          </el-button>

        </el-form-item>



      </el-form>

      <div class="mt-5">

        <div class="mb-3 flex ">



          <el-button
type="danger" plain @click="removeSelectInventory" size="small"
            v-if="selectedInventory && selectedInventory.length > 0">
            移 除
          </el-button>

        </div>
        <el-table :data="inboundSubmit.skuDetails" :stripe="true" @selection-change="onSelectedInventoryChange">
          <template #empty>
            <div class="text-center text-gray-400">请先
              <a-link @click="openStockSelect" class="p-0! m-0!">导入</a-link>
              库存记录
            </div>
          </template>
          <el-table-column type="selection" width="55" />
          <el-table-column label="SKU" align="center" prop="sellerSku" />
          <el-table-column label="总量" align="center" prop="totalCount" />
          <el-table-column label="可用库存" align="center" prop="availableCount" />
          <el-table-column label="预留量" align="center" prop="reservedCount" />
          <el-table-column label="挂起量" align="center" prop="blockCount" />
          <el-table-column label="入库数量" align="center" prop="inboundQuantity">
            <template #default="scope">
              <el-form-item :error="errors[scope.row.sellerSku]" class="mb-0!">
                <el-input v-model="scope.row.inboundQuantity" placeholder="请输入入库数量" />
              </el-form-item>
            </template>
          </el-table-column>

          <el-table-column label="操作" align="center" width="100">
            <template #default="scope">
              <el-button
type="text" size="small"
                @click="inboundSubmit.skuDetails.splice(scope.$index, 1)">移除</el-button>
            </template>
          </el-table-column>



        </el-table>



      </div>



    </ContentWrap>

    <!-- 亚马逊入库组件 -->
    <AmzInbound
ref="amzInboundRef" :skuList="inboundSubmit.skuDetails" v-if="platformInboundVisiable"
      :warehouse-id="inboundSubmit.inboundWarehouseId" @inbound-biz-created="handleInboundBizCreated" />

    <!-- 列表 -->
    <!-- <ContentWrap /> -->

    <WarehouseInventroySelectList
ref="warehouseInventroySelectRef" :transferData="warehouseTransfer"
      @on-selected="fillAvailableInventroys" />

  </el-drawer>
</template>

<script setup lang="ts">
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { WarehouseApi, Warehouse, WarehouseTypes, inboundSrcTypes } from '@/app/erplus/api/stock/warehouse'
import * as StockApi from '@/app/erplus/api/stock/stock'
import { SellPlatformApi, SellPlatformVO } from '@/app/erp/api/sellplatform' // 已存在模块
import { ShopApi } from '@/app/erplus/api/system/shop'
import WarehouseForm from './WarehouseForm.vue'

import WarehouseInventroySelectList from '../components/WarehouseInventorySelectList.vue'
import AmzInbound from '../components/amz_v1/AmzInboundV1.vue'

/** ERP 仓库 列表 */
defineOptions({ name: 'WarehouseInboundForm' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const props = defineProps({
  warehouse: {
    type: Object as PropType<Warehouse>,
    // require: true
  },
  title: {
    type: String,
    default: '创建入库单'
  }
})


const inboundSubmit = ref({
  inboundWarehouseId: props.warehouse?.id,
  srcType: 0,
  purchaseOrderId: undefined,
  srcWarehouseId: undefined,
  srcBizId: undefined,
  skuDetails: []
})




const saveTransferInbound = () => {
  if (!validateInboundSubmit()) {
    return
  }


  // 提交入库单

  console.log('saveTransferInbound:', inboundSubmit.value)
  message.success("入库单提交成功")

  if (props.warehouse && props.warehouse.platformId) {
    platformInboundVisiable.value = true
  }

}

const platformInboundVisiable = ref(false)

const visiable = ref(false);




const queryFormRef = ref() // 搜索的表单

const rules = reactive({
  srcType: [
    {
      required: true,
      message: '入库来源为必填项',
      // trigger: 'change'
    }
  ],
  purchaseOrderId: [
    {
      required: inboundSubmit.value.srcType === 0,
      message: '采购单号为必填项',
      // trigger: 'change'
    }
  ],
  srcWarehouseId: [
    {
      required: computed(() => inboundSubmit.value.srcType === 1),
      message: '出仓库为必填项',
      // trigger: 'change'
    }
  ],

  srcBizId: [
    {
      required: computed(() => inboundSubmit.value.srcType === 2),
      message: '入仓业务号为必填项',
      // trigger: 'change'
    }
  ],

})


/** 重置按钮操作 */
const resetQuery = () => {
  // queryFormRef.value.resetFields()
  inboundSubmit.value.skuDetails = []
  platformInboundVisiable.value = false
}



/** 初始化 **/
onMounted(async () => {
  await loadWarehouse()

})


const showDrawer = () => {
  visiable.value = true;
}

defineExpose({ showDrawer }) // 提供 showDrawer 方法，用于打开侧边栏


const warehouseTransfer = ref<StockApi.WarehouseInventoryQuery>({} as StockApi.WarehouseInventoryQuery)

const onOutboundWarehouseChange = async () => {
  // 校验出库仓不能与入库仓相同
  if (inboundSubmit.value.srcWarehouseId === inboundSubmit.value.inboundWarehouseId) {
    message.error('出库仓不能与入库仓相同');
    inboundSubmit.value.srcBizId = undefined;
    return;
  }

  warehouseTransfer.value = {
    warehouseId: inboundSubmit.value.srcWarehouseId,
    inboundWarehouseId: inboundSubmit.value.inboundWarehouseId
  }


  // StockApi.getTransferAvailablInventory({
  //   warehouseId: inboundSubmit.value.srcWarehouseId,
  //   inboundWarehouseId: inboundSubmit.value.inboundWarehouseId
  // })

  // 获取出库仓的库存列表
  // if (inboundSubmit.value.srcBizId) {
  //   try {
  //     loading.value = true;
  //     const data = await WarehouseApi.getWarehouseInventoryList(inboundSubmit.value.srcBizId);
  //     // 设置 skuDetails
  //     inboundSubmit.value.skuDetails = data.map(item => ({
  //       sku: item.sku,
  //       availableQuantity: item.availableQuantity,
  //       inboundQuantity: 0
  //     }));
  //   } finally {
  //     loading.value = false;
  //   }
  // } else {
  //   inboundSubmit.value.skuDetails = [];
  // }
}

const warehouseList = ref<Warehouse[]>([]);

const loadWarehouse = async () => {
  const data = await WarehouseApi.getWarehouseList();
  warehouseList.value = data;
}


watch(
  () => props.warehouse,
  (data) => {
    if (!data) return
    inboundSubmit.value.inboundWarehouseId = data.id;

  },
  {
    // deep: false,
    immediate: true
  }
)

const fillAvailableInventroys = (data) => {
  console.log('fillAvailableInventroys:', data)


  inboundSubmit.value.skuDetails = data
}

const warehouseInventroySelectRef = ref()

const openStockSelect = async () => {
  await queryFormRef.value.validate()

  warehouseInventroySelectRef.value.openForm()
}

const selectedInventory = ref([])
const onSelectedInventoryChange = (data) => {
  selectedInventory.value = data
}

const removeSelectInventory = () => {
  if (isEmpty(selectedInventory.value)) {
    message.warning('请先选择要移除的库存记录')
    return
  }

  inboundSubmit.value.skuDetails = inboundSubmit.value.skuDetails.filter(item => !selectedInventory.value.includes(item))
  selectedInventory.value = []
}

const errors = ref({})

const validateInboundSubmit = () => {

  if (inboundSubmit.value.skuDetails.length === 0) {
    message.error('请先导入库存记录')
    return false
  }

  errors.value = {} // Clear previous errors

  inboundSubmit.value.skuDetails.forEach(item => {
    if (!item.inboundQuantity || item.inboundQuantity <= 0) {
      errors.value[item.sellerSku] = `SKU ${item.sellerSku} 的入库数量必须大于0`
      return
    }
    if (item.inboundQuantity > item.availableCount) {
      errors.value[item.sellerSku] = `SKU ${item.sellerSku} 的入库数量不能大于可用库存`
      return
    }
  })

  return errors.value && Object.keys(errors.value).length === 0
}

const handleInboundBizCreated = async (planId: string) => {
  console.log('handleInboundBizCreated:', planId)
}



</script>