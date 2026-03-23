<template>

  <ContentWrap title="创建亚马逊货件">

    <el-form :model="inventorySubmit" label-width="120px" class="mb-15px">

      <el-form-item label="发货地址">
        <el-select v-model="inventorySubmit.addressId" placeholder="请选择发货地址" style="width: 200px;">
          <el-option v-for="address in addressList" :key="address.id" :label="address.fullAddress"
            :value="address.id" />
        </el-select>
        <span class="ml-10px">
          <!-- @click="openAddressForm" -->
          <a-link>新增地址</a-link>
        </span>
        <div>
          <span>
            {{addressList.find(addr => addr.id === inventorySubmit.addressId)?.fullAddress}}
          </span>
        </div>


      </el-form-item>

      <el-form-item label="包装箱">
        <div class="mb-5 inline !w-full gap-1">

          <el-row :gutter="5" class="text-gray-500">
            <el-col :span="5">
              <el-row>
                <el-col :span="6">
                  <span>包装类型：</span>
                </el-col>
                <el-col :span="18">
                  <el-form-item lable="包装方式">
                    <el-select v-model="inventorySubmit.packType" placeholder="请选择包装方式" style="width: 200px;">
                      <el-option label="散件" value="loose" />
                      <el-option label="整箱" value="case" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-col>



            <el-col :span="5">
              <el-row>
                <el-col :span="5">
                  <span>箱子数量</span>
                </el-col>
                <el-col :span="18">
                  <el-form-item lable="箱子数量">
                    <el-input-number v-model="inventorySubmit.boxNum" placeholder="请输入箱子数量" style="width: 100px;" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-col>
          </el-row>



        </div>

        <el-table :data="inventorySubmit.skuDetails" border style="width: 100%;">
          <el-table-column prop="sellerSku" label="SKU" width="180" align="center" />
          <el-table-column prop="asin" label="ASIN" align="center" width="180" />
          <el-table-column prop="quantity" label="数量" width="100" align="center" />
          <el-table-column prop="prepDetailsList" label="预处理详情" align="left" width="180">
            <template #default="scope">
              <div v-for="(prep, index) in scope.row.prepDetailsList" :key="index">
                <div>类型: {{ prep.prepType }}</div>
                <div>数量: {{ prep.prepOwner }}</div>
              </div>
            </template>
          </el-table-column>
          <template v-if="inventorySubmit.packType === 'loose'">
            <el-table-column v-for="i in inventorySubmit.boxNum" :key="i" :label="`箱子${i}`">
              <template #default="scope">
                <el-input v-model="scope.row.boxDetails[i - 1]" placeholder="请输入装箱数量" />
              </template>
            </el-table-column>
          </template>


          <template v-else>
            <el-table-column label="总装箱数量">
              <template #default="scope">
                <el-select v-model="scope.row.totalBoxNum" placeholder="请选择装箱模版">
                  <el-option v-for="n in 100" :key="n" :label="n" :value="n" />
                </el-select>
              </template>

            </el-table-column>
          </template>




        </el-table>

      </el-form-item>




      <el-form-item label="运输方式">
        <el-select v-model="inventorySubmit.shipmentMethod" placeholder="请选择运输方式" style="width: 200px;">
          <el-option label="空运" value="air" />
          <el-option label="海运" value="sea" />
          <el-option label="快递" value="express" />
        </el-select>

        <el-button type="primary" plain class="ml-10px" @click="showShipmentProvider = true">

          选择承运商
        </el-button>

        <el-table v-if="showShipmentProvider">

          <el-table-column prop="sellerSku" label="承运商" width="180" align="center" />
          <el-table-column prop="asin" label="时效" align="center" width="180" />
          <el-table-column prop="quantity" label="价格" width="100" align="center" />
          <el-table-column prop="shipmentMethod" label="操作" align="center" width="180">
            <template>
              <el-button type="text" size="small" @click="inventorySubmit.shipmentMethod = ''">选择</el-button>
            </template>
          </el-table-column>

        </el-table>







      </el-form-item>

    </el-form>


  </ContentWrap>

</template>


<script setup lang="ts">

defineOptions({ name: 'AmzInbound' })

const props = defineProps(
  {
    transferId: {
      type: Number,
      // required: true
    },
    warehouseId: {
      type: Number,
      // required: true
    },
    skuList: {
      type: Array,
      default: () => []
    }
  }
)

const inventorySubmit = ref({
  addressId: undefined,
  inboundWarehouseId: props.warehouseId,
  srcType: null,
  skuDetails: [] as Array<any>,
  boxNum: 1,

})

const addressList = ref<Array<any>>([
  {
    id: 1,
    fullAddress: '北京市朝阳区XX路XX号'
  },
  {
    id: 2,
    fullAddress: '上海市浦东新区XX路XX号'
  }
])

const showShipmentProvider = ref(false)






</script>
