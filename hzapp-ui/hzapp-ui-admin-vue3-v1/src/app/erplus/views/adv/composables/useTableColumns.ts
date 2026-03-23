import { ref, computed, watch, type Ref, readonly, onMounted, onUnmounted, shallowRef } from 'vue'
import type { TableColumn, TableColumnType } from '../types/columns'

// 表格类型常量
export const TABLE_TYPE = {
  CAMPAIGN: 'campaign',
  AD_GROUP: 'adGroup',
  AD: 'ad'
} as const

export type TableType = typeof TABLE_TYPE[keyof typeof TABLE_TYPE]

// 默认列配置映射表
export const defaultColumnsMap: Record<TableType, TableColumn[]> = {
  campaign: [
    { key: 'selection', label: '选择框', visible: true, fixed: 'left', width: 55, required: true },
    { key: 'id', label: 'ID', visible: true, width: 80 },
    { key: 'name', label: '活动名称', visible: true, minWidth: 150 },
    { key: 'campaignId', label: 'Amazon活动ID', visible: true, width: 120 },
    { key: 'state', label: '状态', visible: true, width: 100 },
    { key: 'syncStatus', label: '同步状态', visible: true, width: 100 },
    { key: 'campaignType', label: '广告类型', visible: true, width: 120 },
    { key: 'dailyBudget', label: '每日预算', visible: true, width: 100 },
    { key: 'biddingStrategy', label: '出价策略', visible: true, width: 120 },
    { key: 'startDate', label: '开始时间', visible: true, width: 120 },
    { key: 'endDate', label: '结束时间', visible: true, width: 120 },
    { key: 'lastSyncTime', label: '最后同步', visible: true, width: 120 },
    { key: 'operation', label: '操作', visible: true, fixed: 'right', width: 180, required: true }
  ],
  adGroup: [
    { key: 'selection', label: '选择框', visible: true, fixed: 'left', width: 55, required: true },
    { key: 'id', label: 'ID', visible: true, width: 80 },
    { key: 'name', label: '广告组名称', visible: true, minWidth: 150 },
    { key: 'adGroupId', label: 'Amazon广告组ID', visible: true, width: 120 },
    { key: 'campaignName', label: '所属活动', visible: true, minWidth: 150 },
    { key: 'state', label: '状态', visible: true, width: 100 },
    { key: 'defaultBid', label: '默认出价', visible: true, width: 100 },
    { key: 'targetingType', label: '目标类型', visible: true, width: 100 },
    { key: 'syncStatus', label: '同步状态', visible: true, width: 100 },
    { key: 'lastSyncTime', label: '最后同步', visible: true, width: 120 },
    { key: 'operation', label: '操作', visible: true, fixed: 'right', width: 180, required: true }
  ],
  ad: [
    { key: 'selection', label: '选择框', visible: true, fixed: 'left', width: 55, required: true },
    { key: 'id', label: 'ID', visible: true, width: 80 },
    { key: 'name', label: '广告名称', visible: true, minWidth: 150 },
    { key: 'adId', label: 'Amazon广告ID', visible: true, width: 120 },
    { key: 'adGroupName', label: '所属广告组', visible: true, minWidth: 150 },
    { key: 'sku', label: '商品SKU', visible: true, width: 120 },
    { key: 'asin', label: '商品ASIN', visible: true, width: 120 },
    { key: 'adType', label: '广告类型', visible: true, width: 100 },
    { key: 'state', label: '状态', visible: true, width: 100 },
    { key: 'bid', label: '出价', visible: true, width: 100 },
    { key: 'syncStatus', label: '同步状态', visible: true, width: 100 },
    { key: 'lastSyncTime', label: '最后同步', visible: true, width: 120 },
    { key: 'operation', label: '操作', visible: true, fixed: 'right', width: 180, required: true }
  ]
}

// 获取 storage key
export const getStorageKey = (tableType: TableType): string => `table-columns-${tableType}`

// 共享状态缓存 - 确保同一 tableType 的所有实例共享相同状态
const sharedStateCache = new Map<TableType, {
  columns: Ref<TableColumn[]>
  initialized: boolean
}>()

export type TableColumnsState = {
  columns: TableColumn[]
  visibleColumns: TableColumn[]
  searchTerm: string
}

export type UseTableColumnsOptions = {
  tableType: TableType
  onUpdate?: (visibleColumns: TableColumn[]) => void
}

