<template>
  <div class="inbound-plan">
    <el-form :model="formData" label-width="120px" :rules="rules" ref="formRef">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="计划名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入计划名称" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="目标市场" prop="marketId">
            <el-select v-model="formData.marketId" placeholder="请选择目标市场" class="w-full" :disabled="true">
              <el-option label="美国 (ATVPDKIKX0DER)" value="ATVPDKIKX0DER" />
              <el-option label="加拿大 (A2EUQ1WTGCTBG2)" value="A2EUQ1WTGCTBG2" />
              <el-option label="墨西哥 (A1AM78C64UM0Y8)" value="A1AM78C64UM0Y8" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="发货地址" prop="addressId">
        <el-select v-model="formData.addressId" placeholder="请选择发货地址" class="w-full">
          <el-option v-for="addr in addressList" :key="addr.id!" :label="addr.name" :value="addr.id!">
            <span>{{ addr.name }} ({{ addr.city }}, {{ addr.countryCode }})</span>
          </el-option>
        </el-select>
        <div v-if="selectedAddress" class="address-details mt-10px p-10px bg-gray-50 border-rounded">
          <div>{{ selectedAddress.name }}</div>
          <div class="text-gray-500 text-sm">
            {{ selectedAddress.addressLine1 }}, {{ selectedAddress.city }}, {{ selectedAddress.postalCode }}, {{
              selectedAddress.countryCode }}
          </div>
        </div>
      </el-form-item>



      <el-form-item label="商品信息">
        <el-table :data="skuList" border class="mb-20px">
          <el-table-column prop="sellerSku" label="SKU" width="180" />
          <el-table-column prop="asin" label="ASIN" width="150" />
          <el-table-column prop="quantity" label="数量" width="180">
            <template #default="scope">
              <span v-if="scope.row.inboundQuantity">{{ scope.row.inboundQuantity }}</span>
              <el-input-number v-else v-model="scope.row.inboundQuantity" :min="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column prop="prepDetailsList">
            <template #header>
              <div class="flex items-center gap-2">
                <span>预处理</span>
                <el-select v-model="batchPrep" placeholder="批量设置" size="small" style="width: 140px;"
                  @change="handleBatchPrepChange">
                  <el-option v-for="item in preOwnerTypes" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </div>
            </template>

            <template #default="scope">
              <el-select v-model="scope.row.preOwner" placeholder="请选择预处理" class="w-full">
                <el-option v-for="item in preOwnerTypes" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

      <div class="flex justify-center mt-30px">
        <el-button type="primary" :loading="loading" @click="submitForm">创建入库计划</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { AmzInboundApi, ShipFromAddress, CreateInboundPlanRequest, preOwnerTypes } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage } from 'element-plus'
import { PropType } from 'vue'
import { Warehouse } from '@/app/erplus/api/stock/warehouse'

const props = defineProps({
  skuList: {
    type: Array as any,
    default: () => []
  },
  warehouse: {
    type: Object as PropType<Warehouse>,
    default: () => ({})
  }
})

const emit = defineEmits(['next'])

const loading = ref(false)
const formRef = ref()
const addressList = ref<ShipFromAddress[]>([])

const formData = ref({
  name: `FBA_${new Date().toLocaleDateString()}`,
  addressId: undefined,
  marketId: 'ATVPDKIKX0DER',
  packType: 'loose',
  quantity: 1,

})

const batchPrep = ref('')

const handleBatchPrepChange = (val: string) => {
  props.skuList.forEach((item: any) => {
    item.preOwner = val
  })
}

const rules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  addressId: [{ required: true, message: '请选择发货地址', trigger: 'change' }],
  marketId: [{ required: true, message: '请选择目标市场', trigger: 'change' }]
}

const selectedAddress = computed(() => {
  return addressList.value.find(addr => addr.id === formData.value.addressId)
})


// {
//   "addressLine1" : "北京市海淀区羊坊店街道",
//   "addressLine2" : "会城门小区12号楼4门302",
//   "city" : "北京",
//   "companyName" : "北京翰展科技有限公司",
//   "countryCode" : "CN",
//   "districtOrCounty" : "海淀区",
//   "email" : "15601206002@163.com",
//   "name" : "张冬梅",
//   "phoneNumber" : "18600797893",
//   "postalCode" : "100000",
//   "stateOrProvinceCode" : "Beijing"
// }

onMounted(async () => {
  try {
    // const data = await AmzInboundApi.getShipFromAddresses()
    addressList.value = [
      { id: 1, name: '张冬梅', phoneNumber: "18600797893", addressLine1: '北京市海淀区羊坊店街道', addressLine2: "会城门小区12号楼4门302", city: '北京', postalCode: '100000', countryCode: 'CN', stateOrProvinceCode: "beijing" }
    ]
  } catch (error) {
    console.error('Failed to load addresses', error)
  }
})

const submitForm = async () => {

  console.log('Warehouse', props.warehouse)

  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const payload = {
          name: formData.value.name,
          shopId: props.warehouse.shopId,
          address: selectedAddress.value!,
          marketId: formData.value.marketId,
          items: props.skuList.map((item: any) => ({
            sellerSku: item.sellerSku,
            platformProductId: item.asin,
            quantity: item.inboundQuantity
          }))
        } as CreateInboundPlanRequest
        // const res = await AmzInboundApi.createInboundPlan(payload)

        ElMessage.success('入库计划创建成功')
        // console.log('Created Inbound Plan:', res)
        emit('next', "wfe87b2c94-6f9d-444a-b035-90b8a2df0d76") // Fallback for demo
      } catch (error) {
        console.error('Failed to create plan', error)
        ElMessage.error('创建失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.address-details {
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
</style>
