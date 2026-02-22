export interface AdsAccount {
  id: number
  platform: string
  externalAccountId: string
  name: string
  currency?: string
  authStatus: number
  shopId?: number
  lastSyncedAt?: string
  createTime?: string
}

export interface AdsCampaign {
  id: number
  accountId: number
  externalId: string
  name: string
  status: string
  platformStatus: string
  dailyBudget?: number
  budgetType?: string
  syncedAt?: string
  createTime?: string
}

export interface AdsAdGroup {
  id: number
  accountId: number
  campaignId: number
  externalId: string
  name: string
  status: string
  platformStatus: string
  defaultBid?: number
  syncedAt?: string
  createTime?: string
}

export interface AdsAd {
  id: number
  accountId: number
  adGroupId: number
  externalId: string
  name: string
  adType: string
  status: string
  platformStatus: string
  syncedAt?: string
  createTime?: string
}

export interface AdsKeyword {
  id: number
  accountId: number
  adGroupId: number
  externalId: string
  keywordText: string
  matchType: string
  status: string
  platformStatus: string
  bid?: number
  syncedAt?: string
  createTime?: string
}
