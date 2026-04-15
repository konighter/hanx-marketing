<template>
  <div class="purchasable-offer-card">
    <!-- 核心: 售价 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="售价" :required="true" :rules="[{ required: true, message: '请输入售价', trigger: 'blur' }]">
          <el-input-number
            v-model="offerData.ourPrice"
            :precision="2"
            :min="0"
            :controls="true"
            style="width: 100%"
            placeholder="请输入售价"
            @change="emitUpdate"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="货币">
          <el-select 
            v-model="offerData.currency" 
            placeholder="选择货币" 
            style="width: 100%"
            @change="emitUpdate"
          >
            <el-option
              v-for="opt in currencyOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 促销价设置 (可折叠) -->
    <el-collapse v-model="activeCollapse" class="offer-collapse">
      <el-collapse-item title="促销价设置" name="discount">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="促销价">
              <el-input-number
                v-model="offerData.discountedPrice"
                :precision="2"
                :min="0"
                style="width: 100%"
                placeholder="促销价"
                @change="emitUpdate"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="促销开始日期">
              <el-date-picker
                v-model="offerData.discountStartAt"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                @change="emitUpdate"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="促销截止日期">
              <el-date-picker
                v-model="offerData.discountEndAt"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                @change="emitUpdate"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-collapse-item>

      <el-collapse-item title="价格保护" name="priceProtection">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="最低广告价">
              <el-input-number
                v-model="offerData.mapPrice"
                :precision="2"
                :min="0"
                style="width: 100%"
                placeholder="MAP 价格"
                @change="emitUpdate"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="卖方最低价">
              <el-input-number
                v-model="offerData.minSellerPrice"
                :precision="2"
                :min="0"
                style="width: 100%"
                placeholder="最低允许价格"
                @change="emitUpdate"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="卖方最高价">
              <el-input-number
                v-model="offerData.maxSellerPrice"
                :precision="2"
                :min="0"
                style="width: 100%"
                placeholder="最高允许价格"
                @change="emitUpdate"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script lang="ts" setup>
import { reactive, watch, ref, computed } from 'vue';

/**
 * 打平字段 ID → 内部字段映射
 *
 * purchasable_offer.0.our_price.0.schedule.0.value_with_tax  → ourPrice
 * purchasable_offer.0.currency                                → currency
 * purchasable_offer.0.discounted_price.0.schedule.0.value_with_tax → discountedPrice
 * purchasable_offer.0.discounted_price.0.schedule.0.start_at  → discountStartAt
 * purchasable_offer.0.discounted_price.0.schedule.0.end_at    → discountEndAt
 * purchasable_offer.0.map_price.0.schedule.0.value_with_tax   → mapPrice
 * purchasable_offer.0.minimum_seller_allowed_price.0.schedule.0.value_with_tax → minSellerPrice
 * purchasable_offer.0.maximum_seller_allowed_price.0.schedule.0.value_with_tax → maxSellerPrice
 */

/** 打平字段 → 内部 key 的映射表 */
const FIELD_MAP: Record<string, string> = {
  'our_price': 'ourPrice',
  'currency': 'currency',
  'discounted_price': 'discountedPrice',
  'map_price': 'mapPrice',
  'minimum_seller_allowed_price': 'minSellerPrice',
  'maximum_seller_allowed_price': 'maxSellerPrice',
};

/** 通过检查打平字段 ID 中的关键段来识别内部 key */
function resolveInternalKey(flatFieldId: string): string | null {
  const lower = flatFieldId.toLowerCase();
  
  // 促销价的日期字段需要特殊处理
  if (lower.includes('discounted_price') && lower.endsWith('start_at')) return 'discountStartAt';
  if (lower.includes('discounted_price') && lower.endsWith('end_at')) return 'discountEndAt';
  // 促销价的价格字段
  if (lower.includes('discounted_price') && lower.includes('value_with_tax')) return 'discountedPrice';
  
  // 其他带 value_with_tax 的价格字段
  if (lower.includes('value_with_tax')) {
    for (const [segment, internalKey] of Object.entries(FIELD_MAP)) {
      if (lower.includes(segment)) return internalKey;
    }
  }
  
  // currency
  if (lower.includes('currency') && !lower.includes('list_price')) return 'currency';
  
  return null;
}

