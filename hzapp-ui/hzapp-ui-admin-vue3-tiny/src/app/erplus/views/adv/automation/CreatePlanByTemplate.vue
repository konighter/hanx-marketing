<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="text-18px font-bold">基于模版创建自动化计划</div>
    </div>

    <el-steps :active="activeStep" finish-status="success" simple class="mb-30px">
      <el-step title="选择店铺与商品" />
      <el-step title="配置策略与参数" />
      <el-step title="完成创建" />
    </el-steps>

    <!-- 步骤 1: 选择店铺与商品 -->
    <div v-if="activeStep === 0" class="max-w-700px mx-auto py-20px">
      <el-form :model="form" label-width="100px" ref="step0FormRef" :rules="rules">
        <el-form-item label="目标店铺" prop="shopId">
          <el-cascader
            v-model="selectedShopPath"
            :options="shopCascaderList"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择执行此计划的店铺"
            class="!w-full"
            @change="handleShopChange"
          />
        </el-form-item>
        <el-form-item label="推广商品" prop="sku">
          <div v-if="selectedProduct" class="product-info-card !w-full">
            <el-image 
              :src="selectedProduct.mainImage?.url || selectedProduct.mainImage" 
              class="w-48px h-48px rounded flex-shrink-0" 
              fit="cover" 
            />
            <div class="flex-1 min-w-0 mx-12px">
              <div class="text-13px font-bold truncate" :title="selectedProduct.title">
                {{ selectedProduct.title }}
              </div>
              <div class="text-12px text-gray-400 mt-2px">
                SKU: {{ selectedProduct.sellerProductCode || selectedProduct.sellerSku }}
              </div>
            </div>
            <el-button link type="primary" @click="handleSelectProduct">重选</el-button>
          </div>
          <el-input v-else v-model="form.sku" placeholder="请选择或输入商品 SKU/ASIN" class="!w-full">
            <template #append>
              <el-button @click="handleSelectProduct">选择商品</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <div class="flex justify-center gap-20px mt-40px">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" :disabled="!form.shopId || !form.sku" @click="nextStep">
            下一步: 配置策略参数
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 步骤 2: 选择模版 + 配置参数 -->
    <div v-if="activeStep === 1" class="max-w-1000px mx-auto">
      <div class="section-title">1. 选择运营策略模版</div>
      <el-row :gutter="20" class="mb-30px">
        <el-col :span="8" v-for="item in templateList" :key="item.id">
          <el-card 
            shadow="hover" 
            class="template-card mb-20px" 
            :class="{ active: selectedTemplate?.id === item.id }"
            @click="handleSelectTemplate(item)"
          >
            <div class="flex justify-between items-start">
              <div class="font-bold text-15px mb-8px">{{ item.name }}</div>
              <el-tag size="small" type="warning">{{ item.type }}</el-tag>
            </div>
            <div class="text-12px text-gray-400 mb-10px h-34px overflow-hidden line-clamp-2">
              {{ item.remark || '自动化规则驱动，实现广告计划精准控制' }}
            </div>
            <div class="flex justify-end mt-5px" v-if="selectedTemplate?.id === item.id">
              <Icon icon="ep:circle-check-filled" class="text-20px text-blue-500" />
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-collapse-transition>
        <div v-if="selectedTemplate" class="config-section border-t pt-30px">
          <div class="section-title">2. 配置执行参数 ({{ selectedTemplate.name }})</div>
          
          <el-form :model="form" label-width="140px" ref="formRef" :rules="rules" class="max-w-700px">
            <el-form-item label="计划名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入自动化计划名称" class="!w-full" />
            </el-form-item>
            
            <div class="bg-gray-50 p-20px rounded-8px mb-20px">
              <div class="text-14px font-bold mb-15px flex items-center">
                <Icon icon="ep:setting" class="mr-5px" /> 策略参数微调
              </div>
              <div v-for="(val, key) in form.context" :key="key" class="mb-15px">
                <el-form-item :label="getLabel(key)">
                  <div class="flex items-center">
                    <el-input-number v-model="form.context[key]" :precision="2" :step="0.1" class="!w-180px" />
                    <span class="ml-15px text-gray-400 text-12px">{{ getDesc(key) }}</span>
                  </div>
                </el-form-item>
              </div>
            </div>
          </el-form>
        </div>
      </el-collapse-transition>

      <!-- 底部按钮区域 (始终可见) -->
      <div class="flex justify-center gap-20px mt-40px pb-40px">
        <el-button @click="handleCancel">取消</el-button>
        <el-button @click="prevStep">上一步</el-button>
        <el-button 
          v-if="selectedTemplate"
          type="primary" 
          :loading="submitting" 
          @click="handleConfirmCreate" 
          size="large" 
          class="px-40px"
        >
          确认并启动计划
        </el-button>
      </div>
    </div>

    <!-- 步骤 3: 成功 -->
    <div v-if="activeStep === 2" class="success-page flex flex-col items-center py-40px">
      <el-result icon="success" title="创建成功" sub-title="自动化计划已启动，广告结构已初始化">
        <template #extra>
          <el-button type="primary" @click="handleBack">返回计划列表</el-button>
          <el-button @click="handleReset">继续创建</el-button>
        </template>
      </el-result>
    </div>

    <!-- 商品选择弹窗 -->
    <ProductSelectDialog ref="productSelectDialogRef" @confirm="handleProductConfirm" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, unref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from '@/hooks/web/useMessage'
