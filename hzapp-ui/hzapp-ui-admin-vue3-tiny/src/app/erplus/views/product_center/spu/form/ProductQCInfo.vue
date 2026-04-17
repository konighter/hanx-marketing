<template>
  <div class="product-qc-info">
    <div class="section-header mb-6">质检标准与备注</div>
    <el-form-item label="质检准则说明">
      <Editor v-model="modelValue.attributes.compliance.qcStandards" height="300px" />
    </el-form-item>
    <el-form-item label="内部质检备注">
      <el-input v-model="modelValue.attributes.compliance.qcRemark" type="textarea" :rows="4" />
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { Editor } from '@/components/Editor'

const modelValue = defineModel<any>({ required: true })
</script>

<script lang="ts">
export default {
  name: 'ProductQCInfo'
}
</script>

<style scoped lang="scss">
.section-header {
  font-size: 14px;
  font-weight: 700;
  color: #1a202c;
  position: relative;
  padding-left: 10px;
  display: flex;
  align-items: center;
  &::before {
    content: "";
    position: absolute;
    left: 0;
    top: 2px;
    bottom: 2px;
    width: 3px;
    background: #3b82f6;
    border-radius: 2px;
  }
}
</style>
