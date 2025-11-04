import request from '@/config/axios'

export interface TenantVO {
  id: number
  name: string
  contactName: string
  contactMobile: string
  status: number
  domain: string
  packageId: number
  username: string
  password: string
  expireTime: Date
  accountCount: number
  createTime: Date
}

export interface TenantPageReqVO extends PageParam {
  name?: string
  contactName?: string
  contactMobile?: string
  status?: number
  createTime?: Date[]
}

export interface TenantExportReqVO {
  name?: string
  contactName?: string
  contactMobile?: string
  status?: number
  createTime?: Date[]
}

// 查询租户列表
export const getTenantPage = (params: TenantPageReqVO) => {
  return request.get({url: '/system/tenant/page', params})
}

// 查询租户详情
export const getTenant = (id: number) => {
  return request.get({url: '/system/tenant/get?id=' + id})
}

// 新增租户
export const createTenant = (data: TenantVO) => {
  return request.post({url: '/system/tenant/create', data})
}

// 修改租户
export const updateTenant = (data: TenantVO) => {
  return request.put({url: '/system/tenant/update', data})
}

// 删除租户
export const deleteTenant = (id: number) => {
  return request.delete({url: '/system/tenant/delete?id=' + id})
}

// 导出租户
export const exportTenant = (params: TenantExportReqVO) => {
  return request.download({url: '/system/tenant/export-excel', params})
}

// ==================== 子表（租户套餐订阅） ====================

// 获得租户套餐订阅分页
export const getTenantPackageSubscriptionPage = async (params) => {
  return await request.get({url: `/system/tenant/tenant-package-subscription/page`, params})
}
// 新增租户套餐订阅
export const createTenantPackageSubscription = async (data) => {
  return await request.post({url: `/system/tenant/tenant-package-subscription/create`, data})
}

// 修改租户套餐订阅
export const updateTenantPackageSubscription = async (data) => {
  return await request.put({url: `/system/tenant/tenant-package-subscription/update`, data})
}

// 删除租户套餐订阅
export const deleteTenantPackageSubscription = async (id: number) => {
  return await request.delete({url: `/system/tenant/tenant-package-subscription/delete?id=` + id})
}

// 获得租户套餐订阅
export const getTenantPackageSubscription = async (id: number) => {
  return await request.get({url: `/system/tenant/tenant-package-subscription/get?id=` + id})
}
