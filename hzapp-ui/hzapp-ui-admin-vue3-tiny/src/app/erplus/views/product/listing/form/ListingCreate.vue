<template>
  <div class="flex h-full listing-create-page w-full min-h-[500px]">
    <!-- 左侧导航目录 -->
    <div class="w-64 flex-shrink-0 bg-white border-r hidden md:block" style="height: calc(100vh - 100px); position: sticky; top: 0; overflow-y: auto;">
      <div class="p-5 border-b sticky top-0 bg-white z-10 flex items-center justify-between">
        <h2 class="text-lg font-bold m-0 leading-tight">刊登商品</h2>
        <el-button link @click="handleBack"><Icon icon="ep:back" /> 返回</el-button>
      </div>
      <div class="p-4">
        <el-anchor container=".right-scroll-pane" :offset="30">
          <el-anchor-link href="#section-base" title="店铺" />
          <el-anchor-link href="#section-basic-info" title="商品信息" />
          <template v-if="formData.shopId && formData.categoryId">
            <el-anchor-link href="#section-platform-fields" :title="crossPlatformName + ' 商品详情'" />
            <div class="ml-4 pl-2 border-l border-gray-100">
              <el-anchor-link 
                v-for="group in platformGroups" 
                :key="group.title" 
                :href="group.href" 
                :title="group.title" 
              />
            </div>
          </template>
        </el-anchor>
      </div>
    </div>

    <!-- 右侧表单滚动区 -->
    <div class="flex-grow right-scroll-pane px-6 py-6 bg-gray-50" style="height: calc(100vh - 100px); overflow-y: auto; scroll-behavior: smooth;">
      <div class="max-w-[1000px] mx-auto pb-24">
        
        <!-- Mobile Header (hidden on md) -->
        <div class="md:hidden flex items-center justify-between mb-4 bg-white p-4 rounded shadow-sm">
          <h2 class="text-lg font-bold m-0">刊登商品</h2>
          <el-button link @click="handleBack"><Icon icon="ep:back" /> 返回</el-button>
        </div>

        <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" label-position="top">
          
          <div id="section-base" class="mb-6 p-6 bg-white rounded shadow-sm border border-gray-100">
            <h3 class="text-base font-bold mb-6 pb-2 border-b text-gray-800">店铺</h3>
            <!-- 平台和店铺选择 -->
            <el-form-item label="店铺" prop="shopId">
              <div class="flex items-center space-x-4 w-full">
                <ShopCascaderSelect 
                  v-model="shopCascaderValue" 
                  :emit-path="true"
                  class="flex-1"
                  placeholder="请选择平台 / 店铺"
                  @change="handleShopChange"
                />
                <el-button type="primary" plain @click="handleOpenSpuSelect">
                  <Icon icon="ep:link" class="mr-1" /> 关联本地商品
                </el-button>
              </div>
              <div v-if="selectedShop" class="mt-2 text-xs text-gray-400">
                当前平台: <el-tag size="small" type="info">{{ crossPlatformName }}</el-tag>
              </div>
            </el-form-item>

            <!-- 品类选择 -->
            <el-form-item label="刊登品类" prop="categoryId">
              <el-cascader
                v-model="formData.categoryId"
                :options="categories"
                :props="categoryCasProps"
                class="w-full"
                :disabled="!formData.shopId"
                clearable
                filterable
                @change="handleCategoryChange"
                placeholder="请选择刊登类目"
              />
            </el-form-item>
          </div>

          <div id="section-basic-info" class="mb-6 p-6 bg-white rounded shadow-sm border border-gray-100">
            <h3 class="text-base font-bold mb-6 pb-2 border-b text-gray-800">商品信息</h3>
            
            <el-form-item label="标题" prop="title">
              <el-input v-model="formData.title" placeholder="请输入商品刊登标题" maxlength="200" show-word-limit />
            </el-form-item>

            <el-form-item label="描述" prop="description">
              <el-input 
                v-model="formData.description" 
                type="textarea" 
                :rows="4" 
                placeholder="请输入商品刊登描述" 
              />
            </el-form-item>

            <el-form-item label="商品图片" required>
              <div class="flex flex-col gap-4 w-full">
                <div>
                  <div class="text-sm text-gray-500 mb-1">主图</div>
                  <el-upload
                    action="#"
                    list-type="picture-card"
                    :auto-upload="false"
                    v-model:file-list="formData.mainImages"
                    :limit="1"
                  >
                    <Icon icon="ep:plus" />
                  </el-upload>
                </div>
                <div>
                  <div class="text-sm text-gray-500 mb-1">附图</div>
                  <el-upload
                    action="#"
                    list-type="picture-card"
                    :auto-upload="false"
                    v-model:file-list="formData.additionalImages"
                    multiple
                    :limit="8"
                  >
                    <Icon icon="ep:plus" />
                  </el-upload>
                </div>
              </div>
            </el-form-item>
          </div>

          <!-- 平台动态属性表单 -->
          <template v-if="formData.shopId && formData.categoryId">
            <div id="section-platform-fields" class="mb-6 p-6 bg-white rounded shadow-sm border border-gray-100">
              <h3 class="text-xl font-bold mb-6 pb-3 border-b text-indigo-700 flex items-center">
                <Icon icon="ep:expand" class="mr-2" />
                {{ crossPlatformName }} 商品详情
              </h3>
              <div class="dynamic-form-wrap">
                <component
                  :is="dynamicFormComponent"
                  ref="dynamicFormRef"
                  v-model="formData.productAttributes"
                  :shop-id="formData.shopId"
                  :product-type="selectedCategoryCode"
                  @schema-groups-updated="handleSchemaGroupsUpdated"
                  @change="handleDynamicFormChange"
                />
              </div>
            </div>
          </template>

          <div class="flex justify-center mt-10 space-x-4">
            <el-button type="primary" size="large" class="px-10" @click="handleSubmit" :loading="submitting">发布刊登</el-button>
            <el-button size="large" class="px-8" @click="handleBack">返回列表</el-button>
          </div>
        </el-form>
      </div>
    </div>

    <!-- SPU 选择弹窗 -->
    <SpuTableSelect ref="spuSelectRef" @change="handleSpuSelected" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue'
