<script setup lang="ts">

import {TabsPaneContext} from "element-plus";

const activeName = ref('base')
const scrollbarRef = ref<any>()

const scroll = (tab: TabsPaneContext, event: Event) => {
  console.log(activeName.value)
  const el = document.getElementById(activeName.value)
  console.log(el, el?.offsetTop)
  scrollbarRef.value.setScrollTop(el?.offsetTop)
  console.log(tab, event)

}


</script>

<template>
<ContentWrap>

  <el-row >
    <el-col :xl="4" :lg="2" class="ri-tabs" >
      <el-tabs tab-position="left"  v-model="activeName" @tabClick="scroll">
        <el-tab-pane label="基本信息" name="base" />
        <!-- v-if="router.currentRoute.value.query.isAssemable" -->
        <el-tab-pane  label="组合信息" name="composite"  />
        <el-tab-pane  label="采购信息" name="purchase"  />
        <el-tab-pane label="规格信息" name="spec"  />
        <el-tab-pane label="辅料关联" name="material"  />
        <el-tab-pane label="物流信息"  name="ship" />
      </el-tabs>
    </el-col>

    <el-col :xl="16" :lg="21">
        <el-scrollbar ref="scrollbarRef" >
          <h1 id="base">基本信息</h1>
          <div class="box">基本信息</div>
          <h1 id="composite">组合信息</h1>
          <div class="box">组合信息</div>
          <h1 id="purchase">采购信息</h1>
          <div class="box">采购信息</div>
          <h1 id="spec">规格信息</h1>
          <div class="box">规格信息</div>
          <h1 id="material">辅料信息</h1>
          <div class="box">辅料信息</div>
          <h1 id="ship">物流信息</h1>
          <div class="box">物流信息</div>


        </el-scrollbar>





    </el-col>

  </el-row>


</ContentWrap>


</template>

<style scoped lang="scss">

.box {
  min-height: 200px;
}

</style>
