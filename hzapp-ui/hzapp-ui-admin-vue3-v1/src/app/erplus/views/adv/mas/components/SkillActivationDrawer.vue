<template>
  <el-drawer
    v-model="drawerVisible"
    :title="'激活策略: ' + (skill?.name || '')"
    size="40%"
    :destroy-on-close="true"
  >
    <div class="activation-drawer" v-if="skill">
      <el-alert
        title="AI 策略授权声明"
        type="warning"
        description="系统将在设定的周期和安全边界内自动对该商品执行广告操作（包括调价、拓词、新建计划）。请务必核对底线参数。"
        show-icon
        style="margin-bottom: 20px"
      />

      <el-form label-width="140px" label-position="left">
        <!-- 业务基础绑定 -->
        <el-form-item label="目标商品 (Product)">
          <el-select v-model="formData.targetBizId" placeholder="请选择要授权管理的商品" style="width: 100%">
            <!-- MOCK DATA -->
            <el-option label="ASIN: B0C12345 (Wireless Earbuds)" value="B0C12345" />
            <el-option label="ASIN: B0D98765 (Smart Watch)" value="B0D98765" />
          </el-select>
        </el-form-item>
        
        <el-divider>业务边界配置 (Guardrails)</el-divider>

        <!-- 动态解析 schema 渲染 (Mock 简易版) -->
        <template v-for="(fieldInfo, fieldKey) in parsedSchema" :key="fieldKey">
          <el-form-item :label="fieldInfo.label" :required="fieldInfo.required">
            <el-input-number 
              v-if="fieldInfo.type === 'number'" 
              v-model="formData.configParams[fieldKey]" 
              :min="0"
              style="width: 100%"
            />
            <el-input 
              v-else 
              v-model="formData.configParams[fieldKey]" 
            />
          </el-form-item>
        </template>
        
        <el-form-item label="单次竞价上限 ($)">
          <el-input-number v-model="formData.configParams.maxAllowedBid" :min="0" :step="0.1" style="width: 100%" />
        </el-form-item>

        <el-form-item label="人工卡点审核">
          <el-switch
            v-model="formData.configParams.humanInTheLoop"
            active-text="开启 (重要调价前需我同意)"
            inactive-text="关闭 (全自动运行)"
          />
        </el-form-item>
      </el-form>

      <div class="drawer-footer">
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="submitActivation">确认授权并激活</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { activateSkill } from '@/app/erplus/api/adv/mas/skill'

const props = defineProps<{
  modelValue: boolean;
  skill: any;
}>()

const emit = defineEmits(['update:modelValue', 'activated'])

const drawerVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const formData = ref({
  targetBizId: '',
  configParams: {} as Record<string, any>
})

const parsedSchema = computed(() => {
  if (!props.skill || !props.skill.paramSchema) return {}
  try {
    return JSON.parse(props.skill.paramSchema)
  } catch (e) {
    return {}
  }
})

// 初始化表单
watch(() => props.skill, (val) => {
  formData.value.targetBizId = ''
  formData.value.configParams = { maxAllowedBid: 2.5, humanInTheLoop: false }
  
  if (val && val.paramSchema) {
    try {
      const schemaObj = JSON.parse(val.paramSchema)
      for (const k in schemaObj) {
        formData.value.configParams[k] = undefined
      }
    } catch(e) {}
  }
})

async function submitActivation() {
  if (!formData.value.targetBizId) {
    ElMessage.error("请选择目标商品")
    return
  }
  
  try {
    const payload = {
      skillCode: props.skill.skillCode,
      targetBizId: formData.value.targetBizId,
      configParams: JSON.stringify(formData.value.configParams)
    }
    await activateSkill(payload)
    
    ElMessage.success(`策略 ${props.skill.name} 已成功托管至 ${formData.value.targetBizId} !`)
    emit('activated')
    drawerVisible.value = false
  } catch (error) {
    console.error("Failed to activate skill:", error)
    ElMessage.error("激活失败，请查看控制台响应内容")
  }
}
</script>

<style scoped>
.activation-drawer {
  padding: 0 24px;
}
.drawer-footer {
  margin-top: 40px;
  text-align: right;
  border-top: 1px solid #e2e8f0;
  padding-top: 24px;
}
</style>
