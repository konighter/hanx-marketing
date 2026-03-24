<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="产品ID" prop="productId">
        <el-input v-model="formData.productId" placeholder="请输入产品ID" />
      </el-form-item>
      <el-form-item label="平台" prop="platformId">
        <el-select v-model="formData.platformId" placeholder="请选择平台">
          <el-option
v-for="SellPlatform in sellplatformList" :key="SellPlatform.id" :label="SellPlatform.name"
            :value="SellPlatform.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="产品链接" prop="productLink">
        <el-input v-model="formData.productLink" placeholder="请输入产品链接" />
      </el-form-item>
      <el-form-item label="品类ID" prop="categoryId">
        <el-input v-model="formData.categoryId" placeholder="请输入品类ID" />
      </el-form-item>
      <el-form-item label="周期" prop="crone">

        <el-input v-model="formData.crone"  placeholder="请选择周期" class="!w-240px" >

          <template #append>

             <el-select v-model="formData.croneType" placeholder="周期" class="!w-80px" >
              <el-option label="天" value="Day" />
              <el-option label="周" value="Week" />
              <el-option label="月" value="Month" />
            </el-select> 

          </template>

        </el-input>

      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { ProductMonitorApi, ProductMonitorVO } from '@/app/erplus/api/product/productMonitor'
import { SellPlatformApi, SellPlatformVO } from '@/app/erp/api/sellplatform'

/** 产品监控 表单 */
defineOptions({ name: 'ProductMonitorForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  productId: undefined,
  platformId: undefined,
  productLink: undefined,
  categoryId: undefined,
  crone: undefined,
  croneType: 'Day',
  status: undefined
})
const formRules = reactive({
  productLink: [{ required: true, message: '产品链接不能为空', trigger: 'blur' }],
  productId: [{ required: true, message: '产品ID不能为空', trigger: 'blur' }],
  platformId: [{ required: true, message: '平台不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
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
      formData.value = await ProductMonitorApi.getProductMonitor(id)
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
    const data = formData.value as unknown as ProductMonitorVO
    if (formType.value === 'create') {
      await ProductMonitorApi.createProductMonitor(data)
      message.success(t('common.createSuccess'))
    } else {
      await ProductMonitorApi.updateProductMonitor(data)
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
    productId: undefined,
    platformId: undefined,
    productLink: undefined,
    categoryId: undefined,
    crone: undefined,
    croneType: 'Day',
    status: undefined
  }
  formRef.value?.resetFields()
}

onMounted(() => {
  loadPlatformList()
})

const sellplatformList = ref<SellPlatformVO[]>([]) // 平台列表
const loadPlatformList = async () => {
  sellplatformList.value = await SellPlatformApi.getSellPlatformListCache() || []
}
</script>