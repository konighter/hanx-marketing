<template>
  <div class="product-copilot-dashboard">
    <el-row :gutter="20">
      <!-- 左侧: 宏观状态大盘 -->
      <el-col :span="8">
        <el-card class="status-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="title">🤖 AI 副驾驶状态</span>
              <el-tag :type="isRunning ? 'success' : 'info'">
                {{ isRunning ? '自动托管中' : '已暂停' }}
              </el-tag>
            </div>
          </template>
          
          <div class="status-content">
            <h3 class="skill-name">正在执行: 「火箭飙升」新品冷启动</h3>
            
            <div class="progress-section">
              <div class="progress-label">当前阶段: Phase 2 数据清退</div>
              <el-progress :percentage="30" :stroke-width="12" status="success" />
              <div class="progress-desc">周期进度: 第 7 天 / 共 30 天</div>
            </div>

            <div class="progress-section">
              <div class="progress-label">授权预算消耗</div>
              <el-progress :percentage="21" :stroke-width="12" color="#e6a23c" />
              <div class="progress-desc">$ 210 / $ 1000</div>
            </div>
            
            <div class="action-buttons">
              <el-button v-if="isRunning" type="warning" plain @click="pauseSkill">暂停托管</el-button>
              <el-button v-else type="primary" plain @click="resumeSkill">恢复托管</el-button>
              <el-button type="danger" text @click="stopSkill">强制终止策略</el-button>
            </div>
          </div>
        </el-card>

        <el-card class="params-card" shadow="never" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>🎯 目标配置 (实时微调)</span>
              <el-button type="primary" link size="small">更新目标</el-button>
            </div>
          </template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="预期目标 ROAS">
              <el-input-number v-model="targetRoas" :min="0" :step="0.1" size="small" />
            </el-descriptions-item>
            <el-descriptions-item label="最高单次点击">
              <el-input v-model="maxBid" size="small">
                <template #prepend>$</template>
              </el-input>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 右侧: 决策回溯流 -->
      <el-col :span="16">
        <SkillDecisionTimeline />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SkillDecisionTimeline from './SkillDecisionTimeline.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const isRunning = ref(true)
const targetRoas = ref(1.5)
const maxBid = ref(2.5)

function pauseSkill() {
  isRunning.value = false
  ElMessage.warning('人工中断：AI 托管已暂停。')
}

function resumeSkill() {
  isRunning.value = true
  ElMessage.success('托管恢复：AI 已重新接管。')
}

function stopSkill() {
  ElMessageBox.confirm(
    '提前终止策略后，无法恢复当前进度，是否继续？',
    '强制终止提示',
    {
      confirmButtonText: '终止',
      cancelButtonText: '取消',
      type: 'error',
    }
  ).then(() => {
    isRunning.value = false
    ElMessage.info('策略已强行终止。')
  }).catch(() => {})
}
</script>

<style scoped>
.product-copilot-dashboard {
  padding: 24px;
}
.status-card, .params-card {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-weight: 600;
  color: #0f172a;
}
.skill-name {
  color: #3b82f6;
  font-size: 16px;
  font-weight: 600;
  margin-top: 0;
  margin-bottom: 24px;
}
.progress-section {
  margin-bottom: 24px;
}
.progress-label {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  margin-bottom: 8px;
}
.progress-desc {
  font-size: 13px;
  color: #64748b;
  text-align: right;
  margin-top: 6px;
}
.action-buttons {
  margin-top: 32px;
  display: flex;
  justify-content: space-between;
}
</style>
