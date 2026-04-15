<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { handleTree } from '@/utils/tree';
import { unflatten } from '@/utils/unflatten';
import * as CategoryApi from '@/app/erplus/api/product/category';
import * as ListingApi from '@/app/erplus/api/product/listing';
import ShopCascaderSelect from '@/app/erplus/compononts/ShopCascaderSelect.vue';
import SpuTableSelect from '@/app/erplus/views/product/spu/components/SpuTableSelect.vue';
import BasicInfoSection from './components/BasicInfoSection.vue';
import MediaSection from './components/MediaSection.vue';
import AmzDynamicForm from '../components/amz/AmzDynamicForm.vue';

const router = useRouter();
const spuSelectRef = ref();
const scrollPaneRef = ref();
const basicInfoRef = ref();
const dynamicForm_basic = ref();
const dynamicForm_price = ref();
const dynamicForm_more = ref();
const activeAnchor = ref('#section-basic');
const isManualScrolling = ref(false);

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

const getFieldMetaMap = (fields: any[]) => {
  const map: Record<string, { bizField: string, type: string, isComposite: boolean }> = {};
  const traverse = (list: any[]) => {
    list.forEach((f: any) => {
      if (f.id) {
        map[f.id] = { 
          bizField: f.bizField || f.id, 
          type: f.type, 
          isComposite: !!f.isComposite 
        };
      }
      if (f.children && Array.isArray(f.children)) {
        traverse(f.children);
      }
    });
  };
  traverse(fields);
  return map;
};

const handleSubmit = async () => {
  try {
    // 1. Validate All Sections
    const validates: Promise<any>[] = [];
    
    // A. Basic Info
    if (basicInfoRef.value) {
      validates.push(basicInfoRef.value.validate());
    }
    
    // B. Dynamic Forms (Separate refs to handle Vue 3 multi-instance limitation)
    if (dynamicForm_basic.value) validates.push(dynamicForm_basic.value.validate());
    if (dynamicForm_price.value) validates.push(dynamicForm_price.value.validate());
    if (dynamicForm_more.value) validates.push(dynamicForm_more.value.validate());

    const validationResults = await Promise.all(validates);
    const missingFields: string[] = [];

    // C. Manual checks
    if (!context.shopIds || context.shopIds.length < 2) missingFields.push('店铺');
    if (!context.productType) missingFields.push('Amazon 分类');
    if (!formData.media.mainImage) missingFields.push('商品主图');

    // Collect results from sub-components
    validationResults.forEach((labels: string[]) => {
      if (Array.isArray(labels)) {
        missingFields.push(...labels);
      }
    });

    if (missingFields.length > 0) {
      // Remove duplicates and show message
      const uniqueFields = [...new Set(missingFields)];
      ElMessage.warning(`请检查必填项: ${uniqueFields.join(', ')}`);
      return;
    }

    // 2. Prepare Payload
    const metaMap = getFieldMetaMap(schemaFields.value);
    const attributesPool: Record<string, any> = {};
    
    // Core synchronization helper with schema-awareness
    const setAttr = (id: string, val: any) => {
      const meta = metaMap[id];
      if (!meta) {
        attributesPool[id] = val;
        return;
      }

      // Fix double-wrapping: if val is an array but bizField indicates an index (.0)
      let finalVal = val;
      if (Array.isArray(val) && /\.\d+$/.test(meta.bizField)) {
        finalVal = val[0];
      }

      // Check if this is a "True Array" (non-collapsed) or regular array that received a scalar value
      if (meta.type === 'array' && !Array.isArray(finalVal) && finalVal !== null && finalVal !== undefined) {
        attributesPool[meta.bizField] = [{ value: finalVal }];
      } else {
        attributesPool[meta.bizField] = finalVal;
      }
    };

    // A. Dynamic Fields from categorized forms
    Object.keys(formData.attributes).forEach(id => {
      setAttr(id, formData.attributes[id]);
    });

    // B. Manual Basic Info Fields (Explicitly map to SP-API indexed paths)
    // This ensures these fields always produce the required [{ value: "..." }] structure
    if (formData.basic.brand) attributesPool['brand.0.value'] = formData.basic.brand;
    if (formData.basic.title) attributesPool['item_name.0.value'] = formData.basic.title;
    if (formData.basic.description) attributesPool['product_description.0.value'] = formData.basic.description;
    if (formData.basic.searchTerms) attributesPool['generic_keyword.0.value'] = formData.basic.searchTerms;
    
    // Bullet Points (Array of objects)
    const bulletPoints = formData.basic.bulletPoints.filter(b => !!b).map(v => ({ value: v }));
    if (bulletPoints.length > 0) {
      attributesPool['bullet_point'] = bulletPoints;
    }

    console.log('Attributes Pool (Flattened):', attributesPool);
    const nestedAttributes = unflatten(attributesPool);
    console.log('Attributes (Nested):', nestedAttributes);
    
    const payload = {
      platform: 'AMAZON',
      shopId: context.shopIds[context.shopIds.length - 1],
      marketId: 'ATVPDKIKX0DER', // Default US for now or derived from shop
      productType: context.productType,
      sellerSku: formData.basic.sellerSku,
      title: formData.basic.title,
      description: formData.basic.description,
      mainImage: formData.media.mainImage,
      additionalImages: formData.media.additionalImages.filter(u => !!u),
      attributes: nestedAttributes
    };
    
    console.log('Publishing V2 Data (Nested):', payload);
    
    schemaLoading.value = true;
    const res = await ListingApi.publishProductV2(payload);
    ElMessage.success('发布任务已提交');
    
    // 3. Redirect to Listing List
    // router.push('/product/listing');
  } catch (e: any) {
    console.error('Submission failed with error:', e);
    // If it's an API error, it might have a msg or message
    const errorMsg = e.msg || e.message || '未知错误';
    ElMessage.error(`操作失败: ${errorMsg}`);
  } finally {
    schemaLoading.value = false;
  }
};

