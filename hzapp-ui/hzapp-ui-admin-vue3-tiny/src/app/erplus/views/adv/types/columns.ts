// 广告管理模块的列数据类型定义

export type TableColumnKey = 
  // 通用列
  | 'selection' | 'id' | 'name' | 'state' | 'syncStatus' | 'lastSyncTime' | 'operation'
  // 广告活动特有列
  | 'campaignId' | 'campaignType' | 'dailyBudget' | 'biddingStrategy' | 'startDate' | 'endDate'
  // 广告组特有列
  | 'adGroupId' | 'campaignName' | 'defaultBid' | 'targetingType'
  // 广告特有列
  | 'adId' | 'adGroupName' | 'sku' | 'asin' | 'adType' | 'bid'

export type TableColumnType = 'campaign' | 'adGroup' | 'ad'

export type ColumnFixedPosition = 'left' | 'right'

export interface TableColumn {
  key: TableColumnKey
  label: string
  visible: boolean
  width?: number
  minWidth?: number
  fixed?: ColumnFixedPosition
  required?: boolean
  disabled?: boolean
  description?: string
  order?: number
  formatter?: (value: any, row: any) => string
  sortable?: boolean
  resizable?: boolean
}

// 广告活动数据接口
export interface AdCampaign {
  id: number
  name: string
  campaignId: string
  state: 'enabled' | 'paused' | 'archived'
  syncStatus: 'synced' | 'pending' | 'failed'
  campaignType: string
  dailyBudget: number
  biddingStrategy: string
  startDate: string
  endDate?: string
  lastSyncTime: string
  createdAt: string
  updatedAt: string
}

// 广告组数据接口
export interface AdGroup {
  id: number
  name: string
  adGroupId: string
  campaignName: string
  campaignId: number
  state: 'enabled' | 'paused' | 'archived'
  syncStatus: 'synced' | 'pending' | 'failed'
  defaultBid: number
  targetingType: string
  lastSyncTime: string
  createdAt: string
  updatedAt: string
}

// 广告数据接口
export interface Ad {
  id: number
  name: string
  adId: string
  adGroupName: string
  adGroupId: number
  sku: string
  asin: string
  adType: string
  state: 'enabled' | 'paused' | 'archived'
  syncStatus: 'synced' | 'pending' | 'failed'
  bid: number
  lastSyncTime: string
  createdAt: string
  updatedAt: string
}

// 表格行数据联合类型
export type TableRow = AdCampaign | AdGroup | Ad

// 列配置更新事件
export interface ColumnUpdateEvent {
  type: TableColumnType
  visibleColumns: TableColumn[]
  updatedColumn?: TableColumn
}

// 列配置导入导出接口
export interface ColumnConfigExport {
  type: TableColumnType
  columns: Pick<TableColumn, 'key' | 'visible' | 'width' | 'fixed' | 'order'>[]
  exportTime: string
  version?: string
}

// 列配置预设接口
export interface ColumnPreset {
  id: string
  name: string
  description?: string
  type: TableColumnType
  columns: Pick<TableColumn, 'key' | 'visible' | 'width' | 'fixed' | 'order'>[]
  isDefault?: boolean
  isReadOnly?: boolean
  createdAt: string
  updatedAt: string
}

// 表格操作按钮配置
export interface TableAction {
  key: string
  label: string
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  icon?: string
  permission?: string
  handler: (row: TableRow) => void | Promise<void>
  disabled?: (row: TableRow) => boolean
  visible?: (row: TableRow) => boolean
}

// 表格配置接口
export interface TableConfig {
  type: TableColumnType
  columns: TableColumn[]
  actions: TableAction[]
  pagination?: {
    pageSize: number
    showSizePicker?: boolean
    pageSizes?: number[]
  }
  selection?: {
    enabled: boolean
    multiple?: boolean
    reserveSelection?: boolean
  }
  sorting?: {
    enabled: boolean
    defaultSort?: { prop: string; order: 'ascending' | 'descending' }
  }
  filtering?: {
    enabled: boolean
    filters?: Record<string, any>
  }
}

// 列状态管理器配置
export interface TableColumnManagerConfig {
  type: TableColumnType
  defaultColumns: TableColumn[]
  storageKey: string
  enablePersistence?: boolean
  enablePresets?: boolean
  enableImportExport?: boolean
  onColumnsChange?: (columns: TableColumn[]) => void
  onConfigReset?: () => void
}