import SpuTableSelect from '@/app/erplus/views/product/spu/components/SpuTableSelect.vue'
import AmazonListingDynamicFormV2 from '../components/AmazonListingDynamicFormV2.vue'
import * as CategoryApi from '@/app/erplus/api/product/category'
import * as SpuApi from '@/app/erplus/api/product/spu'
import { handleTree } from '@/utils/tree'

defineOptions({ name: 'ListingCreate' })
const emit = defineEmits(['back', 'success'])
const { back } = useRouter()

const formRef = ref()
const dynamicFormRef = ref()
const spuSelectRef = ref()
const submitting = ref(false)

const platformGroups = ref<any[]>([])

const shopCascaderValue = ref()
const selectedShop = ref<any>(null)
const categories = ref<any[]>([])

const formData = reactive({
  shopId: undefined as number | undefined,
  platformId: undefined as number | undefined,
  categoryId: undefined as string[] | undefined,
  title: '',
  description: '',
  mainImages: [] as any[],
  additionalImages: [] as any[],
  productAttributes: {} as any,
})

const rules = {
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择品类', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
}

const categoryCasProps = {
  label: 'name',
  value: 'categoryId',
  children: 'children'
}

// 动态组件映射
const dynamicFormComponent = computed(() => {
  if (!selectedShop.value || !shopCascaderValue.value) return null
  const platformId = shopCascaderValue.value[0]
  const platform = getPlatformCode(platformId)
  if (platform === 'AMAZON') return AmazonListingDynamicFormV2
  // Add other platforms here
  return null
})

const crossPlatformName = computed(() => {
  if (!selectedShop.value || !shopCascaderValue.value) return ''
  const platformId = shopCascaderValue.value[0]
  return getPlatformName(platformId)
})

const selectedCategoryCode = computed(() => {
  if (!formData.categoryId || formData.categoryId.length === 0) return ''
  return formData.categoryId[formData.categoryId.length - 1]
})

function findCategoryName(tree: any[], id: string): string {
  for (const node of tree) {
    if (node.categoryId === id) return node.categoryName
    if (node.children) {
      const found = findCategoryName(node.children, id)
      if (found) return found
    }
  }
  return ''
}

// Platform mapping (Sync with CrossPlatformEnum.java)
const getPlatformCode = (id: number) => {
  if (id === 3) return 'AMAZON'
  if (id === 5) return 'OZON'
  if (id === 4) return 'TTS'
  if (id === 6) return 'ALIEXPRESS'
  return ''
}

const getPlatformName = (id: number) => {
  if (id === 3) return '亚马逊'
  if (id === 5) return 'Ozon'
  if (id === 4) return 'Tiktok Shop'
  if (id === 6) return '速卖通'
  return '未知平台'
}

const handleShopChange = async (node: any) => {
  selectedShop.value = node
  formData.shopId = node?.id
  formData.platformId = shopCascaderValue.value ? shopCascaderValue.value[0] : undefined
  
  // Clear category when shop changes if platform is different
  formData.categoryId = undefined
  categories.value = []
  
  if (formData.shopId) {
    await loadCategories()
  }
}

const loadCategories = async () => {
  if (!formData.shopId || !formData.platformId) return
  const platformId = formData.platformId
  
  try {
    const result = await CategoryApi.getCrossCategories({ 
      platformId, 
      shopIds: [formData.shopId] 
    } as any)
    if (result && result.categories) {
      categories.value = handleTree(result.categories, 'categoryId', 'parentCategoryId')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('加载品类失败')
  }
}

const handleCategoryChange = () => {
  // Clear attributes when category changes
  formData.productAttributes = {}
}

const handleOpenSpuSelect = () => {
  spuSelectRef.value?.open()
}

const handleSpuSelected = (spu: any) => {
  if (!spu) return
  formData.title = spu.name
  formData.description = spu.description || spu.name
  // Handle images if available
  if (spu.picUrl) {
    formData.mainImages = [{ url: spu.picUrl, name: '主图' }]
  }
  ElMessage.success(`已关联本地商品: ${spu.name}`)
}

const handleDynamicFormChange = (data: any) => {
  formData.productAttributes = data
}

const handleSchemaGroupsUpdated = (groups: any[]) => {
  platformGroups.value = groups
}

const handleBack = () => {
  // Use emit for backward compatibility internally, otherwise router back
  emit('back')
  back()
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (dynamicFormRef.value) {
      await dynamicFormRef.value.validate()
    }
    
    submitting.value = true
    const data = {
      ...formData,
      crossPlatform: getPlatformCode(formData.platformId || 3),
      // Construct the final request as needed by SpuApi.spuListing or similar
    }
    
    // await SpuApi.spuListing(data)
    console.log('Publishing listing:', data)
    ElMessage.success('发布成功')
    emit('success')
    back()
  } catch (err) {
    console.error(err)
    ElMessage.warning('请完善表单信息')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.listing-create-page :deep(.el-anchor__link) {
  font-size: 14px;
}
.image-uploader :deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}
.image-uploader :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>
