package com.hzltd.module.erplus.adv.dal.redis;

/**
 * 广告模块 Redis Key 常量
 */
public interface AdsRedisKeyConstants {

    /**
     * 广告授权 State 的缓存
     *
     * KEY 格式：ads_auth_state:{state}
     * VALUE 数据格式：JSON 序列化的 AdsAuthStateDTO
     */
    String ADS_AUTH_STATE = "ads:auth_state:";

}
