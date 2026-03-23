import request from '@/config/axios'

export interface ProductMetricsVO {
                    id: number
                    monitorId: number
                    productId: string
                    platformId: number
                    datekey: number
                    metircs: string
}


export interface MetricsDefVO {
            name: string
            field: string
}

// 查询产品监控指标列表
export const getProductMetricsPage = async (data) => {
  return await request.post({ url: '/erplus/product-monitor/metrics-data/page', data })
}


export const getMetricsDef = async (data) => {
  return await request.post<MetricsDefVO[]>({ url: '/erplus/product-monitor/metrics-def' , data})
} 