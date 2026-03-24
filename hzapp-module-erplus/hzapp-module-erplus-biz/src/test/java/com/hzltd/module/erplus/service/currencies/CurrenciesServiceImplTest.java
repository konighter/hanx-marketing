package com.hzltd.module.erplus.service.currencies;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.test.core.ut.BaseDbUnitTest;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.currencies.CurrenciesDO;
import com.hzltd.module.erplus.sys.dal.mysql.currencies.CurrenciesMapper;
import com.hzltd.module.erplus.sys.service.currencies.CurrenciesServiceImpl;
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
import static com.hzltd.module.spapi.enums.ErrorCodeConstants.CURRENCIES_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CurrenciesServiceImpl} 的单元测试类
 *
 * @author hzadd
 */
@Slf4j
@Import(CurrenciesServiceImpl.class)
public class CurrenciesServiceImplTest extends BaseDbUnitTest {

    @Resource
    private CurrenciesServiceImpl currenciesService;

    @Resource
    private CurrenciesMapper currenciesMapper;


    @Test
    public void testQuery() {
        CurrenciesDO currenciesDO = currenciesService.getCurrencies(164);
        log.info("{}", currenciesDO);
    }
    @Test
    public void testCreateCurrencies_success() {
        // 准备参数
        CurrenciesSaveReqVO createReqVO = randomPojo(CurrenciesSaveReqVO.class).setId(null);

        // 调用
        Integer currenciesId = currenciesService.createCurrencies(createReqVO);
        // 断言
        assertNotNull(currenciesId);
        // 校验记录的属性是否正确
        CurrenciesDO currencies = currenciesMapper.selectById(currenciesId);
        assertPojoEquals(createReqVO, currencies, "id");
    }

    @Test
    public void testUpdateCurrencies_success() {
        // mock 数据
        CurrenciesDO dbCurrencies = randomPojo(CurrenciesDO.class);
        currenciesMapper.insert(dbCurrencies);// @Sql: 先插入出一条存在的数据
        // 准备参数
        CurrenciesSaveReqVO updateReqVO = randomPojo(CurrenciesSaveReqVO.class, o -> {
            o.setId(dbCurrencies.getId()); // 设置更新的 ID
        });

        // 调用
        currenciesService.updateCurrencies(updateReqVO);
        // 校验是否更新正确
        CurrenciesDO currencies = currenciesMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, currencies);
    }

    @Test
    public void testUpdateCurrencies_notExists() {
        // 准备参数
        CurrenciesSaveReqVO updateReqVO = randomPojo(CurrenciesSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> currenciesService.updateCurrencies(updateReqVO), CURRENCIES_NOT_EXISTS);
    }

    @Test
    public void testDeleteCurrencies_success() {
        // mock 数据
        CurrenciesDO dbCurrencies = randomPojo(CurrenciesDO.class);
        currenciesMapper.insert(dbCurrencies);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbCurrencies.getId();

        // 调用
        currenciesService.deleteCurrencies(id);
       // 校验数据不存在了
       assertNull(currenciesMapper.selectById(id));
    }

    @Test
    public void testDeleteCurrencies_notExists() {
        // 准备参数
        Integer id = 3;

        // 调用, 并断言异常
        assertServiceException(() -> currenciesService.deleteCurrencies(id), CURRENCIES_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetCurrenciesPage() {
       // mock 数据
       CurrenciesDO dbCurrencies = randomPojo(CurrenciesDO.class, o -> { // 等会查询到
           o.setCode(null);
           o.setSymbol(null);
           o.setName(null);
           o.setExchangeRate(null);
           o.setIsActive(null);
           o.setCreateTime(null);
       });
       currenciesMapper.insert(dbCurrencies);
       // 测试 code 不匹配
       currenciesMapper.insert(cloneIgnoreId(dbCurrencies, o -> o.setCode(null)));
       // 测试 symbol 不匹配
       currenciesMapper.insert(cloneIgnoreId(dbCurrencies, o -> o.setSymbol(null)));
       // 测试 name 不匹配
       currenciesMapper.insert(cloneIgnoreId(dbCurrencies, o -> o.setName(null)));
       // 测试 exchangeRate 不匹配
       currenciesMapper.insert(cloneIgnoreId(dbCurrencies, o -> o.setExchangeRate(null)));
       // 测试 isActive 不匹配
       currenciesMapper.insert(cloneIgnoreId(dbCurrencies, o -> o.setIsActive(null)));
       // 测试 createTime 不匹配
       currenciesMapper.insert(cloneIgnoreId(dbCurrencies, o -> o.setCreateTime(null)));
       // 准备参数
       CurrenciesPageReqVO reqVO = new CurrenciesPageReqVO();
       reqVO.setCode(null);
       reqVO.setSymbol(null);
       reqVO.setName(null);
       reqVO.setExchangeRate(null);
       reqVO.setIsActive(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<CurrenciesDO> pageResult = currenciesService.getCurrenciesPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbCurrencies, pageResult.getList().get(0));
    }

}