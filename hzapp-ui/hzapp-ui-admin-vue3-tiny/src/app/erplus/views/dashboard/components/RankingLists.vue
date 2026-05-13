<template>
  <el-row :gutter="8" class="flex items-stretch">
    <el-col :span="12" class="flex flex-col">
      <el-card shadow="never" class="ranking-card flex-1 h-full" :body-style="{ padding: '8px 12px' }">
        <template #header>
          <div class="flex justify-between items-center">
            <span class="font-bold">热销商品榜 (Top Products)</span>
            <el-button type="primary" link>更多</el-button>
          </div>
        </template>
        <el-table :data="productRanking" style="width: 100%" size="small">
          <el-table-column type="index" label="排名" width="50" align="center">
            <template #default="scope">
              <span :class="['rank-badge', `rank-${scope.$index + 1}`]">{{ scope.$index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="商品信息">
            <template #default="{ row }">
              <div class="flex items-center">
                <el-image :src="row.image" class="w-8 h-8 rounded mr-2" />
                <div class="flex flex-col truncate">
                  <span class="text-xs font-medium truncate">{{ row.name }}</span>
                  <span class="text-[10px] text-gray-400">{{ row.sku }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column property="sales" label="销量" width="80" align="right" />
          <el-table-column property="amount" label="销售额" width="100" align="right">
            <template #default="{ row }">
              <span class="font-mono">¥{{ row.amount.toLocaleString() }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>

    <el-col :span="12" class="flex flex-col">
      <el-card shadow="never" class="ranking-card flex-1 h-full" :body-style="{ padding: '8px 12px' }">
        <template #header>
          <div class="flex justify-between items-center">
            <span class="font-bold">店铺业绩榜 (Top Stores)</span>
            <el-button type="primary" link>更多</el-button>
          </div>
        </template>
        <el-table :data="storeRanking" style="width: 100%" size="small">
          <el-table-column type="index" label="排名" width="50" align="center">
            <template #default="scope">
              <span :class="['rank-badge', `rank-${scope.$index + 1}`]">{{ scope.$index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column property="name" label="店铺名称" />
          <el-table-column property="orders" label="订单数" width="80" align="right" />
          <el-table-column property="gmv" label="GMV" width="120" align="right">
            <template #default="{ row }">
              <span class="font-mono">¥{{ row.gmv.toLocaleString() }}</span>
            </template>
          </el-table-column>
          <el-table-column label="增长率" width="80" align="right">
            <template #default="{ row }">
              <span :class="row.growth >= 0 ? 'text-success' : 'text-danger'">
                {{ row.growth >= 0 ? '+' : '' }}{{ (row.growth * 100).toFixed(1) }}%
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const productRanking = ref([
  { name: '2024夏季新款透气跑鞋', sku: 'SH-2024-001', sales: 458, amount: 82440, image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=100' },
  { name: '多功能无线蓝牙耳机', sku: 'EA-BT-09', sales: 382, amount: 45840, image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100' },
  { name: '智能变频破壁料理机', sku: 'KIT-MIX-02', sales: 215, amount: 64500, image: 'https://images.unsplash.com/photo-1585232004423-244e0e6904e3?w=100' },
  { name: '便携式迷你手持风扇', sku: 'FAN-P-03', sales: 185, amount: 9250, image: 'https://images.unsplash.com/photo-1591129841117-3adfd313e34f?w=100' },
  { name: '极简风陶瓷餐具套装', sku: 'CER-SET-08', sales: 156, amount: 31200, image: 'https://images.unsplash.com/photo-1574362848149-11496d93a7c7?w=100' }
])

const storeRanking = ref([
  { name: 'Amazon-US-Alpha', orders: 1256, gmv: 458000, growth: 0.15 },
  { name: 'Shopify-Fashion-Global', orders: 845, gmv: 312000, growth: 0.08 },
  { name: 'Walmart-Home-Essentials', orders: 520, gmv: 156000, growth: -0.02 },
  { name: 'eBay-Tech-Outlet', orders: 480, gmv: 96000, growth: 0.12 },
  { name: 'Amazon-EU-Beta', orders: 350, gmv: 84000, growth: 0.05 }
])
</script>

<style scoped>
.ranking-card {
  border-radius: 12px;
}
.ranking-card :deep(.el-card__header) {
  padding: 8px 12px;
}
.rank-badge {
  display: inline-block;
  width: 20px;
  height: 20px;
  line-height: 20px;
  border-radius: 4px;
  background-color: #f1f5f9;
  font-size: 10px;
  font-weight: bold;
}
.rank-1 { background-color: #fef3c7; color: #d97706; }
.rank-2 { background-color: #f1f5f9; color: #64748b; }
.rank-3 { background-color: #fff7ed; color: #c2410c; }
.text-success { color: #52c41a; }
.text-danger { color: #f5222d; }
</style>
