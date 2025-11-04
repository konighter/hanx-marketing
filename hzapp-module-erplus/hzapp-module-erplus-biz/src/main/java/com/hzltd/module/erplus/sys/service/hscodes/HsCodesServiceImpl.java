package com.hzltd.module.erplus.sys.service.hscodes;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.hscodes.HsCodesDO;
import com.hzltd.module.erplus.sys.dal.mysql.hscodes.HsCodesMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.HS_CODES_NOT_EXISTS;

/**
 * [Erplus] 海关编码(HS Code) Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class HsCodesServiceImpl implements HsCodesService {

    @Resource
    private HsCodesMapper hsCodesMapper;

    @Override
    public Long createHsCodes(HsCodesSaveReqVO createReqVO) {
        // 插入
        HsCodesDO hsCodes = BeanUtils.toBean(createReqVO, HsCodesDO.class);
        hsCodesMapper.insert(hsCodes);
        // 返回
        return hsCodes.getId();
    }

    @Override
    public void updateHsCodes(HsCodesSaveReqVO updateReqVO) {
        // 校验存在
        validateHsCodesExists(updateReqVO.getId());
        // 更新
        HsCodesDO updateObj = BeanUtils.toBean(updateReqVO, HsCodesDO.class);
        hsCodesMapper.updateById(updateObj);
    }

    @Override
    public void deleteHsCodes(Long id) {
        // 校验存在
        validateHsCodesExists(id);
        // 删除
        hsCodesMapper.deleteById(id);
    }

    private void validateHsCodesExists(Long id) {
        if (hsCodesMapper.selectById(id) == null) {
            throw exception(HS_CODES_NOT_EXISTS);
        }
    }

    @Override
    public HsCodesDO getHsCodes(Long id) {
        return hsCodesMapper.selectById(id);
    }

    @Override
    public PageResult<HsCodesDO> getHsCodesPage(HsCodesPageReqVO pageReqVO) {
        return hsCodesMapper.selectPage(pageReqVO);
    }

}