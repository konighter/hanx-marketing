import request from '@/config/axios'

// 插件 VO
export interface PluginVO {
  id: number // ID
  name: string // 名称
  pluginKey: string // 插件标识
  status: number // 状态
}

// 插件 API
export const PluginApi = {
  // 查询插件分页
  getPluginPage: async (params: any) => {
    return await request.get({ url: `/erplus/plugin/page`, params })
  },

  // 查询插件详情
  getPlugin: async (id: number) => {
    return await request.get({ url: `/erplus/plugin/get?id=` + id })
  },

  // 新增插件
  createPlugin: async (data: PluginVO) => {
    return await request.post({ url: `/erplus/plugin/create`, data })
  },

  // 修改插件
  updatePlugin: async (data: PluginVO) => {
    return await request.put({ url: `/erplus/plugin/update`, data })
  },

  // 删除插件
  deletePlugin: async (id: number) => {
    return await request.delete({ url: `/erplus/plugin/delete?id=` + id })
  },

  // 导出插件 Excel
  exportPlugin: async (params) => {
    return await request.download({ url: `/erplus/plugin/export-excel`, params })
  }
}