-- Doris 统计不同的时间段的数据
select
	f.pt_time,
    f.campaign_id,
    f.placement,
    sum(impressions),
    sum(clicks),
    sum(cost)
from
  (
    SELECT
      CONVERT_TZ(
        FROM_UNIXTIME(time_window_start / 1000),
        '+00:00',
        '-07:00'
      ) as `pt_time`,
      *
    FROM
      hanx_erplus.ads_amazon_stream_sp_traffic
  ) f
where
  f.pt_time > '2026-03-10'
  group by 	f.pt_time,
    f.campaign_id,
    f.placement