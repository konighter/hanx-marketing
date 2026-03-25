package com.hzltd.module.erplus.service.hscodes;

import cn.idev.excel.EasyExcel;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.date.LocalDateTimeUtils;
import com.hzltd.framework.test.core.ut.BaseDbUnitTest;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesRespVO;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesSaveReqVO;
import com.hzltd.module.erplus.sys.convert.hscode.HsCodeConvert;
import com.hzltd.module.erplus.sys.dal.dataobject.hscodes.HsCodesDO;
import com.hzltd.module.erplus.sys.dal.mysql.hscodes.HsCodesMapper;
import com.hzltd.module.erplus.sys.service.hscodes.HsCodesServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static com.hzltd.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.hzltd.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.hzltd.framework.test.core.util.AssertUtils.assertServiceException;
import static com.hzltd.framework.test.core.util.RandomUtils.randomLongId;
import static com.hzltd.framework.test.core.util.RandomUtils.randomPojo;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.HS_CODES_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link HsCodesServiceImpl} 的单元测试类
 *
 * @author hzadd
 */
@Import(HsCodesServiceImpl.class)
public class HsCodesServiceImplTest extends BaseDbUnitTest {

    @Resource
    private HsCodesServiceImpl hsCodesService;

    @Resource
    private HsCodesMapper hsCodesMapper;

    @Test
    public void testCreateHsCodes_success() throws FileNotFoundException {
//        File file = new File("/Users/hanyabin/Desktop");

        // 准备参数
        HsCodesSaveReqVO createReqVO = randomPojo(HsCodesSaveReqVO.class).setId(null);

        // 调用
        Long hsCodesId = hsCodesService.createHsCodes(createReqVO);
        // 断言
        assertNotNull(hsCodesId);
        // 校验记录的属性是否正确
        HsCodesDO hsCodes = hsCodesMapper.selectById(hsCodesId);
        assertPojoEquals(createReqVO, hsCodes, "id");
    }

    @Test
    public void testUpdateHsCodes_success() {
        // mock 数据
        HsCodesDO dbHsCodes = randomPojo(HsCodesDO.class);
        hsCodesMapper.insert(dbHsCodes);// @Sql: 先插入出一条存在的数据
        // 准备参数
        HsCodesSaveReqVO updateReqVO = randomPojo(HsCodesSaveReqVO.class, o -> {
            o.setId(dbHsCodes.getId()); // 设置更新的 ID
        });

        // 调用
        hsCodesService.updateHsCodes(updateReqVO);
        // 校验是否更新正确
        HsCodesDO hsCodes = hsCodesMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, hsCodes);
    }

    @Test
    public void testUpdateHsCodes_notExists() {
        // 准备参数
        HsCodesSaveReqVO updateReqVO = randomPojo(HsCodesSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> hsCodesService.updateHsCodes(updateReqVO), HS_CODES_NOT_EXISTS);
    }

    @Test
    public void testDeleteHsCodes_success() {
        // mock 数据
        HsCodesDO dbHsCodes = randomPojo(HsCodesDO.class);
        hsCodesMapper.insert(dbHsCodes);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbHsCodes.getId();

        // 调用
        hsCodesService.deleteHsCodes(id);
       // 校验数据不存在了
       assertNull(hsCodesMapper.selectById(id));
    }

    @Test
    public void testDeleteHsCodes_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> hsCodesService.deleteHsCodes(id), HS_CODES_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetHsCodesPage() {
       // mock 数据
       HsCodesDO dbHsCodes = randomPojo(HsCodesDO.class, o -> { // 等会查询到
           o.setCode(null);
           o.setDescription(null);
           o.setCategoryId(null);
           o.setCreateTime(null);
       });
       hsCodesMapper.insert(dbHsCodes);
       // 测试 code 不匹配
       hsCodesMapper.insert(cloneIgnoreId(dbHsCodes, o -> o.setCode(null)));
       // 测试 description 不匹配
       hsCodesMapper.insert(cloneIgnoreId(dbHsCodes, o -> o.setDescription(null)));
       // 测试 categoryId 不匹配
       hsCodesMapper.insert(cloneIgnoreId(dbHsCodes, o -> o.setCategoryId(null)));
       // 测试 createTime 不匹配
       hsCodesMapper.insert(cloneIgnoreId(dbHsCodes, o -> o.setCreateTime(null)));
       // 准备参数
       HsCodesPageReqVO reqVO = new HsCodesPageReqVO();
       reqVO.setCode(null);
       reqVO.setDescription(null);
       reqVO.setCategoryId(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<HsCodesDO> pageResult = hsCodesService.getHsCodesPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbHsCodes, pageResult.getList().get(0));
    }

    @Test
    public void test_insertBatch() throws FileNotFoundException {
        File file = new File("/Users/hanyabin/Downloads/2024最新商品编码和申报要素（2024.01.01）.xlsx");
        List<HsCodesRespVO> data = EasyExcel.read(new FileInputStream(file), HsCodesRespVO.class, null)
                .sheet(0)
                .registerConverter(new DoubleStringConverter1())
                .doReadSync();
//                .autoCloseStream(false)  // 不要自动关闭，交给 Servlet 自己处理
//                .doReadAllSync();
        List<HsCodesDO> hsCodeDOList = HsCodeConvert.INSTANCE.convertList(data).stream().map(d -> {
            d.setCreator("1");
            d.setUpdater("1");
            d.setDeleted(false);
            d.setCreateTime(LocalDateTimeUtils.getToday());
            d.setUpdateTime(LocalDateTimeUtils.getToday());
            return d;
        }).collect(Collectors.toList());


        hsCodesMapper.insertBatch(hsCodeDOList);
    }

}