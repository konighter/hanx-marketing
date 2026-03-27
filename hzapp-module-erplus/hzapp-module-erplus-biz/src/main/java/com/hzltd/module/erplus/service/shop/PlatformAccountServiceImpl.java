package com.hzltd.module.erplus.service.shop;

import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAccountDO;
import com.hzltd.module.erplus.dal.mysql.shop.PlatformAccountMapper;
import com.hzltd.module.erplus.model.shop.PlatformAccountModel;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 平台账号 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class PlatformAccountServiceImpl implements PlatformAccountService {

    @Resource
    private PlatformAccountMapper platformAccountMapper;

    @Override
    public Long createPlatformAccount(PlatformAccountDO accountDO) {
        platformAccountMapper.insert(accountDO);
        return accountDO.getId();
    }

    @Override
    public void updatePlatformAccount(PlatformAccountDO accountDO) {
        platformAccountMapper.updateById(accountDO);
    }

    @Override
    public void deletePlatformAccount(Long id) {
        platformAccountMapper.deleteById(id);
    }

    @Override
    public PlatformAccountDO getPlatformAccount(Long id) {
        return platformAccountMapper.selectById(id);
    }

    @Override
    public List<PlatformAccountDO> getPlatformAccountList() {
        return platformAccountMapper.selectList();
    }

}
