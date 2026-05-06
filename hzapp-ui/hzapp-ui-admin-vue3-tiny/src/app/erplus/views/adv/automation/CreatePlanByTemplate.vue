<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="text-18px font-bold">基于模版创建自动化计划</div>
    </div>

    <el-steps :active="activeStep" finish-status="success" simple class="mb-30px">
      <el-step title="选择自动化模版" />
      <el-step title="配置执行参数" />
      <el-step title="完成创建" />
    </el-steps>

    <!-- 步骤 1: 选择模版 -->
    <div v-if="activeStep === 0" class="template-selection">
      <el-row :gutter="20">
        <el-col :span="8" v-for="item in templateList" :key="item.id">
          <el-card 
            shadow="hover" 
            class="template-card mb-20px" 
            :class="{ active: selectedTemplate?.id === item.id }"
            @click="handleSelectTemplate(item)"
          >
            <div class="flex justify-between items-start">
              <div class="font-bold text-16px mb-10px">{{ item.name }}</div>
              <el-tag size="small">{{ item.type }}</el-tag>
            </div>
            <div class="text-12px text-gray-500 mb-15px h-40px overflow-hidden">
              {{ item.remark || '自动化规则驱动，实现广告计划精准控制' }}
            </div>
            <div class="flex justify-between items-center mt-10px">
              <span class="text-12px text-blue-500">查看规则明细</span>
              <el-button v-if="selectedTemplate?.id === item.id" type="primary" size="small" circle>
                <Icon icon="ep:check" />
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="flex justify-center gap-20px mt-20px">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :disabled="!selectedTemplate" @click="nextStep">
          下一步: 配置参数
        </el-button>
      </div>
    </div>

    <!-- 步骤 2: 配置参数 -->
    <div v-if="activeStep === 1" class="config-form max-w-800px mx-auto">
      <el-form :model="form" label-width="120px" ref="formRef" :rules="rules">
        <div class="section-title">基础配置</div>
        <div class="pl-20px mb-30px">
          <el-form-item label="计划名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入自动化计划名称" class="!w-400px" />
          </el-form-item>
          <el-form-item label="目标店铺" prop="shopId">
            <el-cascader
              v-model="selectedShopPath"
              :options="shopCascaderList"
              :props="{ label: 'name', value: 'id', children: 'children' }"
              placeholder="请选择执行此计划的店铺"
              class="!w-400px"
              @change="handleShopChange"
            />
          </el-form-item>
          <el-form-item label="推广商品" prop="sku">
            <div v-if="selectedProduct" class="product-info-card !w-400px">
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
            <el-input v-else v-model="form.sku" placeholder="请选择或输入商品 SKU/ASIN" class="!w-400px">
              <template #append>
                <el-button @click="handleSelectProduct">选择商品</el-button>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <div class="section-title">策略配置</div>
        <div class="pl-20px">
          <div v-for="(val, key) in form.context" :key="key" class="mb-15px">
            <el-form-item :label="getLabel(key)">
              <div class="flex items-center">
                <el-input-number v-model="form.context[key]" :precision="2" :step="0.1" class="!w-200px" />
                <span class="ml-15px text-gray-400 text-13px">{{ getDesc(key) }}</span>
              </div>
            </el-form-item>
          </div>
        </div>

        <div class="flex justify-center gap-20px mt-40px">
          <el-button @click="handleCancel">取消</el-button>
          <el-button @click="prevStep">上一步</el-button>
          <el-button type="primary" :loading="submitting" @click="handleConfirmCreate">
            确认并启动计划
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 步骤 3: 成功 -->
    <div v-if="activeStep === 2" class="success-page flex flex-col items-center py-40px">
      <el-result icon="success" title="创建成功" sub-title="自动化计划已启动，引擎将定期扫描数据并执行操作">
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
import { AutomationApi, AutomationTemplate } from '@/app/erplus/api/adv/automation'
import { ShopApi } from '@/app/erplus/api/system/shop'
import { useTagsViewStore } from '@/store/modules/tagsView'
import ProductSelectDialog from '@/app/erplus/compononts/ProductSelectDialog.vue'

const { push, currentRoute } = useRouter() // 路由跳转
const { delView } = useTagsViewStore() // 视图操作

const activeStep = ref(0)
const templateList = ref<AutomationTemplate[]>([])
const selectedTemplate = ref<AutomationTemplate | null>(null)
const shopCascaderList = ref<any[]>([])
const selectedShopPath = ref<any[]>([])
const selectedProduct = ref<any>(null)
const submitting = ref(false)

const form = reactive({
  name: '',
  shopId: undefined as number | undefined,
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
  const dict: Record<string, string> = {
    targetCpa: '目标 CPA',
    minOrders: '最低订单数',
    targetRoas: '目标 ROAS',
    roasIsolationThreshold: '隔离 ROAS 阈值',
    targetAcos: '目标 ACOS',
    minSpend: '最小花费',
    maxBid: '最高出价',
    budgetStrategy: '预算分配比例 (%)'
  }
  return dict[key] || key
}

const getDesc = (key: string) => {
  const dict: Record<string, string> = {
    targetCpa: '触发自动流转的最高允许 CPA',
    minOrders: '触发规则所需的最小订单数量',
    targetRoas: '期望达到的最低广告支出回报率',
    roasIsolationThreshold: '触发关键词建立独立 Campaign 的 ROAS 阈值',
    targetAcos: '允许的最高广告销售成本比',
    minSpend: '评估数据前的最小广告花费',
    maxBid: '自动调整时的最高出价上限',
    budgetStrategy: '分配给此计划的账户预算百分比'
  }
  return dict[key] || ''
}

const handleSelectTemplate = (item: AutomationTemplate) => {
  selectedTemplate.value = item
  // 提取模版中的默认 context
  if (item.config && item.config.defaultContext) {
    form.context = { ...item.config.defaultContext }
  } else {
    // 默认方案补充
    form.context = { 
      targetCpa: 15.0, 
      minOrders: 2, 
      roasIsolationThreshold: 4.5,
      budgetStrategy: 30
    }
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

const productSelectDialogRef = ref()
const handleSelectProduct = () => {
  productSelectDialogRef.value.open(form.shopId)
}

const formRef = ref()
const handleProductConfirm = (product: any) => {
  selectedProduct.value = product
  form.sku = product.sellerProductCode || product.sellerSku
  // 动触发校验，清除错误提示
  if (formRef.value) {
    formRef.value.validateField('sku')
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
    await AutomationApi.createPlan({
      name: form.name,
      templateId: selectedTemplate.value.id,
      shopId: form.shopId,
      sku: form.sku,
      platform: form.platform,
      context: form.context,
      status: 'RUNNING'
    })
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

.max-w-800px {
  max-width: 800px;
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
