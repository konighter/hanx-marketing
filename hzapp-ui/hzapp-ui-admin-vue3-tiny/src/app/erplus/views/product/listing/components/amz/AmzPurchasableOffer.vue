<template>
  <div class="amz-purchasable-offer" v-if="field">
    <!-- Internal Section Title -->
    <div class="component-header">
      <div class="title-wrap">
        <span class="header-title">Purchasable Offer</span>
        <el-tooltip content="可购买的报价信息 (Includes Price, Sale Price, and Limits)" placement="top">
          <el-icon class="title-info"><InfoFilled /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <!-- Primary Metrics (Vertical Layout) -->
    <div class="primary-fields">
      <!-- Currency -->
      <el-form-item label-width="180px" class="compact-item">
        <template #label>
          <div class="label-with-tip">
            <span>货币</span>
            <el-tooltip content="Currency used for all pricing fields" placement="top">
              <el-icon class="label-tip-icon"><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
        </template>
        <el-select v-model="currency" placeholder="USD" @change="updateModel" class="currency-input" filterable>
          <el-option label="USD" value="USD" />
          <el-option label="MXN" value="MXN" />
          <el-option label="EUR" value="EUR" />
          <el-option label="GBP" value="GBP" />
          <el-option label="JPY" value="JPY" />
          <el-option label="CAD" value="CAD" />
        </el-select>
        
      </el-form-item>

      <!-- Your Price -->
      <el-form-item label-width="180px" class="compact-item mt-3">
        <template #label>
          <div class="label-with-tip">
            <span>您的价格</span>
            <el-tooltip content="Your Price (Standard Selling Price)" placement="top">
              <el-icon class="label-tip-icon"><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
        </template>
        <el-input
          v-model="yourPrice"
          placeholder="0.00"
          @change="updateModel"
          class="price-input"
        >
          <template #append>{{ currency }}</template>
        </el-input>
      </el-form-item>

      <!-- Sale Price -->
      <el-form-item label-width="180px" class="compact-item mt-3">
        <template #label>
          <div class="label-with-tip">
            <span>优惠价</span>
            <el-tooltip content="Sale Price (Discounted Price)" placement="top">
              <el-icon class="label-tip-icon"><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
        </template>
        <el-input
          v-model="salePrice"
          placeholder="0.00"
          @change="updateModel"
          class="price-input"
        >
          <template #append>{{ currency }}</template>
        </el-input>
      </el-form-item>

          <!-- Sale Dates Range Selection -->
          <el-form-item label-width="180px" class="compact-item mt-3">
            <template #label>
              <div class="label-with-tip">
                <span>优惠日期</span>
                <el-tooltip content="Sale Start & End Date" placement="top">
                  <el-icon class="label-tip-icon"><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              @change="onDateRangeChange"
            />
          </el-form-item>

      <!-- Concise More Toggle (Moved to bottom left, indented) -->
      <div class="more-link mt-3 align-with-input" @click="showMore = !showMore">
        <span>更多</span>
        <el-icon :class="{ 'is-active': showMore }"><ArrowDown /></el-icon>
      </div>
    </div>

    <!-- Hidden Advanced Pricing Section -->
    <transition name="el-zoom-in-top">
      <div v-show="showMore" class="advanced-section">
        <div class="field-column">
          <!-- MAP Price -->
          <el-form-item label="最低广告价" label-width="180px" class="compact-item">
            <el-input v-model="mapPrice" placeholder="0.00" @change="updateModel" class="price-input">
              <template #append>{{ currency }}</template>
            </el-input>
          </el-form-item>

          <!-- Min Price -->
          <el-form-item label="最低售价" label-width="180px" class="compact-item mt-3">
            <el-input v-model="minPrice" placeholder="0.00" @change="updateModel" class="price-input">
                <template #append>{{ currency }}</template>
            </el-input>
          </el-form-item>

          <!-- Max Price -->
          <el-form-item label="最高售价" label-width="180px" class="compact-item mt-3">
            <el-input v-model="maxPrice" placeholder="0.00" @change="updateModel" class="price-input">
                <template #append>{{ currency }}</template>
            </el-input>
          </el-form-item>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { ArrowDown, InfoFilled } from '@element-plus/icons-vue';
import AttributeRenderer from './AttributeRenderer.vue';

interface Field {
  id: string;
  name?: string;
  title: string;
  children?: any[];
}

const props = defineProps<{
  modelValue: any;
  field: Field;
}>();

const emit = defineEmits(['update:modelValue']);

const showMore = ref(false);

// Refs for mapped fields
const yourPrice = ref<any>('');
const currency = ref<string>('USD');
const salePrice = ref<any>('');
const dateRange = ref<[string, string] | null>(null);

const mapPrice = ref<any>('');
const minPrice = ref<any>('');
const maxPrice = ref<any>('');

