import request from '@/config/axios'

/**
 * 亚马逊广告辅助接口 API
 */
export const AmzAdvHelpApi = {
  /**
   * 获取关键词推荐
   */
  getKeywordRecommendations: async (data: {
    shopId: number
    recommendationType: 'KEYWORDS_FOR_ADGROUP' | 'KEYWORDS_FOR_ASINS'
    asins?: string[]
    campaignId?: string
    adGroupId?: string
    targets?: string[]
    maxRecommendations?: number
    sortDimension?: 'CLICKS' | 'CONVERSIONS' | 'DEFAULT'
    locale?: string
  }) => {
    return await request.post({ url: `/erplus/amz/adv/v3/help/keyword/recommendations`, data })
  },

  /**
   * 获取否定品牌推荐
   */
  getNegativeBrandRecommendations: async (data: {
    shopId: number
  }) => {
    return await request.post({ url: `/erplus/amz/adv/v3/help/negative-brand/recommendations`, data })
  },

  /**
   * 获取可投放类目
   */
  getTargetableCategories: async (data: {
    shopId: number
    locale?: string
  }) => {
    return await request.post({ url: `/erplus/amz/adv/v3/help/targetable-categories`, data })
  }
}
