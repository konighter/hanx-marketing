import request from '@/config/axios'

interface CollectTask {
  collectType: string,
  links: string,
  keywords: string,
}

export const createCollectTask  = async function (data : CollectTask) {
  return request.post(
    {
      url: '/bpm/form/create',
      data: data
    }
  )
}
