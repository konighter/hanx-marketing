<template>
  <div class="flex gap-2">
    <span>
      <el-tag
        v-for="tag in dynamicTags"
        :key="tag"
        :closable="closeable"
        :disable-transitions="false"
        @close="handleClose(tag)"
      >
          {{ tag }}
        </el-tag>
    </span>

    <span v-if="!isDetail">
      <el-input
        v-if="inputVisible"
        ref="InputRef"
        v-model="inputValue"
        class="w-20"
        size="small"
        @keyup.enter="handleInputConfirm"
        @blur="handleInputConfirm"
      />
    <el-button v-else class="button-new-tag" size="small" @click="showInput">
      +
    </el-button>
    </span>

  </div>
</template>

<script lang="ts" setup>

import {PropType} from "vue";
import {propTypes} from "@/utils/propTypes";

defineOptions({name: 'MultiTag'})
const props = defineProps({
  data: {
    type: Array as PropType<string[]>,
    default: () => []
  },
  isDetail: {
    type: Boolean,
    default: false,
  } // 是否作为详情组件
})


const closeable = ref(true)
const inputValue = ref('')
const dynamicTags = ref<string[]>([])
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()

const handleClose = (tag: string) => {
  dynamicTags.value.splice(dynamicTags.value.indexOf(tag), 1)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value!.input!.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    dynamicTags.value.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

watch(() => props.data, (newVal) => {
  dynamicTags.value = props.data
})


</script>
