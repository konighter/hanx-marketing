<template>
  <div class="order-item-card mb-8px" :class="{ 'is-expanded': expanded, 'is-selected': selected }">
    <!-- 1. Card Header (Shopee Style) -->
    <div class="card-header px-15px py-6px flex justify-between items-center">
      <div class="header-left flex items-center gap-10px">
        <el-checkbox :model-value="selected" @change="onSelectionChange" />
        <div class="platform-tag" :style="{ backgroundColor: platformConfig.color }">
          {{ platformConfig.label }}
        </div>
        <!-- <span class="shop-name text-12px font-bold text-gray-600 truncate max-w-150px">
          {{ order.shopId }}
        </span> -->
        <el-divider direction="vertical" />
        <span class="order-id text-12px text-gray-500">
          ID: <span class="font-mono">{{ order.platformOrderId }}</span>
        </span>
        <el-icon class="copy-icon cursor-pointer hover:text-primary" @click="copyOrderId">
          <DocumentCopy />
        </el-icon>
      </div>
      <div class="header-right flex items-center gap-15px">
        <span class="order-time text-11px text-gray-400">
          订单时间: {{ dateFormatter(order, {} as any, order.createTime) }}
        </span>
        <el-button type="primary" link size="small" @click="toggleExpand">
          {{ expanded ? '收起' : '展开' }}
          <el-icon class="ml-2px" :class="{ 'rotate-180': expanded }">
            <ArrowDown />
          </el-icon>
        </el-button>
      </div>
    </div>

    <!-- 2. Main Row (High Density Summary) -->
    <div class="card-body px-15px py-8px flex items-center gap-20px">
      <!-- Product Thumbnails -->
      <div class="product-summary flex-1 flex items-center gap-10px overflow-hidden">
        <div class="thumbnails flex -space-x-15px">
          <template v-for="(item, index) in order.orderItemList?.slice(0, 3)" :key="index">
            <el-image
              :src="item.mainImageUrl"
              class="w-48px h-48px rounded border-2 border-white shadow-sm"
              fit="cover"
              :preview-src-list="order.orderItemList?.map(i => i.mainImageUrl)"
              :preview-teleported="true"
            />
          </template>
          <div v-if="order.orderItemList?.length > 3" class="more-count w-48px h-48px rounded bg-gray-100 flex items-center justify-center text-xs text-gray-500 border-2 border-white shadow-sm">
            +{{ order.orderItemList?.length - 3 }}
          </div>
        </div>
        <div class="sku-info flex-1 flex flex-col justify-center min-w-0">
          <div class="sku-code font-bold text-13px text-gray-700 truncate" :title="skuSummary">
            {{ skuSummary }}
          </div>
          <div class="item-count text-11px text-gray-400 mt-2px">
            共 {{ order.orderItemList?.length }} 件商品
          </div>
        </div>
      </div>

      <!-- Financials -->
      <div class="financial-summary w-150px text-right">
        <div class="total-amount font-bold text-16px text-orange-600">
          {{ (order.amount / 100).toFixed(2) }} <span class="text-11px">{{ order.currency }}</span>
        </div>
        <div class="shipping-type text-11px text-gray-400 mt-2px">
          <el-tag size="small" :type="order.fulfillType === 1 ? 'success' : 'warning'" effect="plain" class="!px-4px">
            {{ order.fulfillTypeName }}
          </el-tag>
        </div>
      </div>

      <!-- Status -->
      <div class="status-summary w-120px text-center">
        <div class="status-badge py-2px px-8px rounded-full text-11px font-bold text-white inline-block" :style="{ backgroundColor: statusConfig.color }">
          {{ order.statusName }}
        </div>
        <div class="platform-status text-10px text-gray-400 mt-4px truncate" :title="order.platformStatus">
          {{ order.platformStatus || '-' }}
        </div>
      </div>

      <!-- Quick Actions -->
      <div v-if="!expanded" class="quick-actions w-80px flex justify-end">
        <el-tooltip content="同步并打印" placement="top">
          <el-button circle size="small" @click="onSync">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </div>

    <!-- 3. Expanded Details -->
    <transition name="expand">
      <div v-if="expanded" class="expanded-content border-t border-gray-50 bg-gray-50/30 p-15px">
        <el-row :gutter="30">
          <!-- Item List -->
          <el-col :span="16">
            <div class="section-title text-12px font-bold text-gray-500 mb-10px flex items-center">
               <el-icon class="mr-4px"><ShoppingCart /></el-icon> 订单明细
            </div>
            <table class="w-full text-12px text-gray-600 item-table">
              <thead>
                <tr class="text-gray-400 border-b border-gray-100">
                  <th class="py-5px text-left font-normal">商品图片</th>
                  <th class="py-5px text-left font-normal w-200px">SKU & 标题</th>
                  <th class="py-5px text-center font-normal">单价</th>
                  <th class="py-5px text-center font-normal">数量</th>
                  <th class="py-5px text-center font-normal">税费</th>
                  <th class="py-5px text-center font-normal">运费/优惠</th>
                  <th class="py-5px text-right font-normal">应付</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, idx) in order.orderItemList" :key="idx" class="border-b border-gray-50/50 hover:bg-white/50">
                  <td class="py-8px">
                    <el-image :src="item.mainImageUrl" class="w-40px h-40px rounded" fit="cover" />
                  </td>
                  <td class="py-8px">
                    <div class="font-bold text-gray-700">{{ item.sellerSkuCode }}</div>
                    <div class="text-11px text-gray-400 truncate w-180px" :title="item.title">{{ item.title }}</div>
                  </td>
                  <td class="py-8px text-center">{{ (item.itemPrice / 100).toFixed(2) }}</td>
                  <td class="py-8px text-center">x{{ item.itemCount }}</td>
                  <td class="py-8px text-center text-gray-400">{{ (item.itemTax / 100).toFixed(2) }}</td>
                  <td class="py-8px text-center text-gray-400">
                    <div :title="'总运费: ' + (item.shipFee/100).toFixed(2) + ' 免除: ' + (item.shipFeeDiscount/100).toFixed(2)">
                      {{ ((item.shipFee - item.shipFeeDiscount) / 100).toFixed(2) }}
                    </div>
                    <div class="text-red-400" :title="'促销/手动优惠'">-{{ (item.promoDiscount / 100).toFixed(2) }}</div>
                  </td>
                  <td class="py-8px text-right font-bold">{{ (item.totalAmount / 100).toFixed(2) }}</td>
                </tr>
              </tbody>
            </table>
          </el-col>

          <!-- Financial Breakdown -->
          <el-col :span="8" class="border-l border-gray-100 pl-25px">
            <div class="section-title text-12px font-bold text-gray-500 mb-10px flex items-center">
               <el-icon class="mr-4px"><Money /></el-icon> 费用报表 ({{ order.currency }})
            </div>
            <div class="finance-card bg-white rounded border border-gray-100 p-12px shadow-sm">
              <div class="flex justify-between mb-8px text-13px">
                <span class="text-gray-500">支付总额 (Income)</span>
                <span class="font-bold text-green-600">+{{ (order.amount / 100).toFixed(2) }}</span>
              </div>
              <div class="space-y-4px text-11px text-gray-400 border-t border-gray-50 pt-8px mt-4px">
                <div class="flex justify-between">
                  <span>- 税费 (Tax)</span>
                  <span>+{{ fees.tax }}</span>
                </div>
                <div class="flex justify-between">
                  <span>- 运费 (Shipping)</span>
                  <span>+{{ fees.ship }}</span>
                </div>
                <div class="flex justify-between">
                  <span>- 优惠 (Promo)</span>
                  <span class="text-red-400">-{{ fees.discount }}</span>
                </div>
                <el-divider class="!my-4px" />
                <div class="flex justify-between">
                  <span>- 佣金 (Commission)</span>
                  <span>{{ fees.referral }}</span>
                </div>
                <div class="flex justify-between">
                  <span>- 配送 (Fulfillment)</span>
                  <span>{{ fees.fulfill }}</span>
                </div>
              </div>
              <el-divider class="!my-10px" />
              <div class="flex justify-between items-end">
                <span class="text-12px text-gray-600">利润预估 (Profit)</span>
                <span class="text-16px font-bold" :class="profit >= 0 ? 'text-green-500' : 'text-red-500'">
                  {{ profit.toFixed(2) }}
                </span>
              </div>
            </div>
            <div class="mt-15px flex gap-8px">
              <el-button type="primary" size="small" @click="onSync">重新同步</el-button>
              <el-button plain size="small" class="!px-10px">标记</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { 
  DocumentCopy, 
  ArrowDown, 
  Refresh, 
  ShoppingCart, 
  Money 
} from '@element-plus/icons-vue'
import { dateFormatter } from '@/utils/formatTime'