/** 通过内部 key 反查原始打平字段 ID */
function findFlatFieldId(internalKey: string, fields: any[]): string | undefined {
  for (const f of fields) {
    if (resolveInternalKey(f.id) === internalKey) return f.id;
  }
  return undefined;
}

const props = defineProps<{
  /** 所有 purchasable_offer.* 打平字段 */
  fields: any[];
  modelValue: Record<string, any>;
}>();

const emit = defineEmits(['update:modelValue']);

const activeCollapse = ref<string[]>([]);

const offerData = reactive({
  ourPrice: undefined as number | undefined,
  currency: '' as string,
  discountedPrice: undefined as number | undefined,
  discountStartAt: '' as string,
  discountEndAt: '' as string,
  mapPrice: undefined as number | undefined,
  minSellerPrice: undefined as number | undefined,
  maxSellerPrice: undefined as number | undefined,
});

const currencyField = computed(() => props.fields.find(f => resolveInternalKey(f.id) === 'currency'));

const currencyOptions = computed(() => {
  if (currencyField.value?.options?.length > 0) {
    return currencyField.value.options;
  }
  // 兜底常用货币
  return [
    { label: 'USD - 美元', value: 'USD' },
    { label: 'MXN - 墨西哥比索', value: 'MXN' },
    { label: 'EUR - 欧元', value: 'EUR' },
    { label: 'GBP - 英镑', value: 'GBP' },
    { label: 'CAD - 加拿大元', value: 'CAD' },
    { label: 'JPY - 日元', value: 'JPY' },
    { label: 'AUD - 澳大利亚元', value: 'AUD' }
  ];
});

/** 从 modelValue (打平字段) 同步到内部 reactive */
const syncFromModel = () => {
  for (const field of props.fields) {
    const key = resolveInternalKey(field.id);
    if (key && props.modelValue[field.id] !== undefined) {
      (offerData as any)[key] = props.modelValue[field.id];
    } else if (key === 'currency' && !offerData.currency && field.defaultValue) {
      // 初始化默认货币
      offerData.currency = field.defaultValue;
    }
  }
};

/** 从内部 reactive 同步到 modelValue (打平字段) */
const emitUpdate = () => {
  const patch: Record<string, any> = {};
  
  const keys: (keyof typeof offerData)[] = [
    'ourPrice', 'currency', 'discountedPrice', 
    'discountStartAt', 'discountEndAt',
    'mapPrice', 'minSellerPrice', 'maxSellerPrice'
  ];
  
  for (const internalKey of keys) {
    const flatId = findFlatFieldId(internalKey, props.fields);
    if (flatId) {
      const val = offerData[internalKey];
      if (val !== undefined && val !== '' && val !== null) {
        patch[flatId] = val;
      }
    }
  }
  
  emit('update:modelValue', patch);
};

// 初始化 & 监听外部变化
watch(() => props.modelValue, () => syncFromModel(), { immediate: true, deep: true });
watch(() => props.fields, () => syncFromModel(), { immediate: true });
</script>

<style scoped>
.purchasable-offer-card {
  padding: 0;
  margin-bottom: 16px;
}

.offer-collapse {
  border: none;
  margin-top: 8px;
}

.offer-collapse :deep(.el-collapse-item__header) {
  font-weight: 600;
  color: #606266;
  background: transparent;
  border-bottom: 1px dashed #dcdfe6;
  height: 36px;
  line-height: 36px;
  font-size: 13px;
}

.offer-collapse :deep(.el-collapse-item__wrap) {
  background: transparent;
  border: none;
}

.offer-collapse :deep(.el-collapse-item__content) {
  padding: 16px 0 0;
}
</style>
