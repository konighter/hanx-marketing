package com.hzltd.module.erplus.service.plugin;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.test.core.ut.BaseDbUnitTest;
import com.hzltd.module.erplus.controller.admin.plugin.vo.PluginPageReqVO;
import com.hzltd.module.erplus.controller.admin.plugin.vo.PluginSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.plugin.PluginDO;
import com.hzltd.module.erplus.dal.mysql.plugin.PluginMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static com.hzltd.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.hzltd.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.hzltd.framework.test.core.util.AssertUtils.assertServiceException;
import static com.hzltd.framework.test.core.util.RandomUtils.randomPojo;
import static com.hzltd.module.spapi.enums.ErrorCodeConstants.PLUGIN_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link PluginServiceImpl} 的单元测试类
 *
 * @author 翰展科技
 */
@Import(PluginServiceImpl.class)
public class PluginServiceImplTest extends BaseDbUnitTest {

    @Resource
    private PluginServiceImpl pluginService;

    @Resource
    private PluginMapper pluginMapper;

    @Test
    public void testCreatePlugin_success() {
        // 准备参数
        PluginSaveReqVO createReqVO = randomPojo(PluginSaveReqVO.class).setId(null);

        // 调用
        Integer pluginId = pluginService.createPlugin(createReqVO);
        // 断言
        assertNotNull(pluginId);
        // 校验记录的属性是否正确
        PluginDO plugin = pluginMapper.selectById(pluginId);
        assertPojoEquals(createReqVO, plugin, "id");
    }

    @Test
    public void testUpdatePlugin_success() {
        // mock 数据
        PluginDO dbPlugin = randomPojo(PluginDO.class);
        pluginMapper.insert(dbPlugin);// @Sql: 先插入出一条存在的数据
        // 准备参数
        PluginSaveReqVO updateReqVO = randomPojo(PluginSaveReqVO.class, o -> {
            o.setId(dbPlugin.getId()); // 设置更新的 ID
        });

        // 调用
        pluginService.updatePlugin(updateReqVO);
        // 校验是否更新正确
        PluginDO plugin = pluginMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, plugin);
    }

    @Test
    public void testUpdatePlugin_notExists() {
        // 准备参数
        PluginSaveReqVO updateReqVO = randomPojo(PluginSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> pluginService.updatePlugin(updateReqVO), PLUGIN_NOT_EXISTS);
    }

    @Test
    public void testDeletePlugin_success() {
        // mock 数据
        PluginDO dbPlugin = randomPojo(PluginDO.class);
        pluginMapper.insert(dbPlugin);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbPlugin.getId();

        // 调用
        pluginService.deletePlugin(id);
       // 校验数据不存在了
       assertNull(pluginMapper.selectById(id));
    }

    @Test
    public void testDeletePlugin_notExists() {
        // 准备参数
        Integer id = 0;

        // 调用, 并断言异常
        assertServiceException(() -> pluginService.deletePlugin(id), PLUGIN_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetPluginPage() {
       // mock 数据
       PluginDO dbPlugin = randomPojo(PluginDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setPluginKey(null);
           o.setStatus(null);
           o.setCreateTime(null);
       });
       pluginMapper.insert(dbPlugin);
       // 测试 name 不匹配
       pluginMapper.insert(cloneIgnoreId(dbPlugin, o -> o.setName(null)));
       // 测试 pluginKey 不匹配
       pluginMapper.insert(cloneIgnoreId(dbPlugin, o -> o.setPluginKey(null)));
       // 测试 status 不匹配
       pluginMapper.insert(cloneIgnoreId(dbPlugin, o -> o.setStatus(null)));
       // 测试 createTime 不匹配
       pluginMapper.insert(cloneIgnoreId(dbPlugin, o -> o.setCreateTime(null)));
       // 准备参数
       PluginPageReqVO reqVO = new PluginPageReqVO();
       reqVO.setName(null);
       reqVO.setPluginKey(null);
       reqVO.setStatus(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<PluginDO> pageResult = pluginService.getPluginPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbPlugin, pageResult.getList().get(0));
    }

}