<template>
  <Dialog title="选择租户" v-model="dialogVisible" width="600" height="600">

    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="fromRules"
      label-width="100px"
    >
      <el-row>
        <el-col :span="12">


          <el-form-item label="选择租户" prop="tenant">

            <el-select v-model="formData.tenant" placeholder="请选择租户">
              <el-option v-for="(item, index) in tenants" :key="`tenant-${index}`" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>

      </el-row>
    </el-form>


    <template #footer>
      <el-button @click="submitSelection" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import {TenantVO} from "@/api/system/tenant";
import {setTenantId} from "@/utils/auth";
import {formatTime} from "@/utils";

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用

const loading = ref(true) // 页面加载中

const tenants = ref([] as TenantVO[]) // 租户列表

const formData = ref({
  tenant: undefined,
})

const fromRules = {
  tenant: [required],
}

const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (t: TenantVO[]) => {
  dialogVisible.value = true
  tenants.value = t
}

defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitSelection = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  setTenantId(formData.value.tenant)

  message.success(t('common.updateSuccess'))
  dialogVisible.value = false
  // 发送操作成功的事件
  emit('success')

}

onMounted(() => {
  loading.value = false;
})


</script>
