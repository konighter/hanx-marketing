import request from '@/config/axios'
import { cacheLoader } from '@/app/utils/cache'
import { useCache } from '@/hooks/web/useCache'
const { wsCache } = useCache()
const CACHE_KEY_PLATFORM_LIST = 'platformList'
const CACHE_KEY_SERVICE_MODES = 'serviceModes'

// 销售平台 VO
export interface SellPlatformVO {
  id: number // ID
  name: string // 平台名称
  code: string // 编码
  avatar: string // 头像
  serviceModes: string[] // 配送模式
}

export interface ServiceMode {
  name: string // 名称
  code: string // 编码
}

// 销售平台 API
export const SellPlatformApi = {
  // 查询销售平台分页
  getSellPlatformPage: async (params: any) => {
    return await request.get({ url: `/erplus/sell-platform/page`, params })
  },

  getSellPlatformList: async (params: any) => {
    return await request.get({ url: `/erplus/sell-platform/list`, params })
  },

  getServieModesCache: async () => {
    return await cacheLoader(CACHE_KEY_SERVICE_MODES, async () => {
      return await request.get({ url: `/erplus/sell-platform/serviceModes`})
    })
  },

  getSellPlatformListCache: async () => {
    return await cacheLoader(CACHE_KEY_PLATFORM_LIST, async () => {
      const res = await SellPlatformApi.getSellPlatformList({ status: 0 })
      return res
    })
  },
  // 查询销售平台详情
  getSellPlatform: async (id: number) => {
    return await request.get({ url: `/erplus/sell-platform/get?id=` + id })
  },

  // 新增销售平台
  createSellPlatform: async (data: SellPlatformVO) => {
    return await request.post({ url: `/erplus/sell-platform/create`, data })
  },

  // 修改销售平台
  updateSellPlatform: async (data: SellPlatformVO) => {
    return await request.put({ url: `/erplus/sell-platform/update`, data })
  },

  // 删除销售平台
  deleteSellPlatform: async (id: number) => {
    return await request.delete({ url: `/erplus/sell-platform/delete?id=` + id })
  },

  // 导出销售平台 Excel
  exportSellPlatform: async (params) => {
    return await request.download({ url: `/erplus/sell-platform/export-excel`, params })
  }
}



