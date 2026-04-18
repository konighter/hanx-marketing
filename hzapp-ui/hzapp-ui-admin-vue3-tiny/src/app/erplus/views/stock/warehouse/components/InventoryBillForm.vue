<template>
  <el-drawer v-model="visible" :title="title" size="60%" @close="handleClose">
    <div class="p-20px">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单据类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择" disabled class="w-full">
                <el-option :value="10" label="入库单" />
                <el-option :value="20" label="出库单" />
                <el-option :value="30" label="调拨单" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务类型" prop="refType">
              <el-select v-model="form.refType" placeholder="请选择" class="w-full" :disabled="isPlatformWarehouse">
                <el-option 
                  v-for="opt in currentRefTypeOptions" 
                  :key="opt.value" 
                  :value="opt.value" 
                  :label="opt.label" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="sourceLabel" prop="fromId">
              <el-select 
                v-if="form.type === 30 || form.type === 20" 
                v-model="form.fromId" 
                placeholder="请选择来源仓库" 
                class="w-full"
                filterable
                :disabled="fixedWarehouse"
              >
                <el-option v-for="w in shippingWarehouseList" :key="w.id" :label="w.name" :value="String(w.id)" />
              </el-select>
              <el-input v-else v-model="form.fromId" placeholder="请输入来源标识" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="targetLabel" prop="toId">
              <el-select 
                v-if="form.type === 30 || form.type === 10" 
                v-model="form.toId" 
                placeholder="请选择目标仓库" 
                class="w-full" 
                filterable
                :disabled="fixedWarehouse && form.type === 10"
              >
                <el-option v-for="w in receivingWarehouseList" :key="w.id" :label="w.name" :value="String(w.id)" />
              </el-select>
              <el-input v-else v-model="form.toId" placeholder="请输入去向标识" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="关联单号" prop="refCode">
          <el-input v-model="form.refCode" placeholder="请输入关联单号 (如 PO/SO/SHIP 单号)" />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>

        <!-- 明细列表 -->
        <div class="mt-20px border-t pt-20px">
          <div class="flex justify-between items-center mb-10px">
            <span class="text-16px font-bold">账单明细</span>
            <el-button type="primary" size="small" @click="openItemSelector">
              <Icon icon="ep:plus" class="mr-4px" /> 添加物料
            </el-button>
          </div>

          <el-table :data="form.items" border stripe>
            <el-table-column label="类型" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.itemType === ErpItemTypeEnum.SKU ? 'success' : 'warning'" size="small">
                  {{ row.itemType === ErpItemTypeEnum.SKU ? '商品' : '耗材' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="物料信息" min-width="200">
              <template #default="{ row }">
                <div class="flex items-center gap-2">
                  <el-image :src="row.picUrl" class="w-32px h-32px rounded" />
                  <div class="flex flex-col">
                    <span class="text-xs font-bold">{{ row.sellerSku }}</span>
                    <span class="text-xs text-gray-400 truncate w-150px">{{ row.name }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="可用库存" width="100" align="center" v-if="form.type !== 10">
              <template #default="{ row }">
                <span class="text-orange-600 font-bold">{{ row.availableStock || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="变动数量" width="150" align="center">
              <template #default="{ row }">
                <el-input-number 
                  v-model="row.qty" 
                  :min="1" 
                  :max="form.type !== 10 ? row.availableStock : undefined"
                  size="small" 
                  class="!w-100px" 
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="scope">
                <el-button type="danger" link @click="removeItem(scope.$index)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 p-20px border-t">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">提 交</el-button>
      </div>
    </template>

    <!-- 物料选择器弹窗 -->
    <StockItemSelector ref="itemSelectorRef" @change="handleItemSelect" />
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { WarehouseApi } from '@/app/erplus/api/stock/warehouse'
import { InventoryBillApi, InventoryBillSaveReqVO } from '@/app/erplus/api/stock/inventoryBill'
import StockItemSelector from './StockItemSelector.vue'
import { Icon } from '@/components/Icon'
import { ErpInventoryBillRefTypeEnum, ErpInventoryBillTypeOptions, ErpItemTypeEnum } from './constants'

defineOptions({ name: 'InventoryBillForm' })

const message = useMessage()
const visible = ref(false)
const submitting = ref(false)
const warehouseList = ref<any[]>([])
const itemSelectorRef = ref()
const formRef = ref()

// 状态控制
const fixedWarehouse = ref(false) // 是否固定仓库（从库存列表打开时）
const isPlatformWarehouse = ref(false) // 是否是平台仓

const title = computed(() => {
  const map: Record<number, string> = { 10: '新建入库单', 20: '新建出库单', 30: '新建调拨单' }
  return map[form.type] || '库存操作'
})

const form = reactive({
  type: 10,
  refType: ErpInventoryBillRefTypeEnum.MANUAL,
  fromId: '',
  toId: '',
  refCode: '',
  remark: '',
  items: [] as any[]
})

const rules = {
  type: [{ required: true, message: '请选择单据类型', trigger: 'change' }],
  refType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  fromId: [{ required: true, message: '请输入/选择来源', trigger: 'blur' }],
  toId: [{ required: true, message: '请输入/选择目标', trigger: 'blur' }],
  items: [{ type: 'array', required: true, message: '请至少添加一个物料', trigger: 'change' }]
}

const sourceLabel = computed(() => {
  if (form.type === 10) return '来源标识'
  return '发货仓库'
})

const targetLabel = computed(() => {
  if (form.type === 20) return '去向标识'
  return '收货仓库'
})

/** 获取当前单据类型可选的业务类型列表 */
const currentRefTypeOptions = computed(() => {
  return ErpInventoryBillTypeOptions[form.type as keyof typeof ErpInventoryBillTypeOptions] || []
})

/** 获取发货仓库列表（如果是调拨，排除已选的目标仓库） */
const shippingWarehouseList = computed(() => {
  if (form.type === 30 && form.toId) {
    return warehouseList.value.filter(w => String(w.id) !== String(form.toId))
  }
  return warehouseList.value
})

/** 获取收货仓库列表（如果是调拨，排除已选的来源仓库） */
const receivingWarehouseList = computed(() => {
  if (form.type === 30 && form.fromId) {
    return warehouseList.value.filter(w => String(w.id) !== String(form.fromId))
  }
  return warehouseList.value
})

/** 打开表单 */
const open = async (type: number, options?: { warehouseId?: number, isPlatform?: boolean }) => {
  resetForm()
  form.type = type
  visible.value = true
  
  if (options?.warehouseId) {
    const whId = String(options.warehouseId)
    if (type === 10) form.toId = whId
    else if (type === 20) form.fromId = whId
    else if (type === 30) form.fromId = whId // 调拨单默认从当前仓库调出
    fixedWarehouse.value = true
  }

  isPlatformWarehouse.value = options?.isPlatform || false
  if (isPlatformWarehouse.value) {
    form.refType = ErpInventoryBillRefTypeEnum.SHIPMENT
  }

  // 加载仓库列表
  const res = await WarehouseApi.getWarehouseList()
  // 确保 ID 是 String 类型，否则 el-select 的回显会匹配失败
  warehouseList.value = res.map(w => ({
    ...w,
    id: String(w.id)
  }))
}

const resetForm = () => {
  form.type = 10
  form.refType = ErpInventoryBillRefTypeEnum.MANUAL
  form.fromId = ''
  form.toId = ''
  form.refCode = ''
  form.remark = ''
  form.items = []
  fixedWarehouse.value = false
  isPlatformWarehouse.value = false
}

const handleClose = () => {
  resetForm()
}

const openItemSelector = () => {
  // 出库 (20) 和 调拨 (30) 时，必须限制在发货仓库有的物料
  const warehouseId = (form.type === 20 || form.type === 30) ? form.fromId : undefined
  
  if ((form.type === 20 || form.type === 30) && !warehouseId) {
    message.warning('请先选择发货仓库')
    return
  }
  
  itemSelectorRef.value.open({ warehouseId })
}

const handleItemSelect = (selectedItems: any[]) => {
  // 合并已存在的，避免重复
  selectedItems.forEach(newItem => {
    const exists = form.items.find(i => i.itemType === newItem.itemType && i.itemId === newItem.itemId)
    if (!exists) {
      form.items.push({
        ...newItem,
        qty: 1
      })
    }
  })
}

const removeItem = (index: number) => {
  form.items.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  if (form.items.length === 0) {
    message.error('请添加至少一个物料')
    return
  }

  submitting.value = true
  try {
    const data: InventoryBillSaveReqVO = {
      type: form.type,
      fromId: form.fromId,
      toId: form.toId,
      refType: form.refType,
      refCode: form.refCode,
      remark: form.remark,
      items: form.items.map(i => ({
        itemType: i.itemType,
        itemId: i.itemId,
        sellerSku: i.sellerSku,
        qty: i.qty
      }))
    }
    
    await InventoryBillApi.createInventoryBill(data)
    message.success('单据提交成功')
    visible.value = false
    emit('success')
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const emit = defineEmits(['success'])

defineExpose({ open })
</script>
