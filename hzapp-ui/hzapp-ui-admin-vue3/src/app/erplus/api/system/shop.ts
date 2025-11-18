import request from '@/config/axios'

// 店铺信息 VO
export interface ShopVO {
  id: number // ID
  name: string // 名称
  platform: number // 平台
  region: number // 区域
  status: number // 状态
  platformName: string // 平台
  regionName: string // 区域
}

export interface ShopAuthReqVO {
  id: number // shopId
  selfAuth: number // 自授权
  appkey: string // 应用key
  appSecret: string // 应用密钥
  refreshToken: string // 令牌
}

// 店铺信息 API
export const ShopApi = {
  // 查询店铺信息分页
  getShopPage: async (params: any) => {
    return await request.get({ url: `/erplus/cross/shop/page`, params })
  },

  getShopList: async (params: any) => {
    return await request.get({ url: `/erplus/cross/shop/list`, params })
  },

  getPlatformShop: async (id: any) => {
    return await request.get({ url: `/erplus/cross/shop/platform?id=${id}` })
  },

  // 查询店铺信息详情
  getShop: async (id: number) => {
    return await request.get({ url: `/erplus/cross/shop/get?id=` + id })
  },

  // 新增店铺信息
  createShop: async (data: ShopVO) => {
    return await request.post({ url: `/erplus/cross/shop/create`, data })
  },

  // 修改店铺信息
  updateShop: async (data: ShopVO) => {
    return await request.put({ url: `/erplus/cross/shop/update`, data })
  },

  // 删除店铺信息
  deleteShop: async (id: number) => {
    return await request.delete({ url: `/erplus/cross/shop/delete?id=` + id })
  },

  // 导出店铺信息 Excel
  exportShop: async (params) => {
    return await request.download({ url: `/erplus/cross/shop/export-excel`, params })
  },

  submitShopAuth: async (data : ShopAuthReqVO) => {
    return await request.post({ url: '/erplus/cross/shop/auth', data})
  },
}
