import request from '@/config/axios'

export interface PlatformAuthGenerateReqVO {
  platform: string
  authScope: string
  region: string
  appId: number
  sellerId?: string
}

export interface PlatformAuthSubmitReqVO extends PlatformAuthGenerateReqVO {
  selfAuth: boolean
  refreshToken?: string
}

export const PlatformAuthApi = {
  // 生成授权跳转地址
  generateAuthUrl: (data: PlatformAuthGenerateReqVO) => {
    return request.post({ url: '/erplus/platform-auth/generate-url', data })
  },

  // 提交授权 (支持自授权)
  submitAuth: (data: PlatformAuthSubmitReqVO) => {
    return request.post({ url: '/erplus/platform-auth/submit', data })
  },

  // 刷新授权 (通过 shopId)
  refreshAuth: (shopId: number) => {
    return request.post({ url: '/erplus/platform-auth/refresh/' + shopId })
  }
}

export interface PlatformAppVO {
  id?: number
  name: string
  platform: string
}

export interface PlatformAppSaveReqVO extends PlatformAppVO {
  appKey: string
  appSecret: string
}

export const PlatformAppApi = {
  // 获得平台应用列表
  getPlatformAppList: (params: { platform?: string }) => {
    return request.get({ url: '/erplus/platform-app/list', params })
  },

  // 创建平台应用
  createPlatformApp: (data: PlatformAppSaveReqVO) => {
    return request.post({ url: '/erplus/platform-app/create', data })
  },

  // 修改平台应用
  updatePlatformApp: (data: PlatformAppSaveReqVO) => {
    return request.put({ url: '/erplus/platform-app/update', data })
  },

  // 删除平台应用
  deletePlatformApp: (id: number) => {
    return request.delete({ url: `/erplus/platform-app/delete?id=${id}` })
  }
}

