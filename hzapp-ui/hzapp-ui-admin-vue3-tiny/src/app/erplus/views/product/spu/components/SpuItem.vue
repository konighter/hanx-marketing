<doc-alert title="【商品】商品 SPU 与 SKU" url="https://help.h2z.ltd/mall/product-spu-sku/" />
<template>
  <div class="spu-item-card mb-12px" :class="{ 'is-expanded': expanded, 'is-selected': selected }">
    <!-- 1. Card Header (Apple Style) -->
    <div class="card-header px-20px py-10px flex justify-between items-center bg-white/50 border-b border-gray-100/50">
      <div class="header-left flex items-center gap-12px">
        <el-checkbox :model-value="selected" @change="onSelectionChange" class="apple-checkbox" />
        <span class="spu-id font-mono text-11px text-gray-400">#{{ spu.id }}</span>
        <el-divider direction="vertical" />
        <el-tag v-if="spu.categoryName" size="small" effect="plain" class="apple-tag">{{ spu.categoryName }}</el-tag>
        <span v-if="spu.brandName" class="brand-name text-11px font-bold text-gray-400 uppercase tracking-wider">{{ spu.brandName }}</span>
      </div>
      <div class="header-right flex items-center gap-20px">
        <div class="status-toggle flex items-center gap-8px">
          <span class="text-11px text-gray-400">状态</span>
          <el-switch
            v-if="Number(spu.status) >= 0"
            :model-value="spu.status"
            :active-value="1"
            :inactive-value="2"
            active-text="上架"
            inactive-text="下架"
            inline-prompt
            class="apple-switch"
            @change="onStatusChange"
          />
          <el-tag v-else type="info" size="small" class="apple-tag">已归档</el-tag>
        </div>
        <el-button link class="apple-link-btn" @click="toggleExpand">
          {{ expanded ? '收起详情' : '查看规格' }}
          <el-icon class="ml-4px transition-transform duration-300" :class="{ 'rotate-180': expanded }"><ArrowDown /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 2. Main Row (Expressive Summary) -->
    <div class="card-body px-20px py-16px flex items-center gap-24px cursor-pointer" @click="toggleExpand">
      <!-- Product Image -->
      <div class="product-visual w-64px h-64px flex-none relative group" @click.stop="onPreviewImage">
        <el-image
          :src="spu.picUrl"
          fit="cover"
          class="w-full h-full rounded-8px border border-gray-100 shadow-sm transition-transform duration-300 group-hover:scale-105"
        />
        <div class="absolute inset-0 bg-black/5 opacity-0 group-hover:opacity-100 flex items-center justify-center rounded-8px transition-opacity">
          <el-icon class="text-white text-18px"><ZoomIn /></el-icon>
        </div>
      </div>

      <!-- Identity & Info -->
      <div class="product-info flex-1 min-w-0">
        <h3 class="product-name text-16px font-600 text-gray-900 truncate mb-4px" :title="spu.name">{{ spu.name }}</h3>
        <div class="flex items-center gap-12px text-12px text-apple-muted">
          <span class="font-mono">{{ spu.id }}</span>
          <span class="variants-count">{{ spu.skus?.length || 0 }} 个变体</span>
        </div>
      </div>

      <!-- Price & Stock -->
      <div class="product-metrics flex items-center gap-48px flex-none">
        <div class="metric-item text-right">
          <div class="metric-label text-11px text-apple-muted mb-2px uppercase tracking-tight">售价范围</div>
          <div class="metric-value text-18px font-700 text-gray-900">
            <template v-if="priceRange.min === priceRange.max">
              <span class="currency text-12px font-500 mr-2px">¥</span>{{ fenToYuan(priceRange.min) }}
            </template>
            <template v-else>
              <span class="currency text-12px font-500 mr-2px">¥</span>{{ fenToYuan(priceRange.min) }} <span class="mx-2px text-gray-300">-</span> {{ fenToYuan(priceRange.max) }}
            </template>
          </div>
        </div>
        <div class="metric-item text-right w-80px">
          <div class="metric-label text-11px text-apple-muted mb-2px uppercase tracking-tight">总库存</div>
          <div class="metric-value text-16px font-600" :class="totalStock > 0 ? 'text-gray-900' : 'text-red-500'">
            {{ totalStock }}
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="quick-actions flex items-center gap-8px ml-12px">
        <el-tooltip content="快速编辑" placement="top">
          <el-button circle class="apple-circle-btn" @click.stop="onEdit">
            <el-icon><EditPen /></el-icon>
          </el-button>
        </el-tooltip>
        <template v-if="tabType === 4">
          <el-tooltip content="恢复" placement="top">
             <el-button circle class="apple-circle-btn" @click.stop="onRestore">
              <el-icon><RefreshRight /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="彻底删除" placement="top">
             <el-button circle type="danger" plain class="apple-circle-btn" @click.stop="onDelete">
              <el-icon><Delete /></el-icon>
            </el-button>
          </el-tooltip>
        </template>
      </div>
    </div>

    <!-- 3. Expanded SKU List (Single Line Form) -->
    <transition name="apple-expand">
      <div v-if="expanded" class="expanded-detail bg-apple-gray-light border-t border-gray-100">
        <div class="sku-table-header px-20px py-8px flex items-center text-11px text-apple-muted font-600 uppercase tracking-wider">
          <div class="w-40px mr-16px">图示</div>
          <div class="flex-1">规格属性 / 编码</div>
          <div class="w-120px text-center">单价</div>
          <div class="w-100px text-center">库存</div>
          <div class="w-100px text-right px-12px">操作</div>
        </div>
        <div class="sku-list">
          <div v-for="sku in spu.skus" :key="sku.id" class="sku-row px-20px py-10px flex items-center border-b border-white hover:bg-white/60 transition-colors">
            <!-- SKU Image -->
            <div class="w-40px h-40px flex-none mr-16px cursor-zoom-in" @click="onPreviewSkuImage(sku)">
              <el-image :src="sku.picUrl" fit="cover" class="w-full h-full rounded-4px border border-gray-100 shadow-xs" />
            </div>
            
            <!-- SKU Attributes -->
            <div class="flex-1 min-w-0">
              <div class="flex flex-wrap gap-4px mb-2px">
                <span v-for="p in sku.properties" :key="p.propertyId" class="attribute-pill px-6px py-1px bg-white border border-gray-200 rounded-pill text-10px text-gray-600">
                  <span class="text-gray-400 font-500 mr-2px">{{ p.propertyName }}:</span>{{ p.valueName }}
                </span>
              </div>
              <div class="sku-code font-mono text-11px text-apple-muted">{{ sku.barCode || '无编码' }}</div>
            </div>

            <!-- SKU Price -->
            <div class="w-120px text-center font-600 text-gray-800">
              <span class="text-11px font-500 mr-1px text-gray-400">¥</span>{{ fenToYuan(sku.price) }}
            </div>

            <!-- SKU Stock -->
            <div class="w-100px text-center text-13px font-500" :class="sku.stock > 0 ? 'text-gray-700' : 'text-red-400'">
              {{ sku.stock }}
            </div>

            <!-- SKU Actions -->
            <div class="w-100px text-right">
              <el-button link class="apple-link-btn" size="small" @click="onListing(sku)">刊登</el-button>
            </div>
          </div>
        </div>
        
        <!-- Footer Meta -->
        <div class="expanded-footer px-20px py-12px flex justify-between items-center text-11px text-apple-muted bg-white/20">
          <div class="flex items-center gap-15px">
             <span>创建于: {{ dateFormatter(null, null, spu.createTime) }}</span>
             <el-divider v-if="spu.brandName" direction="vertical" />
             <span v-if="spu.brandName">品牌: {{ spu.brandName }}</span>
          </div>
          <div class="flex items-center gap-15px">
             <span v-if="spu.deliveryTemplateName">运费模板: {{ spu.deliveryTemplateName }}</span>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ArrowDown, ZoomIn, EditPen, Delete, RefreshRight } from '@element-plus/icons-vue'
