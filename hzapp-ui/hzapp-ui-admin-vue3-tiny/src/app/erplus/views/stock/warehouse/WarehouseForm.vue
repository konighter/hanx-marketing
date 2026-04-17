<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" v-loading="formLoading">
      <el-form-item label="仓库名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入仓库名称" />
      </el-form-item>
      <el-form-item label="仓库类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择仓库类型" class="w-full">
          <el-option v-for="item in WarehouseTypes" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="formData.type === 0" label="绑定店铺" prop="shopId">
        <ShopCascaderSelect v-model="formData.shopId" placeholder="请选择绑定的店铺" class="w-full" />
      </el-form-item>
      <el-form-item label="负责人" prop="principal">
        <el-input v-model="formData.principal" placeholder="请输入负责人" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="开启状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio-button :value="0">开启</el-radio-button>
              <el-radio-button :value="1">关闭</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设为默认" prop="defaultStatus">
            <el-switch v-model="formData.defaultStatus" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { WarehouseApi, Warehouse, WarehouseTypes } from '@/app/erplus/api/stock/warehouse'
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue'

/** ERP 仓库 表单 */
defineOptions({ name: 'WarehouseForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  type: undefined,
  shopId: undefined,
  remark: undefined,
  principal: undefined,
  status: 0,
  defaultStatus: false
})
const formRules = reactive({
  name: [{ required: true, message: '仓库名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '仓库类型不能为空', trigger: 'change' }],
  status: [{ required: true, message: '开启状态不能为空', trigger: 'blur' }],
  shopId: [{ required: true, message: '绑定店铺不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await WarehouseApi.getWarehouse(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as Warehouse
    if (formType.value === 'create') {
      await WarehouseApi.createWarehouse(data)
      message.success(t('common.createSuccess'))
    } else {
      await WarehouseApi.updateWarehouse(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    type: undefined,
    shopId: undefined,
    remark: undefined,
    principal: undefined,
    status: 0,
    defaultStatus: false
  }
  formRef.value?.resetFields()
}
</script>