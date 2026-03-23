<template>
  <div class="active-skill-list">
    <!-- 顶部筛选区域 -->
    <header class="filter-header">
      <div class="filter-left">
        <el-select 
          v-model="filterSkill" 
          placeholder="选择技能" 
          clearable 
          class="skill-select"
          @change="refreshList"
        >
          <el-option
            v-for="skill in allSkills"
            :key="skill.skillCode"
            :label="skill.name"
            :value="skill.skillCode"
          />
        </el-select>
        <el-input
          v-model="filterAsin"
          placeholder="搜索 ASIN"
          :prefix-icon="Search"
          clearable
          class="asin-input"
          @keyup.enter="refreshList"
        />
        <el-button type="primary" :icon="Search" @click="refreshList">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
    </header>

    <!-- 列表容器 -->
    <div class="active-skills-container" v-infinite-scroll="loadMore" :infinite-scroll-disabled="loading || noMore">
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无运行中的技能" />
      </div>
      
      <div class="instance-grid">
        <el-card v-for="item in list" :key="item.id" class="instance-card" shadow="hover">
          <div class="card-header">
            <div class="header-left">
              <div class="skill-brand">
                <el-icon class="skill-icon"><Monitor /></el-icon>
                <div class="skill-title">
                  <span class="skill-name">{{ item.skillName }}</span>
                  <div class="skill-subtitle">
                    <span class="asin-label">ASIN:</span>
                    <span class="asin-value">{{ item.targetBizId || '--' }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="header-right">
              <div class="time-info">
                <el-icon><Calendar /></el-icon>
                <span>{{ formatDate(item.createTime) }}</span>
              </div>
              <el-tag :type="getStatusType(item.status)" size="small" effect="dark" round>
                {{ item.status }}
              </el-tag>
            </div>
          </div>
          
          <div class="card-body">
            <div class="progress-section">
              <div class="section-label">
                <el-icon><Operation /></el-icon>
                <span>最新进展</span>
              </div>
              <p class="progress-text">{{ item.latestProgress || '正在进行中，实时监测数据波动...' }}</p>
            </div>
          </div>

          <div class="card-footer">
            <div class="footer-left">
              <span class="process-id" v-if="item.processInstanceId">Process ID: {{ item.processInstanceId }}</span>
            </div>
            <div class="footer-right">
              <el-button type="primary" size="default" link @click="handleShowDetails(item)">运行详情</el-button>
              <el-button type="danger" size="default" link @click="handleStop(item)">停止策略</el-button>
            </div>
          </div>
        </el-card>
      </div>

      <div v-if="loading" class="loading-state">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      <div v-if="noMore && list.length > 0" class="no-more-state">
        <span>已显示全部运行中的技能</span>
      </div>
    </div>

    <!-- 详情抽屉组件 -->
    <SkillInstanceDetailDrawer 
      v-model="drawerVisible" 
      :instance="selectedInstance" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getSkillList, MasSkillApi } from '@/app/erplus/api/adv/mas/skill'
import { Search, Refresh, Loading, Monitor, Calendar, Operation } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import SkillInstanceDetailDrawer from './SkillInstanceDetailDrawer.vue'

const allSkills = ref<any[]>([])
const filterSkill = ref('')
const filterAsin = ref('')

const list = ref<any[]>([])
const loading = ref(false)
const noMore = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

const drawerVisible = ref(false)
const selectedInstance = ref<any>(null)

// 获取列表数据
const getList = async () => {
  if (loading.value || noMore.value) return
  
  loading.value = true
  try {
    const res = await MasSkillApi.getSkillInstancePage({
      pageNo: pageNum.value,
      pageSize: pageSize.value,
      skillCode: filterSkill.value,
      targetBizId: filterAsin.value
    })
    
    if (res && res.list) {
      if (pageNum.value === 1) {
        list.value = res.list
      } else {
        list.value.push(...res.list)
      }
      if (res.list.length < pageSize.value) {
        noMore.value = true
      }
    } else {
      noMore.value = true
    }
  } catch (error) {
    console.error('Failed to fetch skill instances:', error)
  } finally {
    loading.value = false
  }
}

// 加载更多
const loadMore = () => {
  if (!loading.value && !noMore.value && list.value.length > 0) {
    pageNum.value++
    getList()
  }
}

// 刷新列表 (重置分页)
const refreshList = () => {
  pageNum.value = 1
  list.value = []
  noMore.value = false
  getList()
}

// 重置查询条件
const resetQuery = () => {
  filterSkill.value = ''
  filterAsin.value = ''
  refreshList()
}

const fetchAllSkills = async () => {
  try {
    const res = await getSkillList()
    allSkills.value = res || []
  } catch (error) {
    console.error('Failed to fetch skills', error)
  }
}

const handleShowDetails = (instance: any) => {
  selectedInstance.value = instance
  drawerVisible.value = true
}

onMounted(() => {
  getList()
  fetchAllSkills()
})

const getStatusType = (status: string) => {
  if (status === '运行中') return 'success'
  if (status === '已暂停') return 'warning'
  if (status === '已结束') return 'info'
  return 'info'
}

const formatDate = (date: any) => {
  if (!date) return '--'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const handleStop = (item: any) => {
  ElMessageBox.confirm(
    `确定要停止对商品 ${item.targetBizId} 的 ${item.skillName} 策略托管吗？`,
    '停止确认',
    {
      confirmButtonText: '确定停止',
      cancelButtonText: '我再想想',
      type: 'warning',
    }
  ).then(() => {
    ElMessage.success('停止指令已下发')
  })
}
</script>

<style scoped lang="scss">
.active-skill-list {
  padding: 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
  flex: 1;
  width: 100%;
  box-sizing: border-box;
}

.filter-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.filter-left {
  display: flex;
  gap: 12px;
}

.skill-select, .asin-input {
  width: 200px;
}

.active-skills-container {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
}

.instance-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.instance-card {
  border: 1px solid #ebeef5;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 4px;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  }

  :deep(.el-card__body) {
    padding: 0;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-bottom: 1px solid #f0f2f5;

    .header-left {
      .skill-brand {
        display: flex;
        align-items: center;
        gap: 16px;

        .skill-icon {
          font-size: 24px;
          color: #409eff;
          padding: 10px;
          background: #ecf5ff;
          border-radius: 10px;
        }

        .skill-title {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .skill-name {
            font-size: 17px;
            font-weight: 600;
            color: #1d1d1f;
          }

          .skill-subtitle {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 13px;
            
            .asin-label {
              color: #8e8e93;
            }
            .asin-value {
              color: #409eff;
              font-weight: 600;
              font-family: 'JetBrains Mono', monospace;
            }
          }
        }
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 20px;

      .time-info {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: #8e8e93;
      }
    }
  }

  .card-body {
    padding: 16px 20px;

    .progress-section {
      background: #f8f9fb;
      padding: 14px 18px;
      border-radius: 10px;
      border-left: 4px solid #409eff;

      .section-label {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 12px;
        font-weight: 600;
        color: #909399;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        margin-bottom: 8px;
      }

      .progress-text {
        margin: 0;
        font-size: 14px;
        color: #484b4f;
        line-height: 1.6;
        font-weight: 500;
      }
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    background: #fafafa;
    border-top: 1px solid #f0f2f5;

    .process-id {
      font-size: 11px;
      color: #c0c4cc;
      font-family: monospace;
    }

    .footer-right {
      display: flex;
      gap: 16px;
    }
  }
}

.loading-state, .no-more-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  color: #909399;
  font-size: 14px;
}

.is-loading {
  font-size: 24px;
  margin-bottom: 8px;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
