<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" v-loading="formLoading">
      <el-form-item label="标题" prop="title">
        <el-input v-model="formData.title" placeholder="请输入标题" disabled="true" />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择状态">
          <el-option
            v-for="dict in getIntDictOptions('product_potential_status')"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />

        </el-select>
      </el-form-item>
      <el-form-item label="评审意见" prop="analysisOpinion" v-if="formType == 'audit'">
        <el-input type="textarea" aria-rowcount="4" v-model="formData.analysisOpinion" placeholder="请输入评审意见" />
      </el-form-item>
      <el-form-item label="复盘意见" prop="debriefOpinion" v-if="formType == 'debrief'">
        <el-input type="textarea" aria-rowcount="4" v-model="formData.debriefOpinion" placeholder="请输入复盘意见" />
      </el-form-item>
      <el-form-item label="备注" prop="marks">
        <el-input type="textarea" aria-rowcount="4" v-model="formData.marks" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { ProductPotentialApi, ProductPotentialVO } from '@/app/erplus/api/product/productBox'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** 选品提案 表单 */
defineOptions({ name: 'ProductPotentialForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改; debrief-复盘; audit-审核; submit-提交
const formData = ref({
  id: undefined,
  title: undefined,
  status: undefined as string | undefined,
  analysisOpinion: undefined, // 分析意见
  debriefOpinion: undefined, // 复盘意见
  marks: undefined // 备注
})
const formRules = reactive({
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  platformId: [{ required: true, message: '售卖平台不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  console.log('open form type:', type, 'id:', id)
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type) || '修改'
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await ProductPotentialApi.getProductPotential(id)
    } finally {
      formLoading.value = false
    }
    console.log('formData: ', formData.value)
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
    const data = formData.value as unknown as ProductPotentialVO
    await ProductPotentialApi.updateProductPotentialSimple(data)
    message.success(t('common.submitSuccess') || '提交成功')

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
    title: undefined,
    status: undefined as string | undefined,
    analysisOpinion: undefined, // 分析意见
    debriefOpinion: undefined, // 复盘意见
    marks: undefined // 备注
  }
  formRef.value?.resetFields()
}
</script>