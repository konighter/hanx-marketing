<template>
  <ContentWrap :title="formData.id ? '编辑发货计划' : '创建发货计划'">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" class="mt-4" :inline-message="true">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="发货计划编号" prop="code">
            <el-input v-model="formData.code" disabled placeholder="自动生成" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计划名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入计划名称" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="店铺" prop="shopId">
            <ShopCascaderSelect v-model="formData.shopId" class="w-full" />
          </el-form-item>
        </el-col>

        <el-col :span="8">
          <el-form-item label="到货地" prop="destination">
            <el-select v-model="formData.destination" placeholder="请选择到货地(市场)" filterable class="w-full">
              <el-option v-for="item in markets" :key="item.id" :label="`${item.name} (${item.id})`" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="发货仓库" prop="warehouseId">
            <el-select v-model="formData.warehouseId" placeholder="请选择发货仓库" filterable class="w-full" clearable>
              <el-option v-for="item in warehouses" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="发货地址" prop="fromAddress">
            <AddressSelect v-model="formData.fromAddress" placeholder="请选择发货地址" class="w-full" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="发货备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="填写发货注意事项" />
      </el-form-item>

      <div class="mb-2 flex items-center justify-between mt-4">
        <span class="text-lg font-bold">发货商品</span>
        <el-button type="primary" @click="handleAddProduct">
          <Icon icon="ep:plus" class="mr-1" /> 添加商品
        </el-button>
      </div>

      <el-table :data="formData.items" border style="width: 100%">
        <el-table-column type="index" width="60" label="序号" align="center" />
        <el-table-column label="图片" width="80" align="center">
          <template #default="scope">
            <el-image :src="scope.row.image" class="w-12 h-12 rounded" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column label="商品名称" min-width="150">
          <template #default="scope">
            <div class="flex flex-col gap-1">
              <div class="flex items-center">
                <span class="font-bold mr-2">{{ scope.row.sellerSku }}</span>
                <el-tag size="small" type="success" effect="plain">已验证</el-tag>
              </div>
              <div class="text-12px text-gray-400 truncate" :title="scope.row.name">
                {{ scope.row.name }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="availableCount" label="可用库存" width="150" align="center" />
        <el-table-column prop="dimension" label="长*宽*高" width="150" align="center" />
        <el-table-column prop="weight" label="重量" width="150" align="center" />


        <el-table-column prop="quantity" label="发货数量" width="150" align="center">
          <template #default="scope">
            <el-form-item
:prop="`items.${scope.$index}.quantity`" :rules="validateQuantity(scope.row)" class="mb-0px!"
              label-width="0">
              <el-input-number
v-model="scope.row.quantity" :min="1" size="small" controls-position="right"
                class="!w-full" />
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-button type="danger" link @click="handleDeleteProduct(scope.$index)">
              <Icon icon="ep:delete" />
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-center mt-10">
        <el-button @click="handleCancel">取 消</el-button>
        <el-button :loading="submitting" @click="handleSave">保 存</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit" v-if="formData.id">提 交</el-button>
      </div>
    </el-form>

    <StockSkuSelect ref="stockSkuSelectRef" :warehouseId="formData.warehouseId" @selected="handleStockSkuSelected" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, unref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useTagsView } from '@/hooks/web/useTagsView'
import { ContentWrap } from '@/components/ContentWrap'
import { Icon } from '@/components/Icon'
import { ElMessage, ElMessageBox } from 'element-plus'
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue'
import AddressSelect from '@/app/erplus/compononts/AddressSelect.vue'
import StockSkuSelect from '@/app/erplus/views/stock/components/StockSkuSelect.vue'
import { WarehouseApi } from '@/app/erplus/api/stock/warehouse'
import { shipmentApi } from '@/app/erplus/api/stock/shipment'

const { push, currentRoute } = useRouter()
const { query } = useRoute()
const { closeCurrent } = useTagsView()

// 模拟数据
const shops = ref([
  { id: 1, name: 'Amazon-US-Shop' },
  { id: 2, name: 'Amazon-UK-Shop' }
])

const warehouses = ref<any[]>([])

const markets = ref([
  { id: 'ATVPDKIKX0DER', name: '美国' },
  { id: 'A1F8U5RL15Q6QS', name: '英国' },
  { id: 'A1PA6795UKMFR9', name: '德国' }
])



