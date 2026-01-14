import request from '@/config/axios'

export const queryCrossProductListingPage = async (data: any) => {
  return await request.post({ url: `/erplus/cross-product/page`, data })
}

export const syncProductListing = async (data: any) => {
  return await request.post({ url: `/erplus/cross-product/sync`, data })
}

export const getProductListingDetail = async (id: number) => {
  return await request.get({ url: `/erplus/cross-product/detail?id=` + id })
}


