import request from '@/config/axios'
import { MasSession, MasTaskHistory } from '../views/types'

export const MasApi = {
  // 创建新会话
  createSession: async (goal: string): Promise<string> => {
    return await request.post({ url: '/erplus/ai/mas/session/create', data: { goal } })
  },

  // 获取会话历史分页
  getSessionPage: async (params: any) => {
    return await request.get({ url: '/erplus/ai/mas/session/page', params })
  },

  // 获取会话详情
  getSession: async (id: string): Promise<MasSession> => {
    return await request.get({ url: `/erplus/ai/mas/session/get?id=${id}` })
  },

  // 获取任务执行历史列表
  getTaskHistoryList: async (sessionId: string): Promise<MasTaskHistory[]> => {
    return await request.get({ url: `/erplus/ai/mas/session/task-history/list`, params: { sessionId } })
  },

  // 发送用户反馈/消息
  sendFeedback: async (sessionId: string, feedback: string): Promise<void> => {
    return await request.post({ url: '/erplus/ai/mas/session/feedback', data: { sessionId, feedback } })
  },

  // 强制终止会话
  stopSession: async (id: string): Promise<void> => {
    return await request.post({ url: `/erplus/ai/mas/session/stop?id=${id}` })
  }
}
