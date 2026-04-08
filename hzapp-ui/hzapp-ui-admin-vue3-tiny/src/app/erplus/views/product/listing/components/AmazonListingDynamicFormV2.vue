<template>
  <div class="amazon-dynamic-form-v2">
    <el-skeleton :loading="loading" animated :rows="10">
      <template #default>
        <el-form ref="formRef" :model="formData" label-position="top" class="compact-form">
          <!-- 3-Tier Grouping Implementation -->
          <div v-for="group in finalGroups" :key="group.name" :id="'platform-group-' + group.name" class="attribute-group mb-4">
            <h3 class="group-title sticky top-0 z-10 bg-white border-b pb-1 mb-2 text-primary font-bold flex items-center justify-between">
              <div class="flex items-center">
                {{ group.name }}
                <el-tag size="small" type="info" effect="plain" class="ml-2 font-normal">{{ group.fields.length }} 字段</el-tag>
                <el-tag v-if="group.type === 'optional'" size="small" type="warning" effect="light" class="ml-2 font-normal">选填</el-tag>
              </div>

              <!-- Add Attribute Selector for Optional Group -->
              <div v-if="group.type === 'optional'" class="flex items-center">
                <el-select
                  v-model="currentSelectedAttr"
                  placeholder="搜索并添加属性..."
                  size="small"
                  filterable
                  clearable
                  class="w-[240px]"
                  @change="handleAddOptionalField"
                >
                  <el-option
                    v-for="f in getAvailableOptionalFields(group)"
                    :key="f.id"
                    :label="f.title"
                    :value="f.id"
                  />
                </el-select>
              </div>
            </h3>

            <el-row :gutter="12">
              <el-col 
                v-for="field in group.displayFields" 
                :key="field.id" 
                :span="field.span || 12" 
                v-show="isFieldVisible(field.id) && !field.id.startsWith('purchasable_offer.')"
              >
                <el-form-item 
                  :prop="['attributes', field.id]" 
                  :required="isFieldRequired(field.id)"
                  :rules="isFieldRequired(field.id) ? [{ required: true, message: `请填写 ${field.title}`, trigger: ['change', 'blur'] }] : []"
                  :class="{ 'is-optional-item': group.type === 'optional' }"
                >
                  <template #label>
                    <span style="font-weight: 600;">
                      {{ field.title }}
                      <el-tooltip v-if="field.description" :content="field.description" placement="top">
                        <i class="el-icon-info ml-1 text-gray-400 cursor-help" style="vertical-align: middle; margin-top: -2px;"></i>
                      </el-tooltip>
                    </span>
                  </template>

                  <div class="flex items-center w-full">
                    <div class="flex-1 min-w-0">
                      <AmazonAttributeItem
                        v-model="formData.attributes[field.id]"
                        :field="field"
                        @change="handleFieldChange"
                      />
                    </div>
                    <el-link 
                      v-if="group.type === 'optional'"
                      type="info" 
                      :underline="false"
                      class="ml-3 remove-btn"
                      @click.stop="handleRemoveOptionalField(field.id)"
                    >
                      <i class="el-icon-close"></i> 移除
                    </el-link>
                  </div>
                  
                  <div v-if="field.linkageRules?.length > 0" class="attribute-tip text-xs text-orange-500 mt-1">
                    <i class="el-icon-connection"></i> 关联属性：受联动规则约束
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- Special handling for PurchasableOffer in '报价与销售' -->
            <AmazonPurchasableOffer
              v-if="group.name === '报价与销售' && purchasableOfferFields.length > 0"
              :fields="purchasableOfferFields"
              v-model="purchasableOfferModel"
              @update:model-value="handlePurchasableOfferUpdate"
            />
          </div>

          <!-- If no fields in a group (e.g. all optional ones were manually removed if we had that feature) -->
          <el-empty v-if="finalGroups.length === 0" description="暂无可用属性" />
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
import { getUiConfigForField } from './AmazonUiSchema';
import { 
  AmazonFixedGroups, 
  GROUP_NAME_REMAINING_REQUIRED, 
  GROUP_NAME_OPTIONAL 
} from './AmazonGroupingConfig';

