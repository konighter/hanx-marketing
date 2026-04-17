<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="耗材名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入耗材名称" />
      </el-form-item>
      <el-form-item label="耗材编码" prop="code">
        <el-input v-model="formData.code" placeholder="请输入内部编码" />
      </el-form-item>
      <el-form-item label="条码" prop="barcode">
        <el-input v-model="formData.barcode" placeholder="请输入条码" />
      </el-form-item>
      <el-form-item label="耗材类型" prop="category">
        <el-select v-model="formData.category" placeholder="请选择类型" class="w-full">
          <el-option label="包装材料" value="packaging" />
          <el-option label="填充材料" value="filling" />
          <el-option label="标签贴纸" value="label" />
          <el-option label="配件/零件" value="accessory" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item label="长宽高(cm)">
        <div class="flex gap-2">
          <el-input-number v-model="formData.length" :precision="2" :min="0" controls-position="right" placeholder="长" style="width: 100%" />
          <el-input-number v-model="formData.width" :precision="2" :min="0" controls-position="right" placeholder="宽" style="width: 100%" />
          <el-input-number v-model="formData.height" :precision="2" :min="0" controls-position="right" placeholder="高" style="width: 100%" />
        </div>
      </el-form-item>
      <el-form-item label="重量" prop="weight">
        <el-input v-model="formData.weight" type="number" placeholder="请输入重量">
          <template #append>g</template>
        </el-input>
      </el-form-item>
      <el-form-item label="体积" prop="volume">
        <el-input v-model="formData.volume" type="number" placeholder="请输入体积">
          <template #append>cm³</template>
        </el-input>
      </el-form-item>
      <el-form-item label="单位" prop="unit">
        <el-select v-model="formData.unit" placeholder="请选择单位" filterable clearable class="w-full">
          <el-option
            v-for="item in unitList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="0">开启</el-radio>
          <el-radio :label="1">关闭</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注内容" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { MaterialApi, MaterialVO } from '@/app/erplus/api/material'
import { ProductUnitApi } from '@/app/erplus/api/product/unit'
import { ElMessage } from 'element-plus'

const formData = reactive<MaterialVO>({
  id: undefined,
  name: '',
  code: '',
  barcode: '',
  category: '',
  unit: undefined,
  length: undefined,
  width: undefined,
  height: undefined,
  weight: undefined,
  volume: undefined,
  status: 0,
  remark: ''
})

const formRules = reactive({
  name: [{ required: true, message: '耗材名称不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '编码不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})

/** 监听名称变化，自动生成编码 */
const lastGeneratedCode = ref('')
watch(
  () => formData.name,
  async (newName) => {
    // 仅在新增模式下，且编码为空或是之前自动生成的编码时，触发自动生成
    if (newName && !formData.id && (!formData.code || formData.code === lastGeneratedCode.value)) {
      try {
        const code = await MaterialApi.generateMaterialCode(newName)
        formData.code = code
        lastGeneratedCode.value = code
      } catch (e) {
        console.error('自动生成编码失败', e)
      }
    }
  }
)


const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formRef = ref()
const unitList = ref<any[]>([])

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增耗材' : '编辑耗材'
  resetForm()
  await getUnitList()
  if (id) {
    formLoading.value = true
    try {
      const data = await MaterialApi.getMaterial(id)
      Object.assign(formData, data)
      // 确保单位是数字类型，防止回显失败
      if (formData.unit) {
        formData.unit = Number(formData.unit)
      }
    } finally {
      formLoading.value = false
    }
  }
}

const getUnitList = async () => {
  unitList.value = await ProductUnitApi.getProductUnitSimpleList()
}
defineExpose({ open })

/** 重置表单 */
const resetForm = () => {
  Object.assign(formData, {
    id: undefined,
    name: '',
    code: '',
    barcode: '',
    category: '',
    unit: undefined,
    length: undefined,
    width: undefined,
    height: undefined,
    weight: undefined,
    volume: undefined,
    status: 0,
    remark: ''
  })
}

/** 提交表单 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return

  formLoading.value = true
  try {
    if (formData.id) {
      await MaterialApi.updateMaterial(formData)
      ElMessage.success('修改成功')
    } else {
      await MaterialApi.createMaterial(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
