<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="指标" prop="metrics">

         <el-checkbox-group v-model="formData.metrics" placeholder="请输入监控任务ID">  
            <el-checkbox  label="销量" value="sales" ></el-checkbox>
            <el-checkbox  label="访客数" value="visitors" ></el-checkbox>
            <el-checkbox  label="转化率" value="conversionRate" ></el-checkbox>
            <el-checkbox  label="库存" value="stock" ></el-checkbox>
            <el-checkbox  label="价格" value="price" ></el-checkbox>

         </el-checkbox-group>
        



      </el-form-item>
      
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import  * as ProductMetricsApi  from '@/app/erplus/api/product/productMetrics'

/** 产品监控指标 表单 */
defineOptions({ name: 'ProductMetricsForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  metrics: [] as string[]
})

const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (metrics: string[]) => {
  dialogVisible.value = true
  formData.value.metrics = metrics
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

    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success', formData.value.metrics)
  } finally {
    formLoading.value = false
  }
}

</script>