export function useTableColumns(options: UseTableColumnsOptions) {
  const { tableType, onUpdate } = options
  
  // 获取或创建共享状态
  let cache = sharedStateCache.get(tableType)
  if (!cache) {
    cache = {
      columns: ref<TableColumn[]>([]),
      initialized: false
    }
    sharedStateCache.set(tableType, cache)
  }
  
  const columns = cache.columns
  const searchTerm = ref('')
  const isLoading = ref(false)

  // 从localStorage加载配置
  const loadFromStorage = (): TableColumn[] | null => {
    const storageKey = getStorageKey(tableType)
    try {
      const stored = localStorage.getItem(storageKey)
      return stored ? JSON.parse(stored) : null
    } catch (error) {
      console.warn(`Failed to load columns from localStorage:`, error)
      return null
    }
  }

  // 保存配置到localStorage
  const saveToStorage = (columnList: TableColumn[]) => {
    const storageKey = getStorageKey(tableType)
    try {
      localStorage.setItem(storageKey, JSON.stringify(columnList))
      // 触发自定义事件通知其他组件
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new CustomEvent('table-columns-updated', {
          detail: { key: storageKey, tableType }
        }))
      }
    } catch (error) {
      console.warn(`Failed to save columns to localStorage:`, error)
    }
  }

  // 初始化列配置
  const initializeColumns = () => {
    // 如果已经初始化过且有缓存数据，不再重新初始化
    if (cache.initialized && columns.value?.length > 0) {
      return
    }
    
    isLoading.value = true
    
    const storedColumns = loadFromStorage()
    if (storedColumns && storedColumns?.length > 0) {
      // 合并存储的配置和默认配置，确保新增列能够显示
      const mergedColumns = defaultColumnsMap[tableType].map(defaultCol => {
        const storedCol = storedColumns.find(col => col.key === defaultCol.key)
        return {
          ...defaultCol,
          visible: storedCol ? storedCol.visible : defaultCol.visible,
          width: storedCol?.width || defaultCol.width,
          fixed: storedCol?.fixed || defaultCol.fixed
        }
      })
      columns.value = mergedColumns
    } else {
      columns.value = [...defaultColumnsMap[tableType]]
    }
    
    cache.initialized = true
    isLoading.value = false
  }

  // 重置为默认配置
  const resetToDefault = () => {
    columns.value = [...defaultColumnsMap[tableType]]
    saveToStorage(columns.value)
  }

  // 切换列的显示状态
  const toggleColumnVisibility = (columnKey: string) => {
    const column = columns.value.find(col => col.key === columnKey)
    if (column && !column.required) {
      column.visible = !column.visible
      saveToStorage(columns.value)
    }
  }

  // 全选/反选
  const toggleAllColumns = (visible: boolean) => {
    columns.value.forEach(column => {
      if (!column.required) {
        column.visible = visible
      }
    })
    saveToStorage(columns.value)
  }

  // 更新列配置
  const updateColumn = (columnKey: string, updates: Partial<TableColumn>) => {
    const column = columns.value.find(col => col.key === columnKey)
    if (column) {
      Object.assign(column, updates)
      saveToStorage(columns.value)
    }
  }

  // 计算可见列
  const visibleColumns = computed(() => {
    return columns.value
      .filter(column => column.visible)
      .sort((a, b) => (a.order || 0) - (b.order || 0))
  })

  // 计算可选列
  const availableColumns = computed(() => {
    return columns.value.filter(column => 
      column.label.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      column.description?.toLowerCase().includes(searchTerm.value.toLowerCase())
    )
  })

  // 计算选中数量
  const selectedCount = computed(() => {
    return columns.value.filter(col => col.visible)?.length
  })

  // 计算可选数量
  const optionalCount = computed(() => {
    return columns.value.filter(col => !col.required)?.length
  })

  // 导出配置
  const exportConfig = () => {
    const config = {
      columns: columns.value.map(col => ({
        key: col.key,
        visible: col.visible,
        width: col.width,
        fixed: col.fixed,
        order: col.order
      })),
      exportTime: new Date().toISOString()
    }
    return config
  }

  // 导入配置
  const importConfig = (config: any) => {
    if (!config.columns || !Array.isArray(config.columns)) {
      throw new Error('Invalid configuration format')
    }

    const mergedColumns = columns.value.map(currentCol => {
      const importCol = config.columns.find((col: any) => col.key === currentCol.key)
      if (importCol) {
        return {
          ...currentCol,
          visible: importCol.visible !== undefined ? importCol.visible : currentCol.visible,
          width: importCol.width || currentCol.width,
          fixed: importCol.fixed || currentCol.fixed,
          order: importCol.order || currentCol.order
        }
      }
      return currentCol
    })

    columns.value = mergedColumns
    saveToStorage(columns.value)
  }

  // 获取列信息
  const getColumn = (columnKey: string) => {
    return columns.value.find(col => col.key === columnKey)
  }

  // 监听可见列变化
  watch(visibleColumns, (newVisibleColumns) => {
    if (onUpdate) {
      onUpdate(newVisibleColumns)
    }
  }, { deep: true })

  // 监听自定义事件，同步其他组件的更新
  const handleCustomEvent = (e: CustomEvent) => {
    if (e.detail?.tableType === tableType || e.detail?.key === getStorageKey(tableType)) {
      // 重新加载配置
      const storedColumns = loadFromStorage()
      if (storedColumns && storedColumns?.length > 0) {
        const mergedColumns = defaultColumnsMap[tableType].map(defaultCol => {
          const storedCol = storedColumns.find(col => col.key === defaultCol.key)
          return {
            ...defaultCol,
            visible: storedCol ? storedCol.visible : defaultCol.visible,
            width: storedCol?.width || defaultCol.width,
            fixed: storedCol?.fixed || defaultCol.fixed
          }
        })
        columns.value = mergedColumns
      }
    }
  }

  onMounted(() => {
    initializeColumns()
    window.addEventListener('table-columns-updated', handleCustomEvent)
  })

  onUnmounted(() => {
    window.removeEventListener('table-columns-updated', handleCustomEvent)
  })

  return {
    // 状态
    columns: readonly(columns),
    visibleColumns,
    availableColumns,
    searchTerm,
    isLoading,
    selectedCount,
    optionalCount,

    // 方法
    initializeColumns,
    resetToDefault,
    toggleColumnVisibility,
    toggleAllColumns,
    updateColumn,
    exportConfig,
    importConfig,
    getColumn,

    // 搜索
    setSearchTerm: (term: string) => {
      searchTerm.value = term
    }
  }
}

