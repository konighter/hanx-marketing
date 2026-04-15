package com.hzltd.module.erplus.dal.redis.authorization;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.dal.redis.RedisKeyConstants;
import com.hzltd.module.erplus.service.authorization.dto.AuthStateDTO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * 平台授权 State 的 Redis DAO
 */
@Repository
public class PlatformAuthStateRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置 State 元数据，有效期 30 分钟
     *
     * @param state 状态值
     * @param dto   元数据
     */
    public void set(String state, AuthStateDTO dto) {
        String key = RedisKeyConstants.PLATFORM_AUTH_STATE + state;
        stringRedisTemplate.opsForValue().set(key, JsonUtils.toJsonString(dto), Duration.ofMinutes(30));
    }

    /**
     * 获取 State 元数据
     *
     * @param state 状态值
     * @return 元数据
     */
    public AuthStateDTO get(String state) {
        String key = RedisKeyConstants.PLATFORM_AUTH_STATE + state;
        String val = stringRedisTemplate.opsForValue().get(key);
        return JsonUtils.parseObject(val, AuthStateDTO.class);
    }

    /**
     * 删除 State 元数据
     *
     * @param state 状态值
     */
    public void delete(String state) {
        String key = RedisKeyConstants.PLATFORM_AUTH_STATE + state;
        stringRedisTemplate.delete(key);
    }

}
