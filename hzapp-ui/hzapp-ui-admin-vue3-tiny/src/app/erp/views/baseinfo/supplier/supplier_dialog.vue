<template>
  <el-dialog title="Supplier Dialog Mock" v-model="visible">
    <p>This is a mocked component. The original supplier_dialog.vue was missing from the codebase.</p>
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
