<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" v-loading="formLoading">

      <el-form-item label="是否自授权" prop="selfAuth">
        <el-radio-group v-model="formData.selfAuth" placeholder="是否自授权">
          <el-radio value="true">是</el-radio>
          <el-radio value="false">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <template v-if="formData.selfAuth === 'true'">
        <el-form-item label="应用Key" prop="appKey">
          <el-input v-model="formData.appKey" placeholder="请输入Key" />
        </el-form-item>
        <el-form-item label="应用密钥" prop="appSecret">
          <el-input v-model="formData.appSecret" placeholder="请输入Secret" />
        </el-form-item>
        <el-form-item label="刷新令牌" prop="refreshToken">
          <el-input v-model="formData.refreshToken" placeholder="刷新令牌" />
        </el-form-item>

        <div class="flex justify-end w-full!">
          <el-button type="primary" @click="submitShopAuth">授 权</el-button>
        </div>





      </template>

      <template v-else-if="formData.selfAuth === 'false'">
        <div class="flex justify-end w-full!">
          <el-button type="primary" @click="dialogVisible = false">去 授 权</el-button>
        </div>
      </template>




    </el-form>

  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { ShopApi, ShopAuthReqVO, ShopVO } from '@/app/erplus/api/system/shop'
import { SellPlatformApi, SellPlatformVO } from "@/app/erp/api/sellplatform";
import { SellZoneApi, SellZoneVO } from "@/app/erp/api/sellzone";

/** 店铺信息 表单 */
defineOptions({ name: 'ShopForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用

const formData = ref({
  id: undefined,
  appKey: '',
  appSecret: '',
  refreshToken: '',
  selfAuth: undefined,
})
const formRules = reactive({
  appKey: [{ required: true, message: '应用Key不能为空', trigger: 'blur' }],
  appSecret: [{ required: true, message: '密钥不能为空', trigger: 'blur' }],
  refreshToken: [{ required: true, message: '令牌不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (id: number) => {
  resetForm()
  // 根据shop绑定的平台, 判断授权方式, 某些平台只支持自授权, 有些支持店铺授权, 根据平台配置决定。
  dialogVisible.value = true
  dialogTitle.value = '授权'
  formData.value.id = id

}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitShopAuth = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as ShopAuthReqVO

    await ShopApi.submitShopAuth(data)
    message.success('授权成功')

    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
    close()
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    selfAuth: undefined,

  }
  formRef.value?.resetFields()
}


onMounted(async () => {

})

</script>
