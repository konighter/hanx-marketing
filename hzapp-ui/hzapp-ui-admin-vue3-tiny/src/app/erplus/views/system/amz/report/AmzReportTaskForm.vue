<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" v-loading="formLoading">
      <el-form-item label="店铺" prop="shopId">
        <el-select v-model="formData.shopId" placeholder="请选择店铺" class="w-100!">
          <el-option v-for="s in shops" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="报告类型" prop="reportType">
        <el-select v-model="formData.reportType" placeholder="请选择报告类型" class="w-100!">
          <el-option v-for="p in reportTypes" :key="p.value" :label="p.label" :value="p.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="时间类型" prop="period">
        <el-select v-model="formData.period" placeholder="请选择时间周期" class="w-100!" @change="handlePeried">
          <el-option v-for="p in periods" :key="p.value" :label="p.label" :value="p.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间" prop="status">

        <el-date-picker
v-model="formData.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="YYYY-MM-DD" class="w-100!" />
      </el-form-item>

      <el-form-item label="ASIN" prop="asins">
        <el-input type="" v-model="formData.asins" placeholder="请输入ASIN, 空格分割" />
      </el-form-item>

    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { AmzReportTaskApi, AmzReportTask, periods, reportTypes } from '@/app/erplus/api/system/report'
import { ShopApi } from '@/app/erplus/api/system/shop'

/** 亚马逊报告任务 表单 */
defineOptions({ name: 'AmzReportTaskForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  shopId: undefined,
  reportType: undefined,
  period: undefined,
  dateRange: [],
  asins: undefined,
})
const formRules = reactive({
})
const formRef = ref() // 表单 Ref

const shops = ref<any[]>([])


onMounted(async () => {
  // 加载店铺列表 
  shops.value = await ShopApi.getPlatformShop(3) || []

})



const handlePeried = async () => {




}









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
      formData.value = await AmzReportTaskApi.getAmzReportTask(id)
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
    const data = formData.value as unknown as AmzReportTask
    if (formType.value === 'create') {
      await AmzReportTaskApi.createAmzReportTask(data)
      message.success(t('common.createSuccess'))
    } else {
      // await AmzReportTaskApi.updateAmzReportTask(data)
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

    reportId: undefined,
    query: undefined,
    status: undefined,
    reportResult: undefined,
    lastCheckTime: undefined,
    isArchive: undefined
  }
  formRef.value?.resetFields()
}
</script>