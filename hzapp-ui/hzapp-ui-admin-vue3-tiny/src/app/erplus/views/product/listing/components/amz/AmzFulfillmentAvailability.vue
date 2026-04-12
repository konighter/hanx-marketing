<template>
  <div class="amz-fulfillment-availability" v-if="field">
    <!-- Internal Section Title -->
    <div class="component-header">
      <div class="title-wrap">
        <span class="header-title">Fulfillment Availability</span>
        <el-tooltip content="配送与库存设置 (FBA/FBM)" placement="top">
          <el-icon class="title-info"><InfoFilled /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <!-- Fulfillment Fields -->
    <div class="primary-fields">
      <!-- Fulfillment Channel Switcher -->
      <el-form-item label-width="180px" class="compact-item">
        <template #label>
          <div class="label-with-tip">
            <span>配送方式</span>
            <el-tooltip content="Choose between Amazon Fulfillment (FBA) or Merchant Fulfillment (FBM)" placement="top">
              <el-icon class="label-tip-icon"><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
        </template>
        <el-radio-group v-model="channelCode" @change="updateModel">
          <el-radio label="DEFAULT">FBM</el-radio>
          <el-radio label="FBA">FBA</el-radio>
        </el-radio-group>
      </el-form-item>

      <transition name="el-zoom-in-top">
        <div v-show="channelCode === 'DEFAULT'" class="merchant-fields mt-3">
          <!-- Quantity -->
          <el-form-item label-width="180px" class="compact-item">
            <template #label>
              <div class="label-with-tip">
                <span>数量</span>
                <el-tooltip content="Enter the quantity of the item available for sale (FBM only)" placement="top">
                  <el-icon class="label-tip-icon"><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <el-input-number 
              v-model="quantity" 
              :min="0" 
              @change="updateModel"
              class="input-medium"
              placeholder="0"
            />
          </el-form-item>

          <!-- Handling Time -->
          <el-form-item label-width="180px" class="compact-item mt-3">
            <template #label>
              <div class="label-with-tip">
                <span>备货时间</span>
                <el-tooltip content="Time (in days) between receiving an order and shipping (FBM only)" placement="top">
                  <el-icon class="label-tip-icon"><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <el-input v-model="handlingTime" placeholder="Days" @change="updateModel" class="input-medium">
              <template #append>天</template>
            </el-input>
          </el-form-item>

          <!-- Restock Date -->
          <el-form-item label-width="180px" label="补货日期" class="compact-item mt-3">
            <el-date-picker
              v-model="restockDate"
              type="date"
              placeholder="Select date"
              value-format="YYYY-MM-DD"
              @change="updateModel"
              class="input-medium"
            />
          </el-form-item>

          <!-- Inventory Always Available -->
          <el-form-item label-width="180px" class="compact-item mt-3">
            <template #label>
              <div class="label-with-tip">
                <span>库存始终可用</span>
                <el-tooltip content="Alternative to quantity that allows inventory to never deplete" placement="top">
                  <el-icon class="label-tip-icon"><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <el-select v-model="alwaysAvailable" @change="updateModel" class="input-medium">
              <el-option label="Disabled" value="false" />
              <el-option label="Enabled" value="true" />
            </el-select>
          </el-form-item>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { InfoFilled } from '@element-plus/icons-vue';

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

// Mapped state
const channelCode = ref<string>('DEFAULT');
const quantity = ref<number>(0);
const handlingTime = ref<string>('');
const restockDate = ref<string>('');
const alwaysAvailable = ref<string>('false');

const syncFromModel = () => {
  const data = props.modelValue || {};
  const item0 = data['0'] || {};

  channelCode.value = item0.fulfillment_channel_code || 'DEFAULT';
  quantity.value = item0.quantity || 0;
  handlingTime.value = item0.lead_time_to_ship_max_days || '';
  restockDate.value = item0.restock_date || '';
  alwaysAvailable.value = String(item0.is_inventory_available || 'false');
};

watch(() => props.modelValue, syncFromModel, { immediate: true, deep: true });

const updateModel = () => {
  const data = JSON.parse(JSON.stringify(props.modelValue || {}));
  if (!data['0']) data['0'] = {};
  const item0 = data['0'];

  item0.fulfillment_channel_code = channelCode.value;
  
  if (channelCode.value === 'DEFAULT') {
    item0.quantity = quantity.value;
    item0.lead_time_to_ship_max_days = handlingTime.value;
    item0.restock_date = restockDate.value;
    item0.is_inventory_available = alwaysAvailable.value === 'true';
  } else {
    // Optionally clear merchant fields for FBA
    delete item0.quantity;
    delete item0.lead_time_to_ship_max_days;
    delete item0.restock_date;
    delete item0.is_inventory_available;
  }

  emit('update:modelValue', data);
};
</script>

<style scoped>
.amz-fulfillment-availability {
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

.compact-item {
  margin-bottom: 0 !important;
}

.compact-item :deep(.el-form-item__label) {
  font-size: 13px;
  color: #606266;
  padding-bottom: 4px;
  line-height: 1.2;
}

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

.mt-3 { margin-top: 12px; }
.ml-1 { margin-left: 4px; }

.input-medium {
  width: 100% !important;
}

.info-icon {
  font-size: 14px;
  color: #c0c4cc;
  cursor: help;
}

.merchant-fields {
  display: flex;
  flex-direction: column;
}
</style>
