package com.hzltd.module.erplus.sys.service.hscodes;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.hscodes.HsCodesDO;
import jakarta.validation.Valid;

/**
 * [Erplus] 海关编码(HS Code) Service 接口
 *
 * @author hzadd
 */
public interface HsCodesService {

    /**
     * 创建[Erplus] 海关编码(HS Code)
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createHsCodes(@Valid HsCodesSaveReqVO createReqVO);

    /**
     * 更新[Erplus] 海关编码(HS Code)
     *
     * @param updateReqVO 更新信息
     */
    void updateHsCodes(@Valid HsCodesSaveReqVO updateReqVO);

    /**
     * 删除[Erplus] 海关编码(HS Code)
     *
     * @param id 编号
     */
    void deleteHsCodes(Long id);

    /**
     * 获得[Erplus] 海关编码(HS Code)
     *
     * @param id 编号
     * @return [Erplus] 海关编码(HS Code)
     */
    HsCodesDO getHsCodes(Long id);

    /**
     * 获得[Erplus] 海关编码(HS Code)分页
     *
     * @param pageReqVO 分页查询
     * @return [Erplus] 海关编码(HS Code)分页
     */
    PageResult<HsCodesDO> getHsCodesPage(HsCodesPageReqVO pageReqVO);

}