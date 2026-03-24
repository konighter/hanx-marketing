<!-- 商品信息, 物流信息-商品规格 -->
<template>
  <el-form
ref="formRef" v-loading="formLoading" :disabled="isDetail" :model="formData" :rules="rules"
    label-width="120px">

    <el-empty v-if="formData.skus?.filter(s => s.barCode !== '').length == 0" image-size="100" description="请先设置价格库存" />

    <el-tabs v-else v-model="activeName">

      <el-tab-pane
:label="sku.barCode" :name="sku.barCode"
        v-for="(sku, idx) in formData.skus?.filter(s => s.barCode !== '')" :key="idx">

        <el-form-item label="商品关键字" prop="keyword">
          <el-input-tag v-model="sku.keyword" class="w-100!" placeholder="请输入商品关键字" tag-type="primary" />
        </el-form-item>


        <el-form-item label="商品特性" prop="introduction">
          <dev v-for="(feat, i) in sku.introduction" :key="i" class="flex flex-row !w-full mb-2">
            <el-input
:key="i" v-model="sku.introduction[i]" :clearable="true" :show-word-limit="true" class="w-100!"
              maxlength="500" placeholder="请输入商品特性" type="textarea" :autosize="{ minRows: 2, maxRows: 20 }">

              <template #prefix>{{ i }}</template>

            </el-input>
            <span class="mx-2">
              <el-button
type="primary" :icon="Plus" circle @click="addIntroduction(sku.introduction, i)"
                size="small" />
              <el-button
v-if="sku.introduction.length > 1" type="danger" :icon="Delete" circle size="small"
                @click="delIntroduction(sku.introduction, i)" />
            </span>

          </dev>

        </el-form-item>
        <el-form-item label="商品主图" prop="picUrl">
          <UploadImg v-model="sku.picUrl" :disabled="isDetail" height="80px" />
        </el-form-item>
        <el-form-item label="商品轮播图" prop="sliderPicUrls">
          <UploadImgs v-model="sku.sliderPicUrls" :disabled="isDetail" />
        </el-form-item>
        <el-form-item label="商品详情" prop="description">
          <Editor v-model:modelValue="sku.description" height="200px" />
        </el-form-item>

      </el-tab-pane>


    </el-tabs>





  </el-form>

</template>
<script lang="ts" setup>
import { PropType } from 'vue'
import { copyValueToTarget } from '@/utils'
import { propTypes } from '@/utils/propTypes'
import {
  Delete,
  Plus
} from '@element-plus/icons-vue'
import {
  getPropertyList,
  PropertyAndValues,
  RuleConfig,
  SkuDimList
} from '@/app/erplus/views/product/spu/components/index'

import ErpProductPropertyForm from './ProductPropertyAddForm.vue'
import type { Spu } from '@/app/erplus/api/product/spu'

defineOptions({ name: 'ProductSkuSpecForm' })



const message = useMessage() // 消息弹窗
const formLoading = ref(false)
const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => { }
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})
const attributesAddFormRef = ref() // 添加商品属性表单
const formRef = ref() // 表单 Ref
const activeName = ref()



const propertyList = ref<PropertyAndValues[]>([]) // 商品属性列表
const skuListRef = ref() // 商品属性列表 Ref
const formData = reactive<Spu>({
  specType: false, // 商品规格
  skus: []
})


const rules = reactive({
  specType: [required],
  subCommissionType: [required]
})

/** 将传进来的值赋值给 formData */
watch(
  () => props.propFormData?.skus,
  (data) => {
    console.log("SpecSkuForm: skus:", data)
    if (!data || data.length === 0) {
      formData.skus = []
      return
    }

    formData.skus = [...data]

    if (!activeName.value || formData.skus.filter((s) => activeName.value === s.barCode).length === 0) {
      activeName.value = formData.skus?.[0].barCode
    }
    console.log('SpecSkuForm', formData.skus?.[0].barCode, activeName.value)
  },
  {
    immediate: true,
    deep: true
  }
)

/** 表单校验 */
const emit = defineEmits(['update:activeName'])
const validate = async () => {
  if (!formRef) return
  try {
    // 校验 sku
    skuListRef.value.validateSku()
    await unref(formRef).validate()
    // 校验通过更新数据
    Object.assign(props.propFormData, formData)
  } catch (e) {
    message.error('【库存价格】不完善，请填写相关信息')
    emit('update:activeName', 'sku')
    throw e // 目的截断之后的校验
  }
}
defineExpose({ validate })


const addIntroduction = async (intrs: string[], idx: number) => {
  if (intrs.length >= 5) {
    message.warning('最多填写5条！')
    return
  }
  intrs.push('')
}

const delIntroduction = async (intrs: string[], idx: number) => {
  intrs.splice(idx, 1)
}



</script>
