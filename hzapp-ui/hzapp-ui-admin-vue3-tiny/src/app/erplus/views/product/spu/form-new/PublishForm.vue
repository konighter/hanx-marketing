<!-- 商品发布 - 发布设置 -->
<template>
  <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" :disabled="isDetail">
    <el-form-item label="目标店铺" prop="shopIds">
      <ShopCascaderSelect
        v-model="formData.shopIds"
        class="w-80"
        placeholder="请选择目标店铺"
      />
    </el-form-item>
    
    <el-form-item label="保存模式" prop="saveMode">
      <el-radio-group v-model="formData.saveMode">
        <el-radio label="DRAFT">草稿</el-radio>
        <el-radio label="SUBMIT">提交</el-radio>
      </el-radio-group>
    </el-form-item>
    
    <el-form-item label="配送模式" prop="fulfillType">
      <el-select v-model="formData.fulfillType" class="w-80" placeholder="请选择配送模式" clearable>
        <el-option label="FBA" value="FBA" />
        <el-option label="FBM" value="FBM" />
        <el-option label="自配送" value="SELF_DELIVERY" />
      </el-select>
    </el-form-item>
    
    <el-form-item label="延迟提交">
      <el-switch v-model="formData.delaySync" />
    </el-form-item>
    
    <el-form-item v-if="formData.delaySync" label="期望同步时间" prop="scheduleTime">
      <el-date-picker
        v-model="formData.scheduleTime"
        type="datetime"
        placeholder="选择日期时间"
        class="w-80"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
      />
    </el-form-item>
    

  </el-form>
</template>

<script lang="ts" setup>
import type { Spu } from '@/app/erplus/api/product/spu'
import { PropType } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { copyValueToTarget } from '@/utils'
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue'

defineOptions({ name: 'ProductPublishForm' })

const message = useMessage() // 消息弹窗

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => {}
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})

const formRef = ref() // 表单Ref

// 表单数据
const formData = computed(() => {
  return props.propFormData
})

// 表单规则
const rules = reactive({
  shopIds: [{ required: true, message: '请选择至少一个目标店铺', trigger: 'change' }],
  fulfillType: [{ required: true, message: '请选择配送模式', trigger: 'change' }]
})



/**
 * 初始化表单数据
 * 确保所有必要的发布设置字段都有默认值
 */
const initFormData = () => {
  // 检查是否已经初始化过，避免重复初始化
  if (!props.propFormData.crossPlatform) {
    // 使用Object.assign确保响应式
    Object.assign(props.propFormData, {
        // 基础发布信息
        shopIds: [],
      saveMode: 'DRAFT',
      
      // 物流设置
      fulfillType: '',
      packageDimensions: {
        length: undefined,
        width: undefined,
        height: undefined,
        unit: 'CM',
        weight: undefined,
        weightUnit: 'KG'
      },
      
      // 同步设置
      delaySync: false,
      scheduleTime: undefined
    })
  }
}

/** 监听props变化初始化数据 */
watch(
  () => props.propFormData,
  () => {
    initFormData()
  },
  {
    immediate: true
  }
)

/** 表单校验 */
const emit = defineEmits(['update:activeName'])
const validate = async () => {
  if (!formRef) return
  try {
    await unref(formRef)?.validate()
    // 校验通过，数据已经直接绑定到props.propFormData
  } catch (e) {
    message.error('【发布设置】不完善，请填写相关信息')
    emit('update:activeName', 'publish')
    throw e // 目的截断之后的校验
  }
}
defineExpose({ validate })
</script>

<style lang="scss" scoped>
.el-card {
  :deep(.el-card__header) {
    background-color: #f5f7fa;
    font-weight: 600;
  }
}
</style>