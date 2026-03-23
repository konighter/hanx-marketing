import request from '@/config/axios'

export interface HsCodesVO {
                    id: number
                    code: string
                    description: string
                    categoryId: number
}

// 查询[Erplus] 海关编码(HS Code)列表
export const getHsCodesPage = async (params) => {
  return await request.get({ url: '/erplus/hs-codes/page', params })
}

// 查询[Erplus] 海关编码(HS Code)详情
export const getHsCodes = async (id: number) => {
  return await request.get({ url: '/erplus/hs-codes/get?id=' + id })
}

// 新增[Erplus] 海关编码(HS Code)
export const createHsCodes = async (data: HsCodesVO) => {
  return await request.post({ url: '/erplus/hs-codes/create', data })
}

// 修改[Erplus] 海关编码(HS Code)
export const updateHsCodes = async (data: HsCodesVO) => {
  return await request.put({ url: '/erplus/hs-codes/update', data })
}

// 删除[Erplus] 海关编码(HS Code)
export const deleteHsCodes = async (id: number) => {
  return await request.delete({ url: '/erplus/hs-codes/delete?id=' + id })
}

// 导出[Erplus] 海关编码(HS Code) Excel
export const exportHsCodesApi = async (params) => {
  return await request.download({ url: '/erplus/hs-codes/export-excel', params })
}