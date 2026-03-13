<template>
  <div class="skill-marketplace-layout">
    <!-- 左侧侧边栏 (筛选器) -->
    <aside class="sidebar">
      <div class="filter-group">
        <div class="group-header">
          <span class="group-title">策略作者</span>
          <el-icon><Lock /></el-icon>
        </div>
        <div class="group-content">
          <div class="filter-tag">官方自研</div>
          <div class="filter-tag active">推荐专家</div>
        </div>
      </div>

      <div class="filter-group">
        <div class="group-header">
          <span class="group-title">策略适用场景</span>
          <el-icon><Lock /></el-icon>
        </div>
        <div class="group-content">
          <div 
            class="filter-tag" 
            :class="{ active: activeFilter === '全部' }" 
            @click="activeFilter = '全部'"
          >全部场景</div>
          <div 
            v-for="tag in allTags" 
            :key="tag"
            class="filter-tag"
            :class="{ active: activeFilter === tag }"
            @click="activeFilter = tag"
          >
            {{ tag }}
          </div>
        </div>
      </div>
    </aside>

    <!-- 右侧主内容区 -->
    <main class="main-content">
      <!-- 顶部动作栏 -->
      <header class="main-header">
        <div class="header-left">
          <span class="list-title">策略 </span>
          <span class="list-count">{{ filteredSkills.length }}</span>
          <el-input 
            v-model="searchQuery"
            placeholder="请输入策略名称" 
            prefix-icon="Search" 
            class="search-input"
            clearable
          />
        </div>
        <div class="header-right">
          <div class="dropdown-action">全部 适用品类 <el-icon><ArrowDown /></el-icon></div>
          <div class="dropdown-action">全部 目标类型 <el-icon><ArrowDown /></el-icon></div>
        </div>
      </header>

      <!-- Promo Banner 活动横幅 -->
      <!-- <div class="promo-banner" >
        <div class="promo-content">
          <h3 class="promo-title">大模型特惠活动</h3>
          <p class="promo-desc">AI 通用节省计划，可抵扣全系大模型消耗，更省更灵活</p>
          <span class="promo-badge">特惠资源包</span>
        </div>
        <div class="promo-img-placeholder">
          <span>OFF %</span>
        </div>
      </div> -->

      <!-- 技能网格列表 -->
      <div class="skill-grid">
        <el-row :gutter="20">
          <el-col 
            :xs="24" :sm="24" :md="12" :lg="8" :xl="8"
            v-for="skill in searchedSkills" 
            :key="skill.skillCode"
          >
            <div class="model-card" @click="openDetail(skill)">
              <div class="card-header">
                <div class="card-title-row">
                  <div class="icon-wrapper">
                    <el-icon :size="20"><component :is="skill.icon || 'Cpu'" /></el-icon>
                  </div>
                  <h3 class="model-name">{{ skill.name }}</h3>
                  <!-- Example tag like "New" or "Qwen3.5" in the screenshot -->
                  <span class="version-tag" v-if="skill.skillCode.includes('NEW')">NEW</span>
                </div>
                <div class="card-subtitle">
                  <el-icon class="subtitle-icon"><Monitor /></el-icon>
                  <span class="version-text">最新版本 v1.0.0</span>
                </div>
              </div>
              
              <div class="card-body">
                <p class="model-desc">{{ skill.description }}</p>
              </div>
              
              <div class="card-footer">
                <div class="tags-row">
                  <span class="bottom-tag" v-for="tag in skill.useCaseTags.split(',').slice(0, 2)" :key="tag">
                    {{ tag.trim() }}
                  </span>
                </div>
                <span class="date-text">2026-03-12</span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </main>

    <!-- 技能详情弹窗 (独立组件版) -->
    <SkillDetailDialog 
      v-model="detailVisible" 
      :skill="activeSkill" 
      @activate="handleActivateFromDetail" 
    />

    <SkillActivationDrawer
      v-model="drawerVisible"
      :skill="activeSkill"
      @activated="onActivated"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import SkillActivationDrawer from './components/SkillActivationDrawer.vue'
import SkillDetailDialog from './components/SkillDetailDialog.vue'
import { Cpu, ArrowDown, Lock, Search, Monitor } from '@element-plus/icons-vue'
import { getSkillList } from '@/app/erplus/api/adv/mas/skill'

const skills = ref<any[]>([])

const fetchSkills = async () => {
  try {
    const res = await getSkillList()
    skills.value = res || []
  } catch (error) {
    console.error('Failed to fetch mas skills', error)
  }
}

onMounted(() => {
  fetchSkills()
})
// =======================================

