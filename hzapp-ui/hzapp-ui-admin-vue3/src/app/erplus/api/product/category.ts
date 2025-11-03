import request from '@/config/axios'

/**
 * 产品分类
 */
export interface CategoryVO {
  /**
   * 分类编号
   */
  id?: number
  /**
   * 父分类编号
   */
  parentId?: number
  /**
   * 分类名称
   */
  name: string
  /**
   * 移动端分类图
   */
  picUrl: string
  /**
   * 分类排序
   */
  sort: number
  /**
   * 开启状态
   */
  status: number
}

/**
 * 跨平台枚举
 */
export enum CrossPlatformEnum {
  AMZ = 1, // 亚马逊
  OZON = 2, // Ozon
  TTS = 3 // Tiktok Shop
}

/**
 * 语言枚举
 */
export enum LanguageEnum {
  ZH_CN = 1 // 汉语
}

/**
 * 跨平台分类VO
 */
export interface CrossCategoryVO {
  /**
   * 分类ID
   */
  categoryId: string
  /**
   * 分类名称
   */
  categoryName: string
  /**
   * 父分类ID
   */
  parentCategoryId: string
  /**
   * 是否叶子节点
   */
  isLeaf: boolean
}

/**
 * 平台分类请求VO
 */
export interface PlatformCategoryReqVO {
  /**
   * 跨平台类型
   */
  crossPlatform: CrossPlatformEnum
  /**
   * 语言
   */
  language: LanguageEnum
  /**
   * 关键词
   */
  keyword: string
}

/**
 * 平台分类响应VO
 */
export interface PlatformCategoryRespVO {
  /**
   * 语言
   */
  language: LanguageEnum
  /**
   * 分类列表
   */
  categories: CrossCategoryVO[]
}

/**
 * 分类规则VO
 */
export interface CategoryRuleVO {
  // 根据实际业务需求添加字段
}

/**
 * 分类属性VO
 */
export interface CategoryAttributeVO {
  // 根据实际业务需求添加字段
}

// 创建商品分类
export const createCategory = (data: CategoryVO) => {
  return request.post({ url: '/product/category/create', data })
}

// 更新商品分类
export const updateCategory = (data: CategoryVO) => {
  return request.put({ url: '/product/category/update', data })
}

// 删除商品分类
export const deleteCategory = (id: number) => {
  return request.delete({ url: `/product/category/delete?id=${id}` })
}

// 获得商品分类
export const getCategory = (id: number) => {
  return request.get({ url: `/product/category/get?id=${id}` })
}

// 获得商品分类列表
export const getCategoryList = (params: any) => {
  return request.get({ url: '/product/category/list', params })
}

// ==================== 跨平台分类相关API ====================

// 获取跨平台分类列表
export const getCrossCategories = (params: PlatformCategoryReqVO) => {
  return request.get({ url: '/erplus/cross/category/list', params })
}

// 获取分类规则
export const getCategoryRules = (categoryId: string) => {
  return request.get({ url: `/erplus/cross/category/${categoryId}/rules` })
}

// 获取分类属性
export const getCategoryAttributes = (categoryId: string) => {
  return request.get({ url: `/erplus/cross/category/${categoryId}/attributes` })
}


