import request from '@/config/axios'

import { CrossProductSyncRequest } from '../../views/product/listing/types'

export const queryCrossProductListingPage = async (data: any) => {
  return await request.post({ url: `/erplus/cross-product/page`, data })
}

export interface ProductPublishV2Request {
  platform: string
  shopId: number
  marketId: string
  productType: string
  sellerSku: string
  title?: string
  description?: string
  mainImage?: string
  additionalImages?: string[]
  attributes: Record<string, any>
}

export const syncProductListing = async (data: any) => {
  return await request.post({ url: `/erplus/cross-product/sync`, data })
}

export const syncPlatformListing = async (data: CrossProductSyncRequest) => {
  return await request.post({ url: `/erplus/cross-product/sync`, data })
}

export const getProductListingDetail = async (id: number) => {
  return await request.get({ url: `/erplus/cross-product/detail?id=` + id })
}

export const getListingFeedback = async (taskId: number) => {
  return await request.get({ url: `/erplus/product-listing/get-latest-feedback?taskId=` + taskId })
}

export const publishProductV2 = async (data: any) => {
  return await request.post({ url: `/erplus/product/pub-v2`, data })
}

// No cache for now to ensure fresh fetches during development
export const getListingFormConfigV2 = (productType: string) => {
  if (!productType) return Promise.resolve({ fields: [] })
  return request.get({ url: `/erplus/amz/listing/schema?productType=` + productType })
}
