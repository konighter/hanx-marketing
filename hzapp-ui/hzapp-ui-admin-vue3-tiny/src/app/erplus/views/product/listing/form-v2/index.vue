<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { handleTree } from '@/utils/tree';
import * as CategoryApi from '@/app/erplus/api/product/category';
import * as ListingApi from '@/app/erplus/api/product/listing';
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue';
import SpuTableSelect from '@/app/erplus/views/product/spu/components/SpuTableSelect.vue';
import BasicInfoSection from './components/BasicInfoSection.vue';
import MediaSection from './components/MediaSection.vue';
import AmzDynamicForm from '../components/amz/AmzDynamicForm.vue';

const router = useRouter();
const spuSelectRef = ref();

// Context State
const context = reactive({
  shopIds: [] as any[],
  platformId: 3, // Default Amazon in this system
  categoryId: undefined as string[] | undefined,
  productType: ''
});

// Sidebar & Search State
const requiredOnly = ref(false);
const searchQuery = ref('');

const categories = ref<any[]>([]);
const categoryCasProps = {
  label: 'name',
  value: 'categoryId',
  children: 'children',
  emitPath: true
};

const schemaFields = ref<any[]>([]);
const schemaLoading = ref(false);

// Form Data State
const formData = reactive({
  basic: {
    title: '',
    description: '',
    sellerSku: '',
    brand: '',
    searchTerms: '',
    bulletPoints: ['', '', '', '', '']
  },
  media: {
    mainImage: '',
    additionalImages: Array(8).fill('')
  },
  attributes: {}
});

// Logic: Shop/Category
const handleShopChange = async () => {
  context.categoryId = undefined;
  context.productType = '';
  // context.shopIds is [platformId, shopId] when emit-path is true
  if (context.shopIds && context.shopIds.length >= 2) {
    try {
      const pId = context.shopIds[0];
      const sId = context.shopIds[1];
      const result = await CategoryApi.getCrossCategories({ 
        platformId: pId, 
        shopIds: [sId]
      } as any);
      if (result && result.categories) {
        categories.value = handleTree(result.categories, 'categoryId', 'parentCategoryId');
      }
    } catch (e) {
      console.error(e);
      ElMessage.error('Failed to load categories');
    }
  }
};

const handleCategoryChange = (val: string[]) => {
  if (val && val.length > 0) {
    context.productType = val[val.length - 1]; // Use last ID as product type
    fetchSchema(context.productType);
  } else {
    context.productType = '';
    schemaFields.value = [];
  }
};

const fetchSchema = async (pType: string) => {
  if (!pType) return;
  schemaLoading.value = true;
  try {
    const res = await ListingApi.getListingFormConfigV2(pType);
    schemaFields.value = res.fields || [];
  } catch (e) {
    console.error('Failed to fetch schema', e);
    ElMessage.error('获取品类属性失败');
  } finally {
    schemaLoading.value = false;
  }
};

// Logic: SPU Linkage
const openSpuSelect = () => spuSelectRef.value?.open();

const handleSpuSelected = (spu: any) => {
  if (!spu) return;
  formData.basic.title = spu.name;
  formData.basic.description = spu.description || spu.name;
  formData.basic.sellerSku = spu.productCode || '';
  if (spu.picUrl) {
    // Media mapping logic could go here if MediaSection supports it
  }
  ElMessage.success(`Linked to SPU: ${spu.name}`);
};

const handleSubmit = async () => {
  try {
    // Prepare Payload
    const payload = {
      productType: context.productType,
      shopId: context.shopIds[context.shopIds.length - 1],
      ...formData.basic,
      mainImage: formData.media.mainImage,
      additionalImages: formData.media.additionalImages.filter(u => !!u),
      attributes: {
        ...formData.attributes,
        brand: formData.basic.brand,
        generic_keyword: formData.basic.searchTerms,
        bullet_point: formData.basic.bulletPoints.filter(b => !!b)
      }
    };
    
    console.log('Publishing V2 Data:', payload);
    ElMessage.success('Publish task submitted via V2 Pipeline!');
  } catch (e) {
    console.error('Validation failed', e);
    ElMessage.warning('Please check the required fields');
  }
};

