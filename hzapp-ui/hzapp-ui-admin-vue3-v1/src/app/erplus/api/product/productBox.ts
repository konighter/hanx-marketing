import request from '@/config/axios'

// 选品提案 VO
export interface ProductPotentialVO {

  id:  number   // ID
  title: string // 标题
  category: number  // 类目
  categoryName: string // 类目名称
  platformId: number // 售卖平台
  platformName: string // 售卖平台
  feature: string // 产品特性
  target: string // 目标人群
  sellPoints: string // 卖点
  competitorList: CompetitorVO[] // 竞品分析
  financeList: FinanceVO[] // 运营分析
  oneTimeInvest: 0, // 一次性投入
  analysisOpinion: string // 分析意见
  debriefOpinion: string // 复盘意见
  reviewProduct: string // 复盘产品
  status: number // 状态
  creator: string // 创建人
  createTime: string // 创建时间
  marks: string // 备注
}

// 选品提案 API
export const ProductPotentialApi = {
  // 查询选品提案分页
  getProductPotentialPage: async (params: any) => {
    return await request.get({ url: `/erplus/product-potential/page`, params })
  },

  // 查询选品提案详情
  getProductPotential: async (id: number) => {
    return await request.get({ url: `/erplus/product-potential/get?id=` + id })
  },

    // 查询选品提案详情
  getProductPotentialSimple: async (id: number) => {
    return await request.get({ url: `/erplus/product-potential/getSimple?id=` + id })
  },

    // 修改选品提案
  updateProductPotentialSimple: async (data: ProductPotentialVO) => {
    return await request.put({ url: `/erplus/product-potential/updateSimple`, data })
  },

  // 新增选品提案
  createProductPotential: async (data: ProductPotentialVO) => {
    return await request.post({ url: `/erplus/product-potential/create`, data })
  },

  // 修改选品提案
  updateProductPotential: async (data: ProductPotentialVO) => {
    return await request.put({ url: `/erplus/product-potential/update`, data })
  },

  // 删除选品提案
  deleteProductPotential: async (id: number) => {
    return await request.delete({ url: `/erplus/product-potential/delete?id=` + id })
  },

  // 导出选品提案 Excel
  exportProductPotential: async (params) => {
    return await request.download({ url: `/erplus/product-potential/export-excel`, params })
  }
}


export interface CompetitorVO { 
  platform?: string
  asin?: string
  listDate?: string
  price?: number
  monthSales?: number
  reviews?: number
  autoMonitor?: boolean 
  edit?: boolean
}

export interface FinanceVO {
    targetPrice?: number // 目标售价
    purchasePrice?: number // 采购价
    freight?: number // 运费
    commissionRate?: number // 平台佣金率
    fbaCost?: number // FBA费用
    cpc?: number // 预估CPC
    targetAcos?: number // 目标ACOS
    gpm?: number // 毛利率(估算)
    npm?: number // 净利率(%)
    edit: boolean
}

