<template>
  <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
    <el-form-item label="规则名称" prop="ruleName">
      <el-input v-model="formData.ruleName" placeholder="例如: morning_bid_increase" />
    </el-form-item>

    <el-divider content-position="left">执行动作</el-divider>
    <el-form-item label="操作" prop="action.actionDetails.actionOperator">
      <el-radio-group v-model="formData.action.actionDetails.actionOperator">
        <el-radio value="INCREMENT">增加</el-radio>
        <el-radio value="DECREMENT">减少</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="单位" prop="action.actionDetails.actionUnit">
      <el-radio-group v-model="formData.action.actionDetails.actionUnit">
        <el-radio value="PERCENT">百分比 (%)</el-radio>
        <el-radio value="CURRENCY">固定金额</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="数值" prop="action.actionDetails.value">
      <el-input-number v-model="formData.action.actionDetails.value" :min="0" :precision="2" />
    </el-form-item>

    <el-divider content-position="left">执行时间</el-divider>
    <el-form-item label="执行类型" prop="recurrence.type">
      <el-select v-model="formData.recurrence.type" placeholder="请选择频率">
        <el-option label="每日" value="DAILY" />
        <el-option label="每周" value="WEEKLY" />
      </el-select>
    </el-form-item>

    <el-form-item v-if="formData.recurrence.type === 'WEEKLY'" label="选择日期" prop="recurrence.daysOfWeek">
      <el-checkbox-group v-model="formData.recurrence.daysOfWeek">
        <el-checkbox value="MONDAY">周一</el-checkbox>
        <el-checkbox value="TUESDAY">周二</el-checkbox>
        <el-checkbox value="WEDNESDAY">周三</el-checkbox>
        <el-checkbox value="THURSDAY">周四</el-checkbox>
        <el-checkbox value="FRIDAY">周五</el-checkbox>
        <el-checkbox value="SATURDAY">周六</el-checkbox>
        <el-checkbox value="SUNDAY">周日</el-checkbox>
      </el-checkbox-group>
    </el-form-item>

    <el-form-item label="时间段">
      <div v-for="(time, index) in formData.recurrence.timesOfDay" :key="index" class="flex items-center mb-2">
        <el-time-select
          v-model="time.startTime"
          start="00:00"
          step="00:30"
          end="23:30"
          placeholder="开始时间"
          class="mr-2"
        />
        <el-time-select
          v-model="time.endTime"
          start="00:00"
          step="00:30"
          end="23:30"
          placeholder="结束时间"
          class="mr-2"
        />
        <el-button v-if="formData.recurrence.timesOfDay.length > 1" type="danger" circle @click="removeTime(index)">
          <Icon icon="ep:minus" />
        </el-button>
      </div>
      <el-button type="primary" link @click="addTime">
        <Icon icon="ep:plus" class="mr-1" />
        添加时间段
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { AdsOptimizationRuleApi } from '@/app/erplus/api/adv/optimization_rule'

defineOptions({ name: 'AdsOptimizationRuleForm' })

const props = defineProps({
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

const emit = defineEmits(['success'])

const formRef = ref()
const formData = reactive({
  ruleName: '',
  ruleCategory: 'BID',
  ruleSubCategory: 'SCHEDULE',
  status: 'ENABLED',
  action: {
    actionType: 'ADOPT',
    actionDetails: {
      actionOperator: 'INCREMENT',
      actionUnit: 'PERCENT',
      value: 15
    }
  },
  recurrence: {
    type: 'DAILY',
    daysOfWeek: [] as string[],
    duration: {
      startTime: new Date().toISOString()
    },
    timesOfDay: [
      { startTime: '09:00', endTime: '18:00' }
    ]
  }
})

const formRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  'action.actionDetails.value': [{ required: true, message: '请输入数值', trigger: 'blur' }],
  'recurrence.daysOfWeek': [
    {
      validator: (rule: any, value: any, callback: any) => {
        if (formData.recurrence.type === 'WEEKLY' && (!value || value.length === 0)) {
          callback(new Error('请至少选择一天'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const addTime = () => {
  formData.recurrence.timesOfDay.push({ startTime: '00:00', endTime: '00:00' })
}

const removeTime = (index: number) => {
  formData.recurrence.timesOfDay.splice(index, 1)
}

const submit = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    console.log('accountId:', props.accountId)
    const submitData = {
      shopId: props.shopId,
      accountId: props.accountId,
      profileId: props.profileId,
      optimizationRule: formData
    }
    const ruleId = await AdsOptimizationRuleApi.createOptimizationRule(submitData)
    ElMessage.success('创建成功')
    emit('success', ruleId)
  } catch (error) {
    console.error('新建优化规则失败', error)
    ElMessage.error('创建失败')
  }
}

// 暴露 submit 方法给父组件
defineExpose({ submit })
</script>

<style scoped>
.flex {
  display: flex;
}
.items-center {
  align-items: center;
}
.mr-2 {
  margin-right: 0.5rem;
}
.mb-2 {
  margin-bottom: 0.5rem;
}
</style>
