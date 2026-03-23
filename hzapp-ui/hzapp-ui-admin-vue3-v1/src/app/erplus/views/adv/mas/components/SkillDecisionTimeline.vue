<template>
  <el-card shadow="never" class="timeline-card">
    <template #header>
      <div class="card-header">
        <span>🕒 MAS Agent 决策回溯流</span>
        <el-tag size="small" type="info">每 1 小时记录</el-tag>
      </div>
    </template>
    
    <div class="timeline-container">
      <el-timeline>
        <!-- Mocked Events from MAS Engine -->
        <el-timeline-item
          v-for="(event, index) in mockEvents"
          :key="index"
          :timestamp="event.time"
          :type="event.type"
          :hollow="index !== 0"
          :size="index === 0 ? 'large' : 'normal'"
        >
          <div class="event-capsule">
            <h4 class="event-title">{{ event.title }}</h4>
            
            <div class="event-body">
              <div class="log-section observation" v-if="event.observation">
                <strong>[观测] Observation:</strong>
                <p>{{ event.observation }}</p>
              </div>
              
              <div class="log-section thought" v-if="event.thought">
                <strong>[思考] Thought:</strong>
                <p>{{ event.thought }}</p>
              </div>
              
              <div class="log-section action" v-if="event.action">
                <strong>[动作] Action:</strong>
                <el-alert :title="event.action" type="success" :closable="false" show-icon />
              </div>

              <!-- 如果遇到问题挂起，例如 REVIEW_REQUIRED -->
              <div class="log-section suspend" v-if="event.suspendReason">
                <strong>[挂起] SUSPEND:</strong>
                <p>等待唤醒原因：{{ event.suspendReason }}</p>
              </div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const mockEvents = ref([
  {
    time: '2026-03-12 08:30:00',
    type: 'primary',
    title: 'Phase 2 迭代优化执行完毕',
    observation: '从近 48 小时的报表获取到：关键词 "wireless fast charger" 花费 $15, 转化率 0%。关键词 "dock station" 转化率 15%, ROAS=2.1。',
    thought: '根据技能规则 [新品打法阶段2]："若转化率为0且花费较高，需立即精准否定"。对跑得好的核心词："进行提取并在新组提高竞价"。',
    action: '执行了 2 笔调价工具 API 调用: [Negative Exact "wireless fast charger"], [Create Exact Match "dock station", bid=$1.8]',
    suspendReason: '已完成今日迭代，设定 nextExecuteAt = "明天早上 08:30" 进入 SUSPEND 状态收集数据。'
  },
  {
    time: '2026-03-11 08:30:00',
    type: 'success',
    title: '系统唤醒，重新加载缓存成功',
    observation: '发现任务到达了执行时间，开始提取全局上下文 memory...',
    thought: '',
    action: '加载 GlobalSessionMemory 完毕，PlannerAgent 开始规划。'
  },
  {
    time: '2026-03-10 09:00:00',
    type: 'info',
    title: 'Phase 1 基础建设完成',
    observation: '成功通过 工具创建了 1 个 Auto 广告组。',
    thought: '初始数据空白，按照新建策略逻辑需要至少跑 48 小时让亚马逊分配流量。',
    action: '不进行任何调价动作。',
    suspendReason: '等待初次流量涌入，设定 nextExecuteAt = "后天 08:30" 进入 SUSPEND 状态。'
  },
  {
    time: '2026-03-10 08:50:00',
    type: 'warning',
    title: '用户人工授权激活策略',
    observation: '用户初始化，载入设置：ASIN=B0C12345, 总预算=$1000, 目标ROAS=1.5。',
    thought: '解析 Strategy Schema 成功。',
    action: '生成首个 DAG 图，包含建广告计划任务。'
  }
])
</script>

<style scoped>
.timeline-card {
  height: 100%;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}
.card-header {
  font-weight: 600;
  color: #0f172a;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.timeline-container {
  height: 600px;
  overflow-y: auto;
  padding: 16px 10px;
}
.event-capsule {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}
.event-title {
  margin-top: 0;
  margin-bottom: 16px;
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
}
.log-section {
  margin-bottom: 12px;
  font-size: 13px;
  line-height: 1.6;
}
.log-section strong {
  color: #475569;
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
}
.log-section p {
  margin: 0;
  color: #334155;
}
.observation p {
  background-color: #eff6ff;
  padding: 10px 12px;
  border-radius: 4px;
  border-left: 3px solid #3b82f6;
}
.thought p {
  background-color: #fefce8;
  padding: 10px 12px;
  border-radius: 4px;
  border-left: 3px solid #eab308;
}
.suspend p {
  color: #64748b;
  font-style: italic;
}
</style>
