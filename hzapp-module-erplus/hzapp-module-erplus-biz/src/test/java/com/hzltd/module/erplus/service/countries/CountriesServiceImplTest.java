package com.hzltd.module.erplus.service.countries;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.test.core.ut.BaseDbUnitTest;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.countries.CountriesDO;
import com.hzltd.module.erplus.sys.dal.mysql.countries.CountriesMapper;
import com.hzltd.module.erplus.sys.service.countries.CountriesServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static com.hzltd.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.hzltd.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.hzltd.framework.test.core.util.AssertUtils.assertServiceException;
import static com.hzltd.framework.test.core.util.RandomUtils.randomPojo;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.COUNTRIES_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CountriesServiceImpl} 的单元测试类
 *
 * @author hzadd
 */
@Slf4j
@Import(CountriesServiceImpl.class)
public class CountriesServiceImplTest extends BaseDbUnitTest {

    @Resource
    private CountriesServiceImpl countriesService;

    @Resource
    private CountriesMapper countriesMapper;

    @Test
    public void testQueryCountries() {
        CountriesDO country = countriesService.getCountries(1);
       log.info("Country: {}", country);
    }

    @Test
    public void testCreateCountries_success() {
        // 准备参数
        CountriesSaveReqVO createReqVO = randomPojo(CountriesSaveReqVO.class).setId(null);

        // 调用
        Integer countriesId = countriesService.createCountries(createReqVO);
        // 断言
        assertNotNull(countriesId);
        // 校验记录的属性是否正确
        CountriesDO countries = countriesMapper.selectById(countriesId);
        assertPojoEquals(createReqVO, countries, "id");
    }

    @Test
    public void testUpdateCountries_success() {
        // mock 数据
        CountriesDO dbCountries = randomPojo(CountriesDO.class);
        countriesMapper.insert(dbCountries);// @Sql: 先插入出一条存在的数据
        // 准备参数
        CountriesSaveReqVO updateReqVO = randomPojo(CountriesSaveReqVO.class, o -> {
            o.setId(dbCountries.getId()); // 设置更新的 ID
        });

        // 调用
        countriesService.updateCountries(updateReqVO);
        // 校验是否更新正确
        CountriesDO countries = countriesMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, countries);
    }

    @Test
    public void testUpdateCountries_notExists() {
        // 准备参数
        CountriesSaveReqVO updateReqVO = randomPojo(CountriesSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> countriesService.updateCountries(updateReqVO), COUNTRIES_NOT_EXISTS);
    }

    @Test
    public void testDeleteCountries_success() {
        // mock 数据
        CountriesDO dbCountries = randomPojo(CountriesDO.class);
        countriesMapper.insert(dbCountries);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbCountries.getId();

        // 调用
        countriesService.deleteCountries(id);
       // 校验数据不存在了
       assertNull(countriesMapper.selectById(id));
    }

    @Test
    public void testDeleteCountries_notExists() {
        // 准备参数
        Integer id = null;

        // 调用, 并断言异常
        assertServiceException(() -> countriesService.deleteCountries(id), COUNTRIES_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetCountriesPage() {
       // mock 数据
       CountriesDO dbCountries = randomPojo(CountriesDO.class, o -> { // 等会查询到
           o.setIsoCode2(null);
           o.setIsoCode3(null);
           o.setName(null);
           o.setDefaultLanguageCode(null);
           o.setDefaultCurrencyCode(null);
           o.setIsActive(null);
           o.setCreateTime(null);
       });
       countriesMapper.insert(dbCountries);
       // 测试 isoCode2 不匹配
       countriesMapper.insert(cloneIgnoreId(dbCountries, o -> o.setIsoCode2(null)));
       // 测试 isoCode3 不匹配
       countriesMapper.insert(cloneIgnoreId(dbCountries, o -> o.setIsoCode3(null)));
       // 测试 name 不匹配
       countriesMapper.insert(cloneIgnoreId(dbCountries, o -> o.setName(null)));
       // 测试 defaultLanguageCode 不匹配
       countriesMapper.insert(cloneIgnoreId(dbCountries, o -> o.setDefaultLanguageCode(null)));
       // 测试 defaultCurrencyCode 不匹配
       countriesMapper.insert(cloneIgnoreId(dbCountries, o -> o.setDefaultCurrencyCode(null)));
       // 测试 isActive 不匹配
       countriesMapper.insert(cloneIgnoreId(dbCountries, o -> o.setIsActive(null)));
       // 测试 createTime 不匹配
       countriesMapper.insert(cloneIgnoreId(dbCountries, o -> o.setCreateTime(null)));
       // 准备参数
       CountriesPageReqVO reqVO = new CountriesPageReqVO();
       reqVO.setIsoCode2(null);
       reqVO.setIsoCode3(null);
       reqVO.setName(null);
       reqVO.setDefaultLanguageCode(null);
       reqVO.setDefaultCurrencyCode(null);
       reqVO.setIsActive(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<CountriesDO> pageResult = countriesService.getCountriesPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbCountries, pageResult.getList().get(0));
    }

}