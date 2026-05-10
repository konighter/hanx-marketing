<template>
  <div class="p-20px">
    <div class="flex justify-end mb-20px">
      <el-button 
        :type="showOrders ? 'primary' : 'default'" 
        plain 
        size="small" 
        @click="showOrders = !showOrders" 
        class="rounded-full shadow-sm px-15px"
      >
        <Icon :icon="showOrders ? 'ep:arrow-right' : 'ep:list'" class="mr-5px" /> 
        {{ showOrders ? '隐藏订单' : '我的订单' }}
      </el-button>
    </div>

    <div class="flex flex-row items-start transition-all duration-500 ease-in-out">
      <!-- 左侧：套餐方案 -->
      <div class="flex-1 min-w-0 transition-all duration-500 ease-in-out">
        <div v-loading="loading">
          <div v-if="paidPackages.length > 0" class="flex flex-wrap justify-center gap-30px px-20px pb-20px">
            <div v-for="(item, index) in paidPackages" :key="item.id" class="package-card-item">
              <div class="package-card-wrapper" :class="{ 'is-popular': index === 1 }">
                <div v-if="index === 1" class="popular-badge">MOST POPULAR</div>
                <el-card class="package-card" shadow="never">
                  <div class="p-10px">
                    <div class="flex justify-between items-start mb-15px">
                      <el-tag size="small" effect="plain" class="rounded-full px-12px">{{ item.name }}</el-tag>
                      <Icon :icon="index === 0 ? 'ep:promotion' : index === 1 ? 'ep:cold-drink' : 'ep:bicycle'" 
                            class="text-20px text-blue-500" />
                    </div>
                    
                    <h3 class="text-20px font-800 mb-8px text-[#1a1a1a]">{{ item.name }}</h3>
                    <p class="text-13px text-gray-400 mb-20px h-40px leading-relaxed">
                      {{ item.remark || '为企业提供全方位的数字化解决方案' }}
                    </p>
                    
                    <div class="price-box mb-25px">
                      <span class="currency">¥</span>
                      <span class="price">{{ (item.price / 100).toFixed(0) }}</span>
                      <span class="unit">/{{ item.validityCount > 1 ? item.validityCount : '' }}{{ item.validityUnit === 1 ? '天' : item.validityUnit === 2 ? '月' : '年' }}</span>
                    </div>

                    <el-button 
                      :type="index === 1 ? 'primary' : 'default'" 
                      class="w-full h-42px rounded-8px text-14px font-bold transition-all hover:scale-102"
                      @click="handleOrder(item)"
                    >
                      立即订购 →
                    </el-button>

                    <div class="mt-20px text-left">
                      <ul class="feature-list">
                        <li v-for="feature in getMockFeatures(index)" :key="feature" class="flex items-center mb-8px">
                          <Icon icon="ep:circle-check-filled" class="mr-8px text-blue-500 text-14px" />
                          <span class="text-12px text-[#4a4a4a]">{{ feature }}</span>
                        </li>
                      </ul>
                    </div>
                  </div>
                </el-card>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无付费套餐可供订阅" />
        </div>
      </div>

      <!-- 右侧：我的订单与订阅状态 -->
      <div 
        class="transition-all duration-500 ease-in-out overflow-hidden"
        :style="{ 
          width: showOrders ? '380px' : '0', 
          opacity: showOrders ? 1 : 0,
          marginLeft: showOrders ? '24px' : '0'
        }"
      >
        <div class="w-380px">
        <!-- 当前订阅状态卡片 -->
        <el-card shadow="never" class="mb-20px rounded-12px border-none bg-gradient-to-br from-blue-500 to-blue-600 text-white overflow-hidden relative">
          <Icon icon="ep:medal" class="absolute -right-10px -bottom-10px text-80px opacity-10 rotate-12" />
          <div class="relative z-10">
            <div class="text-14px opacity-80 mb-10px">当前有效订阅</div>
            <div v-if="activeSubscription" class="mb-15px">
              <div class="text-24px font-800 mb-5px">
                {{ packageList.find(p => p.id === activeSubscription.packageId)?.name || '高级套餐' }}
              </div>
              <div class="text-13px opacity-90 flex items-center">
                <Icon icon="ep:calendar" class="mr-5px" />
                有效期至：{{ dateFormatter(null, null, activeSubscription.endTime) }}
              </div>
            </div>
            <div v-else class="text-20px font-800 mb-15px">暂无活跃订阅</div>
            <div class="flex items-center text-12px bg-white/20 w-fit px-8px py-3px rounded-full">
              <div class="w-6px h-6px rounded-full bg-green-400 mr-6px"></div>
              {{ activeSubscription ? '服务正常运行中' : '请在左侧选择方案' }}
            </div>
          </div>
        </el-card>

        <!-- 历史订单记录 -->
        <div class="bg-white p-20px rounded-16px border border-gray-100 shadow-sm">
          <div class="flex justify-between items-center mb-15px">
            <div class="flex items-center">
              <Icon icon="ep:list" class="mr-8px text-blue-500 text-18px" />
              <h3 class="text-16px font-bold text-[#1a1a1a]">我的订单记录</h3>
            </div>
          </div>
          
          <el-table :data="subscribeHistoryList" v-loading="subscribeHistoryLoading" size="small" class="order-table">
            <el-table-column label="套餐方案" min-width="100">
              <template #default="scope">
                <span class="font-600">{{ packageList.find(p => p.id === scope.row.packageId)?.name }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80" align="center">
              <template #default="scope">
                <span :class="scope.row.status === 1 ? 'text-green-500' : 'text-gray-400'" class="text-12px">
                  {{ scope.row.status === 1 ? '生效中' : '已过期' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="截止日期" width="100" align="right">
              <template #default="scope">
                <span class="text-gray-500 text-11px">{{ dateFormatter(null, null, scope.row.endTime).split(' ')[0] }}</span>
              </template>
            </el-table-column>
          </el-table>
          
          <div v-if="subscribeHistoryList.length > 5" class="mt-15px text-center">
            <el-button link type="primary" size="small">查看全部订单</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 订购确认弹窗 -->
    <el-dialog v-model="orderDialogVisible" title="套餐订购确认" width="420px" class="rounded-16px">
      <div v-if="selectedPackage" class="px-10px">
        <div class="bg-gray-50 p-20px rounded-12px mb-20px">
          <div class="flex justify-between items-center mb-15px">
            <span class="text-gray-500">已选套餐</span>
            <span class="text-16px font-bold text-[#1a1a1a]">{{ selectedPackage.name }}</span>
          </div>
          <div class="flex justify-between items-center mb-15px">
            <span class="text-gray-500">订阅时长</span>
            <el-radio-group v-model="orderMonths" size="small">
              <el-radio-button :label="1">1个月</el-radio-button>
              <el-radio-button :label="12">1年 (推荐)</el-radio-button>
            </el-radio-group>
          </div>
          <div class="pt-15px border-t border-gray-200 flex justify-between items-center">
            <span class="text-gray-500">应付金额</span>
            <span class="text-24px font-800 text-[#1a1a1a]">¥{{ (selectedPackage.price * orderMonths / 100).toFixed(2) }}</span>
          </div>
        </div>
        
        <el-button type="primary" class="w-full h-50px rounded-10px text-16px font-bold" :loading="orderLoading" @click="submitOrder">
          确认订购并立即支付 →
        </el-button>
        <p class="text-center text-12px text-gray-400 mt-15px italic">
          支付完成后将立即生效，支持多种支付方式
        </p>
      </div>
    </el-dialog>
  </div>
</div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import * as TenantPackageApi from '@/api/system/tenantPackage'
import * as TenantSubscribeApi from '@/api/system/tenant/subscribe'
import * as TenantOrderApi from '@/api/system/tenant/order'
import { dateFormatter } from '@/utils/formatTime'
import { useMessage } from '@/hooks/web/useMessage'
import { getTenantId } from '@/utils/auth'

const message = useMessage()
const packageList = ref<any[]>([])
const paidPackages = computed(() => packageList.value.filter(p => p.price > 0))

const showOrders = ref(false)
const subscribeHistoryLoading = ref(false)
const subscribeHistoryList = ref<any[]>([])
const activeSubscription = computed(() => subscribeHistoryList.value.find(s => s.status === 1))

const loading = ref(false)
const getList = async () => {
  loading.value = true
  try {
    packageList.value = await TenantPackageApi.getTenantPackageList()
  } finally {
    loading.value = false
  }
  const tenantId = getTenantId()
  if (tenantId) {
    subscribeHistoryLoading.value = true
    try {
      const data = await TenantSubscribeApi.getSubscribePage({ tenantId, pageSize: 100 })
      subscribeHistoryList.value = data.list
    } finally {
      subscribeHistoryLoading.value = false
    }
  }
}

const selectedPackage = ref<any>(null)
const orderDialogVisible = ref(false)
const orderMonths = ref(1)
const orderLoading = ref(false)

const handleOrder = (pkg: any) => {
  selectedPackage.value = pkg
  orderDialogVisible.value = true
}

const getMockFeatures = (index: number) => {
  const features = [
    ['基础业务管理', '单用户授权', '5GB 云存储', '标准邮件支持'],
    ['无限项目管理', '50GB 云存储', '高级数据报表', '优先技术支持', 'API 接口访问'],
    ['专属客户经理', '无限云存储', '7x24 尊享服务', 'SSO 单点登录', '私有化部署选项']
  ]
  return features[index] || features[0]
}

const submitOrder = async () => {
  orderLoading.value = true
  try {
    const res = await TenantOrderApi.createOrder({
      packageId: selectedPackage.value.id,
      subscribeMonths: orderMonths.value
    })
    message.success('订单已创建，请前往支付 (Mock)')
    orderDialogVisible.value = false
    await getList()
  } finally {
    orderLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.package-card-item {
  width: 300px;
  flex-shrink: 0;
  margin-bottom: 20px;
}

.package-card-wrapper {
  position: relative;
  transition: all 0.3s ease;
  width: 100%;
  
  &.is-popular {
    transform: scale(1.05);
    z-index: 10;
    
    .package-card {
      border: 2px solid #409eff;
      box-shadow: 0 20px 40px rgba(64, 158, 255, 0.1);
    }
  }
}

.popular-badge {
  position: absolute;
  top: -12px;
  right: 20px;
  background: #409eff;
  color: white;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 800;
  z-index: 11;
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}

.package-card {
  border-radius: 16px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.price-box {
  display: flex;
  align-items: baseline;
  .currency {
    font-size: 20px;
    font-weight: 700;
    color: #1a1a1a;
    margin-right: 4px;
  }
  .price {
    font-size: 44px;
    font-weight: 800;
    color: #1a1a1a;
    line-height: 1;
  }
  .unit {
    font-size: 14px;
    color: #999;
    margin-left: 4px;
  }
}

.animate-fade-in-right {
  animation: fadeInRight 0.3s ease-out;
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
