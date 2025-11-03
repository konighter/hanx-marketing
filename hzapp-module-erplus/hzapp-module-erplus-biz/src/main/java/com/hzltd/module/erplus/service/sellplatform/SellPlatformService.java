package com.hzltd.module.erplus.service.sellplatform;

import java.util.*;
import javax.validation.*;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformPageReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;

/**
 * 销售平台 Service 接口
 *
 * @author hzadd
 */
public interface SellPlatformService {

    /**
     * 创建销售平台
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createSellPlatform(@Valid SellPlatformSaveReqVO createReqVO);

    /**
     * 更新销售平台
     *
     * @param updateReqVO 更新信息
     */
    void updateSellPlatform(@Valid SellPlatformSaveReqVO updateReqVO);

    /**
     * 删除销售平台
     *
     * @param id 编号
     */
    void deleteSellPlatform(Integer id);

    /**
     * 获得销售平台
     *
     * @param id 编号
     * @return 销售平台
     */
    SellPlatformDO getSellPlatform(Integer id);

    /**
     * 获得销售平台
     *
     * @param id 编号
     * @return 销售平台
     */
    SellPlatformDO getSellPlatformCache(Integer id);

     /**
     * 获得销售平台
     *
     * @param code 编码
     * @return 销售平台
     */
    SellPlatformDO getSellPlatformByCode(String code);

    /**
     * 获得销售平台分页
     *
     * @param pageReqVO 分页查询
     * @return 销售平台分页
     */
    PageResult<SellPlatformDO> getSellPlatformPage(SellPlatformPageReqVO pageReqVO);

    /**
     * 获得销售平台
     *
     * @param pageReqVO 分页查询
     * @return 销售平台分页
     */
    List<SellPlatformDO> getSellPlatformList(SellPlatformReqVO pageReqVO);

}