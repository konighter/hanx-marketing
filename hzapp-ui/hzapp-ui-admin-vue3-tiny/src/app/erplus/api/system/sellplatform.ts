import request from '@/config/axios'

// 销售平台 API
export const SellPlatformApi = {
  // 查询销售平台分页
  getSellPlatformPage: async (params: any) => {
    return await request.get({ url: `/erplus/sell-platform/page`, params })
  },

  // 查询销售平台列表
  getSellPlatformList: async (params: any) => {
    return await request.get({ url: `/erplus/sell-platform/list`, params })
  },

  // 查询销售平台详情
  getSellPlatform: async (id: number) => {
    return await request.get({ url: `/erplus/sell-platform/get?id=` + id })
  },

  // 新增销售平台
  createSellPlatform: async (data: any) => {
    return await request.post({ url: `/erplus/sell-platform/create`, data })
  },

  // 修改销售平台
  updateSellPlatform: async (data: any) => {
    return await request.put({ url: `/erplus/sell-platform/update`, data })
  },

  // 删除销售平台
  deleteSellPlatform: async (id: number) => {
    return await request.delete({ url: `/erplus/sell-platform/delete?id=` + id })
  },

  // 导出销售平台 Excel
  exportSellPlatform: async (params: any) => {
    return await request.download({ url: `/erplus/sell-platform/export-excel`, params })
  }
}
