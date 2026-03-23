<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" v-loading="formLoading">
      <el-form-item label="仓库名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入仓库名称" />
      </el-form-item>
      <el-form-item label="类型: 平台仓/海外仓/家庭仓/活动仓" prop="type">
        <el-select v-model="formData.type" placeholder="请选择类型: 平台仓/海外仓/家庭仓/活动仓">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-input v-model="formData.shopId" placeholder="请输入店铺" />
      </el-form-item>
      <el-form-item label="平台ID" prop="platformId">
        <el-input v-model="formData.platformId" placeholder="请输入平台ID" />
      </el-form-item>
      <el-form-item label="市场" prop="marketId">
        <el-input v-model="formData.marketId" placeholder="请输入市场" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" placeholder="请输入备注" />
      </el-form-item>
      <el-form-item label="负责人" prop="principal">
        <el-input v-model="formData.principal" placeholder="请输入负责人" />
      </el-form-item>
      <el-form-item label="开启状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否默认" prop="defaultStatus">
        <el-radio-group v-model="formData.defaultStatus">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { WarehouseApi, Warehouse } from '@/app/erplus/api/stock/warehouse'

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
  platformId: undefined,
  marketId: undefined,
  remark: undefined,
  principal: undefined,
  status: undefined,
  defaultStatus: undefined
})
const formRules = reactive({
  name: [{ required: true, message: '仓库名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '类型: 平台仓/海外仓/家庭仓/活动仓不能为空', trigger: 'change' }],
  status: [{ required: true, message: '开启状态不能为空', trigger: 'blur' }]
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
    platformId: undefined,
    marketId: undefined,
    remark: undefined,
    principal: undefined,
    status: undefined,
    defaultStatus: undefined
  }
  formRef.value?.resetFields()
}
</script>