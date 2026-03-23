<template>
  <el-dialog 
    v-model="visible" 
    :title="skill?.name" 
    width="60%"
    :destroy-on-close="true"
  >
    <div v-if="skill" class="skill-detail-content">
      <!-- 策略说明 -->
      <section class="detail-section">
        <h4 class="section-title">策略说明</h4>
        <p class="section-desc">{{ skill.description }}</p>
        <p class="section-desc text-muted" v-if="summaryData?.description">
          <strong>原理简介：</strong>{{ summaryData.description }}
        </p>
      </section>
      
      <!-- 阶段流转解析 -->
      <section class="detail-section">
        <h4 class="section-title">阶段白盒流转解析 (Timeline Flow)</h4>
        <div class="workflow-steps" v-if="summaryData?.phases && summaryData.phases.length > 0">
          <el-steps :active="summaryData.phases.length" align-center finish-status="success">
            <el-step 
              v-for="(phase, index) in summaryData.phases" 
              :key="index"
              :title="phase.name"
              :description="phase.description"
            />
          </el-steps>
        </div>
        <div v-else class="empty-hint">暂无分阶段说明</div>
      </section>

      <!-- 参数要求 -->
      <section class="detail-section">
        <h4 class="section-title">托管参数要求</h4>
        <div class="param-list" v-if="parsedSchema && Object.keys(parsedSchema).length > 0">
          <ul>
            <li v-for="(propDef, propName) in parsedSchema" :key="propName" class="param-item">
              <div class="param-header">
                <strong>{{ propDef.label || propName }}</strong>
                <span class="param-name">({{ propName }})</span>
                <el-tag size="small" type="info" class="ml-2">{{ propDef.type || 'string' }}</el-tag>
                <el-tag size="small" :type="propDef.required ? 'danger' : 'info'" class="ml-2">
                  {{ propDef.required ? '必填' : '选填' }}
                </el-tag>
              </div>
              <p class="param-desc" v-if="propDef.description">{{ propDef.description }}</p>
            </li>
          </ul>
        </div>
        <div v-else class="empty-hint">该策略无需额外参数配置</div>
      </section>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
        <el-button type="primary" @click="handleActivate">激活应用至商品</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  modelValue: boolean;
  skill: any;
}>()

const emit = defineEmits(['update:modelValue', 'activate'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 从 skill.strategyInstruction.summary 中提取数据
const summaryData = computed(() => {
  if (!props.skill || !props.skill.strategyInstruction || !props.skill.strategyInstruction.summary) {
    return null
  }
  return props.skill.strategyInstruction.summary
})

// 解析 param_schema JSON 字符串
const parsedSchema = computed(() => {
  if (!props.skill || !props.skill.paramSchema) {
    return {}
  }
  try {
    return JSON.parse(props.skill.paramSchema)
  } catch (e) {
    console.error("Failed to parse paramSchema", e)
    return {}
  }
})

function handleActivate() {
  visible.value = false
  emit('activate', props.skill)
}
</script>

<style scoped>
.skill-detail-content {
  padding: 0 16px;
}
.detail-section {
  margin-bottom: 32px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
  border-left: 4px solid #3b82f6;
  padding-left: 8px;
}
.section-desc {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
}
.text-muted {
  color: #64748b;
  margin-top: 8px;
}
.workflow-steps {
  margin-top: 24px;
  padding: 24px 0;
  background-color: #f8fafc;
  border-radius: 8px;
}
.param-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.param-item {
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  margin-bottom: 12px;
  background-color: #fafafa;
}
.param-header {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}
.param-name {
  color: #64748b;
  font-size: 13px;
  margin-left: 8px;
  font-family: monospace;
}
.ml-2 {
  margin-left: 8px;
}
.param-desc {
  margin: 0;
  font-size: 13px;
  color: #475569;
}
.empty-hint {
  font-size: 13px;
  color: #94a3b8;
  font-style: italic;
  padding: 12px 0;
}
</style>
