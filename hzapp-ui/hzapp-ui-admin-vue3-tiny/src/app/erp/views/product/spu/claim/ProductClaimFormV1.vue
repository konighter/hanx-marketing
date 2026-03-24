<template>
  <el-drawer v-model="dialogVisible" :title="dialogTitle" size="95%">

<!--  <Dialog :title="dialogTitle" v-model="dialogVisible" :fullscreen="true" >-->
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      :inline="true"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-divider content-position="left">认领平台</el-divider>
      <el-row>
        <el-form-item label="平台" prop="platform">
          <el-select v-model="formData.platform" placeholder="请选择平台类型" @change="sellPlatformChange" clearable >
            <el-option v-for="p in sellPlatform" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="区域" prop="sellZone">
          <el-select v-model="formData.sellZone" placeholder="请选择区域" @change="zoneChange" clearable>
            <el-option v-for="p in sellZone" :key="p.id" :label="p.zoneName" :value="p.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="店铺" prop="shopId">
          <el-select multiple v-model="formData.shopId" placeholder="请选择店铺" width="1000px" clearable>
            <el-option v-for="p in shops" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>

      </el-row>

      <el-row>

        <el-form-item label="语言" prop="language">
          <el-select v-model="formData.language" placeholder="请选择语言" clearable>


            <el-option
v-for="dict in getStrDictOptions(DICT_TYPE.OV_LANGUAGE_TYPE)"
                       :key="dict.value"
                       :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="币种" prop="currency">
          <el-select v-model="formData.currency" placeholder="请选择币种" clearable>


            <el-option
v-for="dict in getStrDictOptions(DICT_TYPE.OV_CURRENCY)"
                       :key="dict.value"
                       :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
      </el-row>

      <el-row>
        <el-form-item label="品类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择品类" clearable>
            <el-option label="默认品类" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌" prop="brandId">
          <el-select v-model="formData.brandId" placeholder="请选择品牌" clearable>
            <el-option v-for="p in brands" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务类型" prop="serviceMode">
          <el-select v-model="formData.serviceMode" placeholder="请选择服务类型" clearable>
            <el-option v-for="(item, idx) in serviceModes" :key="idx" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
      </el-row>
    </el-form>

      <el-divider content-position="left">变种信息</el-divider>
      <el-row>

        <SkuForm
          ref="skuRef"
          :is-detail="isDetail"
          :propFormData="spuData"
        />

      </el-row>


    <el-divider content-position="left">
      <el-checkbox v-model="useSpu">使用采集默认商品详情</el-checkbox>
    </el-divider>
    <div v-if="!useSpu">
      <el-row>
      <!--    商品详情    -->
      <DescriptionForm ref="descRef" :propFormData="spuData"  :is-detail="isDetail" />

      </el-row>
    </div>

    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">保 存</el-button>
      <el-button @click="submitForm" type="success" :disabled="formLoading">发 布</el-button>
      <el-button @click="close">取 消</el-button>
    </template>
<!--  </Dialog>-->
  </el-drawer>
</template>
<script setup lang="ts">
import { ProductClaimApi } from '@/app/erp/api/claim'
import SkuForm from "@/app/erp/views/product/spu/form/SkuForm.vue"
import DescriptionForm from "@/app/erp/views/product/spu/form/DescriptionForm.vue"
import {SellPlatformApi, SellPlatformVO} from "@/app/erp/api/sellplatform"
import {SellZoneApi, SellZoneVO} from "@/app/erp/api/sellzone"
import {BrandVO } from "@/app/erp/api/brand"
import * as BrandApi from "@/app/erp/api/brand"
import { ShopApi, ShopVO } from '@/app/erp/api/shop'
import * as SpuApi from '@/app/erp/api/spu'
import {Spu} from '@/app/erp/api/spu'
import {DICT_TYPE, getStrDictOptions} from "@/utils/dict";
/** 商品认领 表单 */
defineOptions({ name: 'ProductClaimFormV1' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const isDetail = ref(false)
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  spuId: undefined,
  skus: undefined,
  specType: undefined,
  platform: undefined,
  shopId: [],
  serviceMode: undefined,
  language: undefined,
  sellZone: undefined, // 站点的概念
  category: undefined,
  brandId: undefined,
  sellPrice: undefined,
  currency: undefined,
  extra: undefined,
  status: undefined,
  keywords: [] as string[]
})

const spuData = ref<Spu>({})


const useSpu = ref(true)



const formRules = reactive({
  platform: [{ required: true, message: '请选择平台', type: 'number' }],
  sellZone: [{ required: true, message: '请选择站点', type: 'number' }],
  shopId: [{ required: true, message: '请选择店铺', type: 'array' }],
  brandId: [{ required: true, message: '请选择品牌', type: 'number' }],
  language: [{ required: true, message: '请选择语言', type: 'string' }],
  serviceMode: [{ required: true, message: '请选择服务方式', type: 'string' }],
})
const formRef = ref() // 表单 Ref
const skuRef = ref() //  SkuRef
const descRef = ref() //  DescRef


// 注册
/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await ProductClaimApi.getProductClaim(id)
    } finally {
      formLoading.value = false
    }
  }
}

/** 关闭弹窗 */
const close = () => {
  resetForm()
  spuData.value = {}
  dialogVisible.value = false
}

/** 认领 */
const claim = async (spuId: number) => {
  dialogVisible.value = true
  formLoading.value = true
  try {
    spuData.value = await SpuApi.getSpu(spuId)
  } finally {
    formLoading.value = false
  }
}

defineExpose({ open , claim}) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {

  try {
    await unref(skuRef)?.validate()
    console.log(useSpu.value)
    if(!useSpu.value) {
      await unref(descRef)?.validate()
    }
    // 校验表单
    await formRef.value.validate()
    // 提交请求
    formLoading.value = true

    const data = formData.value
    data.spuId = spuData.value.id
    data.specType = spuData.value.specType
    data.skus = spuData.value.skus
    data.extra = JSON.stringify({
      description: spuData.value.description,
    } as Spu)

    await ProductClaimApi.batchProductClaim(data)
    message.success(t('common.createSuccess'))


    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    spuId: undefined,
    skuInfo: undefined,
    specType: undefined,
    platform: undefined,
    language: undefined,
    sellZone: undefined,
    category: undefined,
    brandId: undefined,
    sellPrice: undefined,
    currency: undefined,
    extra: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}





// 下拉框联动
const sellPlatform = ref<SellPlatformVO[]>([])
const sellZone     = ref<SellZoneVO[]>([])
const shops        = ref<ShopVO[]>([])
const brands       = ref<BrandVO[]>([])
const serviceModes = ref([])

const sellPlatformChange = async () => {
  formData.value.sellZone = undefined
  formData.value.shopId = []
  formData.value.serviceMode = undefined
  serviceModes.value = []

  if (formData.value.platform) {
    sellZone.value = await SellZoneApi.getSellZoneList({ platformId: formData.value.platform })
    serviceModes.value = sellPlatform.value.findLast((item) => item.id === formData.value.platform)?.serviceModes
  }
}

const zoneChange = async () => {
  formData.value.shopId = []
  if (formData.value.sellZone) {
    shops.value = await ShopApi.getShopList({ platformId: formData.value.platform,  region: formData.value.sellZone, status: 0})
    // 填充语言
    formData.value.language = sellZone.value.findLast(z => z.id === formData.value.sellZone).language
  }
}



onMounted(async  () => {
  sellPlatform.value = await SellPlatformApi.getSellPlatformList({})
  brands.value = await BrandApi.getSimpleBrandList()

})




</script>
