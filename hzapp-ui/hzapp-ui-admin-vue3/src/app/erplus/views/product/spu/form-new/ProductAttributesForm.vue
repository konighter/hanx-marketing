<!-- 商品发布 - 产品属性 -->
<template>
  <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" :disabled="isDetail">
    <!-- 属性加载状态 -->
    <div v-if="attributesLoading" class="text-center py-8">
      <el-icon class="is-loading"><Loading /></el-icon>
      <div class="mt-2 text-gray-500">正在加载属性...</div>
    </div>

    <!-- 无属性提示 -->
    <div v-else-if="!categoryAttributes.length" class="text-center py-8">
      <el-empty description="该分类暂无可配置属性" />
    </div>

    <!-- 属性表单 -->
    <div v-else>
      <!-- 必填属性 -->
      <div v-if="requiredAttributes.length">
        <el-divider content-position="left">
          <span class="text-red-500">* 必填属性</span>
        </el-divider>
        
        <div v-for="attribute in requiredAttributes" :key="attribute.id" class="mb-4">
          <el-form-item 
            :label="attribute.name" 
            :prop="`attributes.${attribute.id}`"
            :required="attribute.required"
          >
            <!-- 文本输入 -->
            <el-input 
              v-if="attribute.inputType === 'TEXT'"
              v-model="formData.attributes[attribute.id]"
              :placeholder="`请输入${attribute.name}`"
              :maxlength="attribute.maxLength"
              show-word-limit
            />
            
            <!-- 数字输入 -->
            <el-input-number 
              v-else-if="attribute.inputType === 'NUMBER'"
              v-model="formData.attributes[attribute.id]"
              :placeholder="`请输入${attribute.name}`"
              :min="attribute.minValue"
              :max="attribute.maxValue"
              :precision="attribute.precision || 0"
              class="w-full"
            />
            
            <!-- 单选 -->
            <el-select 
              v-else-if="attribute.inputType === 'SELECT'"
              v-model="formData.attributes[attribute.id]"
              :placeholder="`请选择${attribute.name}`"
              class="w-full"
            >
              <el-option 
                v-for="option in attribute.options" 
                :key="option.value" 
                :label="option.label" 
                :value="option.value" 
              />
            </el-select>
            
            <!-- 多选 -->
            <el-select 
              v-else-if="attribute.inputType === 'MULTI_SELECT'"
              v-model="formData.attributes[attribute.id]"
              :placeholder="`请选择${attribute.name}`"
              multiple
              class="w-full"
            >
              <el-option 
                v-for="option in attribute.options" 
                :key="option.value" 
                :label="option.label" 
                :value="option.value" 
              />
            </el-select>
            
            <!-- 日期选择 -->
            <el-date-picker 
              v-else-if="attribute.inputType === 'DATE'"
              v-model="formData.attributes[attribute.id]"
              type="date"
              :placeholder="`请选择${attribute.name}`"
              class="w-full"
            />
            
            <!-- 开关 -->
            <el-switch 
              v-else-if="attribute.inputType === 'BOOLEAN'"
              v-model="formData.attributes[attribute.id]"
            />
            
            <!-- 文本域 -->
            <el-input 
              v-else-if="attribute.inputType === 'TEXTAREA'"
              v-model="formData.attributes[attribute.id]"
              type="textarea"
              :placeholder="`请输入${attribute.name}`"
              :maxlength="attribute.maxLength"
              :autosize="{ minRows: 3, maxRows: 6 }"
              show-word-limit
            />
            
            <!-- 属性描述 -->
            <div v-if="attribute.description" class="text-xs text-gray-500 mt-1">
              {{ attribute.description }}
            </div>
          </el-form-item>
        </div>
      </div>

      <!-- 可选属性 -->
      <div v-if="optionalAttributes.length">
        <el-divider content-position="left">
          <el-button 
            type="text" 
            @click="showOptionalAttributes = !showOptionalAttributes"
            class="text-blue-500"
          >
            <el-icon class="mr-1">
              <ArrowDown v-if="!showOptionalAttributes" />
              <ArrowUp v-else />
            </el-icon>
            可选属性 ({{ optionalAttributes.length }})
          </el-button>
        </el-divider>
        
        <el-collapse-transition>
          <div v-show="showOptionalAttributes">
            <div v-for="attribute in optionalAttributes" :key="attribute.id" class="mb-4">
              <el-form-item 
                :label="attribute.name" 
                :prop="`attributes.${attribute.id}`"
              >
                <!-- 文本输入 -->
                <el-input 
                  v-if="attribute.inputType === 'TEXT'"
                  v-model="formData.attributes[attribute.id]"
                  :placeholder="`请输入${attribute.name}`"
                  :maxlength="attribute.maxLength"
                  show-word-limit
                />
                
                <!-- 数字输入 -->
                <el-input-number 
                  v-else-if="attribute.inputType === 'NUMBER'"
                  v-model="formData.attributes[attribute.id]"
                  :placeholder="`请输入${attribute.name}`"
                  :min="attribute.minValue"
                  :max="attribute.maxValue"
                  :precision="attribute.precision || 0"
                  class="w-full"
                />
                
                <!-- 单选 -->
                <el-select 
                  v-else-if="attribute.inputType === 'SELECT'"
                  v-model="formData.attributes[attribute.id]"
                  :placeholder="`请选择${attribute.name}`"
                  clearable
                  class="w-full"
                >
                  <el-option 
                    v-for="option in attribute.options" 
                    :key="option.value" 
                    :label="option.label" 
                    :value="option.value" 
                  />
                </el-select>
                
                <!-- 多选 -->
                <el-select 
                  v-else-if="attribute.inputType === 'MULTI_SELECT'"
                  v-model="formData.attributes[attribute.id]"
                  :placeholder="`请选择${attribute.name}`"
                  multiple
                  clearable
                  class="w-full"
                >
                  <el-option 
                    v-for="option in attribute.options" 
                    :key="option.value" 
                    :label="option.label" 
                    :value="option.value" 
                  />
                </el-select>
                
                <!-- 日期选择 -->
                <el-date-picker 
                  v-else-if="attribute.inputType === 'DATE'"
                  v-model="formData.attributes[attribute.id]"
                  type="date"
                  :placeholder="`请选择${attribute.name}`"
                  clearable
                  class="w-full"
                />
                
                <!-- 开关 -->
                <el-switch 
                  v-else-if="attribute.inputType === 'BOOLEAN'"
                  v-model="formData.attributes[attribute.id]"
                />
                
                <!-- 文本域 -->
                <el-input 
                  v-else-if="attribute.inputType === 'TEXTAREA'"
                  v-model="formData.attributes[attribute.id]"
                  type="textarea"
                  :placeholder="`请输入${attribute.name}`"
                  :maxlength="attribute.maxLength"
                  :autosize="{ minRows: 3, maxRows: 6 }"
                  show-word-limit
                />
                
                <!-- 属性描述 -->
                <div v-if="attribute.description" class="text-xs text-gray-500 mt-1">
                  {{ attribute.description }}
                </div>
              </el-form-item>
            </div>
          </div>
        </el-collapse-transition>
      </div>
    </div>
  </el-form>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { copyValueToTarget } from '@/utils'