const goBack = () => router.back();

onMounted(() => {
  // Initial loads if needed
});
</script>

<template>
  <div class="amz-listing-form-v2">
    <!-- Left Sticky Sidebar -->
    <div class="sidebar-nav">
      <el-anchor :offset="100" class="custom-anchor">
          <el-anchor-link href="#section-basic" title="基础设置" />
          <template v-if="context.productType">
            <el-anchor-link href="#section-media" title="图片管理" />
            <el-anchor-link href="#section-pricing" title="报价信息" />
            <el-anchor-link href="#section-more" title="更多属性" />
          </template>
      </el-anchor>
      
      <div class="sidebar-footer">
        <el-button type="primary" :disabled="!context.productType" @click="handleSubmit" class="flex-1">
           发布
        </el-button>
        <el-button @click="goBack" class="flex-1">取消</el-button>
      </div>
    </div>

    <!-- Main Content Area -->
    <div class="form-scroll-pane" v-loading="schemaLoading">
      <div class="form-inner">
        <!-- Unified White Container -->
        <div class="main-form-paper">
          
          <!-- Section: 基本设置 -->
          <div id="section-basic" class="unified-section">
            <div class="section-header">
              <div class="left">
                <span class="vertical-bar"></span>
                <span class="title">基本设置</span>
              </div>
              <div class="right-tools">
                <el-button type="primary" plain icon="Link" @click="openSpuSelect">关联商品</el-button>
              </div>
            </div>
            <div class="section-content">
              <el-form :model="context" label-position="right" label-width="180px" class="entry-form" style="max-width: 850px">
                <el-form-item label="店铺" required>
                  <ShopCascaderSelect 
                    v-model="context.shopIds" 
                    :emit-path="true"
                    @change="handleShopChange"
                  />
                </el-form-item>
                <el-form-item label="Amazon 分类" required>
                  <el-cascader
                    v-model="context.categoryId"
                    :options="categories"
                    :props="categoryCasProps"
                    @change="handleCategoryChange"
                    placeholder="选择分类"
                    clearable
                    filterable
                  />
                </el-form-item>
              </el-form>

              <template v-if="context.productType">
                <div class="sub-divider"></div>
                <!-- Standard Fields -->
                <BasicInfoSection v-model="formData.basic" />
                <!-- Dynamic Basic Fields -->
                <AmzDynamicForm 
                  :product-type="context.productType"
                  :target-groups="['基本信息']"
                  :initial-data="formData.attributes"
                  :required-only="requiredOnly"
                  :search-query="searchQuery"
                  :exclude-fields="['brand', 'bullet_point', 'generic_keyword']"
                  :fields="schemaFields"
                />
              </template>
            </div>
          </div>

          <!-- Dynamic Sections -->
          <template v-if="context.productType">
            
            <!-- Section: 图片管理 -->
            <div id="section-media" class="unified-section border-t">
              <div class="section-header">
                <div class="left">
                  <span class="vertical-bar"></span>
                  <span class="title">图片管理</span>
                </div>
              </div>
              <div class="section-content">
                <MediaSection v-model="formData.media" />
              </div>
            </div>

            <!-- Section: 报价信息 -->
            <div id="section-pricing" class="unified-section border-t">
              <div class="section-header">
                <div class="left">
                  <span class="vertical-bar"></span>
                  <span class="title">报价信息</span>
                </div>
              </div>
              <div class="section-content">
                <AmzDynamicForm 
                  :product-type="context.productType"
                  :target-groups="['报价']"
                  :initial-data="formData.attributes"
                  :required-only="requiredOnly"
                  :search-query="searchQuery"
                  :exclude-fields="['brand', 'bullet_point', 'generic_keyword']"
                  :fields="schemaFields"
                />
              </div>
            </div>

            <div id="section-more" class="unified-section border-t">
              <div class="section-header">
                <div class="left">
                  <span class="vertical-bar"></span>
                  <span class="title">更多属性</span>
                </div>
                <!-- Integrated Toolbar -->
                <div class="right-tools">
                  <el-checkbox v-model="requiredOnly">仅显示必填项</el-checkbox>
                  <el-input 
                    v-model="searchQuery" 
                    placeholder="搜索字段 (Ctrl+F)..." 
                    prefix-icon="Search"
                    clearable
                    size="small"
                    style="width: 240px"
                  />
                </div>
              </div>
              <div class="section-content">
                <AmzDynamicForm 
                  :product-type="context.productType"
                  :target-groups="['描述', '更多属性', '技术规格', '其他属性', '关键词']"
                  :initial-data="formData.attributes"
                  :required-only="requiredOnly"
                  :search-query="searchQuery"
                  :exclude-fields="['brand', 'bullet_point', 'generic_keyword']"
                  :fields="schemaFields"
                />
              </div>
            </div>

          </template>

          <div v-if="!context.productType" class="empty-guide">
             <el-empty description="请先选择店铺与亚马逊分类" :image-size="120" />
          </div>

        </div>
      </div>
    </div>

    <!-- SPU Selector Dialog -->
    <SpuTableSelect ref="spuSelectRef" @change="handleSpuSelected" />
  </div>
