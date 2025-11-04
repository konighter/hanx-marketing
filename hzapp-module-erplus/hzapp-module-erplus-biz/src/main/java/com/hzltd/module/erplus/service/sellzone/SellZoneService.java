package com.hzltd.module.erplus.service.sellzone;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZonePageReqVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneReqVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellzone.SellZoneDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 销售区域 Service 接口
 *
 * @author hzadd
 */
public interface SellZoneService {

    /**
     * 创建销售区域
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createSellZone(@Valid SellZoneSaveReqVO createReqVO);

    /**
     * 更新销售区域
     *
     * @param updateReqVO 更新信息
     */
    void updateSellZone(@Valid SellZoneSaveReqVO updateReqVO);

    /**
     * 删除销售区域
     *
     * @param id 编号
     */
    void deleteSellZone(Integer id);

    /**
     * 获得销售区域
     *
     * @param id 编号
     * @return 销售区域
     */
    SellZoneDO getSellZone(Integer id);

    /**
     * 获得销售区域分页
     *
     * @param pageReqVO 分页查询
     * @return 销售区域分页
     */
    PageResult<SellZoneDO> getSellZonePage(SellZonePageReqVO pageReqVO);

    /**
     * 获得销售区域分页
     *
     * @param pageReqVO 分页查询
     * @return 销售区域分页
     */
    List<SellZoneDO> getSellZoneList(SellZoneReqVO pageReqVO);

}