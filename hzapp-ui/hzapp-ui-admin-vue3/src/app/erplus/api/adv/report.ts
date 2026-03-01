import request from '@/config/axios'

export interface PerformanceReq {
  accountId?: number
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

export const AdsReportApi = {
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
