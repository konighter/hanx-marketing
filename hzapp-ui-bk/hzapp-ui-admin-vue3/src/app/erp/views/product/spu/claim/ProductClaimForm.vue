<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="50%">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="商品ID" prop="spuId">
        <el-input v-model="formData.spuId" placeholder="请输入商品ID" />
      </el-form-item>
      <el-form-item label="sku信息" prop="skuInfo">
        <el-input v-model="formData.skuInfo" placeholder="请输入sku信息" />
      </el-form-item>
      <el-form-item label="变种类型" prop="specType">
        <el-select v-model="formData.specType" placeholder="请选择变种类型">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="平台类型" prop="platform">
        <el-select v-model="formData.platform" placeholder="请选择平台类型">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="语言" prop="language">
        <el-select v-model="formData.language" placeholder="请选择语言">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="站点" prop="siteId">
        <el-select v-model="formData.siteId" placeholder="请选择站点">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="品类" prop="category">
        <el-select v-model="formData.category" placeholder="请选择品类">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="品牌" prop="brandId">
        <el-select v-model="formData.brandId" placeholder="请选择品牌">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="售价，多变种填最大值" prop="sellPrice">
        <el-input v-model="formData.sellPrice" placeholder="请输入售价，多变种填最大值" />
      </el-form-item>
      <el-form-item label="币种" prop="currency">
        <el-select v-model="formData.currency" placeholder="请选择币种">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="扩展信息" prop="extra">
        <el-input v-model="formData.extra" placeholder="请输入扩展信息" />
      </el-form-item>
      <el-form-item label="状态， 0-认领， 1-已同步， 9-同步失败" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio label="1">请选择字典生成</el-radio>
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
import { ProductClaimApi, ProductClaimVO } from '@/app/erp/api/claim'

/** 商品认领 表单 */
defineOptions({ name: 'ProductClaimForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  spuId: undefined,
  skuInfo: undefined,
  specType: undefined,
  platform: undefined,
  language: undefined,
  siteId: undefined,
  category: undefined,
  brandId: undefined,
  sellPrice: undefined,
  currency: undefined,
  extra: undefined,
  status: undefined
})
const formRules = reactive({
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
      formData.value = await ProductClaimApi.getProductClaim(id)
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
    const data = formData.value as unknown as ProductClaimVO
    if (formType.value === 'create') {
      await ProductClaimApi.createProductClaim(data)
      message.success(t('common.createSuccess'))
    } else {
      await ProductClaimApi.updateProductClaim(data)
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
    spuId: undefined,
    skuInfo: undefined,
    specType: undefined,
    platform: undefined,
    language: undefined,
    siteId: undefined,
    category: undefined,
    brandId: undefined,
    sellPrice: undefined,
    currency: undefined,
    extra: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}
</script>
