package com.hzltd.module.erplus.adv.auth.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountPageReqVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountRespVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAuthCallbackReqVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAuthUrlReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;

import java.util.List;

/**
 * 广告授权 Service 接口
 */
public interface AdsAuthService {

    /**
     * 获得广告授权链接
     *
     * @param reqVO 请求
     * @return 授权链接
     */
    String getAuthorizeUrl(AdsAuthUrlReqVO reqVO);

    /**
     * 处理授权回调
     *
     * @param reqVO 回调请求
     */
    void handleCallback(AdsAuthCallbackReqVO reqVO);

    /**
     * 刷新指定账号的 Token
     *
     * @param accountId 账号编号
     */
    void refreshToken(Long accountId);

    /**
     * 获得广告账号列表
     *
     * @return 账号列表
     */
    List<AdsAccountDO> getAccountList();

    /**
     * 获得广告账号分页
     *
     * @param reqVO 分页查询
     * @return 广告账号分页
     */
    PageResult<AdsAccountDO> getAccountPage(AdsAccountPageReqVO reqVO);

    /**
     * 删除广告账号
     *
     * @param id 编号
     */
    void deleteAccount(Long id);

    /**
     * 获得广告账号
     */
    AdsAccountDO getAccount(Long id);
}
