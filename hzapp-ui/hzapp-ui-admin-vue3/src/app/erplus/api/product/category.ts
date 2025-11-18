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
  platformId: number

  /**
   * 店铺ID列表
   */
  shopIds: number[]
  /**
   * 语言
   */
  language: string
  /**
   * 关键词
   */
  name: string
}

/**
 * 平台分类响应VO
 */
export interface PlatformCategoryRespVO {
  /**
   * 语言
   */
  language: string
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
  return request.post({ url: '/erplus/product-category/create', data })
}

// 更新商品分类
export const updateCategory = (data: CategoryVO) => {
  return request.put({ url: '/erplus/product-category/update', data })
}

// 删除商品分类
export const deleteCategory = (id: number) => {
  return request.delete({ url: `/erplus/product-category/delete?id=${id}` })
}

// 获得商品分类
export const getCategory = (id: number) => {
  return request.get({ url: `/erplus/product-category/get?id=${id}` })
}

// 获得商品分类列表
export const getCategoryList = (params: any) => {
  return request.get({ url: '/erplus/product-category/list', params })
}


// ==================== 跨平台分类相关API ====================

// 获取跨境平台分类列表
export const getCrossCategories = (data: PlatformCategoryReqVO) => {
  return request.post({ url: '/erplus/cross/category/list', data })
}

// 获取跨境分类规则
export const getCategoryRules = (categoryId: string, platformId: number) => {
  return request.get({ url: `/erplus/cross/category/${platformId}/rules?id=${categoryId}`})
}

// 获取跨境分类属性
export const getCategoryAttributes = (categoryId: string[], platformId: number, shopId: number) => {
  return request.post({ url: `/erplus/cross/category/attributes`, data: { categoryIds: categoryId, platformId, shopId } })
}


