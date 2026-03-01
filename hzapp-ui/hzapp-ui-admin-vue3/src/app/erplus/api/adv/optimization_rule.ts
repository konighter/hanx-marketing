import request from '@/config/axios'

export const AdsOptimizationRuleApi = {
  // 创建优化规则
  createOptimizationRule: async (data: any) => {
    return await request.post<string>({ url: '/erplus/adv/ads/rules/optimization', data })
  },

  // 获取优化规则列表
  getOptimizationRuleList: async () => {
    return await request.get({ url: '/erplus/adv/ads/rules/optimization/list' })
  }
}
