import request from '@/config/axios'
import { AdsAccount, AdsCampaign, AdsAdGroup, AdsAd, AdsKeyword, AmazonProfile } from '../../views/adv/types/ads'

// 广告授权相关 API
export const AdsAuthApi = {
  // 获得授权链接
  getAuthorizeUrl: async (params: { platform: string; shopId: number; state?: string }): Promise<string> => {
    return await request.get({ url: `/erplus/adv/auth/authorize-url`, params })
  },

  // 刷新授权
  refreshAuth: async (id: number): Promise<void> => {
    return await request.post({ url: `/erplus/adv/auth/refresh-token?id=${id}` })
  },

  // 获得广告账号分页
  getAccountPage: async (params: any) => {
    return await request.get({ url: `/erplus/adv/auth/account/page`, params })
  },
  // 获得亚马逊广告 Profile 列表
  getAmazonProfileList: async (accountId: number) => {
    return await request.get({ url: '/erplus/adv/amazon-profile/list', params: { accountId } })
  },
  // 初始化亚马逊广告 Stream 订阅
  initStream: async (accountId: number) => {
    return await request.get({ url: '/erplus/adv/amazon-profile/init-stream', params: { accountId } })
  },
  // 从亚马逊广告 API 同步 Profile 数据
  syncProfiles: async (accountId: number) => {
    return await request.post({ url: '/erplus/adv/amazon-profile/sync-profiles', params: { accountId } })
  },
  // 处理授权回调
  handleCallback: async (params: { platform: string; code: string; state?: string }) => {
    return await request.get({ url: `/erplus/adv/auth/callback`, params })
  },
  // 解除授权
  deleteAccount: async (id: number) => {
    return await request.delete({ url: `/erplus/adv/auth/account/delete?id=${id}` })
  }
}

// 广告计划 API
export const AdsCampaignApi = {
  getCampaignPage: async (data: any) => {
    return await request.post({ url: `/erplus/adv/campaign/page`, data })
  },
  getCampaign: async (id: number) => {
    return await request.get({ url: `/erplus/adv/campaign/get?id=${id}` })
  },
  updateCampaignStatus: async (params: { id: number; status: string }) => {
    return await request.put({ url: `/erplus/adv/campaign/update-status`, params })
  },
  updateCampaignBudget: async (params: { id: number; budget: number }) => {
    return await request.put({ url: `/erplus/adv/campaign/update-budget`, params })
  },
  updateCampaign: async (data: any) => {
    return await request.put({ url: `/erplus/adv/campaign/update`, data })
  }
}

// 广告组 API
export const AdsAdGroupApi = {
  getAdGroupPage: async (data: any) => {
    return await request.post({ url: `/erplus/adv/ad-group/page`, data })
  },
  getAdGroup: async (id: number) => {
    return await request.get({ url: `/erplus/adv/ad-group/get?id=${id}` })
  },
  updateAdGroupStatus: async (params: { id: number; status: string }) => {
    return await request.put({ url: `/erplus/adv/ad-group/update-status`, params })
  }
}

// 广告 API
export const AdsAdApi = {
  getAdPage: async (data: any) => {
    return await request.post({ url: `/erplus/adv/ad/page`, data })
  },
  getAd: async (id: number) => {
    return await request.get({ url: `/erplus/adv/ad/get?id=${id}` })
  },
  updateAdStatus: async (params: { id: number; status: string }) => {
    return await request.put({ url: `/erplus/adv/ad/update-status`, params })
  }
}

// 关键词 API
export const AdsKeywordApi = {
  getKeywordPage: async (data: any) => {
    return await request.post({ url: `/erplus/adv/keyword/page`, data })
  },
  getKeyword: async (id: number) => {
    return await request.get({ url: `/erplus/adv/keyword/get?id=${id}` })
  },
  updateKeywordStatus: async (params: { id: number; status: string }) => {
    return await request.put({ url: `/erplus/adv/keyword/update-status`, params })
  },
  updateKeywordBid: async (params: { id: number; bid: number }) => {
    return await request.put({ url: `/erplus/adv/keyword/update-bid`, params })
  }
}

// 同步 API
export const AdsSyncApi = {
  syncAllMetadata: async (accountId: number) => {
    return await request.post({ url: `/erplus/adv/sync/metadata-all?accountId=${accountId}` })
  },
  syncIncrMetadata: async (accountId: number) => {
    return await request.post({ url: `/erplus/adv/sync/metadata-incr?accountId=${accountId}` })
  },
  syncAllMetadataByShop: async (shopId: number) => {
    return await request.post({ url: `/erplus/adv/sync/metadata-all?shopId=${shopId}` })
  },
  syncIncrMetadataByShop: async (shopId: number) => {
    return await request.post({ url: `/erplus/adv/sync/metadata-incr?shopId=${shopId}` })
  },
  syncMetadataByCampaign: async (campaignId: number) => {
    return await request.post({ url: `/erplus/adv/sync/metadata-campaign?campaignId=${campaignId}` })
  },
}