const props = defineProps<{
  shopId: number;
  productType: string;
  marketId?: string;
}>();

const emit = defineEmits(['update:modelValue', 'schema-groups-updated', 'change']);
const modelValue = defineModel<Record<string, any>>({ default: () => ({}) });

const loading = ref(false);
const formRef = ref();
const rawSchema = ref<any>(null);

// Visibility and Requirement maps for linkage logic
const visibilityMap = reactive<Record<string, boolean>>({});
const requirementMap = reactive<Record<string, boolean>>({});

const formData = reactive({
  attributes: { ...modelValue.value }
});

// Track which optional fields are manually added to the view
const addedOptionalFields = ref<string[]>([]);
const currentSelectedAttr = ref('');

// Initialize addedOptionalFields with fields that already have non-empty values
watch(() => formData.attributes, (newAttrs) => {
  if (!rawSchema.value?.fields) return;
  rawSchema.value.fields.forEach((f: any) => {
    if (newAttrs[f.id] !== undefined && newAttrs[f.id] !== '' && newAttrs[f.id] !== null) {
      if (!isFieldRequired(f.id) && !addedOptionalFields.value.includes(f.id)) {
        addedOptionalFields.value.push(f.id);
      }
    }
  });
}, { immediate: true, deep: true });

const handleAddOptionalField = (fieldId: string) => {
  if (!fieldId) return;
  if (!addedOptionalFields.value.includes(fieldId)) {
    addedOptionalFields.value.push(fieldId);
  }
  // Clear selection after adding
  currentSelectedAttr.value = '';
};

const handleRemoveOptionalField = (fieldId: string) => {
  addedOptionalFields.value = addedOptionalFields.value.filter(id => id !== fieldId);
  delete formData.attributes[fieldId];
  handleFieldChange();
};

const getAvailableOptionalFields = (group: any) => {
  if (group.type !== 'optional') return [];
  return group.fields.filter((f: any) => !addedOptionalFields.value.includes(f.id));
};

const purchasableOfferFields = ref<any[]>([]);
const purchasableOfferModel = ref<Record<string, any>>({});

const handlePurchasableOfferUpdate = (patch: Record<string, any>) => {
  Object.assign(formData.attributes, patch);
  purchasableOfferModel.value = { ...patch };
};

// 3-Tier Grouping Algorithm (Re-implemented for stability)
const finalGroups = computed(() => {
  if (!rawSchema.value || !rawSchema.value.fields) return [];

  const allFields = [...rawSchema.value.fields].filter(f => !f.hidden);
  const assignedFieldIds = new Set<string>();
  
  // 1. Prepare Tier 1: Fixed Groups
  const tier1Groups = AmazonFixedGroups.map(g => {
    const matchedFields = allFields.filter(f => {
      // Rule: Capture ALL matching fields for this specific business module
      const isMatched = g.match.some(m => f.id === m || f.id.startsWith(m + '.'));
      if (isMatched && !assignedFieldIds.has(f.id)) {
        assignedFieldIds.add(f.id);
        return true;
      }
      return false;
    });

    return {
      name: g.name,
      type: 'fixed' as const,
      fields: matchedFields
    };
  });

  // 2. Prepare Tier 2: Remaining required fields
  const tier2Fields = allFields.filter(f => {
    if (assignedFieldIds.has(f.id)) return false;
    if (isFieldRequired(f.id)) {
      assignedFieldIds.add(f.id);
      return true;
    }
    return false;
  });

  // 3. Prepare Tier 3: Optional fields
  const tier3Fields = allFields.filter(f => !assignedFieldIds.has(f.id));

  // Determine final visibility of groups
  const resultGroups: any[] = [];

  // Tier 1 logic: Force key groups if they have fields OR special components
  tier1Groups.forEach(g => {
    if (g.name === '报价与销售') {
      // 强制标记价格前缀字段，防止它们流向 Tier 3
      allFields.forEach(f => {
        if (f.id.startsWith('purchasable_offer.')) assignedFieldIds.add(f.id);
      });
      if (purchasableOfferFields.value.length > 0 || g.fields.length > 0) {
        resultGroups.push({ ...g, displayFields: g.fields });
      }
    } else if (g.fields.length > 0) {
      resultGroups.push({ ...g, displayFields: g.fields });
    }
  });

  // Adding Tier 2
  if (tier2Fields.length > 0) {
    resultGroups.push({
      name: GROUP_NAME_REMAINING_REQUIRED,
      type: 'required',
      fields: tier2Fields,
      displayFields: tier2Fields
    });
  }

  // Adding Tier 3
  if (tier3Fields.length > 0) {
    resultGroups.push({
      name: GROUP_NAME_OPTIONAL,
      type: 'optional',
      fields: tier3Fields,
      // IMPORTANT: Tier 3 displays only added fields
      displayFields: tier3Fields.filter(f => addedOptionalFields.value.includes(f.id))
    });
  }

  return resultGroups;
});

