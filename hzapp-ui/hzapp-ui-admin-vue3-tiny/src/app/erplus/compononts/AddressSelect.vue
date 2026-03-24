<template>
  <div class="address-select-container">
    <div class="flex items-center gap-2 mb-2">
      <el-select
v-model="modelValue" :placeholder="placeholder" clearable filterable class="flex-1"
        @change="handleChange">
        <el-option v-for="item in addressList" :key="item.id" :label="item.name" :value="item.id as number">
          <div class="flex flex-col">
            <span class="font-bold">{{ item.name }}</span>
            <span class="text-xs text-gray-500">{{ item.city }}, {{ item.countryCode }}</span>
          </div>
        </el-option>
      </el-select>
      <el-button type="primary" plain @click="showAddDialog = true">
        <Icon icon="ep:plus" class="mr-1" /> 新增地址
      </el-button>
    </div>

    <!-- 选中地址详情展示 -->
    <div v-if="selectedAddress" class="address-details mt-2 p-3 bg-gray-50 border rounded text-sm text-gray-600">
      <div class="flex justify-between items-start mb-2">
        <span class="font-bold text-base text-gray-800">{{ selectedAddress.name }}</span>

      </div>
      <div class="space-y-1">
        <span class="text-gray-500">{{ selectedAddress.phoneNumber }}</span>
        <div>{{ selectedAddress.addressLine1 }}{{ selectedAddress.addressLine2 ? ', ' + selectedAddress.addressLine2 :
          '' }}</div>
        <div>{{ selectedAddress.city }}{{ selectedAddress.stateOrRegion ? ', ' + selectedAddress.stateOrRegion : '' }}
          {{ selectedAddress.postalCode }}</div>
        <div class="font-medium">{{ selectedAddress.countryCode }}</div>
      </div>
    </div>

    <!-- 新增地址弹窗 -->
    <Dialog title="新增发货地址" v-model="showAddDialog" width="500px">
      <el-form ref="formRef" :model="addressForm" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="addressForm.name" placeholder="请输入名称/联系人" />
        </el-form-item>
        <el-form-item label="电话" prop="phoneNumber">
          <el-input v-model="addressForm.phoneNumber" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="国家/地区" prop="countryCode">
          <el-input v-model="addressForm.countryCode" placeholder="例如: CN, US" />
        </el-form-item>
        <el-form-item label="州/省" prop="stateOrRegion">
          <el-input v-model="addressForm.stateOrRegion" placeholder="请输入州/省" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="地址1" prop="addressLine1">
          <el-input v-model="addressForm.addressLine1" placeholder="请输入街道地址" />
        </el-form-item>
        <el-form-item label="地址2" prop="addressLine2">
          <el-input v-model="addressForm.addressLine2" placeholder="可选: 公寓, 单元等" />
        </el-form-item>
        <el-form-item label="邮编" prop="postalCode">
          <el-input v-model="addressForm.postalCode" placeholder="请输入邮政编码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">确定</el-button>
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ShipFromAddress } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'AddressSelect' })

const props = defineProps({
  placeholder: {
    type: String,
    default: '请选择地址'
  }
})

const emit = defineEmits(['change'])

const modelValue = defineModel<any>({ default: null })
const addressList = ref<ShipFromAddress[]>([])

const selectedAddress = computed(() => {
  return addressList.value.find((addr) => addr.id === modelValue.value)
})

/** 获取地址列表 (Mocked) */
const getAddressList = async () => {
  // 模拟初始数据
  addressList.value = [
    {
      id: 1,
      name: '张三',
      phoneNumber: '13800138000',
      addressLine1: '广东省深圳市宝安区西乡街道',
      city: '深圳',
      postalCode: '518000',
      countryCode: 'CN',
      stateOrRegion: '广东'
    },
    {
      id: 2,
      name: '李四',
      phoneNumber: '13900139000',
      addressLine1: '浙江省杭州市余杭区仓前街道',
      city: '杭州',
      postalCode: '310000',
      countryCode: 'CN',
      stateOrRegion: '浙江'
    }
  ] as ShipFromAddress[]
}

const handleChange = (id) => {
  const address = addressList.value.find((item) => item.id === id)
  emit('change', address)
}

// 新增地址相关
const showAddDialog = ref(false)
const formRef = ref()
const addressForm = reactive<ShipFromAddress>({
  name: '',
  phoneNumber: '',
  addressLine1: '',
  addressLine2: '',
  city: '',
  stateOrRegion: '',
  postalCode: '',
  countryCode: ''
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  phoneNumber: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  countryCode: [{ required: true, message: '请输入国家代码', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  addressLine1: [{ required: true, message: '请输入街道地址', trigger: 'blur' }],
  postalCode: [{ required: true, message: '请输入邮编', trigger: 'blur' }]
}

const saveAddress = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      const newId = Math.max(0, ...addressList.value.map(a => a.id || 0)) + 1
      const newAddress = { ...addressForm, id: newId }
      addressList.value.push(newAddress)
      modelValue.value = newId
      emit('change', newAddress)

      ElMessage.success('新增地址成功')
      showAddDialog.value = false
      // 重置表单
      Object.assign(addressForm, {
        name: '',
        phoneNumber: '',
        addressLine1: '',
        addressLine2: '',
        city: '',
        stateOrRegion: '',
        postalCode: '',
        countryCode: ''
      })
    }
  })
}

onMounted(() => {
  getAddressList()
})
</script>

<style scoped>
.address-details {
  border-left: 4px solid #409eff;
}

.el-select-dropdown__item {
  height: auto;
  line-height: 1.4;
  padding: 8px 20px;
}
</style>
