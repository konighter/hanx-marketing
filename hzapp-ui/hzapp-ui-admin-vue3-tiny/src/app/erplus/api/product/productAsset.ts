import request from '@/config/axios'
import type { Dayjs } from 'dayjs';

/** 商品素材信息 */
export interface ProductAssets {
          id: number; // ID
          productId?: number; // 产品ID
          productName?: number; // 产品名称
          tags: string; // 标签
          type?: number; // 素材类型
          source?: number; // 素材来源
          assetLink?: string; // 素材链接
          assetInfo: string; // 素材信息
          status?: number; // 状态
  }
/**
 * 素材来源
 */
export const AssetSourceEnum = {
  UPLOAD: 10, // 进行中
  AI: 20, // 已完成
}


// 商品素材 API
export const ProductAssetsApi = {
  // 查询商品素材分页
  getProductAssetsPage: async (params: any) => {
    return await request.get({ url: `/erplus/product-assets/page`, params })
  },

  // 查询商品素材详情
  getProductAssets: async (id: number) => {
    return await request.get({ url: `/erplus/product-assets/get?id=` + id })
  },

  // 新增商品素材
  createProductAssets: async (data: ProductAssets) => {
    return await request.post({ url: `/erplus/product-assets/create`, data })
  },

  // 修改商品素材
  updateProductAssets: async (data: ProductAssets) => {
    return await request.put({ url: `/erplus/product-assets/update`, data })
  },

  // 删除商品素材
  deleteProductAssets: async (id: number) => {
    return await request.delete({ url: `/erplus/product-assets/delete?id=` + id })
  },

  /** 批量删除商品素材 */
  deleteProductAssetsList: async (ids: number[]) => {
    return await request.delete({ url: `/erplus/product-assets/delete-list?ids=${ids.join(',')}` })
  },

  // 导出商品素材 Excel
  exportProductAssets: async (params) => {
    return await request.download({ url: `/erplus/product-assets/export-excel`, params })
  }
}