import { propTypes } from '@/utils/propTypes'
import type { Spu } from '@/app/erplus/api/product/spu'
import { getCategoryAttributes } from '@/app/erplus/api/product/category'
import { ArrowDown, ArrowUp, Loading } from '@element-plus/icons-vue'

defineOptions({ name: 'ProductAttributesForm' })

const message = useMessage() // 消息弹窗

// 属性数据接口
interface AttributeOption {
  label: string
  value: string | number
}

interface CategoryAttribute {
  id: string
  name: string
  description?: string
  required: boolean
  inputType: 'TEXT' | 'NUMBER' | 'SELECT' | 'MULTI_SELECT' | 'DATE' | 'BOOLEAN' | 'TEXTAREA'
  options?: AttributeOption[]
  maxLength?: number
  minValue?: number
  maxValue?: number
  precision?: number
}

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => ({})
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})

const formRef = ref() // 表单Ref
const attributesLoading = ref(false) // 属性加载状态
const showOptionalAttributes = ref(false) // 是否显示可选属性
const categoryAttributes = ref<CategoryAttribute[]>([]) // 分类属性列表

// 表单数据
const formData = reactive<Spu>({
  attributes: {} as Record<string, any>
})

// 计算属性：必填属性
const requiredAttributes = computed(() => {
  return categoryAttributes.value.filter(attr => attr.required)
})

