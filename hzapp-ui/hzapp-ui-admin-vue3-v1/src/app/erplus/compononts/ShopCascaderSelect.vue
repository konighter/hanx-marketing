<template>
  <el-cascader v-model="modelValue" :options="options" :props="cascaderProps" :placeholder="placeholder" clearable
    filterable @change="handleChange" />
</template>

<script setup lang="ts">
import { ShopApi } from '@/app/erplus/api/system/shop'

defineOptions({ name: 'ShopCascaderSelect' })

const props = defineProps({
  placeholder: {
    type: String,
    default: '请选择店铺'
  }
})

const emit = defineEmits(['change'])

const modelValue = defineModel<number | string>()
const options = ref<any[]>([])

const cascaderProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  emitPath: false,
  checkStrictly: false
}

/** 获取店铺列表（后端已分组） */
const getTreeData = async () => {
  const data = await ShopApi.getCascaderShopList()
  options.value = data
}

const handleChange = (value) => {
  // 找到选中的 shop 对象并抛出 change 事件
  const shop = findShop(options.value, value)
  emit('change', shop)
}

const findShop = (nodes: any[], id: any): any => {
  if (!nodes) return null
  for (const node of nodes) {
    if (node.id === id) return node
    if (node.children) {
      const found = findShop(node.children, id)
      if (found) return found
    }
  }
  return null
}

onMounted(() => {
  getTreeData()
})
</script>