import { fenToYuan } from '@/utils'
import { dateFormatter } from '@/utils/formatTime'
import { createImageViewer } from '@/components/ImageViewer'

const props = defineProps<{
  spu: any
  selected: boolean
  tabType: number
}>()

const emit = defineEmits(['selection-change', 'update-status', 'delete', 'edit', 'restore', 'listing'])

const expanded = ref(false)

/** Price Range Calculation */
const priceRange = computed(() => {
  if (!props.spu.skus || props.spu.skus.length === 0) return { min: 0, max: 0 }
  const prices = props.spu.skus.map((s: any) => s.price)
  return {
    min: Math.min(...prices),
    max: Math.max(...prices)
  }
})

/** Total Stock Calculation */
const totalStock = computed(() => {
  if (!props.spu.skus || props.spu.skus.length === 0) return 0
  return props.spu.skus.reduce((sum: number, s: any) => sum + (s.stock || 0), 0)
})

/** Handlers */
const toggleExpand = () => {
  expanded.value = !expanded.value
}

const onSelectionChange = (val: boolean) => {
  emit('selection-change', val, props.spu)
}

const onStatusChange = (val: any) => {
  emit('update-status', props.spu, val)
}

const onEdit = () => {
  emit('edit', props.spu.id)
}

const onDelete = () => {
  emit('delete', props.spu.id)
}

