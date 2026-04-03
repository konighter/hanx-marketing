import request from '@/config/axios'

import { CrossProductSyncRequest } from '../../views/product/listing/types'

export const queryCrossProductListingPage = async (data: any) => {
  return await request.post({ url: `/erplus/cross-product/page`, data })
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


