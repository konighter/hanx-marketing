package com.hzltd.module.erplus.service.shop;

import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAccountDO;

import java.util.List;

public interface PlatformAccountService {
    Long createPlatformAccount(PlatformAccountDO accountDO);

    void updatePlatformAccount(PlatformAccountDO accountDO);

    void deletePlatformAccount(Long id);

    PlatformAccountDO getPlatformAccount(Long id);

    List<PlatformAccountDO> getPlatformAccountList();
}