import { useI18n } from 'vue-i18n'
import { AutomationApi, AutomationTemplate } from '@/app/erplus/api/adv/automation'
import { ShopApi } from '@/app/erplus/api/system/shop'
import { useTagsViewStore } from '@/store/modules/tagsView'
import ProductSelectDialog from '@/app/erplus/compononts/ProductSelectDialog.vue'
import type { FormInstance } from 'element-plus'

const message = useMessage()
const { t } = useI18n()
const formRef = ref<FormInstance>()

const { push, currentRoute } = useRouter() // 路由跳转
const { delView } = useTagsViewStore() // 视图操作

const activeStep = ref(0)
const templateList = ref<AutomationTemplate[]>([])
const selectedTemplate = ref<AutomationTemplate | null>(null)
const shopCascaderList = ref<any[]>([])
const selectedShopPath = ref<any[]>([])
const selectedProduct = ref<any>(null)
const productSelectDialogRef = ref()
const submitting = ref(false)

const form = reactive({
  name: '',
  templateId: null as number | null,
  shopId: null as number | null,
  sku: '',
  platform: 'AMAZON',
  context: {} as Record<string, any>
})

const rules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  sku: [{ required: true, message: '请输入或选择商品', trigger: 'blur' }]
}

const getLabel = (key: string) => {
  if (selectedTemplate.value?.config?.params) {
    const param = selectedTemplate.value.config.params.find((p: any) => p.key === key)
    if (param?.label) return param.label
  }
  return key
}

const getDesc = (key: string) => {
  if (selectedTemplate.value?.config?.params) {
    const param = selectedTemplate.value.config.params.find((p: any) => p.key === key)
    if (param?.desc) return param.desc
  }
  return ''
}

const handleSelectTemplate = (item: AutomationTemplate) => {
  selectedTemplate.value = item
  
  // 1. 如果有明确的 params 定义，优先使用
  if (item.config?.params) {
    const strategy: Record<string, any> = {}
    item.config.params.forEach((p: any) => {
      strategy[p.key] = p.defaultValue
    })
    form.context = strategy
  } 
  // 2. 只有 defaultContext 的情况 (兼容旧模版)
  else if (item.config?.defaultContext) {
    form.context = { ...item.config.defaultContext }
  }
}

const handleShopChange = (value: any[]) => {
  if (value && value.length === 2) {
    if (form.shopId !== value[1]) {
      form.shopId = value[1]
      // 店铺变更时重置已选商品
      form.sku = ''
      selectedProduct.value = null
    }
  } else {
    form.shopId = undefined
    form.sku = ''
    selectedProduct.value = null
  }
}


const handleSelectProduct = () => {
  productSelectDialogRef.value.open(form.shopId)
}

const step0FormRef = ref()
const handleProductConfirm = (product: any) => {
  selectedProduct.value = product
  form.sku = product.sellerProductCode || product.sellerSku
  // 填充默认名称
  if (!form.name) {
    form.name = `${product.sellerProductCode || product.sellerSku} 自动化运营计划`
  }
  // 动触发校验，清除错误提示
  if (step0FormRef.value) {
    step0FormRef.value.validateField('sku')
  }
}

const nextStep = () => {
  activeStep.value++
}

const prevStep = () => {
  activeStep.value--
}

const handleCancel = () => {
  delView(unref(currentRoute))
  push({ name: 'automationIndex' })
}

const handleConfirmCreate = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  
  if (!selectedTemplate.value?.id) return
  
  submitting.value = true
  try {
    const res = await AutomationApi.createPlan({
      name: form.name,
      templateId: selectedTemplate.value.id,
      shopId: form.shopId,
      sku: form.sku,
      platform: form.platform,
      context: form.context,
      status: 'RUNNING'
    })
    
    // 创建成功后立即初始化结构
    if (res) {
      await AutomationApi.initStructure(res)
    }
    
    activeStep.value = 2
  } catch (error) {
    console.error('Failed to create plan:', error)
  } finally {
    submitting.value = false
  }
}

const handleBack = () => {
  push({ name: 'automationIndex' })
}

const handleReset = () => {
  activeStep.value = 0
  selectedTemplate.value = null
  selectedShopPath.value = []
  selectedProduct.value = null
  form.name = ''
  form.shopId = undefined
  form.sku = ''
}

onMounted(async () => {
  // 加载模版
  const res = await AutomationApi.getTemplateList({ pageNo: 1, pageSize: 20 })
  templateList.value = res.list || []
  
  // 加载店铺级联
  shopCascaderList.value = await ShopApi.getCascaderShopList()
})
</script>

<style scoped>
.template-card {
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.template-card.active {
  border-color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}

.max-w-1000px {
  max-width: 1000px;
}

.max-w-700px {
  max-width: 700px;
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 25px;
  position: relative;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 18px;
  background-color: var(--el-color-primary);
  margin-right: 10px;
  border-radius: 2px;
}

.product-info-card {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  background-color: var(--el-fill-color-blank);
  line-height: 1.4;
}

.product-info-card:hover {
  border-color: var(--el-color-primary-light-3);
}
</style>
