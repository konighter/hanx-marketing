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
      <el-form-item label="产品名称" prop="productName">
        <el-input v-model="formData.productName" placeholder="请输入产品名称" />
      </el-form-item>
      <el-form-item label="标签" prop="tags">
        <el-input v-model="formData.tags" placeholder="请输入标签" />
      </el-form-item>
      <el-form-item label="素材类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择素材类型">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="素材链接" prop="assetLink">
        <el-input v-model="formData.assetLink" placeholder="请输入素材链接" />
      </el-form-item>
      <el-form-item label="素材信息" prop="assetInfo">
        <el-input v-model="formData.assetInfo" placeholder="请输入素材信息" />
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
import { ProductAssetsApi, ProductAssets } from '@/app/erplus/api/product/productAsset'

/** 商品素材 表单 */
defineOptions({ name: 'ProductAssetsForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  productId: undefined,
  productName: undefined,
  tags: undefined,
  type: undefined,
  assetLink: undefined,
  assetInfo: undefined,
  status: undefined
})
const formRules = reactive({
  productId: [{ required: true, message: '产品ID不能为空', trigger: 'blur' }],
  productName: [{ required: true, message: '产品名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '素材类型不能为空', trigger: 'change' }],
  assetLink: [{ required: true, message: '素材链接不能为空', trigger: 'blur' }],
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
      formData.value = await ProductAssetsApi.getProductAssets(id)
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
    const data = formData.value as unknown as ProductAssets
    if (formType.value === 'create') {
      await ProductAssetsApi.createProductAssets(data)
      message.success(t('common.createSuccess'))
    } else {
      await ProductAssetsApi.updateProductAssets(data)
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
    productName: undefined,
    tags: undefined,
    type: undefined,
    assetLink: undefined,
    assetInfo: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}
</script>