<!-- 跨境电商ERP - 商品管理 -->
<template>
  <doc-alert title="跨境电商ERP - 商品管理模块" url="#" />

  <el-tabs v-model="activeTab" class="demo-tabs">
    <el-tab-pane label="商品收集" name="collect">
      <ProductCollect v-if="activeTab === 'collect'" />
    </el-tab-pane>
    <el-tab-pane label="商品管理" name="manage">
      <ProductManage v-if="activeTab === 'manage'" />
    </el-tab-pane>
    <el-tab-pane label="平台商品同步" name="sync">
      <ProductSync v-if="activeTab === 'sync'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ProductCollect from './compononts/ProductCollect.vue'
import ProductManage from './compononts/ProductManage.vue'
import ProductSync from './compononts/ProductSync.vue'

/** 跨境电商ERP - 商品管理 */
defineOptions({ name: 'ErplusProduct' })

// 当前激活的标签页
const activeTab = ref('collect')
</script>