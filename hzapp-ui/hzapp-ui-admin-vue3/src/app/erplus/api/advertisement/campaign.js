import request from '@/utils/request'

// 查询广告活动列表
export function listCampaign(query) {
  return request({
    url: '/erplus/amzadv/campaign/list',
    method: 'get',
    params: query
  })
}

// 查询广告活动详细
export function getCampaign(id) {
  return request({
    url: '/erplus/amzadv/campaign/get?id=' + id,
    method: 'get'
  })
}

// 新增广告活动
export function addCampaign(data) {
  return request({
    url: '/erplus/amzadv/campaign/create',
    method: 'post',
    data: data
  })
}

// 修改广告活动
export function updateCampaign(data) {
  return request({
    url: '/erplus/amzadv/campaign/update',
    method: 'put',
    data: data
  })
}

// 删除广告活动
export function delCampaign(id) {
  return request({
    url: '/erplus/amzadv/campaign/delete?id=' + id,
    method: 'delete'
  })
}

// 同步广告活动到亚马逊
export function syncCampaignToAmazon(data) {
  return request({
    url: '/erplus/amzadv/campaign/sync-to-amazon',
    method: 'post',
    data: data
  })
}