<template>
  <div class="skill-marketplace-container">
    <!-- 顶部 Tab 布局 -->
    <div class="tab-header">
      <div class="tab-sc">
        <div 
          v-for="tab in tabs" 
          :key="tab.value"
          class="tab-item"
          :class="{ active: activeTab === tab.value }"
          @click="activeTab = tab.value"
        >
          {{ tab.label }}
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="tab-content">
      <SkillMarketHall v-if="activeTab === 'market'" />
      <ActiveSkillList v-else-if="activeTab === 'running'" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SkillMarketHall from './components/SkillMarketHall.vue'
import ActiveSkillList from './components/ActiveSkillList.vue'

const activeTab = ref('market')

const tabs = [
  { label: '技能大厅', value: 'market' },
  { label: '运行中的技能', value: 'running' }
]
</script>

<style scoped>
.skill-marketplace-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f8fafc;
}

.tab-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border-bottom: 1px solid #e5e7eb;
}

/* 模仿截图中的精选模型/全部模型切换样式 (Segmented Control) */
.tab-sc {
  display: flex;
  background-color: #f3f4f6;
  padding: 4px;
  border-radius: 8px;
  gap: 4px;
}

.tab-item {
  padding: 6px 20px;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  font-weight: 500;
}

.tab-item:hover {
  color: #111827;
}

.tab-item.active {
  background-color: #fff;
  color: #111827;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.tab-content {
  flex: 1;
  display: flex;
}
</style>
