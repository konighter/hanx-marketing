<!-- 商品发布 - 基础设置 -->
<template>
  <el-form ref="formRef" :disabled="isDetail" :model="formData" :rules="rules" label-width="120px">

        <el-form-item label="商品分类" prop="categoryId">
      <el-cascader
        v-model="formData.categoryId"
        :options="categoryList"
        :props="defaultProps"
        class="w-80"
        clearable
        filterable
        placeholder="请选择商品分类"
      />
    </el-form-item>
    <el-form-item label="商品品牌" prop="brandId">
      <el-select v-model="formData.brandId" class="w-80" placeholder="请选择商品品牌">
        <el-option
          v-for="item in brandList"
          :key="item.id"
          :label="item.name"
          :value="item.id as number"
        />
      </el-select>
    </el-form-item>

    <!-- 语种选择 -->
    <el-form-item label="支持语种" prop="supportedLanguages">
      <el-select
          :model-value="formData.supportedLanguages || []"
          multiple
          placeholder="请选择支持的语种"
          class="w-full"
          @change="onLanguageChange"
        >
        <el-option label="中文" value="zh" />
        <el-option label="英文" value="en" />
        <el-option label="日文" value="ja" />
        <el-option label="德文" value="de" />
        <el-option label="法文" value="fr" />
        <el-option label="西班牙文" value="es" />
        <el-option label="意大利文" value="it" />
        <el-option label="俄文" value="ru" />
      </el-select>
    </el-form-item>
    
    <!-- 商品名称 - 多语种 -->
    <el-form-item label="商品名称" prop="name">
      <el-tabs v-model="activeNameTab" type="border-card" class="w-full compact-tabs">
        <el-tab-pane
          v-for="lang in selectedLanguages"
          :key="lang"
          :label="getLanguageLabel(lang)"
          :name="lang"
        >
          <el-input
            v-model="multiLanguageData.name[lang]"
            :autosize="{ minRows: 2, maxRows: 2 }"
            :clearable="true"
            :show-word-limit="true"
            class="w-80!"
            maxlength="64"
            :placeholder="`请输入${getLanguageLabel(lang)}商品名称`"
            type="textarea"
          />
        </el-tab-pane>
      </el-tabs>
    </el-form-item>

    <!-- 商品关键字 - 多语种 -->
    <el-form-item label="商品关键字" prop="keyword">
      <el-tabs v-model="activeKeywordTab" type="border-card" class="w-full compact-tabs">
        <el-tab-pane
          v-for="lang in selectedLanguages"
          :key="lang"
          :label="getLanguageLabel(lang)"
          :name="lang"
        >
          <el-input-tag
            v-model="multiLanguageData.keyword[lang]"
            class="w-full"
            :placeholder="`请输入${getLanguageLabel(lang)}商品关键字`"
          />
        </el-tab-pane>
      </el-tabs>
    </el-form-item>
    <!-- 商品简介 - 多语种 -->
    <el-form-item label="商品简介" prop="introduction">
      <el-tabs v-model="activeIntroTab" type="border-card" class="w-full compact-tabs">
        <el-tab-pane
          v-for="lang in selectedLanguages"
          :key="lang"
          :label="getLanguageLabel(lang)"
          :name="lang"
        >
          <div v-for="(feat, idx) in multiLanguageData.introduction[lang] || []" :key="idx" class="flex flex-row w-full mb-2">
            <el-input
              v-model="multiLanguageData.introduction[lang][idx]"
              :autosize="{ minRows: 2, maxRows: 20 }"
              :clearable="true"
              :show-word-limit="true"
              class="w-full"
              maxlength="500"
              :placeholder="`请输入${getLanguageLabel(lang)}商品简介`"
              type="textarea"
            />
            <span class="mx-2">
              <el-button link type="primary" :icon="Plus" @click="addIntroduction(lang, idx)" />
              <el-button
                v-if="multiLanguageData.introduction[lang]?.length > 1" link type="danger" :icon="Delete"
                @click="delIntroduction(lang, idx)" />
            </span>
          </div>
          <el-button v-if="!multiLanguageData.introduction[lang]?.length" text type="primary" @click="addIntroduction(lang, -1)">
            <template #icon><el-icon><Plus /></el-icon></template>添加简介
          </el-button>
        </el-tab-pane>
      </el-tabs>
    </el-form-item>
    <!-- 商品详情 - 多语种 -->
    <el-form-item label="商品详情" prop="description">
      <el-tabs v-model="activeDescTab" type="border-card" class="w-full compact-tabs">
        <el-tab-pane
          v-for="lang in selectedLanguages"
          :key="lang"
          :label="getLanguageLabel(lang)"
          :name="lang"
        >
          <Editor v-model:modelValue="multiLanguageData.description[lang]" :disabled="isDetail" />
        </el-tab-pane>
      </el-tabs>
    </el-form-item>
    <el-form-item label="商品封面图" prop="picUrl">
      <UploadImg v-model="formData.picUrl" :disabled="isDetail" height="80px" />
    </el-form-item>
    <el-form-item label="商品轮播图" prop="sliderPicUrls">
      <UploadImgsPlus v-model="formData.sliderPicUrls" :disabled="isDetail" />

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
import { CategoryVO } from '@/api/mall/product/category'
import * as ProductBrandApi from '@/api/mall/product/brand'
import { BrandVO } from '@/api/mall/product/brand'
import UploadImgsPlus from '../components/UploadImgsPlus.vue'
import { Editor } from '@/components/Editor'
import { Plus, Delete } from '@element-plus/icons-vue'

