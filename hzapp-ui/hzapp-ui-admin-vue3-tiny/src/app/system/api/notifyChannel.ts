import request from '@/config/axios'

// 通知渠道 VO
export interface NotifyChannelVO {
  id: number
  name: string
  channelType: number
  webhookUrl: string
  config: string
  status: number
  createTime: string
}

// 渠道类型选项
export const ChannelTypeOptions = [
  { label: '飞书', value: 1 },
  { label: '钉钉', value: 2 },
  { label: '企业微信', value: 3 }
]

// 通知渠道 API
export const NotifyChannelApi = {
  // 分页查询
  getPage: async (params: any) => {
    return await request.get({ url: `/erplus/notify-channel/page`, params })
  },

  // 获取详情
  get: async (id: number) => {
    return await request.get({ url: `/erplus/notify-channel/get?id=` + id })
  },

  // 创建
  create: async (data: NotifyChannelVO) => {
    return await request.post({ url: `/erplus/notify-channel/create`, data })
  },

  // 修改
  update: async (data: NotifyChannelVO) => {
    return await request.put({ url: `/erplus/notify-channel/update`, data })
  },

  // 删除
  delete: async (id: number) => {
    return await request.delete({ url: `/erplus/notify-channel/delete?id=` + id })
  },

  // 测试发送
  testSend: async (id: number) => {
    return await request.post({ url: `/erplus/notify-channel/test-send?id=` + id })
  }
}
