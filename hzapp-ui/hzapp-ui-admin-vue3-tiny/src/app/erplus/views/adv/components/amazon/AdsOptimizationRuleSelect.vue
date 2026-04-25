<template>
  <div class="optimization-rule-select">
    <el-select
      v-model="modelValue"
      :placeholder="placeholder"
      clearable
      filterable
      :teleported="false"
      class="optimization-select"
      @change="handleChange"
    >
      <el-option
        v-for="item in ruleList"
        :key="item.ruleId"
        :label="item.name"
        :value="item.ruleId"
      />
      <template #footer>
        <div class="create-rule-footer">
          <el-button type="primary" link @click="openCreateDialog">
            <el-icon class="mr-1"><Plus /></el-icon>
            新建优化规则
          </el-button>
        </div>
      </template>
    </el-select>

    <!-- 新建规则弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="新建优化规则"
      width="600px"
      append-to-body
      destroy-on-close
    >
      <AdsOptimizationRuleForm
        ref="formRef"
        :shop-id="shopId"
        :account-id="accountId"
        :profile-id="profileId"
        @success="handleCreateSuccess"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="loading" @click="submitForm">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { AdsOptimizationRuleApi } from '@/app/erplus/api/adv/optimization_rule'
import AdsOptimizationRuleForm from './AdsOptimizationRuleForm.vue'

defineOptions({ name: 'AdsOptimizationRuleSelect' })

const props = defineProps({
  placeholder: {
    type: String,
    default: '请选择优化规则'
  },
  shopId: {
    type: Number,
    required: true
  },
  accountId: {
    type: Number,
    required: true
  },
  profileId: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['change', 'update:modelValue'])

const modelValue = defineModel<string>()
const ruleList = ref<any[]>([])
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref()

/** 获取规则列表 */
const getRuleList = async () => {
  try {
    const data = await AdsOptimizationRuleApi.getOptimizationRuleList({ shopId: props.shopId })
    ruleList.value = data || []
  } catch (error) {
    console.error('获取优化规则列表失败', error)
  }
}

const handleChange = (value: string) => {
  const rule = ruleList.value.find(item => item.ruleId === value)
  emit('change', rule)
}

const openCreateDialog = () => {
  dialogVisible.value = true
}

const handleCreateSuccess = async (newRuleId: string) => {
  dialogVisible.value = false
  await getRuleList()
  
  if (newRuleId) {
    modelValue.value = newRuleId
    handleChange(newRuleId)
  }
}

const submitForm = async () => {
  if (formRef.value) {
    loading.value = true
    try {
      await formRef.value.submit()
    } finally {
      loading.value = false
    }
  }
}

onMounted(() => {
  getRuleList()
})
</script>

<style scoped>
.optimization-rule-select {
  display: inline-block;
  vertical-align: middle;
}

.optimization-select {
  width: 100%;
}

.create-rule-footer {
  padding: 8px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
  background-color: #fff;
  position: sticky;
  bottom: 0;
  z-index: 10;
}
</style>
