<template>
  <div class="amazon-dynamic-form">
    <el-skeleton :loading="loading" animated :rows="10">
      <template #default>
        <el-form ref="formRef" :model="formData" label-width="160px" label-position="right">
          <div v-for="group in groupedFields" :key="group.name" :id="'platform-group-' + group.name" class="attribute-group mb-4">
            <h3 class="group-title border-b pb-2 mb-4 text-primary font-bold flex items-center">
              {{ group.name }}
              <el-tag size="small" type="info" class="ml-2">{{ group.fields.length }} fields</el-tag>
            </h3>

            <el-row :gutter="20">
              <el-col 
                v-for="field in group.fields" 
                :key="field.id" 
                :span="24" 
                v-show="isFieldVisible(field.id)"
                class="root-col"
              >
                <el-form-item 
                  :prop="['attributes', field.id]" 
                  :required="isFieldRequired(field.id)"
                  :rules="isFieldRequired(field.id) ? [{ required: true, message: `Please fill in ${field.title} (${field.id})`, trigger: ['change', 'blur'] }] : []"
                  :class="{ 'top-aligned-label': isTopAligned(field) }"
                >
                  <template #label>
                    <span style="font-weight: 600; text-align: right; display: inline-block;">
                      {{ field.title }}
                      <el-tooltip v-if="field.description" :content="field.description" placement="top">
                        <i class="el-icon-info ml-1 text-gray-400 cursor-help" style="vertical-align: middle; margin-top: -2px;"></i>
                      </el-tooltip>
                    </span>
                  </template>

                  <AmazonAttributeItem
                    v-model="formData.attributes[field.id]"
                    :field="field"
                    @change="handleFieldChange"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- PurchasableOffer 组件：仅在"报价与销售"分组中，其他字段之后渲染 -->
            <AmazonPurchasableOffer
              v-if="group.name === '报价与销售' && purchasableOfferFields.length > 0"
              :fields="purchasableOfferFields"
              v-model="purchasableOfferModel"
              @update:model-value="handlePurchasableOfferUpdate"
            />
          </div>

          <!-- Additional Optional Info Card -->
          <div v-if="allOptionalFields.length > 0" id="platform-group-optional" class="attribute-group mb-4 additional-info-group">
            <h3 class="group-title border-b pb-2 mb-4 text-primary font-bold flex items-center justify-between">
              <span>附加可选属性</span>
              <div class="flex items-center gap-2">
                <el-select 
                  v-model="selectedOptionalFieldId" 
                  filterable 
                  placeholder="搜索并选择附加属性..." 
                  size="small"
                  style="width: 260px;"
                >
                  <el-option
                    v-for="f in availableOptionalFields"
                    :key="f.id"
                    :label="f.title"
                    :value="f.id"
                  />
                </el-select>
                <el-button type="primary" size="small" :disabled="!selectedOptionalFieldId" @click="addOptionalField">
                  Add Attribute
                </el-button>
              </div>
            </h3>

            <div v-if="activeOptionalFields.length > 0" class="optional-fields-container px-4">
              <el-row :gutter="20">
                <el-col 
                  v-for="field in activeOptionalFields" 
                  :key="field.id" 
                  :span="24"
                  v-show="isFieldVisible(field.id)"
                  class="root-col"
                >
                  <div class="flex items-start mb-4 bg-gray-50 p-3 rounded" style="border: 1px solid #ebeef5;">
                    <div class="field-title-col flex-shrink-0" :class="{ 'pt-2': isTopAligned(field), 'flex items-center': !isTopAligned(field) }" style="width: 180px; text-align: right; padding-right: 15px; min-height: 32px;">
                      <span v-if="isFieldRequired(field.id)" class="text-red-500 mr-1">*</span>
                      <span class="font-bold text-gray-700 block truncate" :title="field.title" style="display: inline-block;">{{ field.title }}</span>
                      <el-tooltip v-if="field.description" :content="field.description" placement="top">
                        <i class="el-icon-info ml-1 text-gray-400 cursor-help" style="vertical-align: middle; margin-top: -2px;"></i>
                      </el-tooltip>
                    </div>
                    <div class="field-input-col flex-grow">
                      <el-form-item 
                        :prop="['attributes', field.id]" 
                        :required="isFieldRequired(field.id)"
                        :rules="isFieldRequired(field.id) ? [{ required: true, message: `请填写 ${field.title}`, trigger: ['change', 'blur'] }] : []"
                        label-width="0"
                        style="margin-bottom: 0;"
                        :class="{ 'top-aligned-label': isTopAligned(field) }"
                      >
                        <div class="flex items-start gap-3 w-full">
                          <div class="flex-grow">
                            <AmazonAttributeItem
                              v-model="formData.attributes[field.id]"
                              :field="field"
                              @change="handleFieldChange"
                            />
                          </div>
                          <el-button type="danger" plain size="small" @click="removeOptionalField(field.id)">移除</el-button>
                        </div>
                      </el-form-item>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
            <el-empty v-else description="请添加扩展属性" :image-size="60" />
          </div>
        </el-form>
      </template>
    </el-skeleton>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/config/axios';
