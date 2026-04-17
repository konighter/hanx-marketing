<template>
  <el-drawer
    v-model="visible"
    :title="title"
    size="85%"
    direction="rtl"
    :destroy-on-close="true"
    class="product-form-drawer"
  >
    <ProductFormV2 ref="formRef" @close="visible = false" @save="handleSave" />
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import ProductFormV2 from '../form/ProductFormV2.vue'
import * as ProductSpuApi from '@/app/erplus/api/product/spu'

const visible = ref(false)
const formRef = ref()
const title = ref('添加产品')

const open = async (entryMode: string, specificType: string, id?: number) => {
  const isEdit = !!id
  const prefix = isEdit ? '编辑' : '添加'
  
  if (entryMode === 'SKU') {
    title.value = specificType === 'ORDINARY' ? `${prefix} SKU (单品)` : `${prefix} SKU (组合)`
  } else {
    title.value = specificType === 'SINGLE' ? `${prefix} SPU (单规格)` : `${prefix} SPU (多规格/变体)`
  }

  visible.value = true
  await nextTick()
  formRef.value?.initForm(entryMode, specificType, id)
}

const handleSave = async (data: any) => {
  try {
    if (data.id) {
      await ProductSpuApi.updateSpu(data)
    } else {
      await ProductSpuApi.createSpu(data)
    }
    message.success('保存成功')
    visible.value = false
    emit('success')
  } catch (error) {
    console.error(error)
  }
}

const emit = defineEmits(['success'])
defineExpose({ open })
const message = useMessage()
</script>

<style lang="scss">
.product-form-drawer {
  .el-drawer__body {
    padding: 0;
    overflow: hidden;
  }
  .el-drawer__header {
    margin-bottom: 0;
    padding: 16px 24px;
    border-bottom: 1px solid #e5e7eb;
    font-weight: 600;
    color: #1a202c;
  }
}
</style>
