import request from '@/config/axios'

export interface PerformanceReq {
  accountId?: number
  shopId?: number
  entityType?: 'CAMPAIGN' | 'ADGROUP' | 'AD' | 'KEYWORD' | 'ACCOUNT'
  entityId?: number
  startDate: string
  endDate: string
  timeUnit: 'day' | 'week' | 'month'
}

export interface PerformanceResp {
  metrics: Record<string, any>
  trends: Record<string, number>
  spend: number
  impressions: number
  clicks: number
  sales: number
  orders: number
  roas: number
  cpc: number
}

export interface TrendResp {
  timeList: string[]
  seriesData: Record<string, any[]>
}

export interface AdsReportQueryReq {
  shopId?: number
  startDate: string
  endDate: string
  dimensions?: string[]
  metrics?: string[]
  platforms?: string[]
  campaignIds?: number[]
  adGroupIds?: number[]
  adIds?: number[]
  keywordIds?: number[]
  productIds?: string[]
  limit?: number
  orderBy?: string
  isAsc?: boolean
}

export interface DimensionValue {
  key: string
  value: string
  label?: string
}

export interface MetricValue {
  key: string
  value: any
}

export interface AdsReportRow {
  dimensions: DimensionValue[]
  metrics: MetricValue[]
}

export interface AdsReportDataResp {
  rows: AdsReportRow[]
  summary: AdsReportRow
  total: number
}

export const AdsReportApi = {
  // 获得结构化聚合数据 (OLAP)
  queryAdsReport: (data: AdsReportQueryReq) => {
    return request.post({ url: '/erplus/adv/report/query', data })
  },

  // 获得核心指标卡片数据
  getPerformanceScorecard: (params: PerformanceReq) => {
    return request.post({ url: '/erplus/adv/report/scorecard', data: params })
  },

  // 获得指标趋势图数据
  getPerformanceTrend: (params: PerformanceReq) => {
    return request.post({ url: '/erplus/adv/report/trend', data: params })
  },

  // 获得下钻层级数据
  getPerformanceDrilldown: (params: PerformanceReq) => {
    return request.post({ url: '/erplus/adv/report/drilldown', data: params })
  }
}
