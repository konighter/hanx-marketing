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

// 店铺信息 API
export const ShopApi = {
  // 查询店铺信息分页
  getShopPage: async (params: any) => {
    return await request.get({ url: `/ov/shop/page`, params })
  },

  getShopList: async (params: any) => {
    return await request.get({ url: `/ov/shop/list`, params })
  },

  // 查询店铺信息详情
  getShop: async (id: number) => {
    return await request.get({ url: `/ov/shop/get?id=` + id })
  },

  // 新增店铺信息
  createShop: async (data: ShopVO) => {
    return await request.post({ url: `/ov/shop/create`, data })
  },

  // 修改店铺信息
  updateShop: async (data: ShopVO) => {
    return await request.put({ url: `/ov/shop/update`, data })
  },

  // 删除店铺信息
  deleteShop: async (id: number) => {
    return await request.delete({ url: `/ov/shop/delete?id=` + id })
  },

  // 导出店铺信息 Excel
  exportShop: async (params) => {
    return await request.download({ url: `/ov/shop/export-excel`, params })
  }
}
