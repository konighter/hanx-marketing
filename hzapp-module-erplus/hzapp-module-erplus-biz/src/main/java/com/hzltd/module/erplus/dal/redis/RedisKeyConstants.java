package com.hzltd.module.erplus.dal.redis;

/**
 * ERP Redis Key 枚举类
 *
 * @author 翰展科技
 */
public interface RedisKeyConstants {

    /**
     * 序号的缓存
     *
     * KEY 格式：seq_no:{prefix}
     * VALUE 数据格式：编号自增
     */
    String NO = "erp:seq_no:";

    /**
     * 平台授权 State 的缓存
     *
     * KEY 格式：platform_auth_state:{state}
     * VALUE 数据格式：AuthStateDTO
     */
    String PLATFORM_AUTH_STATE = "erp:platform_auth_state:";

    /**
     * 产品单位的缓存
     */
    String PRODUCT_UNIT_ALL = "erp:product_unit:all";
}
