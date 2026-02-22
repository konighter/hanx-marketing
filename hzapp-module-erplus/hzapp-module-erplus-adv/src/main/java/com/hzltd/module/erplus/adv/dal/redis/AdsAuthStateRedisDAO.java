package com.hzltd.module.erplus.adv.dal.redis;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.auth.service.dto.AdsAuthStateDTO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * 广告授权 State 的 Redis DAO
 */
@Repository
public class AdsAuthStateRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置 State 元数据，有效期 30 分钟
     *
     * @param state 状态值
     * @param dto   元数据
     */
    public void set(String state, AdsAuthStateDTO dto) {
        String key = AdsRedisKeyConstants.ADS_AUTH_STATE + state;
        stringRedisTemplate.opsForValue().set(key, JsonUtils.toJsonString(dto), Duration.ofMinutes(30));
    }

    /**
     * 获取 State 元数据
     *
     * @param state 状态值
     * @return 元数据
     */
    public AdsAuthStateDTO get(String state) {
        String key = AdsRedisKeyConstants.ADS_AUTH_STATE + state;
        String val = stringRedisTemplate.opsForValue().get(key);
        return JsonUtils.parseObject(val, AdsAuthStateDTO.class);
    }

    /**
     * 删除 State 元数据
     *
     * @param state 状态值
     */
    public void delete(String state) {
        String key = AdsRedisKeyConstants.ADS_AUTH_STATE + state;
        stringRedisTemplate.delete(key);
    }

}
