package com.hzltd.module.erplus.service.authorization;

import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthGenerateReqVO;
import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthSubmitReqVO;

/**
 * 平台授权业务服务接口
 */
public interface PlatformAuthService {

    /**
     * 生成授权跳转地址
     *
     * @param reqVO 生成请求
     * @return 授权地址
     */
    String generateAuthUrl(PlatformAuthGenerateReqVO reqVO);

    /**
     * 提交授权 (支持自授权)
     *
     * @param reqVO 提交请求
     */
    void submitAuth(PlatformAuthSubmitReqVO reqVO);

    /**
     * 处理授权回调
     *
     * @param authType 授权范围
     * @param code      授权码
     * @param state     状态标识
     */
    void handleCallback(String authType, String code, String state);

    /**
     * 刷新特定店铺的授权信息
     *
     * @param shopId 店铺ID
     */
    void refreshAuth(Long shopId);
}