const formData = reactive({
  id: undefined,
  code: '',
  name: '',
  shopId: undefined as number | undefined,
  warehouseId: undefined as number | undefined,
  destination: 'ATVPDKIKX0DER',
  fromAddress: undefined as number | undefined,
  remark: '',
  items: [] as any[]
})

const rules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  warehouseId: [{ required: false, message: '请选择发货仓库', trigger: 'change' }],
  destination: [{ required: true, message: '请选择到货地', trigger: 'change' }],
  fromAddress: [{ required: true, message: '请选择发货地址', trigger: 'change' }]
}

const formRef = ref()
const stockSkuSelectRef = ref()
const submitting = ref(false)

const validateQuantity = (row: any) => {
  return [
    { required: true, message: '数量不能为空', trigger: 'blur' },
    {
      validator: (_rule: any, value: any, callback: any) => {
        if (value <= 0) {
          callback(new Error('发货数量必须大于0'))
        } else if (value > row.availableCount) {
          callback(new Error(`不能超过可用库存(${row.availableCount})`))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}



// 生成编号
const generatePlanId = () => {
  const now = new Date()
  const dateStr = now.getFullYear() +
    String(now.getMonth() + 1).padStart(2, '0') +
    String(now.getDate()).padStart(2, '0')
  const timeStr = String(now.getHours()).padStart(2, '0') +
    String(now.getMinutes()).padStart(2, '0') +
    String(now.getSeconds()).padStart(2, '0')
  return `FBA_${dateStr}_${timeStr}`
}

/** 获取仓库列表 */
const getWarehouseList = async () => {
  const data = await WarehouseApi.getWarehouseList()
  warehouses.value = data
}

onMounted(async () => {
  await getWarehouseList()
  if (query.id) {
    const data = await shipmentApi.getShipment(Number(query.id))
    Object.assign(formData, data)
  } else {
    formData.code = generatePlanId()
    formData.name = `发货计划_${new Date().toLocaleDateString()}`
  }
})

const handleAddProduct = () => {
  stockSkuSelectRef.value.open()
}

const handleStockSkuSelected = (products: any[]) => {
  console.log('Selected products:', products)

  if (!products || products.length === 0) return

  let addedCount = 0
  products.forEach(product => {
    // 检查是否已存在
    const exists = formData.items.some(item => item.sellerSku === product.sellerSku)
    if (!exists) {
      formData.items.push({
        sellerSku: product.sellerSku,
        name: product.name || product.sellerSku, // 暂无名称字段时回退
        image: product.picUrl,
        availableCount: product.availableCount,
        totalCount: product.totalCount,
        reservedCount: product.reservedCount,
        blockCount: product.blockCount,
        quantity: 1
      })
      addedCount++
    }
  })

  if (addedCount > 0) {
    ElMessage.success(`已添加 ${addedCount} 个商品`)
  } else {
    ElMessage.warning('所选商品已在列表中')
  }
}

const handleDeleteProduct = (index: number) => {
  formData.items.splice(index, 1)
}

const emit = defineEmits(['success', 'close'])

const handleSave = async () => {
  doSubmit('save')
}

const handleSubmit = async () => {
  try {
    await ElMessageBox.confirm('确定要提交该发货计划吗？提交后将进入审核流程。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    doSubmit('submit')
  } catch (e) {
    // User canceled
  }
}

const doSubmit = async (type: 'save' | 'submit') => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      if (formData.items.length === 0) {
        ElMessage.warning('请至少添加一个商品')
        return
      }

      submitting.value = true
      try {
        console.log(`${type === 'save' ? 'Saving' : 'Submitting'} data:`, formData)

        if (type === 'save') {
          const res = await shipmentApi.saveShipment(formData)

          formData.id = res
        } else {
          await shipmentApi.submitShipment(formData)
        }

        ElMessage.success(type === 'save' ? '保存成功' : '提交成功')
        emit('success')
        if (type === 'submit') {
          close()
        }
      } catch (error) {
        ElMessage.error(type === 'save' ? '保存失败' : '提交失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleCancel = () => {
  close()
}

/** 关闭当前窗口 */
const close = () => {
  closeCurrent(unref(currentRoute))
  push('/erplusv2/stock/shipment')
}

</script>

<style scoped>
.address-details {
  border-left: 4px solid #409eff;
}
</style>
