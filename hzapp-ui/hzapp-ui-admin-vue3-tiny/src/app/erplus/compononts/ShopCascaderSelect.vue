<template>
  <el-cascader
    v-model="modelValue"
    :options="options"
    :props="cascaderProps"
    :placeholder="placeholder"
    :clearable="clearable"
    :filterable="filterable"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import { ShopApi } from '@/app/erplus/api/system/shop'

defineOptions({ name: 'ShopCascaderSelect' })

const props = defineProps({
  placeholder: {
    type: String,
    default: '请选择平台 / 店铺'
  },
  multiple: {
    type: Boolean,
    default: false
  },
  emitPath: {
    type: Boolean,
    default: false // 默认 false 以保持与旧页面兼容
  },
  checkStrictly: {
    type: Boolean,
    default: false
  },
  clearable: {
    type: Boolean,
    default: true
  },
  filterable: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['change'])

const modelValue = defineModel<any>()
const options = ref<any[]>([])

const cascaderProps = computed(() => ({
  value: 'id',
  label: 'name',
  children: 'children',
  multiple: props.multiple,
  emitPath: props.emitPath,
  checkStrictly: props.checkStrictly
}))

/** 
 * 递归过滤掉没有叶子节点的父节点
 * 如果一个节点没有 children 或者 children 经过过滤后为空，且它本身不是可以选中的叶子（在当前业务场景下只有第二层是叶子），则剔除
 */
const filterEmptyNodes = (nodes: any[]): any[] => {
  if (!nodes) return []
  return nodes
    .map(node => {
      if (node.children && node.children.length > 0) {
        const filteredChildren = filterEmptyNodes(node.children)
        if (filteredChildren.length > 0) {
          return { ...node, children: filteredChildren }
        }
      }
      // 如果没有子节点且它是叶子节点（shop），则保留；如果是父节点（platform）但没有子节点，则剔除
      // 注意：这里的后端接口 getCascaderShopList 通常第一层是平台，第二层是店铺
      // 如果后端数据结构更深，需要根据业务逻辑判断“叶子”的特征
      return node.children ? null : node
    })
    .filter(node => node !== null)
}

/** 获取店铺列表 */
const getTreeData = async () => {
  try {
    const data = await ShopApi.getCascaderShopList()
    options.value = filterEmptyNodes(data)
  } catch (e) {
    console.error('Failed to load shop cascader list:', e)
  }
}

const handleChange = (value: any) => {
  // 发出选中对象的详细信息（方便外部业务逻辑）
  if (props.multiple) {
    // 多选模式下：value 为数组的数组 [[p1, s1], [p1, s2]]
    const selectedNodes = Array.isArray(value) 
      ? value.map(path => findNodeByPath(options.value, path))
      : []
    emit('change', selectedNodes)
  } else {
    // 单选模式下
    const node = findNodeByValueOrPath(options.value, value)
    emit('change', node)
  }
}

const findNodeByPath = (nodes: any[], path: any[]): any => {
  if (!path || path.length === 0) return null
  let currentSelection: any = null
  let currentNodes = nodes
  for (const id of path) {
    const found = currentNodes.find(n => n.id === id)
    if (found) {
      currentSelection = found
      currentNodes = found.children || []
    } else {
      return null
    }
  }
  return currentSelection
}

const findNodeByValueOrPath = (nodes: any[], value: any): any => {
  if (Array.isArray(value)) {
    return findNodeByPath(nodes, value)
  }
  // 如果只是 ID
  return findShopById(nodes, value)
}

const findShopById = (nodes: any[], id: any): any => {
  if (!nodes) return null
  for (const node of nodes) {
    if (node.id === id) return node
    if (node.children) {
      const found = findShopById(node.children, id)
      if (found) return found
    }
  }
  return null
}

onMounted(() => {
  getTreeData()
})
</script>