const goBack = () => router.back();

const handleAnchorChange = (href: string) => {
  if (isManualScrolling.value) return;
  activeAnchor.value = href;
};

const handleAnchorClick = (e: MouseEvent, link: any) => {
  e.preventDefault();
  const href = typeof link === 'string' ? link : link?.href;
  if (!href) return;
  
  activeAnchor.value = href;
  isManualScrolling.value = true;
  
  const targetId = href.split('#')[1];
  if (!targetId) {
    isManualScrolling.value = false;
    return;
  }
  
  const targetEl = document.getElementById(targetId);
  if (targetEl && scrollPaneRef.value) {
    const scrollPaneRect = scrollPaneRef.value.getBoundingClientRect();
    const targetRect = targetEl.getBoundingClientRect();
    // Fine-tuned buffer (-138) to ensure the marker snaps instantly to the target without flickering or missing the trigger zone
    const top = targetRect.top - scrollPaneRect.top + scrollPaneRef.value.scrollTop - 138;
    scrollPaneRef.value.scrollTo({
      top: top,
      behavior: 'smooth'
    });
  }
  
  // Unlock after the smooth scroll completes
  setTimeout(() => {
    isManualScrolling.value = false;
  }, 800);
};

onMounted(() => {
  // Initial loads if needed
});
</script>

<template>
  <div class="amz-listing-form-v2">
    <!-- Left Sticky Sidebar -->
    <div class="sidebar-nav">
      <el-anchor 
        :offset="140" 
        class="custom-anchor" 
        :class="{ 'is-navigating': isManualScrolling }"
        :container="scrollPaneRef" 
        :active-href="activeAnchor"
        @click="handleAnchorClick"
        @change="handleAnchorChange"
      >
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
    <div ref="scrollPaneRef" class="form-scroll-pane" v-loading="schemaLoading">
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
                <BasicInfoSection ref="basicInfoRef" v-model="formData.basic" />
                <!-- Dynamic Basic Fields -->
                <AmzDynamicForm 
                  ref="dynamicForm_basic"
                  :product-type="context.productType"
                  :target-groups="['基本信息']"
                  :initial-data="formData.attributes"
                  :required-only="requiredOnly"
                  :search-query="searchQuery"
                  :exclude-fields="['brand', 'bullet_point', 'generic_keyword', 'item_name', 'product_description']"
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
                  ref="dynamicForm_price"
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
                  ref="dynamicForm_more"
                  :product-type="context.productType"
                  :target-groups="['描述', '更多属性', '技术规格', '其他属性', '关键词']"
                  :initial-data="formData.attributes"
                  :required-only="requiredOnly"
                  :search-query="searchQuery"
                  :exclude-fields="['brand', 'bullet_point', 'generic_keyword', 'item_name', 'product_description']"
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
  position: relative;
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

/* Stabilize the marker during manual navigation to prevent jumping or flickering */
.custom-anchor.is-navigating :deep(.el-anchor__marker),
.custom-anchor :deep(.el-anchor__marker) {
  transition: none !important;
}
.custom-anchor.is-navigating :deep(.el-anchor__marker) {
  visibility: hidden;
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
