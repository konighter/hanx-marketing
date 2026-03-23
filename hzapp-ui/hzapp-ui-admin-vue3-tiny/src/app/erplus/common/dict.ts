export enum DICT_TYPE {
  AD_STATUS = 'ad_status',
  AD_CAMPAIGN_TYPE = 'ad_campaign_type',
  AD_TYPE = 'ad_type',
  AD_MATCH_TYPE = 'ad_match_type',
}

export const ad_status = [
  { label: '启用', value: 'ENABLED', colorType: 'success' },
  { label: '暂停', value: 'PAUSED', colorType: 'warning' },
  { label: '归档', value: 'ARCHIVED', colorType: 'info' },
]

export const ad_campaign_type = [
  { label: '商品推广', value: 'SP', colorType: '' },
  { label: '品牌推广', value: 'SB', colorType: '' },
  { label: '展示推广', value: 'SD', colorType: '' },
]

export const ad_type = [
  { label: '商品广告', value: 'PRODUCT_AD', colorType: '' },
]

export const ad_match_type = [
  { label: '精准匹配', value: 'EXACT', colorType: '' },
  { label: '短语匹配', value: 'PHRASE', colorType: '' },
  { label: '广泛匹配', value: 'BROAD', colorType: '' },
]
