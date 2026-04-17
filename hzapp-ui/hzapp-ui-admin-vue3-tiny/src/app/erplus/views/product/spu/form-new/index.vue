<template>
  <ContentWrap v-loading="formLoading">
    <el-tabs v-model="activeName">
      <el-tab-pane label="发布设置" name="publish">
        <PublishForm
          ref="publishRef"
          v-model:activeName="activeName"
          :is-detail="isDetail"
          :propFormData="formData"
        />
      </el-tab-pane>
      <el-tab-pane label="基础设置" name="info">
        <InfoForm
          ref="infoRef"
          v-model:activeName="activeName"
          :is-detail="isDetail"
          :propFormData="formData"
        />
      </el-tab-pane>

      <el-tab-pane label="价格库存" name="sku">
        <SkuForm
          ref="skuRef"
          v-model:activeName="activeName"
          :is-detail="isDetail"
          :propFormData="formData"
        />
      </el-tab-pane>
      <el-tab-pane label="产品属性" name="attributes">
        <ProductAttributesForm
          ref="attributesRef"
          v-model:activeName="activeName"
          :is-detail="isDetail"
          :propFormData="formData"
        />
      </el-tab-pane>
      <el-tab-pane label="物流设置" name="delivery">
        <DeliveryForm
          ref="deliveryRef"
          v-model:activeName="activeName"
          :is-detail="isDetail"
          :propFormData="formData"
        />
      </el-tab-pane>
      
      
  
      <el-tab-pane label="安全合规" name="compliance">
        <ComplianceForm
          ref="complianceRef"
          v-model:activeName="activeName"
          :is-detail="isDetail"
          :propFormData="formData"
        />
      </el-tab-pane>
    </el-tabs>

    <el-form>
      <el-form-item style="float: right">
        <el-button v-if="!isDetail" :loading="formLoading" type="primary" @click="submitForm">
          保存
        </el-button>
        <el-button @click="nextStep">下一步</el-button>
        <el-button @click="close">返回</el-button>
      </el-form-item>
    </el-form>

  </ContentWrap>
</template>
<script lang="ts" setup>
import { cloneDeep } from 'lodash-es'
import { useTagsViewStore } from '@/store/modules/tagsView'
import * as ProductSpuApi from '@/app/erplus/api/product/spu'
import InfoForm from './InfoForm.vue'
import SkuForm from './SkuForm.vue'
import ProductAttributesForm from './ProductAttributesForm.vue'
import DeliveryForm from './DeliveryForm.vue'
import PublishForm from './PublishForm.vue'
import ComplianceForm from './ComplianceForm.vue'
import { convertToInteger, floatToFixed2, formatToFraction } from '@/utils'