import AmazonAttributeItem from './AmazonAttributeItem.vue';
import AmazonPurchasableOffer from './AmazonPurchasableOffer.vue';
import { 
  getUiConfigForField, 
  AmazonGroups,
  AmazonPriceWhitelist,
  AmazonIdentifierWhitelist,
  AmazonPriceBlacklist,
  AmazonPriceOfferKeywords,
  PURCHASABLE_OFFER_PREFIX,
  PURCHASABLE_OFFER_HIDDEN_FIELDS
} from './AmazonUiSchema';
import { evaluateLinkageExpression } from './amzLinkage';

const props = defineProps<{
  shopId: number;
  productType: string;
  marketId?: string;
}>();

const emit = defineEmits(['update:modelValue', 'schema-groups-updated']);
const modelValue = defineModel<Record<string, any>>({ default: () => ({}) });

const loading = ref(false);
const formRef = ref();
const config = ref<any>(null);

const activeOptionalFieldIds = ref<string[]>([]);
const selectedOptionalFieldId = ref('');

// Visibility and Requirement maps for linkage logic
const visibilityMap = reactive<Record<string, boolean>>({});
const requirementMap = reactive<Record<string, boolean>>({});

const formData = reactive({
  attributes: { ...modelValue.value }
});

/** purchasable_offer 子字段单独管理 */
const purchasableOfferFields = ref<any[]>([]);
const purchasableOfferModel = ref<Record<string, any>>({});

/** 处理 PurchasableOffer 组件的 v-model 更新，同步回 formData.attributes */
const handlePurchasableOfferUpdate = (patch: Record<string, any>) => {
  Object.assign(formData.attributes, patch);
  purchasableOfferModel.value = { ...patch };
};

// Watch for external data changes
watch(() => modelValue.value, (newVal) => {
  if (JSON.stringify(newVal) !== JSON.stringify(formData.attributes)) {
    Object.assign(formData.attributes, newVal);
  }
}, { deep: true });

// Sync internal changes to parent
watch(() => formData.attributes, (newVal) => {
  emit('update:modelValue', { ...newVal });
  applyLinkageRules();
}, { deep: true });

const allOptionalFields = computed(() => {
  if (!config.value || !config.value.fields) return [];
  return config.value.fields.filter((f: any) => f.optional);
});

const availableOptionalFields = computed(() => {
  return allOptionalFields.value.filter((f: any) => 
    !activeOptionalFieldIds.value.includes(f.id) && 
    (formData.attributes[f.id] === undefined || formData.attributes[f.id] === '')
  );
});

const activeOptionalFields = computed(() => {
  return allOptionalFields.value.filter((f: any) => {
    if (activeOptionalFieldIds.value.includes(f.id)) return true;
    const val = formData.attributes[f.id];
    if (val === undefined || val === '') return false;
    if (Array.isArray(val) && val.length === 0) return false;
    return true;
  });
});

const groupedFields = computed(() => {
  if (!config.value || !config.value.fields) return [];
  
  // Only group non-optional, non-hidden fields
  const fields = config.value.fields.filter((f: any) => !f.optional && !f.hidden);

  const groupBuckets = AmazonGroups.map(g => ({ name: g.name, fields: [] as any[] }));

  fields.forEach((field: any) => {
    let assigned = false;
    const fieldIdLower = field.id.toLowerCase();
    for (let i = 0; i < AmazonGroups.length - 1; i++) {
        if (AmazonGroups[i].match.some(keyword => fieldIdLower.includes(keyword))) {
        groupBuckets[i].fields.push(field);
        assigned = true;
        break;
      }
    }
    // If no match, put in '其他常规属性' (the last group)
    if (!assigned) {
      groupBuckets[groupBuckets.length - 1].fields.push(field);
    }
  });

  // Filter out empty groups and order required fields first within each group
  return groupBuckets
    .filter(g => g.fields.length > 0)
    .map(g => {
       g.fields.sort((a, b) => {
         const orderA = a.order !== undefined ? a.order : 999;
         const orderB = b.order !== undefined ? b.order : 999;
         if (orderA !== orderB) return orderA - orderB;
         
         const reqA = isFieldRequired(a.id) ? 1 : 0;
         const reqB = isFieldRequired(b.id) ? 1 : 0;
         return reqB - reqA; // Required first
       });
       return g;
    });
});

