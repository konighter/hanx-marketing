import request from '@/config/axios'

/**
 * 获取聚合 KPI 汇总
 */
export const getDashboardSummary = async () => {
  return await request.get({ url: `/erplus/dashboard/summary` })
}

/**
 * 获取业务预警列表
 */
export const getDashboardAlerts = async () => {
  return await request.get({ url: `/erplus/dashboard/alerts` })
}

/**
 * 获取图表分析数据
 */
export const getDashboardCharts = async () => {
  return await request.get({ url: `/erplus/dashboard/charts` })
}