const props = defineProps<{
  order: any
  selected: boolean
}>()

const emit = defineEmits(['selection-change', 'sync-request'])

const message = useMessage()
const expanded = ref(false)

/** 平台配置 */
const platformConfig = computed(() => {
  const map: any = {
    1: { color: '#ee4d2d', label: 'SHP' },  // Shopee
    2: { color: '#005aff', label: 'LZD' },  // Lazada
    3: { color: '#ff9900', label: 'AMZ' },  // Amazon
    4: { color: '#000000', label: 'TK' },   // TikTok
    5: { color: '#005bff', label: 'OZN' },  // Ozon
    6: { color: '#e62e04', label: 'AE' },   // AliExpress
  }
  return map[props.order.platformId] || { color: '#999', label: '?' }
})

/** 状态配置 */
const statusConfig = computed(() => {
  const map: any = {
    10: { color: '#909399' }, // 待处理
    20: { color: '#ff9900' }, // 待出库
    30: { color: '#409eff' }, // 部分发货
    40: { color: '#67c23a' }, // 已发货
    50: { color: '#f56c6c' }, // 已取消
    60: { color: '#f56c6c' }, // 不可配送
    80: { color: '#409eff' }, // 待库存确认
  }
  return map[props.order.status] || { color: '#999' }
})

