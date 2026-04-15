import { ListingV2VO, ListingDiagnosisVO, ListingPerformanceVO } from './types'

/**
 * 模拟素材诊断数据
 */
export const getMockDiagnosis = (id: number): ListingDiagnosisVO => ({
  score: (id % 40) + 60,
  issues: [
    { type: 'warning', label: '主图非纯白背景', description: '建议更换为纯白色背景图以提升权重' },
    { type: 'info', label: '标题过短建议补充关键词', description: '增加关键词可提升搜索量' }
  ]
})

/**
 * 模拟业绩指标数据
 */
export const getMockPerformance = (): ListingPerformanceVO => ({
  sales7d: Math.floor(Math.random() * 500) + 100,
  sales30d: Math.floor(Math.random() * 2000) + 1000,
  gmv30d: Math.floor(Math.random() * 50000) + 5000,
  revenueCurve: Array.from({ length: 15 }, () => Math.floor(Math.random() * 100))
})

/**
 * 模拟变体数据
 */
export const getMockVariants = (sku: string) => [
  { sku: sku + '-BLK', spec: 'Black / Large', price: 8900, stock: 124 },
  { sku: sku + '-WHT', spec: 'White / Medium', price: 9500, stock: 23 },
  { sku: sku + '-GLD', spec: 'Gold / Small', price: 12000, stock: 0 }
]

/**
 * 为原始数据注入 Mock 字段
 */
export const injectMockData = (item: any): ListingV2VO => {
  return {
    ...item,
    diagnosis: getMockDiagnosis(item.id),
    performance: getMockPerformance(),
    mockVariants: getMockVariants(item.sellerProductCode)
  }
}
