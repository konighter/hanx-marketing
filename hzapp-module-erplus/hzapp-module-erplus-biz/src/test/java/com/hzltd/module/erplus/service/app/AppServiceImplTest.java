package com.hzltd.module.erplus.service.app;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.test.core.ut.BaseDbUnitTest;
import com.hzltd.module.erplus.controller.admin.app.vo.AppPageReqVO;
import com.hzltd.module.erplus.controller.admin.app.vo.AppSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.app.AppDO;
import com.hzltd.module.amz.dal.mysql.AmzInboundPlanMapper;
import com.hzltd.module.erplus.dal.mysql.app.AppMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static com.hzltd.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.hzltd.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.hzltd.framework.test.core.util.AssertUtils.assertServiceException;
import static com.hzltd.framework.test.core.util.RandomUtils.randomPojo;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.APP_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AppServiceImpl} 的单元测试类
 *
 * @author 翰展科技
 */
@Import(AppServiceImpl.class)
public class AppServiceImplTest extends BaseDbUnitTest {

    @Resource
    private AppServiceImpl appService;

    @Resource
    private AppMapper appMapper;

    @Resource
    private AmzInboundPlanMapper inboundPlanMapper;

    @Test
    public void testCreateApp_success() {

        inboundPlanMapper.selectById(1L);


        // 准备参数
        AppSaveReqVO createReqVO = randomPojo(AppSaveReqVO.class).setId(null);

        // 调用
        Integer appId = appService.createApp(createReqVO);
        // 断言
        assertNotNull(appId);
        // 校验记录的属性是否正确
        AppDO app = appMapper.selectById(appId);
        assertPojoEquals(createReqVO, app, "id");
    }

    @Test
    public void testUpdateApp_success() {
        // mock 数据
        AppDO dbApp = randomPojo(AppDO.class);
        appMapper.insert(dbApp);// @Sql: 先插入出一条存在的数据
        // 准备参数
        AppSaveReqVO updateReqVO = randomPojo(AppSaveReqVO.class, o -> {
            o.setId(dbApp.getId()); // 设置更新的 ID
        });

        // 调用
        appService.updateApp(updateReqVO);
        // 校验是否更新正确
        AppDO app = appMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, app);
    }

    @Test
    public void testUpdateApp_notExists() {
        // 准备参数
        AppSaveReqVO updateReqVO = randomPojo(AppSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> appService.updateApp(updateReqVO), APP_NOT_EXISTS);
    }

    @Test
    public void testDeleteApp_success() {
        // mock 数据
        AppDO dbApp = randomPojo(AppDO.class);
        appMapper.insert(dbApp);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbApp.getId();

        // 调用
        appService.deleteApp(id);
       // 校验数据不存在了
       assertNull(appMapper.selectById(id));
    }

    @Test
    public void testDeleteApp_notExists() {
        // 准备参数
        Integer id = 0;

        // 调用, 并断言异常
        assertServiceException(() -> appService.deleteApp(id), APP_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetAppPage() {
       // mock 数据
       AppDO dbApp = randomPojo(AppDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setPlatformId(null);
           o.setAppId(null);
           o.setAppKey(null);
           o.setAppSecret(null);
           o.setCreateTime(null);
       });
       appMapper.insert(dbApp);
       // 测试 name 不匹配
       appMapper.insert(cloneIgnoreId(dbApp, o -> o.setName(null)));
       // 测试 platformId 不匹配
       appMapper.insert(cloneIgnoreId(dbApp, o -> o.setPlatformId(null)));
       // 测试 appId 不匹配
       appMapper.insert(cloneIgnoreId(dbApp, o -> o.setAppId(null)));
       // 测试 appKey 不匹配
       appMapper.insert(cloneIgnoreId(dbApp, o -> o.setAppKey(null)));
       // 测试 appSecret 不匹配
       appMapper.insert(cloneIgnoreId(dbApp, o -> o.setAppSecret(null)));
       // 测试 createTime 不匹配
       appMapper.insert(cloneIgnoreId(dbApp, o -> o.setCreateTime(null)));
       // 准备参数
       AppPageReqVO reqVO = new AppPageReqVO();
       reqVO.setName(null);
       reqVO.setPlatformId(null);
       reqVO.setAppId(null);
       reqVO.setAppKey(null);
       reqVO.setAppSecret(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<AppDO> pageResult = appService.getAppPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbApp, pageResult.getList().get(0));
    }

}