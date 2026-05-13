<template>
  <el-row :gutter="16">
    <el-col v-for="alert in alerts" :key="alert.type" :xl="6" :lg="6" :md="12" :sm="12" :xs="24" class="mb-4">
      <div 
        class="alert-card p-6 border-none rounded-xl cursor-pointer transition-all hover:-translate-y-1"
        :style="{ backgroundColor: getAlertStyles(alert.type).bg }"
      >
        <div class="flex justify-between items-start mb-4">
          <div class="flex flex-col">
            <span class="text-gray-800 text-base font-bold">{{ alert.title }}</span>
            <span class="text-gray-600 text-xs mt-1 leading-relaxed">{{ alert.message }}</span>
          </div>
          <Icon 
            :icon="getAlertStyles(alert.type).icon" 
            :size="20" 
            :style="{ color: getAlertStyles(alert.type).iconColor }" 
          />
        </div>
        
        <div class="mt-auto flex justify-between items-center">
          <el-button 
            type="primary" 
            class="!bg-white !text-gray-800 !border-none !rounded-lg !px-4 hover:!bg-gray-100 font-medium text-xs h-8"
          >
            {{ getAlertStyles(alert.type).btnText }}
          </el-button>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
const props = defineProps({
  alerts: Array,
  loading: Boolean
})

const getAlertStyles = (type: string) => {
  const map = {
    'SALES_DROP': {
      bg: '#fef3c7', // Amber 100
      iconColor: '#d97706',
      icon: 'ep:trend-charts',
      btnText: '查看详情'
    },
    'REFUND_HIGH': {
      bg: '#fee2e2', // Red 100
      iconColor: '#dc2626',
      icon: 'ep:warning',
      btnText: '分析原因'
    },
    'STOCK_LOW': {
      bg: '#ffedd5', // Orange 100
      iconColor: '#ea580c',
      icon: 'ep:box',
      btnText: '申请补货'
    },
    'ACOS_HIGH': {
      bg: '#fce7f3', // Pink 100
      iconColor: '#db2777',
      icon: 'ep:data-line',
      btnText: '优化竞价'
    }
  }
  return map[type] || { bg: '#f3f4f6', iconColor: '#4b5563', icon: 'ep:bell', btnText: '立即处理' }
}
</script>

<style scoped>
.alert-card {
  height: 140px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}
</style>