// Sync groups to parent for anchor navigation
watch(() => finalGroups.value, (groups) => {
  if (groups && groups.length > 0) {
    const groupItems = groups.map((g: any) => ({
      href: '#platform-group-' + g.name,
      title: g.name
    }));
    emit('schema-groups-updated', groupItems);
  }
}, { immediate: true });

// Load and process schema
const loadSchema = async () => {
  if (!props.productType) return;
  
  loading.value = true;
  try {
    const res: any = await request.get({
      url: `/erplus/amz/listing/schema`,
      params: { productType: props.productType }
    });
    
    if (res && res.fields) {
      // Pre-process fields (UI overrides)
      res.fields = res.fields.map((f: any) => {
        const uiConfig = getUiConfigForField(f.id);
        if (uiConfig.label) f.title = uiConfig.label;
        if (uiConfig.uiWidget) f.uiWidget = uiConfig.uiWidget;
        if (uiConfig.span) f.span = uiConfig.span;
        if (uiConfig.order !== undefined) f.order = uiConfig.order;
        if (uiConfig.tooltip) f.description = uiConfig.tooltip;
        if (uiConfig.placeholder) f.extra = { ...f.extra, placeholder: uiConfig.placeholder };
        return f;
      });

      // Extract special fields
      purchasableOfferFields.value = res.fields.filter((f: any) => f.id.startsWith('purchasable_offer.'));
      // Remove them from general rendering but keep in schema for logic
      // Actually AmazonListingDynamicForm hides them using prefix check, we'll do the same in filter
    }

    rawSchema.value = res;
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
  if (!rawSchema.value?.fields) return;
  
  rawSchema.value.fields.forEach((field: any) => {
    visibilityMap[field.id] = !field.hidden;
    requirementMap[field.id] = field.required;
    
    if (formData.attributes[field.id] === undefined && field.defaultValue !== undefined) {
      formData.attributes[field.id] = field.defaultValue;
    }
  });

  // Init purchasableOfferModel
  const poModel: Record<string, any> = {};
  purchasableOfferFields.value.forEach(f => {
    if (formData.attributes[f.id] !== undefined) {
      poModel[f.id] = formData.attributes[f.id];
    }
  });
  purchasableOfferModel.value = poModel;
};

// ... Linkage Logic (Evaluate, etc) - Copying from V1 for now ...
const applyLinkageRules = () => {
  if (!rawSchema.value?.fields) return;

  rawSchema.value.fields.forEach((field: any) => {
    if (!field.linkageRules || field.linkageRules.length === 0) return;

    let isVisible = !field.hidden;
    let isReq = field.required;

    field.linkageRules.forEach((rule: any) => {
      const match = evaluateCondition(rule.condition);
      if (match) {
        if (rule.type === 'visibility' && rule.action === 'show') isVisible = true;
        if (rule.type === 'visibility' && rule.action === 'hide') isVisible = false;
        if (rule.type === 'requirement' && rule.action === 'required') isReq = true;
      }
    });

    visibilityMap[field.id] = isVisible;
    requirementMap[field.id] = isReq;
  });
};

const evaluateCondition = (conditionStr: string) => {
  const parts = conditionStr.split(' == ');
  if (parts.length !== 2) return false;
  
  const fieldId = parts[0].trim();
  const targetVal = parts[1].replace(/'/g, '').trim();
  
  let currentVal = formData.attributes[fieldId];
  if (currentVal === undefined) {
    const possibleKeys = Object.keys(formData.attributes).filter(k => k === fieldId || k.startsWith(fieldId + '.'));
    if (possibleKeys.length > 0) currentVal = formData.attributes[possibleKeys[0]];
  }
  
  const extractValue = (val: any) => {
    if (val === undefined || val === null) return val;
    if (Array.isArray(val) && val.length > 0 && val[0].value !== undefined) return val[0].value;
    if (typeof val === 'object' && val.value !== undefined) return val.value;
    return val;
  };
  
  const actualVal = extractValue(currentVal);
  return String(actualVal) === targetVal;
};

const isFieldVisible = (fieldId: string) => visibilityMap[fieldId] !== false;
const isFieldRequired = (fieldId: string) => requirementMap[fieldId] === true;

const handleFieldChange = () => {
    emit('change', formData.attributes);
};

onMounted(() => {
  loadSchema();
});

watch(() => props.productType, () => {
  loadSchema();
});

defineExpose({
  getSubmitData: () => formData.attributes,
  validate: () => formRef.value?.validate()
});
</script>

<style lang="scss" scoped>
.amazon-dynamic-form-v2 {
  padding: 0;

  // === 紧凑表单：缩减垂直间距 ===
  .compact-form {
    :deep(.el-form-item) {
      margin-bottom: 10px;
    }

    :deep(.el-form-item__label) {
      padding-bottom: 2px !important;
      font-size: 13px;
      color: #606266;
      font-weight: 600;
    }
  }

  // === 分组区块 ===
  .attribute-group {
    padding: 0 12px;
    margin-bottom: 16px;
  }

  .group-title {
    font-size: 14px;
    letter-spacing: 0.3px;
    margin-bottom: 8px;
    color: #303133;
    font-weight: bold;
  }

  // === 强制所有表单控件填满列宽 ===
  :deep(.el-form-item__content) {
    display: flex;
    flex-wrap: wrap;
    width: 100%;
  }

  :deep(.el-input),
  :deep(.el-select),
  :deep(.el-cascader),
  :deep(.el-textarea),
  :deep(.el-date-editor) {
    width: 100% !important;
  }

  :deep(.el-input-number) {
    width: 100% !important;

    .el-input {
      width: 100% !important;
    }
  }

  :deep(.amazon-attribute-item) {
    width: 100%;
  }

  // === 选填项样式 ===
  .is-optional-item {
    padding: 8px;
    border-radius: 4px;
    border: 1px solid transparent;

    &:hover {
      background: #f5f7fa;
    }
  }

  // === 移除按钮：默认红色、靠右 ===
  .remove-btn {
    font-size: 13px;
    font-weight: 500;
    color: var(--el-color-danger);
    opacity: 0.85;
    white-space: nowrap;
    flex-shrink: 0;

    &:hover {
      color: #f56c6c !important;
      opacity: 1;
    }

    i {
      font-size: 14px;
      vertical-align: middle;
      margin-top: -2px;
    }
  }

  // === 联动提示 ===
  .attribute-tip {
    font-size: 11px;
    color: #e6a23c;
    background: #fdf6ec;
    padding: 4px 8px;
    border-radius: 4px;
    display: inline-flex;
    align-items: center;
    margin-top: 4px;
    border: 1px solid #faecd8;
  }
}
</style>