</template>

<style scoped>
.amz-listing-form-v2 {
  display: flex;
  height: calc(100vh - 84px);
  background-color: #f5f7fa;
}

.sidebar-nav {
  width: 200px;
  background: #fff;
  border-right: 1px solid #ebeef5;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  margin-bottom: 32px;
}

.sidebar-header .title {
  font-size: 16px;
  font-weight: 700;
  margin: 0;
  color: #1a1a1a;
}

.sidebar-header .subtitle {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
}

.sidebar-footer {
  margin-top: auto;
  padding-top: 24px;
  display: flex;
  gap: 8px;
}

.form-scroll-pane {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  scroll-behavior: smooth;
  background-color: #f8f9fa;
}

.form-inner {
  max-width: 1400px;
  margin: 0; 
}

.main-form-paper {
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  box-shadow: 0 4px 16px rgba(0,0,0,0.04);
  min-height: 600px;
  font-size: 13px; /* Smaller base font size for the paper content */
}

.unified-section {
  padding: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px 8px 24px;
}

.section-header .left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-header .vertical-bar {
  width: 4px;
  height: 18px;
  background-color: #409eff;
  border-radius: 2px;
}

.section-header .title {
  font-size: 15px;
  font-weight: 700;
  color: #1a1a1a;
}

.section-header .right-tools {
  display: flex;
  align-items: center;
  gap: 20px;
}

.section-content {
  padding: 0 24px 8px 24px;
}

.entry-form {
  padding: 0;
}

.sub-divider {
  margin: 16px 0;
  border-top: 1px dashed #ebeef5;
}

.border-t {
  border-top: 1px solid #f0f2f5;
}

.empty-guide {
  padding: 120px 0;
  display: flex;
  justify-content: center;
}

.custom-anchor :deep(.el-anchor__link) {
  font-size: 12px;
  padding: 8px 0;
  color: #606266;
}

.custom-anchor :deep(.el-anchor__link-active) {
  color: #409eff;
  font-weight: 600;
}

/* Base adjustment for global font size and width within the form section */
:deep(.el-form-item__label),
:deep(.el-input__inner),
:deep(.el-textarea__inner),
:deep(.el-button) {
  font-size: 13px;
}

:deep(.el-input),
:deep(.el-select),
:deep(.el-cascader),
:deep(.el-textarea) {
  width: 100%;
}

.flex-1 {
  flex: 1;
}
.w-full {
  width: 100%;
}
.mt-2 {
  margin-top: 8px;
}
</style>