// 只读版本 - 使用共享状态缓存确保同步
export function useTableColumnsReadonly(tableType: TableType) {
  // 获取或创建共享状态
  let cache = sharedStateCache.get(tableType)
  if (!cache) {
    cache = {
      columns: ref<TableColumn[]>([]),
      initialized: false
    }
    sharedStateCache.set(tableType, cache)
  }
  
  const columns = cache.columns
  const storageKey = getStorageKey(tableType)
  
  const loadFromStorage = () => {
    try {
      const stored = localStorage.getItem(storageKey)
      if (stored) {
        // 合并存储的配置和默认配置
        const storedColumns = JSON.parse(stored)
        columns.value = defaultColumnsMap[tableType].map(defaultCol => {
          const storedCol = storedColumns.find((col: TableColumn) => col.key === defaultCol.key)
          return {
            ...defaultCol,
            visible: storedCol ? storedCol.visible : defaultCol.visible,
            width: storedCol?.width || defaultCol.width,
            fixed: storedCol?.fixed || defaultCol.fixed
          }
        })
      } else {
        // 没有存储数据时使用默认配置
        columns.value = [...defaultColumnsMap[tableType]]
      }
      cache!.initialized = true
    } catch (error) {
      console.warn(`Failed to load columns from localStorage:`, error)
      columns.value = [...defaultColumnsMap[tableType]]
      cache!.initialized = true
    }
  }

  const visibleColumns = computed(() => {
    return columns.value
      .filter(column => column.visible)
      .sort((a, b) => (a.order || 0) - (b.order || 0))
  })

  // 监听自定义事件，同步其他组件的更新
  const handleCustomEvent = (e: CustomEvent) => {
    if (e.detail?.tableType === tableType || e.detail?.key === storageKey) {
      loadFromStorage()
    }
  }

  // 监听 localStorage 变化（同窗口其他标签页）
  const handleStorageChange = (e: StorageEvent) => {
    if (e.key === storageKey) {
      loadFromStorage()
    }
  }

  onMounted(() => {
    // 如果还没有初始化，加载数据
    if (!cache!.initialized) {
      loadFromStorage()
    }
    window.addEventListener('storage', handleStorageChange)
    window.addEventListener('table-columns-updated', handleCustomEvent)
  })

  onUnmounted(() => {
    window.removeEventListener('storage', handleStorageChange)
    window.removeEventListener('table-columns-updated', handleCustomEvent)
  })

  // 提供手动刷新方法
  const refresh = () => {
    loadFromStorage()
  }

  return {
    visibleColumns,
    columns: readonly(columns),
    refresh
  }
}