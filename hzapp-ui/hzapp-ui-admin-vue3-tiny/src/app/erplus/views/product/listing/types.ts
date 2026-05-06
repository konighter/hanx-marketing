export interface ListingDiagnosisVO {
  score: number           // 素材健康分 (0-100)
  issues: Array<{
    type: 'warning' | 'info' | 'error'
    label: string         // 诊断标签，如 "主图非纯白"
    description?: string  // 详细改进建议
  }>
}

export interface ListingPerformanceVO {
  sales7d: number         // 7日销量
  sales30d: number        // 30日销量
  gmv30d: number          // 30日 GMV
  sales7dGrowth?: number  // 7日销量增长率
  revenueCurve: number[]  // 趋势图数据点
}

export interface ListingV2VO {
  id: number
  platformId: number
  shopId: number
  platformProductCode: string
  sellerProductCode: string
  title: string
  status: string
  statusName: string
  mainImage?: { url: string }
  prices?: { salePrice: number; currency: string; type: string }[]
  // 扩展字段
  diagnosis?: ListingDiagnosisVO
  performance?: ListingPerformanceVO
  variants?: any[]
  // 刊登状态扩展
  syncStatus?: number
  syncStatusName?: string
  latestTaskId?: number
  inventory?: {
    fulfillableQuantity: number
    reservedQuantity: number
    inboundShippedQuantity: number
    unfulfillableQuantity: number
  }
}

export interface CrossProductSyncRequest {
  platformId?: number
  shopId: number
  syncType: string
  createTimeStart?: string
  createTimeEnd?: string
  productIds?: string[]
}