defineOptions({ name: 'ProductSpuInfoForm' })
const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => {}
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})

const message = useMessage() // 消息弹窗

const formRef = ref() // 表单 Ref
const formData = reactive<Spu>({
  name: '', // 商品名称
  categoryId: undefined, // 商品分类
  keyword: [], // 关键字
  picUrl: '', // 商品封面图
  sliderPicUrls: [], // 商品轮播图
  introduction: [''], // 商品简介
  description: '', // 商品详情
  brandId: undefined, // 商品品牌
  supportedLanguages: [] as string[], // 支持的语种列表
  multiLanguage: {} as Record<string, string> // 多语种数据
})

// 多语种相关状态
const selectedLanguages = ref<string[]>(['zh']) // 默认选择中文
const activeNameTab = ref('zh')
const activeKeywordTab = ref('zh')
const activeIntroTab = ref('zh')
const activeDescTab = ref('zh')

// 多语种数据
const multiLanguageData = reactive({
  name: {} as Record<string, string>,
  keyword: {} as Record<string, any>,
  introduction: {} as Record<string, any>,
  description: {} as Record<string, string>
})
const rules = reactive({
  name: [required],
  categoryId: [required],
  keyword: [required],
  introduction: [required],
  description: [required],
  picUrl: [required],
  sliderPicUrls: [required],
  brandId: [required],
  supportedLanguages: [required]
})

// 语种标签映射
const languageLabels: Record<string, string> = {
  zh: '中文',
  en: '英文',
  ja: '日文',
  de: '德文',
  fr: '法文',
  es: '西班牙文',
  it: '意大利文',
  ru: '俄文'
}

// 获取语种标签
const getLanguageLabel = (lang: string): string => {
  return languageLabels[lang] || lang
}

// 语种变化处理
const onLanguageChange = (languages: string[]) => {
  // 更新selectedLanguages
  selectedLanguages.value = languages
  
  // 确保每个选中的语种都有对应的数据
  languages.forEach(lang => {
    if (!multiLanguageData.name[lang]) {
      multiLanguageData.name[lang] = ''
    }
    if (!multiLanguageData.keyword[lang]) {
      multiLanguageData.keyword[lang] = []
    }
    if (!multiLanguageData.introduction[lang]) {
      multiLanguageData.introduction[lang] = ['']
    }
    if (!multiLanguageData.description[lang]) {
      multiLanguageData.description[lang] = ''
    }
  })
  
  // 移除未选中语种的数据
  Object.keys(multiLanguageData.name).forEach(lang => {
    if (!languages.includes(lang)) {
      delete multiLanguageData.name[lang]
      delete multiLanguageData.keyword[lang]
      delete multiLanguageData.introduction[lang]
      delete multiLanguageData.description[lang]
    }
  })
  
  // 更新活跃tab
  if (languages.length > 0) {
    const firstLang = languages[0]
    activeNameTab.value = firstLang
    activeKeywordTab.value = firstLang
    activeIntroTab.value = firstLang
    activeDescTab.value = firstLang
  }
  
  // 同步到formData
  syncMultiLanguageToFormData()
}

// 同步多语种数据到formData
const syncMultiLanguageToFormData = () => {
  // 将多语种数据存储到multiLanguage字段
  if (!formData.multiLanguage) {
    formData.multiLanguage = {}
  }
  
  // 存储多语种数据
  selectedLanguages.value.forEach(lang => {
    formData.multiLanguage![`${lang}_name`] = multiLanguageData.name[lang] || ''
    formData.multiLanguage![`${lang}_keyword`] = Array.isArray(multiLanguageData.keyword[lang]) ? JSON.stringify(multiLanguageData.keyword[lang]) : (multiLanguageData.keyword[lang] || '')
    formData.multiLanguage![`${lang}_introduction`] = Array.isArray(multiLanguageData.introduction[lang]) ? JSON.stringify(multiLanguageData.introduction[lang]) : (multiLanguageData.introduction[lang] || '')
    formData.multiLanguage![`${lang}_description`] = multiLanguageData.description[lang] || ''
  })
  
  // 设置默认语种的值到原字段（向后兼容）
  if (selectedLanguages.value.includes('zh')) {
    formData.name = multiLanguageData.name['zh'] || ''
    formData.keyword = multiLanguageData.keyword['zh'] || []
    formData.introduction = multiLanguageData.introduction['zh'] || ['']
    formData.description = multiLanguageData.description['zh'] || ''
  } else if (selectedLanguages.value.length > 0) {
    const firstLang = selectedLanguages.value[0]
    formData.name = multiLanguageData.name[firstLang] || ''
    formData.keyword = multiLanguageData.keyword[firstLang] || []
    formData.introduction = multiLanguageData.introduction[firstLang] || ['']
    formData.description = multiLanguageData.description[firstLang] || ''
  }
}