watch(() => groupedFields.value, (groups) => {
  if (groups && groups.length > 0) {
    const groupItems = groups.map((g: any) => ({
      href: '#platform-group-' + g.name,
      title: g.name
    }));
    if (allOptionalFields.value.length > 0) {
      groupItems.push({ href: '#platform-group-optional', title: '附加可选属性' });
    }
    emit('schema-groups-updated', groupItems);
  }
}, { immediate: true });

const loadConfig = async () => {
  if (!props.productType) return;
  
  loading.value = true;
  try {
    const res: any = await request.get({
      url: `/erplus/amz/listing/schema`,
      params: { productType: props.productType }
    });
    
    // Filter out image, variation, and purchasable_offer.* fields
    if (res && res.fields) {
      // 1. 抽取 purchasable_offer.* 子字段给专用组件
      const poFields: any[] = [];
      const mainFields: any[] = [];

      for (const f of res.fields) {
        const id = f.id.toLowerCase();
        if (id.includes('image') || id.includes('product_image')) continue;
        if (id === 'variation' || id.includes('parentage')) continue;

        // purchasable_offer.* 字段→ 抽离到专用组件
        if (f.id.startsWith(PURCHASABLE_OFFER_PREFIX)) {
          // 过滤掉创建时不需要的子字段
          const isHidden = PURCHASABLE_OFFER_HIDDEN_FIELDS.some(seg => f.id.includes(seg));
          if (!isHidden) {
            poFields.push(f);
          }
          continue;
        }

        // street_date 隐藏，由 merchant_release_date 联动自动同步
        if (id === 'street_date') {
          // 保留字段但标记为隐藏
          f.hidden = true;
        }

        // 价格模块与产品标识模块的白名单逻辑 (确保即使非必填也不被标记为 optional 而隐藏)
        const whitelist = [...AmazonPriceWhitelist, ...AmazonIdentifierWhitelist];
        const isWhitelisted = whitelist.some(wl => id === wl || id.startsWith(wl + '.') || id.endsWith('.' + wl));
        
        if (isWhitelisted) {
          f.optional = false;
        } else if (AmazonPriceOfferKeywords.some(kw => id.includes(kw))) {
          const isBlacklisted = AmazonPriceBlacklist.some(bl => id.includes(bl));
          if (isBlacklisted && !f.required) {
            f.optional = true;
          } else if (!f.required) {
            f.optional = true;
          }
        }

        mainFields.push(f);
      }

      // 2. 应用 UiSchema 覆写
      res.fields = mainFields.map((f: any) => {
        const uiConfig = getUiConfigForField(f.id);
        f.span = 24;
        if (uiConfig.label) f.title = uiConfig.label;
        if (uiConfig.uiWidget) f.uiWidget = uiConfig.uiWidget;
        if (uiConfig.span) f.span = uiConfig.span;
        if (uiConfig.order !== undefined) f.order = uiConfig.order;
        if (uiConfig.tooltip) f.description = uiConfig.tooltip;
        if (uiConfig.placeholder) f.extra = { ...f.extra, placeholder: uiConfig.placeholder };
        return f;
      });

      // 3. 设置 purchasable_offer 字段
      purchasableOfferFields.value = poFields;
      // 初始化 purchasableOffer model 从已有数据
      const poModel: Record<string, any> = {};
      for (const f of poFields) {
        if (formData.attributes[f.id] !== undefined) {
          poModel[f.id] = formData.attributes[f.id];
        }
      }
      purchasableOfferModel.value = poModel;
    }

    config.value = res;
    initFormState();
    applyLinkageRules();
  } catch (err) {
    console.error(err);
    ElMessage.error('加载刊登配置失败');
  } finally {
    loading.value = false;
  }
};

