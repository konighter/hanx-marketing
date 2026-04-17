<template>
  <el-dialog v-model="dialogVisible" title="新建装配单" width="600px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="目标产成品" prop="skuId">
        <el-input v-model="selectedSkuInfo" readonly placeholder="请选择要完成的目标产成品">
          <template #append>
            <el-button @click="spuSelectRef.open()">选择</el-button>
          </template>
        </el-input>
      </el-form-item>

      <SpuTableSelect ref="spuSelectRef" @change="handleSpuSelected" />
      <SkuTableSelect ref="skuSelectRef" :spuId="tempSpuId" @change="handleSkuSelected" />
      
      <el-form-item label="收货仓库" prop="warehouseId">
        <el-select v-model="formData.warehouseId" placeholder="请选择装配成品入库仓库" class="w-full">
          <el-option
            v-for="item in warehouseList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="计划装配量" prop="plannedQty">
        <el-input-number v-model="formData.plannedQty" :min="1" :precision="0" class="!w-full" />
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入装配备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { AssemblyApi, AssemblyOrderVO } from '@/app/erplus/api/stock/assembly'
import SkuTableSelect from '@/app/erplus/views/product/spu/components/SkuTableSelect.vue'
import SpuTableSelect from '@/app/erplus/views/product/spu/components/SpuTableSelect.vue'
import request from '@/config/axios'
import { ElMessage } from 'element-plus'

const dialogVisible = ref(false)
const formLoading = ref(false)
const formRef = ref()
const spuSelectRef = ref()
const skuSelectRef = ref()

const warehouseList = ref<any[]>([])
const selectedSkuInfo = ref('')
const tempSpuId = ref<number>()

const formData = reactive<AssemblyOrderVO>({
  skuId: 0,
  warehouseId: 0,
  plannedQty: 1,
  remark: ''
})

const formRules = reactive({
  skuId: [{ required: true, message: '请选择目标产成品', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择入库仓库', trigger: 'change' }],
  plannedQty: [{ required: true, message: '计划数量不能为空', trigger: 'blur' }]
})

/** 获取仓库列表 */
const getWarehouseList = async () => {
  // 假设这个接口存在
  const data = await request.get({ url: '/erplus/warehouse/simple-list' })
  warehouseList.value = data
}

/** 打开弹窗 */
const open = () => {
  dialogVisible.value = true
  resetForm()
  getWarehouseList()
}
defineExpose({ open })

/** 选择商品后的处理 */
const handleSpuSelected = (spu: any) => {
  tempSpuId.value = spu.id
  skuSelectRef.value.open()
}

const handleSkuSelected = (sku: any) => {
  formData.skuId = sku.id
  selectedSkuInfo.value = `${sku.name || ''} ${sku.properties?.map(p => p.valueName).join(' ') || ''}`
}

/** 重置表单 */
const resetForm = () => {
  Object.assign(formData, {
    skuId: undefined,
    warehouseId: undefined,
    plannedQty: 1,
    remark: ''
  })
  selectedSkuInfo.value = ''
  tempSpuId.value = undefined
}

/** 提交表单 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return

  formLoading.value = true
  try {
    await AssemblyApi.createAssemblyOrder(formData)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