/** SKU 摘要 */
const skuSummary = computed(() => {
  if (!props.order.orderItemList || props.order.orderItemList.length === 0) return 'No Item'
  const skus = props.order.orderItemList.map((i: any) => i.sellerSkuCode).join(', ')
  return skus
})

const orderItemCount = computed(() => {
  if (!props.order.orderItemList || props.order.orderItemList.length === 0) return 0
  return props.order.orderItemList.reduce((acc: number, item: any) => acc + (item.itemCount || 0), 0)
})

/** 费用计算 */
const fees = computed(() => {
  let referral = 0
  let fulfill = 0
  let tax = 0
  let ship = 0
  let discount = 0
  
  props.order.orderItemList?.forEach((item: any) => {
    referral += (item.actualReferralFee || item.estimatedReferralFee || 0)
    fulfill += (item.actualFulfillFee || item.estimatedFulfillFee || 0)
    tax += (item.itemTax || 0)
    ship += ((item.shipFee || 0) - (item.shipFeeDiscount || 0))
    discount += (item.promoDiscount || 0)
  })
  
  return {
    referral: (referral / 100).toFixed(2),
    fulfill: (fulfill / 100).toFixed(2),
    tax: (tax / 100).toFixed(2),
    ship: (ship / 100).toFixed(2),
    discount: (discount / 100).toFixed(2),
    totalFees: referral + fulfill
  }
})

/** 利润预估 */
const profit = computed(() => {
  const income = props.order.amount || 0
  const totalFees = fees.value.totalFees || 0
  return (income - totalFees) / 100
})

/** 交互事件 */
const toggleExpand = () => {
  expanded.value = !expanded.value
}

const onSelectionChange = (val: boolean) => {
  emit('selection-change', val, props.order)
}

const onSync = () => {
  emit('sync-request', props.order)
}

const copyOrderId = () => {
  if (navigator.clipboard) {
    navigator.clipboard.writeText(props.order.platformOrderId).then(() => {
      message.success('已复制到剪贴板')
    }).catch(() => {
      message.error('复制失败')
    })
  }
}
</script>

<style scoped lang="scss">
.order-item-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    border-color: var(--el-color-primary-light-7);
  }

  &.is-selected {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
  }

  &.is-expanded {
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
  }

  .card-header {
    background: #fcfcfc;
    border-bottom: 1px solid #f2f6fc;
  }

  .platform-tag {
    height: 18px;
    line-height: 18px;
    padding: 0 5px;
    border-radius: 3px;
    color: #fff;
    font-size: 10px;
    font-weight: 800;
  }

  .rotate-180 {
    transform: rotate(180deg);
  }

  .item-table {
    border-collapse: collapse;
    th {
      font-size: 11px;
    }
  }

  /* 展开动画 */
  .expand-enter-active, .expand-leave-active {
    transition: all 0.3s ease;
    max-height: 800px;
  }
  .expand-enter-from, .expand-leave-to {
    max-height: 0;
    opacity: 0;
    padding-top: 0;
    padding-bottom: 0;
  }
}

.copy-icon {
  font-size: 14px;
  margin-left: 4px;
  transition: color 0.2s;
}

.thumbnails {
   padding-left: 8px; /* 补偿第一个图像的边距 */
}

:deep(.el-image) {
  background: #f5f7fa;
}
</style>
