<template>
  <div class="shipment-label">
    <el-result icon="success" title="货件已成功配置" sub-title="现在您可以下载标签并打印了">
      <template #extra>
        <div class="flex flex-col gap-4 items-center">
          <div class="flex gap-4">
            <el-form label-width="100px" inline>
              <el-form-item label="标签格式">
                <el-select v-model="labelFormat" class="w-150px">
                  <el-option label="PDF" value="pdf" />
                  <el-option label="PNG" value="png" />
                </el-select>
              </el-form-item>
              <el-form-item label="标签尺寸">
                <el-select v-model="pageSize" class="w-150px">
                  <el-option label="4 x 6 in" value="4x6" />
                  <el-option label="A4" value="a4" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>

          <div class="flex gap-4">
            <el-button type="primary" @click="downloadBoxLabels">下载箱子标签</el-button>
            <el-button type="primary" plain @click="downloadShipmentLabels">下载货件标签</el-button>
          </div>
        </div>
      </template>
    </el-result>

    <div class="mt-40px text-center">
      <el-button @click="$emit('finish')">完成并返回</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  planId: {
    type: String,
    required: true
  },
  active: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['back', 'finish'])

const labelFormat = ref('pdf')
const pageSize = ref('4x6')

const downloadBoxLabels = async () => {
  ElMessage.info('正在生成箱子标签...')
  // Simulate download
  window.open('https://example.com/labels/box.pdf', '_blank')
}

const downloadShipmentLabels = async () => {
  ElMessage.info('正在生成货件标签...')
  // Simulate download
  window.open('https://example.com/labels/shipment.pdf', '_blank')
}
</script>

<style scoped></style>
