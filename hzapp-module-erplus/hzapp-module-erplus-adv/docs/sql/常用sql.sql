-- Doris 统计不同的时间段的数据
select
pt_time,
local_time,
    f.campaign_id,
    f.placement,
    sum(impressions),
    sum(clicks),
        sum(clicks)/    sum(impressions) as ctr,
    sum(cost),
        sum(cost)/    sum(clicks) as cpc
from
  (
    SELECT
      CONVERT_TZ(
        FROM_UNIXTIME(time_window_start / 1000),
        '+00:00',
        '-07:00'
      ) as `pt_time`,
      CONVERT_TZ(
        FROM_UNIXTIME(time_window_start / 1000),
        '+00:00',
        '+08:00'
      ) as `local_time`,
      *
    FROM
      hanx_erplus.ads_amazon_stream_sp_traffic
      where campaign_id = '66479471549568'
  ) f
where
  f.pt_time > '2026-03-11'
  group by
  pt_time,
  local_time,
    f.campaign_id,
    f.placement
  order by
 pt_time desc,
      f.campaign_id