// Sync from modelValue
const syncFromModel = () => {
  const data = props.modelValue || {};
  const offer0 = data['0'] || {};

  currency.value = offer0.currency || 'USD';
  
  // Mapping logic following the schema nesting (array -> items.0 -> our_price.0 -> schedule.0)
  yourPrice.value = offer0.our_price?.['0']?.schedule?.['0']?.value_with_tax || '';
  
  const sale = offer0.discounted_price?.['0']?.schedule?.['0'] || {};
  salePrice.value = sale.value_with_tax || '';
  if (sale.start_at && sale.end_at) {
    dateRange.value = [sale.start_at, sale.end_at];
  } else {
    dateRange.value = null;
  }

  mapPrice.value = offer0.map_price?.['0']?.schedule?.['0']?.value_with_tax || '';
  minPrice.value = offer0.minimum_seller_allowed_price?.['0']?.schedule?.['0']?.value_with_tax || '';
  maxPrice.value = offer0.maximum_seller_allowed_price?.['0']?.schedule?.['0']?.value_with_tax || '';
};

watch(() => props.modelValue, syncFromModel, { immediate: true, deep: true });

const onDateRangeChange = (val: any) => {
  updateModel();
};

const updateModel = () => {
  let data = props.modelValue;
  if (!Array.isArray(data)) {
    data = [];
  } else {
    data = JSON.parse(JSON.stringify(data));
  }

  if (!data[0]) data[0] = {};
  const offer0 = data[0];

  // 1. Set Selectors (Backend will handle marketplace_id and audience)
  offer0.currency = currency.value;

  // Helper to ensure nested array structure for Amazon: [ { schedule: [ { ... } ] } ]
  const setNestedPrice = (parent: any, key: string, value: any, extraFields?: any) => {
    if (value === null || value === undefined || value === '') {
      delete parent[key];
      return;
    }
    
    // Structure: array -> object -> schedule -> array -> object
    if (!parent[key] || !Array.isArray(parent[key])) {
      parent[key] = [
        { 
          schedule: [
            { value_with_tax: Number(value) }
          ] 
        }
      ];
    } else {
      const target = parent[key][0].schedule[0];
      target.value_with_tax = Number(value);
    }

    if (extraFields) {
      Object.assign(parent[key][0].schedule[0], extraFields);
    }
  };

  // 2. Set Price Fields using nested structure
  setNestedPrice(offer0, 'our_price', yourPrice.value);

  const saleFields: any = {};
  if (dateRange.value && dateRange.value.length === 2) {
    saleFields.start_at = dateRange.value[0];
    saleFields.end_at = dateRange.value[1];
  }
  setNestedPrice(offer0, 'discounted_price', salePrice.value, saleFields);

  setNestedPrice(offer0, 'map_price', mapPrice.value);
  setNestedPrice(offer0, 'minimum_seller_allowed_price', minPrice.value);
  setNestedPrice(offer0, 'maximum_seller_allowed_price', maxPrice.value);

  emit('update:modelValue', data);
};

// Filtered children for the recursive part (Currently not rendered per user request)
const handledKeys = [
  'our_price', 'currency', 'discounted_price', 
  'map_price', 'minimum_seller_allowed_price', 'maximum_seller_allowed_price'
];
</script>

<style scoped>
.amz-purchasable-offer {
  border-radius: 4px;
  background-color: #f9fafc;
  padding: 8px 0;
}

.component-header {
  margin-bottom: 8px;
  border-bottom: 1px solid #f0f2f5;
  padding: 0 0 8px 0;
  margin-left: 20px;
  margin-right: 20px;
}

.title-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.vertical-bar {
  width: 4px;
  height: 16px;
  background-color: #409eff;
  border-radius: 2px;
}

.header-title {
  font-size: 14px;
  font-weight: 700;
  color: #1a1a1a;
}

.title-info {
  font-size: 14px;
  color: #c0c4cc;
  cursor: help;
}

.primary-fields {
  padding: 16px 20px 0 0;
}

.field-row {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.flex-1 { flex: 1; }
.flex-half { flex: 0.5; }
.flex-2 { flex: 2; }

.compact-item {
  margin-bottom: 0 !important;
}

.compact-item :deep(.el-form-item__label) {
  font-size: 13px;
  color: #606266;
  padding-bottom: 4px;
  line-height: 1.2;
}

.field-desc {
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
}

.range-picker {
  width: 100% !important;
}

.mt-3 { margin-top: 12px; }
.mt-4 { margin-top: 16px; }

.label-with-tip {
  display: flex;
  align-items: center;
  gap: 4px;
}

.label-tip-icon {
  font-size: 14px;
  color: #c0c4cc;
  cursor: help;
}

/** Consolidated styles above **/

.price-input, .currency-input {
  width: 100%;
}

.align-with-input {
  margin-left: 180px;
}

.more-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #409eff;
  cursor: pointer;
  font-size: 13px;
  user-select: none;
  width: fit-content;
}

.more-link:hover {
  text-decoration: underline;
}

.more-link .el-icon {
  transition: transform 0.3s;
}

.more-link .el-icon.is-active {
  transform: rotate(180deg);
}

.advanced-section {
  padding: 0 20px 16px 0;
}

.advanced-section :deep(.el-form-item) {
  margin-bottom: 0px !important;
}
</style>
