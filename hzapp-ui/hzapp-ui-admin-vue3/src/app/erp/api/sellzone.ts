import request from '@/config/axios'

// 销售区域 VO
export interface SellZoneVO {
  id: number // ID
  platformId: number // 销售平台
  zoneName: string // 区域名称
  zoneCode: string // 区域编码
  locale: string // 区域Locale
  language: string // 语言
}

// 销售区域 API
export const SellZoneApi = {
  // 查询销售区域分页
  getSellZonePage: async (params: any) => {
    return await request.get({ url: `/erplus/sell-zone/page`, params })
  },

  // 查询销售区域列表
  getSellZoneList: async (params: any) => {
    return await request.get({ url: `/erplus/sell-zone/list`, params })
  },


  // 查询销售区域详情
  getSellZone: async (id: number) => {
    return await request.get({ url: `/erplus/sell-zone/get?id=` + id })
  },

  // 新增销售区域
  createSellZone: async (data: SellZoneVO) => {
    return await request.post({ url: `/erplus/sell-zone/create`, data })
  },

  // 修改销售区域
  updateSellZone: async (data: SellZoneVO) => {
    return await request.put({ url: `/erplus/sell-zone/update`, data })
  },

  // 删除销售区域
  deleteSellZone: async (id: number) => {
    return await request.delete({ url: `/erplus/sell-zone/delete?id=` + id })
  },

  // 导出销售区域 Excel
  exportSellZone: async (params) => {
    return await request.download({ url: `/erplus/sell-zone/export-excel`, params })
  }
}