defineOptions({ name: 'ProductSpuForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const { push, currentRoute } = useRouter() // 路由
const { query, params, name } = useRoute() // 查询参数
const { delView, setTitle } = useTagsViewStore() // 视图操作

const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const activeName = ref('publish') // Tag 激活的窗口
const isDetail = ref(false) // 是否查看详情
const infoRef = ref() // 商品信息 Ref
const skuRef = ref() // 商品规格 Ref
const attributesRef = ref() // 产品属性 Ref
const deliveryRef = ref() // 物流设置 Ref
const publishRef = ref() // 发布设置 Ref
const complianceRef = ref() // 安全合规 Ref

const tabs: string[] = ['publish', 'info', 'sku', 'attributes', 'delivery', 'compliance']
 

// SPU 表单数据
const formData = ref<ProductSpuApi.Spu>({
  name: '', // 商品名称
  categoryId: undefined, // 商品分类
  keyword: '', // 关键字
  picUrl: '', // 商品封面图
  sliderPicUrls: [], // 商品轮播图
  introduction: '', // 商品简介
  deliveryTypes: [], // 配送方式数组
  deliveryTemplateId: undefined, // 运费模版
  brandId: undefined, // 商品品牌
  specType: false, // 商品规格
  subCommissionType: false, // 分销类型
  skus: [],
  description: '', // 商品详情
  sort: 1, // 商品排序
  giveIntegral: 1, // 赠送积分
  virtualSalesCount: 1, // 虚拟销量
  
  // 产品属性
  attributes: {},
  
  // 发布设置相关属性
  shopIds: [],
  saveMode: '',
  fulfillType: '',
  delaySync: false,
  scheduleTime: '',
  productAttributes: [],
  itemDim: {},
  pkgDim: {},
  boxDim: {},
  inboxnum: undefined,
  certifications: [],
  
  // 安全合规相关属性
  safetyStandards: [],
  safetyWarnings: [],
  materials: [],
  hazardousSubstances: [],
  environmentalCertifications: [],
  packagingMaterials: [],
  applicableRegulations: [],
  restrictedRegions: [],
  specialLicenses: [],
  qualityReports: []
})

/** 获得详情 */
const getDetail = async () => {
  if ('ProductSpuDetail' === name) {
    isDetail.value = true
  }
  const id = (query.id || params.id) as unknown as number
  if (id) {
    const title = `商品编辑-id = ${id}`
    setTitle(title)

    formLoading.value = true
    try {
      const res = (await ProductSpuApi.getSpu(id)) as ProductSpuApi.Spu
      res.skus?.forEach((item) => {
        if (isDetail.value) {
          item.price = floatToFixed2(item.price)
          item.marketPrice = floatToFixed2(item.marketPrice)
          item.costPrice = floatToFixed2(item.costPrice)
          item.firstBrokeragePrice = floatToFixed2(item.firstBrokeragePrice)
          item.secondBrokeragePrice = floatToFixed2(item.secondBrokeragePrice)
        } else {
          // 回显价格分转元
          item.price = formatToFraction(item.price)
          item.marketPrice = formatToFraction(item.marketPrice)
          item.costPrice = formatToFraction(item.costPrice)
          item.firstBrokeragePrice = formatToFraction(item.firstBrokeragePrice)
          item.secondBrokeragePrice = formatToFraction(item.secondBrokeragePrice)
        }
      })
      formData.value = res
    } finally {
      formLoading.value = false
    }

  }
}

/** 提交按钮 */
const submitForm = async () => {
  // 提交请求
  formLoading.value = true
  try {
    // 校验各表单
    await unref(infoRef)?.validate()
    await unref(skuRef)?.validate()
    await unref(attributesRef)?.validate()
    await unref(deliveryRef)?.validate()
    await unref(publishRef)?.validate()
    await unref(complianceRef)?.validate()
    // 深拷贝一份, 这样最终 server 端不满足，不需要影响原始数据
    const deepCopyFormData = cloneDeep(unref(formData)) as ProductSpuApi.Spu
    deepCopyFormData.skus!.forEach((item) => {
      // 给sku name赋值
      item.name = deepCopyFormData.name
      // sku相关价格元转分
      item.price = convertToInteger(item.price)
      item.marketPrice = convertToInteger(item.marketPrice)
      item.costPrice = convertToInteger(item.costPrice)
      item.firstBrokeragePrice = convertToInteger(item.firstBrokeragePrice)
      item.secondBrokeragePrice = convertToInteger(item.secondBrokeragePrice)
    })
    // 处理轮播图列表
    const newSliderPicUrls: any[] = []
    deepCopyFormData.sliderPicUrls!.forEach((item: any) => {
      // 如果是前端选的图
      typeof item === 'object' ? newSliderPicUrls.push(item.url) : newSliderPicUrls.push(item)
    })
    deepCopyFormData.sliderPicUrls = newSliderPicUrls
    // 校验都通过后提交表单
    const data = deepCopyFormData as ProductSpuApi.Spu
    const id = (query.id || params.id) as unknown as number
    if (!id) {
      await ProductSpuApi.createSpu(data)
      message.success(t('common.createSuccess'))
    } else {
      await ProductSpuApi.updateSpu(data)
      message.success(t('common.updateSuccess'))
    }
    close()
  } finally {
    formLoading.value = false
  }
}

const nextStep = () => {
  const index = (tabs.indexOf(activeName.value) + 1) % tabs.length
  activeName.value = tabs[index]
}

/** 关闭按钮 */
const close = () => {
  const current = unref(currentRoute)
  push({ path: '/erplusv2/product/spu' }).then(() => {
    delView(current)
  })
}

/** 初始化 */
onMounted(async () => {
  await getDetail()
})
</script>

