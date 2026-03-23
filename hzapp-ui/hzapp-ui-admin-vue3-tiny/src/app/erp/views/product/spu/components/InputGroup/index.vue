

<template>

  <div v-for="(item, index) in dynamicVals" :key="index" class="flex items-center">
    <el-input class="mr-5"  v-model="item.value"></el-input> <span class="flex mr-5"> <Icon class="mr-5px" icon="ep:plus" /> <Icon class="mr-5px" icon="ep:minus" /> </span>
  </div>



</template>


<script setup lang="ts">

  import {PropType} from "vue";

  defineOptions( {name : 'InputGroupUtil'} )

  interface InputVal {
     index: number
     value: string
  }

  const props = defineProps({
    data: {
      type: Array as PropType<InputVal[]>,
      default: () => [{
        index: 0,
        value: ''
      }]
    },
  })

  const dynamicVals = ref([''])



</script>


<style scoped lang="scss">

</style>
