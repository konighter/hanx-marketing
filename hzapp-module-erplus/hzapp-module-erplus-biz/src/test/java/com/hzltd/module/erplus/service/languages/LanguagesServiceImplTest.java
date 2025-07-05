package com.hzltd.module.erplus.service.languages;

import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.currencies.CurrenciesDO;
import com.hzltd.module.erplus.sys.service.languages.LanguagesServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import com.hzltd.framework.test.core.ut.BaseDbUnitTest;

import com.hzltd.module.erplus.sys.dal.dataobject.languages.LanguagesDO;
import com.hzltd.module.erplus.sys.dal.mysql.languages.LanguagesMapper;
import com.hzltd.framework.common.pojo.PageResult;

import org.springframework.context.annotation.Import;

import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;
import static com.hzltd.framework.test.core.util.AssertUtils.*;
import static com.hzltd.framework.test.core.util.RandomUtils.*;
import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.*;
import static com.hzltd.framework.common.util.object.ObjectUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link LanguagesServiceImpl} 的单元测试类
 *
 * @author hzadd
 */
@Slf4j
@Import(LanguagesServiceImpl.class)
public class LanguagesServiceImplTest extends BaseDbUnitTest {

    @Resource
    private LanguagesServiceImpl languagesService;

    @Resource
    private LanguagesMapper languagesMapper;


    @Test
    public void testQuery() {
        LanguagesDO currenciesDO = languagesService.getLanguages(164);
        log.info("{}", currenciesDO);
    }
    @Test
    public void testCreateLanguages_success() {
        // 准备参数
        LanguagesSaveReqVO createReqVO = randomPojo(LanguagesSaveReqVO.class).setId(null);

        // 调用
        Integer languagesId = languagesService.createLanguages(createReqVO);
        // 断言
        assertNotNull(languagesId);
        // 校验记录的属性是否正确
        LanguagesDO languages = languagesMapper.selectById(languagesId);
        assertPojoEquals(createReqVO, languages, "id");
    }

    @Test
    public void testUpdateLanguages_success() {
        // mock 数据
        LanguagesDO dbLanguages = randomPojo(LanguagesDO.class);
        languagesMapper.insert(dbLanguages);// @Sql: 先插入出一条存在的数据
        // 准备参数
        LanguagesSaveReqVO updateReqVO = randomPojo(LanguagesSaveReqVO.class, o -> {
            o.setId(dbLanguages.getId()); // 设置更新的 ID
        });

        // 调用
        languagesService.updateLanguages(updateReqVO);
        // 校验是否更新正确
        LanguagesDO languages = languagesMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, languages);
    }

    @Test
    public void testUpdateLanguages_notExists() {
        // 准备参数
        LanguagesSaveReqVO updateReqVO = randomPojo(LanguagesSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> languagesService.updateLanguages(updateReqVO), LANGUAGES_NOT_EXISTS);
    }

    @Test
    public void testDeleteLanguages_success() {
        // mock 数据
        LanguagesDO dbLanguages = randomPojo(LanguagesDO.class);
        languagesMapper.insert(dbLanguages);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbLanguages.getId();

        // 调用
        languagesService.deleteLanguages(id);
       // 校验数据不存在了
       assertNull(languagesMapper.selectById(id));
    }

    @Test
    public void testDeleteLanguages_notExists() {
        // 准备参数
        Integer id = -1;

        // 调用, 并断言异常
        assertServiceException(() -> languagesService.deleteLanguages(id), LANGUAGES_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetLanguagesPage() {
       // mock 数据
       LanguagesDO dbLanguages = randomPojo(LanguagesDO.class, o -> { // 等会查询到
           o.setCode(null);
           o.setName(null);
           o.setIsActive(null);
           o.setCreateTime(null);
       });
       languagesMapper.insert(dbLanguages);
       // 测试 code 不匹配
       languagesMapper.insert(cloneIgnoreId(dbLanguages, o -> o.setCode(null)));
       // 测试 name 不匹配
       languagesMapper.insert(cloneIgnoreId(dbLanguages, o -> o.setName(null)));
       // 测试 isActive 不匹配
       languagesMapper.insert(cloneIgnoreId(dbLanguages, o -> o.setIsActive(null)));
       // 测试 createTime 不匹配
       languagesMapper.insert(cloneIgnoreId(dbLanguages, o -> o.setCreateTime(null)));
       // 准备参数
       LanguagesPageReqVO reqVO = new LanguagesPageReqVO();
       reqVO.setCode(null);
       reqVO.setName(null);
       reqVO.setIsActive(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<LanguagesDO> pageResult = languagesService.getLanguagesPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbLanguages, pageResult.getList().get(0));
    }

}