const initFormState = () => {
  if (!config.value?.fields) return;
  
  config.value.fields.forEach((field: any) => {
    // Initialize maps with default values from backend
    visibilityMap[field.id] = !field.hidden;
    requirementMap[field.id] = field.required;
    
    // Set default values if not present in formData (Only for non-optional fields to prevent auto-rendering)
    if (!field.optional) {
      if (formData.attributes[field.id] === undefined && field.defaultValue !== undefined) {
        formData.attributes[field.id] = field.defaultValue;
      }
    }
  });
};

/**
 * Linkage Observer: Simplified frontend rule engine
 */
const applyLinkageRules = () => {
  if (!config.value?.fields) return;

  config.value.fields.forEach((field: any) => {
    if (!field.linkageRules || field.linkageRules.length === 0) return;

    let isVisible = !field.hidden;
    let isReq = field.required;

    field.linkageRules.forEach((rule: any) => {
      const match = evaluateLinkageExpression(rule.condition, formData.attributes);
      if (match) {
        if (rule.type === 'visibility' && rule.action === 'show') isVisible = true;
        if (rule.type === 'visibility' && rule.action === 'hide') isVisible = false;
        if (rule.type === 'requirement' && rule.action === 'required') isReq = true;
      }
    });

    visibilityMap[field.id] = isVisible;
    requirementMap[field.id] = isReq;
  });

  // Custom Linkage for FBA vs FBM (Focus on Approach B)
  // Support both flat and nested keys in formData.attributes
  const FULFILL_FID = 'fulfillment_channel_code';
  const channelCodeKey = Object.keys(formData.attributes).find(k => 
    k === FULFILL_FID || k.startsWith(FULFILL_FID + '.') || k.endsWith('.' + FULFILL_FID)
  );
  const channelCode = channelCodeKey ? formData.attributes[channelCodeKey] : undefined;
  
  if (channelCode !== undefined && channelCode !== '') {
    let valStr = '';
    if (Array.isArray(channelCode) && channelCode.length > 0) {
      valStr = channelCode[0]?.value || channelCode[0] || '';
    } else if (typeof channelCode === 'object' && channelCode !== null) {
      valStr = (channelCode as any).value || '';
    } else {
      valStr = String(channelCode);
    }

    // Hide FBM-specific fields if FBA is selected (fulfillment_channel_code != DEFAULT)
    const isFBA = valStr && valStr !== 'DEFAULT'; 
    const fbmFields = [
      'merchant_shipping_group',
      'is_inventory_available', 
      'quantity', 
      'lead_time_to_ship_max_days', 
      'restock_date'
    ];

    const allFieldIds = config.value.fields.map((f: any) => f.id);
    fbmFields.forEach(fid => {
      // Find all keys in config fields that match these FBM field names
      const actualIds = allFieldIds.filter(id => 
        id === fid || id.startsWith(fid + '.') || id.endsWith('.' + fid)
      );
      actualIds.forEach(id => {
        visibilityMap[id] = !isFBA;
        requirementMap[id] = !isFBA; // FBA mode doesn't require these inventory fields
      });
    });
  }

  // Custom Linkage for Product Identifier Exemption
  const ID_EXEMPTION_FID = 'supplier_declared_has_product_identifier_exemption';
  const exemptionKey = Object.keys(formData.attributes).find(k => 
    k === ID_EXEMPTION_FID || k.startsWith(ID_EXEMPTION_FID + '.') || k.endsWith('.' + ID_EXEMPTION_FID)
  );
  const exemptionVal = exemptionKey ? formData.attributes[exemptionKey] : undefined;
  
  if (exemptionVal !== undefined) {
    let hasExemption = false;
    // Common SP-API formats: boolean, string "true", or [{value: true}]
    if (Array.isArray(exemptionVal) && exemptionVal.length > 0) {
      hasExemption = exemptionVal[0]?.value === true || exemptionVal[0] === true || exemptionVal[0] === 'true';
    } else if (typeof exemptionVal === 'object' && exemptionVal !== null) {
      hasExemption = (exemptionVal as any).value === true || (exemptionVal as any).value === 'true';
    } else {
      hasExemption = exemptionVal === true || exemptionVal === 'true';
    }

    // Rule: If exempt (true), hide externally_assigned_product_identifier
    // Show only when exemption is false
    const hideIdentifier = hasExemption;
    const identifierFields = ['externally_assigned_product_identifier'];
    const allFieldIds = config.value.fields.map((f: any) => f.id);
    
    identifierFields.forEach(fid => {
      const actualIds = allFieldIds.filter(id => 
        id === fid || id.startsWith(fid + '.') || id.endsWith('.' + fid)
      );
      actualIds.forEach(id => {
        visibilityMap[id] = !hideIdentifier;
      });
    });
  }
};

