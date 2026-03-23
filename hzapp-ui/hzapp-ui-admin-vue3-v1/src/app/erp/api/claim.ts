import request from '@/config/axios'

// 商品认领 VO
export interface ProductClaimVO {
  id: number // ID
  spuId: number // 商品ID
  skuInfo: string // sku信息
  specType: number // 变种类型
  platform: number // 平台类型
  language: string // 语言
  siteId: number // 站点
  category: string // 品类
  brandId: number // 品牌
  sellPrice: number // 售价，多变种填最大值
  currency: string // 币种
  extra: Object // 扩展信息
  status: number // 状态， 0-认领， 1-已同步， 9-同步失败
}

// 商品认领 API
export const ProductClaimApi = {
  // 查询商品认领分页
  getProductClaimPage: async (params: any) => {
    return await request.get({ url: `/erplus/product-claim/page`, params })
  },

  // 查询商品认领详情
  getProductClaim: async (id: number) => {
    return await request.get({ url: `/erplus/product-claim/get?id=` + id })
  },

  // 新增商品认领
  createProductClaim: async (data: ProductClaimVO) => {
    return await request.post({ url: `/erplus/product-claim/create`, data })
  },

  // 新增商品认领
  batchProductClaim: async (data) => {
    return await request.post({ url: `/erplus/product-claim/batch`, data })
  },

  // 修改商品认领
  updateProductClaim: async (data: ProductClaimVO) => {
    return await request.put({ url: `/erplus/product-claim/update`, data })
  },

  // 删除商品认领
  deleteProductClaim: async (id: number) => {
    return await request.delete({ url: `/erplus/product-claim/delete?id=` + id })
  },

  // 导出商品认领 Excel
  exportProductClaim: async (params) => {
    return await request.download({ url: `/erplus/product-claim/export-excel`, params })
  }
}
