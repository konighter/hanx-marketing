<template>
  <div v-loading="formLoading" class="h-full">
    <ProductFormV2
      :initial-data="formData"
      @close="close"
      @save="submitForm"
    />
  </div>
</template>

<script lang="ts" setup>
import { useTagsViewStore } from '@/store/modules/tagsView'
import * as ProductSpuApi from '@/app/erplus/api/product/spu'
import ProductFormV2 from './ProductFormV2.vue'
import { floatToFixed2, formatToFraction, convertToInteger } from '@/utils'

defineOptions({ name: 'ProductSpuForm' })

const message = useMessage()
const { t } = useI18n()
const { push, currentRoute } = useRouter()
const { params, name } = useRoute()
const { delView } = useTagsViewStore()

const formLoading = ref(false)
const formData = ref<any>({})

/** 获得详情 */
const getDetail = async () => {
  const id = params.id as unknown as number
  if (id) {
    formLoading.value = true
    try {
      const res = await ProductSpuApi.getSpu(id)
      // 处理元转分
      if (res.skus) {
        res.skus.forEach((item: any) => {
          item.price = formatToFraction(item.price)
          item.marketPrice = formatToFraction(item.marketPrice)
          item.costPrice = formatToFraction(item.costPrice)
        })
      }
      formData.value = res
    } finally {
      formLoading.value = false
    }
  }
}

/** 提交按钮 */
const submitForm = async (data: any) => {
  formLoading.value = true
  try {
    const submitData = { ...data }
    // 处理分转元
    if (submitData.skus) {
      submitData.skus.forEach((item: any) => {
        item.price = convertToInteger(item.price)
        item.marketPrice = convertToInteger(item.marketPrice)
        item.costPrice = convertToInteger(item.costPrice)
      })
    }
    
    const id = params.id as unknown as number
    if (!id) {
      await ProductSpuApi.createSpu(submitData)
      message.success(t('common.createSuccess'))
    } else {
      await ProductSpuApi.updateSpu(submitData)
      message.success(t('common.updateSuccess'))
    }
    close()
  } finally {
    formLoading.value = false
  }
}

/** 关闭按钮 */
const close = () => {
  delView(unref(currentRoute))
  push({ name: 'ErplusProductSpu' })
}

onMounted(async () => {
  await getDetail()
})
</script>

<style scoped>
.h-full {
  height: 100%;
}
</style>

