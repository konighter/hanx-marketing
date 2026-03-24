<template>
  <el-dialog title="Material Dialog Mock" v-model="visible">
    <p>This is a mocked component. The original materialDialog.vue was missing from the codebase.</p>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'

const visible = ref(false)

const show = () => {
  visible.value = true
}

defineExpose({ show })
</script>
