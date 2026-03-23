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

// 属性类型枚举
export enum AttributeTypeEnum {
  SALES_PROPERTY = 0,  // 销售属性, 用于变体
  PRODUCT_PROPERTY = 1  // 商品属性, 用户商品描述
}

// 属性值模型
export interface AttributeValueModel {
  valueId: string;
  valueName: string;
}

// 分类属性模型
export interface CategoryAttributeModel {
  /**
   * 属性ID
   */
  attrCode: string;
  
  /**
   * 属性名
   */
  attrName: string;
  
  /**
   * 属性描述, 用于提示用户输入
   */
  attrDescription: string;
  
  /**
   * 属性类型
   */
  attrType: AttributeTypeEnum;
  
  /**
   * 是否必填
   */
  isRequired: boolean;
  
  /**
   * 可选值
   */
  options?: AttributeValueModel[];
  
  
  /**
   * 是否多选
   */
  isMulSelection: boolean;
  
  /**
   * 是否支持自定义
   */
  isCustomizable: boolean;
  
  /**
   * 是否通用属性
   */
  isCommon: boolean;
  
  /**
   * 扩展信息
   */
  extra?: string;
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

// 获取跨境分类属性
export const renderCategoryAttributes = (categoryId: string[], platformId: number, shopId: number, spuId: number) => {
  return request.post({ url: `/erplus/cross/category/attributes/render`, data: { categoryIds: categoryId, platformId, shopId, spuId } })
}