// 计算属性：可选属性
const optionalAttributes = computed(() => {
  return categoryAttributes.value.filter(attr => !attr.required)
})

// 表单验证规则
const rules = computed(() => {
  const formRules: Record<string, any> = {}
  
  requiredAttributes.value.forEach(attr => {
    formRules[`attributes.${attr.id}`] = [
      { required: true, message: `请填写${attr.name}`, trigger: 'blur' }
    ]
    
    // 添加特定类型的验证规则
    if (attr.inputType === 'TEXT' || attr.inputType === 'TEXTAREA') {
      if (attr.maxLength) {
        formRules[`attributes.${attr.id}`].push({
          max: attr.maxLength,
          message: `${attr.name}不能超过${attr.maxLength}个字符`,
          trigger: 'blur'
        })
      }
    } else if (attr.inputType === 'NUMBER') {
      formRules[`attributes.${attr.id}`].push({
        type: 'number',
        message: `${attr.name}必须是数字`,
        trigger: 'blur'
      })
      
      if (attr.minValue !== undefined) {
        formRules[`attributes.${attr.id}`].push({
          validator: (rule: any, value: any, callback: any) => {
            if (value !== undefined && value < attr.minValue!) {
              callback(new Error(`${attr.name}不能小于${attr.minValue}`))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        })
      }
      
      if (attr.maxValue !== undefined) {
        formRules[`attributes.${attr.id}`].push({
          validator: (rule: any, value: any, callback: any) => {
            if (value !== undefined && value > attr.maxValue!) {
              callback(new Error(`${attr.name}不能大于${attr.maxValue}`))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        })
      }
    }
  })
  
  return formRules
})

// 加载分类属性
const loadCategoryAttributes = async (categoryId: string) => {
  if (!categoryId) {
    categoryAttributes.value = []
    return
  }
  
  try {
    attributesLoading.value = true
    const response = await getCategoryAttributes(categoryId)
    categoryAttributes.value = response.data || []
    
    // 初始化属性值
    categoryAttributes.value.forEach(attr => {
      if (formData.attributes[attr.id] === undefined) {
        // 设置默认值
        if (attr.inputType === 'BOOLEAN') {
          formData.attributes[attr.id] = false
        } else if (attr.inputType === 'MULTI_SELECT') {
          formData.attributes[attr.id] = []
        } else {
          formData.attributes[attr.id] = ''
        }
      }
    })
  } catch (error) {
    console.error('加载分类属性失败:', error)
    message.error('加载分类属性失败，请重试')
    categoryAttributes.value = []
  } finally {
    attributesLoading.value = false
  }
}

// 监听分类变化
watch(
  () => props.propFormData?.categoryId,
  (newCategoryId) => {
    if (newCategoryId) {
      loadCategoryAttributes(newCategoryId.toString())
    } else {
      categoryAttributes.value = []
    }
  },
  { immediate: true }
)

// 监听表单数据变化
watch(
  () => props.propFormData,
  (data) => {
    if (!data) return
    copyValueToTarget(formData, data)
    
    // 确保 attributes 对象存在
    if (!formData.attributes) {
      formData.attributes = {}
    }
  },
  { immediate: true, deep: true }
)

// 表单校验
const emit = defineEmits(['update:activeName'])
const validate = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    // 校验通过，更新数据
    Object.assign(props.propFormData, formData)
  } catch (e) {
    message.error('【产品属性】不完善，请填写相关信息')
    emit('update:activeName', 'attributes')
    throw e // 目的截断之后的校验
  }
}

defineExpose({ validate })
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 18px;
}

.el-divider {
  margin: 24px 0 16px 0;
  
  .el-divider__text {
    font-weight: 600;
  }
}

.text-red-500 {
  color: #ef4444;
}

.text-blue-500 {
  color: #3b82f6;
}

.text-gray-500 {
  color: #6b7280;
}

.is-loading {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>