const evaluateCondition = (conditionStr: string) => {
  return evaluateLinkageExpression(conditionStr, formData.attributes);
};

const isFieldVisible = (fieldId: string) => visibilityMap[fieldId] !== false;
const isFieldRequired = (fieldId: string) => requirementMap[fieldId] === true;

const isTopAligned = (field: any) => {
  if (field.uiWidget === 'bullet_point_editor') return true;
  if (field.extra?.multiline) return true;
  if (field.id === 'description') return true;
  return false;
};

const handleFieldChange = () => {
  // Changes are handled by the watcher on formData.attributes
};

/**
 * 日期联动逻辑
 * merchant_release_date 改变 → 自动同步 street_date + purchasable_offer.0.start_at
 * product_site_launch_date 改变 → 如果 > merchant_release_date，自动调整 merchant_release_date
 */
watch(() => formData.attributes['merchant_release_date'], (newVal) => {
  if (newVal) {
    // 同步 street_date
    formData.attributes['street_date'] = newVal;
    // 同步 purchasable_offer.0.start_at (如果字段存在)
    const startAtKey = Object.keys(formData.attributes).find(k => k.startsWith('purchasable_offer') && k.endsWith('start_at') && !k.includes('discounted_price'));
    if (startAtKey) {
      formData.attributes[startAtKey] = newVal;
    }
  }
}, { deep: true });

watch(() => formData.attributes['product_site_launch_date'], (newVal) => {
  if (newVal && formData.attributes['merchant_release_date']) {
    // 如果上架日期 > 发售日期，自动将发售日期调整为上架日期
    if (newVal > formData.attributes['merchant_release_date']) {
      formData.attributes['merchant_release_date'] = newVal;
    }
  }
}, { deep: true });

const addOptionalField = () => {
  if (selectedOptionalFieldId.value && !activeOptionalFieldIds.value.includes(selectedOptionalFieldId.value)) {
    activeOptionalFieldIds.value.push(selectedOptionalFieldId.value);
    selectedOptionalFieldId.value = '';
  }
};

const removeOptionalField = (fieldId: string) => {
  activeOptionalFieldIds.value = activeOptionalFieldIds.value.filter(id => id !== fieldId);
  delete formData.attributes[fieldId];
};

const validate = async () => {
  return await formRef.value?.validate();
};

/**
 * Unflattens a flat object into a nested object based on dot notation keys
 */
const unflatten = (data: Record<string, any>) => {
  const result: Record<string, any> = {};
  for (const key in data) {
    const value = data[key];
    if (value === undefined || value === null || value === '') continue;

    const parts = key.split('.');
    let current = result;
    for (let i = 0; i < parts.length; i++) {
      const part = parts[i];
      const isLast = i === parts.length - 1;
      
      if (isLast) {
        current[part] = value;
      } else {
        const nextPart = parts[i + 1];
        const isNextDigit = /^\d+$/.test(nextPart);
        
        if (isNextDigit) {
          current[part] = current[part] || [];
        } else {
          current[part] = current[part] || {};
        }
        current = current[part];
      }
    }
  }
  return result;
};

/**
 * Expose save method that returns unflattened data
 */
const getSubmitData = async () => {
  const isValid = await validate();
  if (!isValid) throw new Error('表单验证未通过');
  return unflatten(formData.attributes);
};

defineExpose({ validate, getSubmitData });

onMounted(() => {
  loadConfig();
});

watch(() => props.productType, (val) => {
  if (val) loadConfig();
});
</script>

<style scoped>
.amazon-dynamic-form {
  padding: 20px;
}
.attribute-group {
  margin-bottom: 24px;
  background: #fff;
}
.attribute-tip {
  color: #e6a23c;
  background: #fdf6ec;
  padding: 4px 8px;
  border-radius: 4px;
}

/* Label Alignment */
.amazon-dynamic-form :deep(.root-col) {
  width: 100% !important;
  flex: 0 0 100% !important;
  max-width: 100% !important;
}

.amazon-dynamic-form :deep(.el-form-item__label) {
  align-items: center; 
  line-height: 1.2;
}

:deep(.top-aligned-label) .el-form-item__label {
  align-items: flex-start !important;
  padding-top: 8px; /* 补偿文本框顶部留白 */
}
</style>