const onRestore = () => {
  emit('restore', props.spu)
}

const onListing = (sku: any) => {
  emit('listing', [sku.id])
}

const onPreviewImage = () => {
  if (props.spu.picUrl) {
    createImageViewer({
      urlList: [props.spu.picUrl]
    })
  }
}

const onPreviewSkuImage = (sku: any) => {
  if (sku.picUrl) {
    createImageViewer({
      urlList: [sku.picUrl]
    })
  }
}
</script>

<style scoped lang="scss">
.spu-item-card {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;

  &:hover {
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.06);
    transform: translateY(-2px);
    border-color: rgba(0, 113, 227, 0.1);
  }

  &.is-selected {
    border-color: #0071e3;
    background-color: rgba(0, 113, 227, 0.01);
  }

  &.is-expanded {
    box-shadow: 0 16px 40px rgba(0, 0, 0, 0.08);
  }

  /* Spu Identity */
  .product-name {
    letter-spacing: -0.01em;
  }

  .text-apple-muted {
    color: rgba(0, 0, 0, 0.48);
  }

  .bg-apple-gray-light {
    background-color: #fcfcfd;
  }

  /* Custom Components */
  .apple-tag {
    border-radius: 6px;
    background: transparent;
    border: 1px solid rgba(0, 0, 0, 0.08);
    color: rgba(0, 0, 0, 0.6);
    font-weight: 500;
  }

  .apple-circle-btn {
    border: none;
    background: #f5f5f7;
    color: rgba(0, 0, 0, 0.56);
    &:hover {
      background: #e8e8ed;
      color: #0071e3;
    }
  }

  .apple-link-btn {
    color: #0071e3;
    font-weight: 500;
    font-size: 13px;
    &:hover {
      text-decoration: underline;
    }
  }

  .attribute-pill {
    line-height: normal;
  }

  /* Expansion Transition */
  .apple-expand-enter-active, .apple-expand-leave-active {
    transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
    max-height: 1200px;
    opacity: 1;
  }
  .apple-expand-enter-from, .apple-expand-leave-to {
    max-height: 0;
    opacity: 0;
    overflow: hidden;
  }
}

.rounded-pill {
  border-radius: 980px;
}
</style>
