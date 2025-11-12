<!-- 商品发布 - 基础设置 -->
<template>
  <el-form ref="formRef" :disabled="isDetail" :model="formData" :rules="rules" label-width="120px">
    <el-form-item label="商品名称" prop="name">
      <el-input v-model="formData.name" :autosize="{ minRows: 2, maxRows: 2 }" :clearable="true" :show-word-limit="true"
        class="w-100!" maxlength="64" placeholder="请输入商品名称" type="textarea" />
    </el-form-item>
    <el-form-item label="商品分类" prop="categoryId">
      <el-cascader v-model="formData.categoryId" :options="categoryList" :props="defaultProps" class="w-100!" clearable
        filterable placeholder="请选择商品分类" />
    </el-form-item>
    <el-form-item label="商品品牌" prop="brandId">
      <el-select v-model="formData.brandId" class="w-100!" placeholder="请选择商品品牌">
        <el-option v-for="item in brandList" :key="item.id" :label="item.name" :value="item.id as number" />
      </el-select>
    </el-form-item>
    <el-form-item label="商品关键字" prop="keyword">
      <el-input-tag v-model="formData.keyword" class="w-100!" placeholder="请输入商品关键字" tag-type="primary" />
    </el-form-item>
    <el-form-item label="条形码" prop="barCode">
      <el-input v-model="formData.productCode" :clearable="true" :show-word-limit="true" class="w-100!" maxlength="64"
        placeholder="请输入条形码" />
    </el-form-item>
    <el-form-item label="商品特性" prop="introduction">
      <dev v-for="(feat, idx) in formData.introduction" :key="idx" class="flex flex-row !w-full mb-2">
        <el-input :key="idx" v-model="formData.introduction[idx]" :clearable="true" :show-word-limit="true"
          class="w-100!" maxlength="500" placeholder="请输入商品简介" type="textarea"
          :autosize="{ minRows: 2, maxRows: 20 }" />
        <span class="mx-2">
          <el-button type="primary" :icon="Plus" circle @click="addIntroduction(idx)" size="small" />
          <el-button v-if="formData.introduction.length > 1" type="danger" :icon="Delete" circle size="small"
            @click="delIntroduction(idx)" />
        </span>

      </dev>

    </el-form-item>
    <el-form-item label="商品主图" prop="picUrl">
      <UploadImg v-model="formData.picUrl" :disabled="isDetail" height="80px" />
    </el-form-item>
    <el-form-item label="商品轮播图" prop="sliderPicUrls">
      <UploadImgs v-model="formData.sliderPicUrls" :disabled="isDetail" />
    </el-form-item>
  </el-form>
</template>
<script lang="ts" setup>
import { PropType } from 'vue'
import { copyValueToTarget } from '@/utils'
import { propTypes } from '@/utils/propTypes'
import { defaultProps, handleTree } from '@/utils/tree'
import type { Spu } from '@/app/erplus/api/product/spu'
import * as ProductCategoryApi from '@/app/erplus/api/product/category'
import { CategoryVO } from '@/app/erplus/api/product/category'
import * as ProductBrandApi from '@/app/erplus/api/product/brand'
import { BrandVO } from '@/app/erplus/api/product/brand'
import {
  Delete,
  Plus
} from '@element-plus/icons-vue'

defineOptions({ name: 'ProductSpuInfoForm' })
const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => { }
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})

const message = useMessage() // 消息弹窗

const formRef = ref() // 表单 Ref
const formData = reactive<Spu>({
  name: '', // 商品名称
  productCode: '', // 商品编码
  categoryId: undefined, // 商品分类
  keyword: '', // 关键字
  picUrl: '', // 商品封面图
  sliderPicUrls: [], // 商品轮播图
  introduction: [''], // 商品简介
  brandId: undefined // 商品品牌
})
const rules = reactive({
  name: [required],
  categoryId: [required],
  keyword: [required],
  introduction: [required],
  picUrl: [required],
  sliderPicUrls: [required],
  brandId: [required]
})

/** 将传进来的值赋值给 formData */
watch(
  () => props.propFormData,
  (data) => {
    if (!data) {
      return
    }
    copyValueToTarget(formData, data)
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
    message.error('【基础设置】不完善，请填写相关信息')
    emit('update:activeName', 'info')
    throw e // 目的截断之后的校验
  }
}
defineExpose({ validate })

/** 初始化 */
const brandList = ref<BrandVO[]>([]) // 商品品牌列表
const categoryList = ref<CategoryVO[]>([]) // 商品分类树
onMounted(async () => {
  // 获得分类树
  const data = await ProductCategoryApi.getCategoryList({})
  categoryList.value = handleTree(data, 'id')
  // 获取商品品牌列表
  brandList.value = await ProductBrandApi.getSimpleBrandList()
})




const addIntroduction = (idx: number) => {
  if (formData.introduction.length >= 5) {
    message.warning('最多添加5个商品简介')
    return
  }
  formData.introduction.splice(idx + 1, 0, '')
}

const delIntroduction = (idx: number) => {
  formData.introduction.splice(idx, 1)
}



</script>

<style scoped>
/* * 使用 :deep() 来穿透 scoped 样式，
 * 影响 el-upload 的子组件
*/

/* * 1. 移除文件列表项（增/删）的过渡动画
 * (针对 .el-list-enter-active 和 .el-list-leave-active)
*/
:deep(.el-list-enter-active),
:deep(.el-list-leave-active) {
  transition: none;
}

/* * 2. 移除文件状态（如"上传中" -> "成功"图标）的淡入淡出动画 
 * (针对 .el-fade-in-linear-enter-active 等)
*/
:deep(.el-fade-in-linear-enter-active),
:deep(.el-fade-in-linear-leave-active) {
  transition: none;
}
</style>
