<template>
  <div class="expandable-container w-full!">
    <div
ref="contentRef" class="content-text" :class="{ 'expanded': isExpanded, 'collapsed-fixed': !isExpanded }"
      :style="!isExpanded ? { maxHeight: collapsedHeight + 'px' } : { maxHeight: '9999px' }">
      <slot></slot>
    </div>
    <div class="flex justify-center">
      <el-button link @click="toggleExpand" class="toggle-btn" type="info">
        {{ isExpanded ? '收起' : '展开更多' }}
        <el-icon class="icon" :class="{ 'rotated': isExpanded }">
          <ArrowDown />
        </el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'

const props = defineProps({
  collapsedHeight: {
    type: Number,
    default: 120
  }
})

const isExpanded = ref(false)
const showToggleButton = ref(false)
const contentRef = ref(null)

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}

const checkOverflow = () => {
  if (contentRef.value) {
    const scrollHeight = contentRef.value.scrollHeight
    showToggleButton.value = scrollHeight > props.collapsedHeight
  }
}

onMounted(() => {
  nextTick(checkOverflow)
  window.addEventListener('resize', checkOverflow)
})
</script>

<style scoped>
.expandable-container {
  width: 100%;
  border: none;
  padding: 15px;
}

.content-text {
  overflow: hidden;
  transition: max-height 0.5s ease-in-out, opacity 0.5s ease-in-out;
  /* 添加 opacity 过渡 */
  opacity: 1;
  /* 默认不透明度 */
}

.collapsed-fixed {
  /* max-height 由 style 动态控制 */
}

.expanded {
  max-height: 9999px;
  /* 使用一个较大的值 */
  opacity: 1;
  /* 展开时完全不透明 */
}

.collapsed {
  opacity: 0;
  /* 收起时透明 */
}

.toggle-btn {
  margin-top: 10px;
}

.icon {
  margin-left: 5px;
  transition: transform 0.1s;
}

.icon.rotated {
  transform: rotate(180deg);
}
</style>