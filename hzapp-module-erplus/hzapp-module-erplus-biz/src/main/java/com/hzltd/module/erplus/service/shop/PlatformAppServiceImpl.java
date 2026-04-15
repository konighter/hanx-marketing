package com.hzltd.module.erplus.service.shop;

import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAppDO;
import com.hzltd.module.erplus.dal.mysql.shop.PlatformAppMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 平台应用 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class PlatformAppServiceImpl implements PlatformAppService {

    @Resource
    private PlatformAppMapper platformAppMapper;

    @Override
    public Long createPlatformApp(PlatformAppDO app) {
        platformAppMapper.insert(app);
        return app.getId();
    }

    @Override
    public void updatePlatformApp(PlatformAppDO app) {
        // 校验存在
        validatePlatformAppExists(app.getId());
        // 更新
        platformAppMapper.updateById(app);
    }

    @Override
    public void deletePlatformApp(Long id) {
        // 校验存在
        validatePlatformAppExists(id);
        // 删除
        platformAppMapper.deleteById(id);
    }

    private void validatePlatformAppExists(Long id) {
        if (platformAppMapper.selectById(id) == null) {
            // throw exception(PLATFORM_APP_NOT_EXISTS);
            throw new RuntimeException("平台应用不存在");
        }
    }

    @Override
    public PlatformAppDO getPlatformApp(Long id) {
        return platformAppMapper.selectById(id);
    }

    @Override
    public List<PlatformAppDO> getPlatformAppList(String platform) {
        return platformAppMapper.selectListByPlatform(platform);
    }

}