// 从formData同步多语种数据
const syncFormDataToMultiLanguage = () => {
  if (formData.multiLanguage) {
    Object.keys(formData.multiLanguage).forEach(key => {
      if (key.endsWith('_name')) {
        const lang = key.replace('_name', '')
        multiLanguageData.name[lang] = formData.multiLanguage![key]
        if (!selectedLanguages.value.includes(lang)) {
          selectedLanguages.value.push(lang)
          if (!formData.supportedLanguages!.includes(lang)) {
            formData.supportedLanguages!.push(lang)
          }
        }
      } else if (key.endsWith('_keyword')) {
        const lang = key.replace('_keyword', '')
        let val = formData.multiLanguage![key]
        try { val = JSON.parse(val) } catch(e) { val = val ? [val] : [] }
        multiLanguageData.keyword[lang] = val
      } else if (key.endsWith('_introduction')) {
        const lang = key.replace('_introduction', '')
        let val = formData.multiLanguage![key]
        try { val = JSON.parse(val) } catch(e) { val = val ? [val] : [''] }
        multiLanguageData.introduction[lang] = val
      } else if (key.endsWith('_description')) {
        const lang = key.replace('_description', '')
        multiLanguageData.description[lang] = formData.multiLanguage![key]
      }
    })
  }
  
  // 如果没有多语种数据，使用默认字段初始化
  if (selectedLanguages.value.length === 0) {
    selectedLanguages.value = ['zh']
    formData.supportedLanguages = ['zh']
  }
  
  // 确保中文数据存在
  if (selectedLanguages.value.includes('zh')) {
    multiLanguageData.name['zh'] = formData.name || ''
    multiLanguageData.keyword['zh'] = Array.isArray(formData.keyword) ? formData.keyword : (formData.keyword ? [formData.keyword] : [])
    multiLanguageData.introduction['zh'] = Array.isArray(formData.introduction) ? formData.introduction : (formData.introduction ? [formData.introduction] : [''])
    multiLanguageData.description['zh'] = formData.description || ''
  }
  
  // 设置活跃tab
  if (selectedLanguages.value.length > 0) {
    const firstLang = selectedLanguages.value[0]
    activeNameTab.value = firstLang
    activeKeywordTab.value = firstLang
    activeIntroTab.value = firstLang
    activeDescTab.value = firstLang
  }
}

// 监听多语种数据变化
watch(
  () => multiLanguageData,
  () => {
    syncMultiLanguageToFormData()
  },
  { deep: true }
)

/** 将传进来的值赋值给 formData */
watch(
  () => props.propFormData,
  (data) => {
    if (!data) return
    copyValueToTarget(data, formData)
    // 确保supportedLanguages有默认值
    if (!formData.supportedLanguages || formData.supportedLanguages.length === 0) {
      formData.supportedLanguages = ['zh']
    }
    // 同步selectedLanguages
    selectedLanguages.value = formData.supportedLanguages
    // 同步多语种数据
    syncFormDataToMultiLanguage()
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

/** 表单值重置 */
const resetFields = () => {
  formRef.value.resetFields()
  // 重置多语种数据
  selectedLanguages.value = ['zh']
  formData.supportedLanguages = ['zh']
  multiLanguageData.name = {}
  multiLanguageData.keyword = {}
  multiLanguageData.introduction = {}
  multiLanguageData.description = {}
  activeNameTab.value = 'zh'
  activeKeywordTab.value = 'zh'
  activeIntroTab.value = 'zh'
  activeDescTab.value = 'zh'
}

defineExpose({ validate, resetFields })

/** 初始化 */
const brandList = ref<BrandVO[]>([]) // 商品品牌列表
const categoryList = ref<CategoryVO[]>([]) // 商品分类树

const addIntroduction = (lang: string, idx: number) => {
  if (!multiLanguageData.introduction[lang]) {
    multiLanguageData.introduction[lang] = []
  }
  if (multiLanguageData.introduction[lang].length >= 5) {
    message.warning('最多添加5个商品简介')
    return
  }
  multiLanguageData.introduction[lang].splice(idx + 1, 0, '')
}

const delIntroduction = (lang: string, idx: number) => {
  multiLanguageData.introduction[lang].splice(idx, 1)
}

onMounted(async () => {
  // 获得分类树
  const data = await ProductCategoryApi.getCrossCategories({} as ProductCategoryApi.PlatformCategoryReqVO)
  categoryList.value = handleTree(data, 'id')
  // 获取商品品牌列表
  brandList.value = await ProductBrandApi.getSimpleBrandList()
})
</script>

<style scoped>
.compact-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 8px;
  }
  
  :deep(.el-tabs__nav-wrap) {
    padding: 0;
  }
  
  :deep(.el-tabs__item) {
    height: 32px;
    line-height: 32px;
    padding: 0 12px;
    font-size: 12px;
  }
  
  :deep(.el-tabs__content) {
    padding-top: 8px;
  }
  
  :deep(.el-tab-pane) {
    padding: 0;
  }
}
</style>
