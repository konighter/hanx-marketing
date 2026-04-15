import request from '@/config/axios'
import type { Dayjs } from 'dayjs';

/** 亚马逊报告任务信息 */
export interface AmzReportTask {
          id: number; // ID
          reportId: string; // 报告ID
          query: string; // 查询条件
          status: number; // 查询状态
          reportResult: string; // 报告结果
          lastCheckTime: string | Dayjs; // 上次检索时间
          isArchive: number; // 结果是否已归档
  }

// 亚马逊报告任务 API
export const AmzReportTaskApi = {
  // 查询亚马逊报告任务分页
  getAmzReportTaskPage: async (params: any) => {
    return await request.get({ url: `/erplus/amz-report/page`, params })
  },

  checkAmzReportTaskStatus: async (id: number) => {
    return await request.get({ url: `/erplus/amz-report/check?id=` + id })
  },

  // 查询亚马逊报告任务详情
  getAmzReportTask: async (id: number) => {
    return await request.get({ url: `/erplus/amz-report/get?id=` + id })
  },

  // 新增亚马逊报告任务
  createAmzReportTask: async (data: AmzReportTask) => {
    return await request.post({ url: `/erplus/amz-report/create`, data })
  },

}

export const periods = [
  { label: '天', value: 'DAY' },
  { label: '周', value: 'WEEK' },
  { label: '月', value: 'MONTH' },
  { label: '年', value: 'YEAR' }
]

export const reportTypes = [
  { label: '搜索目录绩效报告', value: 'GET_BRAND_ANALYTICS_SEARCH_CATALOG_PERFORMANCE_REPORT' },
  { label: '搜索查询绩效报告', value: 'GET_BRAND_ANALYTICS_SEARCH_QUERY_PERFORMANCE_REPORT' },
  { label: '商城购买行为分析报告', value: 'GET_BRAND_ANALYTICS_MARKET_BASKET_REPORT' },
  { label: '亚马逊搜索词报告', value: 'GET_BRAND_ANALYTICS_SEARCH_TERMS_REPORT' },
  { lable: '重复购买', value: 'GET_BRAND_ANALYTICS_REPEAT_PURCHASE_REPORT' },
  { lable: '亚马逊库存报告', value: 'GET_LEDGER_DETAIL_VIEW_DATA' }
]

export const reportStatus = [
  { label: '取消', value: 0 },
  { label: '成功', value: 1 },
  { label: '失败', value: 2 },
  { label: '处理中', value: 3 },
  { label: '排队中', value: 4 }
]