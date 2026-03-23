import request from '@/config/axios'

// 产品监控 VO
export interface ProductMonitorVO {
  id: number // ID
  productId: string // 产品ID
  platformId: number // 平台
  productLink: string // 产品链接
  imageLink: string // 产品主图
  categoryId: number // 品类ID
  crone: string // 周期(默认Day)
  status: number // 状态
}

// 产品监控 API
export const ProductMonitorApi = {
  // 查询产品监控分页
  getProductMonitorPage: async (params: any) => {
    return await request.get({ url: `/erplus/product-monitor/page`, params })
  },

  // 查询产品监控详情
  getProductMonitor: async (id: number) => {
    return await request.get({ url: `/erplus/product-monitor/get?id=` + id })
  },

  // 新增产品监控
  createProductMonitor: async (data: ProductMonitorVO) => {
    return await request.post({ url: `/erplus/product-monitor/create`, data })
  },

  // 修改产品监控
  updateProductMonitor: async (data: ProductMonitorVO) => {
    return await request.put({ url: `/erplus/product-monitor/update`, data })
  },

  // 删除产品监控
  deleteProductMonitor: async (id: number) => {
    return await request.delete({ url: `/erplus/product-monitor/delete?id=` + id })
  },

  // 导出产品监控 Excel
  exportProductMonitor: async (params) => {
    return await request.download({ url: `/erplus/product-monitor/export-excel`, params })
  }
}