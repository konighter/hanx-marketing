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
  campaignType: string
  status: string
  platformStatus: string
  dailyBudget?: number
  totalBudget?: number
  budgetType?: string
  shopId?: number
  startDate?: string
  endDate?: string
  deliverySchedule?: any
  platform?: string
  extData?: any
  syncedAt?: string
  createTime?: string
  targetingType?: string // AUTO or MANUAL
  biddingStrategy?: string
  // Indices
  impressions?: number
  clicks?: number
  cost?: number
  spend?: number
  sales?: number
  ctr?: number
  cpc?: number
  orders?: number
  roas?: number
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
  shopId?: number
  platform?: string
  extData?: any
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
  shopId?: number
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
  shopId?: number
  platformStatus: string
  bid?: number
  syncedAt?: string
  createTime?: string
}

export interface AmazonProfile {
  id: number
  accountId: number
  profileId: string
  countryCode: string
  currencyCode: string
  timezone: string
  region: string
}