const detailVisible = ref(false)
const drawerVisible = ref(false)
const activeSkill = ref<any>(null)
const activeStep = ref(0)
const activeFilter = ref('全部')
const searchQuery = ref('')

const allTags = computed(() => {
  const tags = new Set<string>()
  skills.value.forEach(s => {
    s.useCaseTags.split(',').forEach(t => tags.add(t.trim()))
  })
  return Array.from(tags)
})

const filteredSkills = computed(() => {
  if (activeFilter.value === '全部') {
    return skills.value
  }
  return skills.value.filter(s => {
    return s.useCaseTags.split(',').map(t => t.trim()).includes(activeFilter.value)
  })
})

const searchedSkills = computed(() => {
  if (!searchQuery.value) return filteredSkills.value
  return filteredSkills.value.filter(s => 
    s.name.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
    s.description.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

function openDetail(skill: any) {
  activeSkill.value = skill
  detailVisible.value = true
}

function handleActivateFromDetail(skill: any) {
  detailVisible.value = false
  drawerVisible.value = true
}

function onActivated() {
  console.log("Skill applied successfully!")
}
</script>

<style scoped>
.skill-marketplace-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f8fafc; /* light background matching the screenshot border areas */
}

/* 侧边栏样式 */
.sidebar {
  width: 240px;
  background-color: #fff;
  border-right: 1px solid #e5e7eb;
  padding: 24px 16px;
  flex-shrink: 0;
}
.filter-group {
  margin-bottom: 24px;
}
.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #111827;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  padding: 0 8px;
}
.group-title {
  color: #374151;
}
.group-content {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.filter-tag {
  display: inline-block;
  padding: 6px 12px;
  background-color: #f3f4f6;
  color: #4b5563;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}
.filter-tag:hover {
  background-color: #e5e7eb;
}
.filter-tag.active {
  background-color: #eff6ff;
  color: #3b82f6;
  border-color: #bfdbfe;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  padding: 32px 40px;
  min-width: 0;
  background-color: #fff;
}
.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.list-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}
.list-count {
  font-size: 16px;
  color: #6b7280;
}
.search-input {
  width: 240px;
  margin-left: 16px;
}
:deep(.search-input .el-input__wrapper) {
  border-radius: 20px;
  box-shadow: 0 0 0 1px #d1d5db inset;
}
.header-right {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
}
.dropdown-action {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Banner 样式 */
.promo-banner {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  border: 1px solid #ffedd5;
  border-radius: 12px;
  margin-bottom: 24px;
  overflow: hidden;
  position: relative;
  height: 100px;
}
.promo-content {
  padding: 16px 32px;
  flex: 1;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.promo-title {
  margin: 0 0 6px 0;
  font-size: 16px;
  font-weight: 600;
  color: #9a3412;
}
.promo-desc {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #c2410c;
}
.promo-badge {
  background-color: #fff;
  color: #ea580c;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
  align-self: flex-start;
}
.promo-img-placeholder {
  width: 150px;
  background: linear-gradient(135deg, #fdba74 0%, #f97316 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: bold;
  font-size: 24px;
  font-style: italic;
  clip-path: polygon(20% 0, 100% 0, 100% 100%, 0 100%);
}

/* 卡片样式 */
.skill-grid {
  margin-top: 20px;
}
.model-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
  background-color: #fff;
  margin-bottom: 24px;
  cursor: pointer;
  transition: all 0.2s ease;
  height: 200px;
  display: flex;
  flex-direction: column;
}
.model-card:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}
.card-header {
  margin-bottom: 16px;
}
.card-title-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.icon-wrapper {
  color: #6366f1;
  background-color: #eef2ff;
  border-radius: 8px;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}
.model-name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
  flex: 1;
}
.version-tag {
  font-size: 12px;
  color: #f59e0b;
  font-weight: 600;
}
.card-subtitle {
  display: flex;
  align-items: center;
  color: #6b7280;
  font-size: 12px;
  gap: 6px;
}
.card-body {
  flex: none;
}
.model-desc {
  font-size: 13px;
  color: #4b5563;
  line-height: 20px;
  height: 60px;
  margin: 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  overflow: hidden;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}
.tags-row {
  display: flex;
  gap: 8px;
}
.bottom-tag {
  background-color: #f3f4f6;
  color: #4b5563;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}
.date-text {
  font-size: 12px;
  color: #9ca3af;
}

/* 弹窗通用样式 */
.workflow-steps {
  padding: 20px 0;
  margin: 10px 0;
  background-color: #f8fafc;
  border-radius: 8px;
}
.param-list {
  margin-top: 10px;
}
</style>
