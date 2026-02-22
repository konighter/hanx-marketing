import request from '@/config/axios'
import type { Dayjs } from 'dayjs';

/** 亚马逊广告活动信息 */
export interface AmzAdvCampaign {
          id: number; // ID
          shopId?: string; // 店铺ID
          campaignId: string; // 广告活动ID (Amazon侧)
          name?: string; // 广告活动名称
          syncStatus: number; // 同步状态(0-未同步，1-同步中，2-已同步，3-同步失败)
          lastSyncTime: string | Dayjs; // 最后同步时间
          syncErrorMsg: string; // 同步错误信息
          state: string; // 广告活动状态(enabled,paused,archived)
          campaignType: string; // 广告类型
          dailyBudget: number; // 每日预算
          biddingStrategy: string; // 出价策略
          startDate: string | Dayjs; // 开始日期
          endDate: string | Dayjs; // 结束日期
          targetingType: string; // 目标市场(auto,manual)
          campaignSubType: string; // 广告系列类型
          description: string; // 描述
          tags: string; // 标签
  }

// 亚马逊广告活动 API
export const AmzAdvCampaignApi = {
  // 查询亚马逊广告活动分页
  getAmzAdvCampaignPage: async (params: any) => {
    return await request.get({ url: `/erplus/amz-adv-campaign/page`, params })
  },

  // 查询亚马逊广告活动详情
  getAmzAdvCampaign: async (id: number) => {
    return await request.get({ url: `/erplus/amz-adv-campaign/get?id=` + id })
  },

  // 新增亚马逊广告活动
  createAmzAdvCampaign: async (data: AmzAdvCampaign) => {
    return await request.post({ url: `/erplus/amz-adv-campaign/create`, data })
  },

  // 修改亚马逊广告活动
  updateAmzAdvCampaign: async (data: AmzAdvCampaign) => {
    return await request.put({ url: `/erplus/amz-adv-campaign/update`, data })
  },

  // 删除亚马逊广告活动
  deleteAmzAdvCampaign: async (id: number) => {
    return await request.delete({ url: `/erplus/amz-adv-campaign/delete?id=` + id })
  },

  /** 批量删除亚马逊广告活动 */
  deleteAmzAdvCampaignList: async (ids: number[]) => {
    return await request.delete({ url: `/erplus/amz-adv-campaign/delete-list?ids=${ids.join(',')}` })
  },

  // 导出亚马逊广告活动 Excel
  exportAmzAdvCampaign: async (params) => {
    return await request.download({ url: `/erplus/amz-adv-campaign/export-excel`, params })
  },

  // 同步广告活动到亚马逊
  syncCampaignToAmazon: async (params: { shopId: string; id?: number; startDate?: string; endDate?: string }) => {
    return await request.post({ url: `/erplus/amz-adv-campaign/sync-to-amazon`, data: params })
  },

  // 批量同步广告活动到亚马逊
  syncCampaignListToAmazon: async (params: { shopId: string; ids?: number[]; startDate?: string; endDate?: string }) => {
    return await request.post({ url: `/erplus/amz-adv-campaign/sync-list-to-amazon`, data: params })
  }
}