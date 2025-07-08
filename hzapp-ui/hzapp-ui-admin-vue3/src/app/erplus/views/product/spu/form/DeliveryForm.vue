<!-- 商品发布 - 物流设置 -->
<template>
  <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" :disabled="isDetail">
    <!-- 包裹信息 -->
    <el-divider content-position="left">包裹信息</el-divider>
    
    <template v-if="formData.packageDimensions">
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="尺寸">
            <div class="flex items-center gap-2">
              <el-input 
                v-model="formData.packageDimensions.length" 
                class="flex-1" 
                placeholder="长度"
              >
                
              </el-input>
              <span class="text-gray-400">×</span>
              <el-input 
                v-model="formData.packageDimensions.width" 
                class="flex-1" 
                placeholder="宽度"
              />
              <span class="text-gray-400">×</span>
              <el-input 
                v-model="formData.packageDimensions.height" 
                class="flex-1" 
                placeholder="高度"
              >
            
            <template #append>
                  <el-select 
                    v-model="formData.packageDimensions.unit" 
                    class="w-16"
                    style="width: 80px;"
                  >
                    <el-option label="厘米" value="CM" />
                    <el-option label="英寸" value="INCH" />
                    <el-option label="毫米" value="MM" />
                  </el-select>
                </template>
            </el-input>
            </div>
          </el-form-item>
        </el-col>

      </el-row>
      
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="重量" prop="packageDimensions.weight">
            <el-input 
              v-model="formData.packageDimensions.weight" 
              class="w-full" 
              placeholder="请输入重量"
            >
              <template #append>
                <el-select 
                  v-model="formData.packageDimensions.weightUnit" 
                  class="w-16"
                  style="width: 60px;"
                >
                  <el-option label="千克" value="KG" />
                  <el-option label="克" value="G" />
                  <el-option label="磅" value="LB" />
                  <el-option label="盎司" value="OZ" />
                </el-select>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </template>
    
  </el-form>
</template>
<script lang="ts" setup>
import { PropType } from 'vue'
import { copyValueToTarget } from '@/utils'
import { propTypes } from '@/utils/propTypes'
import type { Spu } from '@/app/erplus/api/product/spu'


defineOptions({ name: 'ProductDeliveryForm' })

const message = useMessage() // 消息弹窗

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => {}
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})
const formRef = ref() // 表单 Ref
const formData = reactive<Spu>({
  packageDimensions: {
    length: undefined,
    width: undefined,
    height: undefined,
    unit: 'CM',
    weight: undefined,
    weightUnit: 'KG'
  }
})
const rules = reactive({})

/** 将传进来的值赋值给 formData */
watch(
  () => props.propFormData,
  (data) => {
    if (!data) {
      return
    }
    copyValueToTarget(formData, data)
    // 确保 packageDimensions 存在
    if (!formData.packageDimensions) {
      formData.packageDimensions = {
        length: undefined,
        width: undefined,
        height: undefined,
        unit: 'CM',
        weight: undefined,
        weightUnit: 'KG'
      }
    }
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
    // 校验通过更新数据
    Object.assign(props.propFormData, formData)
  } catch (e) {
    message.error('【物流设置】不完善，请填写相关信息')
    emit('update:activeName', 'delivery')
    throw e // 目的截断之后的校验
  }
}
defineExpose({ validate })


</script>
