import request from '@/config/axios'

export const AdsOptimizationRuleApi = {
  // 创建优化规则
  createOptimizationRule: async (data: any) => {
    return await request.post({ url: '/erplus/adv/ads/rules/optimization', data })
  },

  // 关联优化规则
  associateOptimizationRules: async (campaignId: number, data: any) => {
    return await request.post({ url: `/erplus/adv/sp/campaigns/${campaignId}/optimizationRules`, data })
  },

  // 获取优化规则列表
  getOptimizationRuleList: async () => {
    return await request.get({ url: '/erplus/adv/ads/rules/optimization/list' })
  }
}
