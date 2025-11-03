import request from '@/config/axios'
import { useCache } from '@/hooks/web/useCache'
const { wsCache } = useCache()
const CACHE_KEY_APP_LIST = 'appList'

// 应用注册信息 VO
export interface AppVO {
  name: string // 名称
  platformId: number // 平台ID
  appId: string // AppId
  appKey: string // 应用Key
  appSecret: string // 应用密钥
}

// 应用注册信息 API
export const AppApi = {
  // 查询应用注册信息分页
  getAppPage: async (params: any) => {
    return await request.get({ url: `/erplus/app/page`, params })
  },

  // 查询应用注册信息详情
  getApp: async (id: number) => {
    return await request.get({ url: `/erplus/app/get?id=` + id })
  },

  // 新增应用注册信息
  createApp: async (data: AppVO) => {
    return await request.post({ url: `/erplus/app/create`, data })
  },

  // 修改应用注册信息
  updateApp: async (data: AppVO) => {
    return await request.put({ url: `/erplus/app/update`, data })
  },

  // 删除应用注册信息
  deleteApp: async (id: number) => {
    return await request.delete({ url: `/erplus/app/delete?id=` + id })
  },

  // 导出应用注册信息 Excel
  exportApp: async (params) => {
    return await request.download({ url: `/erplus/app/export-excel`, params })
  },

  getAppListByCache: async () => {
    const applist = wsCache.get(CACHE_KEY_APP_LIST) || undefined
    if (applist) {
      return applist
    } else {
      const res = await request.get({ url: `/erplus/app/list-by-cache` })
      wsCache.set(CACHE_KEY_APP_LIST, res)
      return res
    }
  }

}