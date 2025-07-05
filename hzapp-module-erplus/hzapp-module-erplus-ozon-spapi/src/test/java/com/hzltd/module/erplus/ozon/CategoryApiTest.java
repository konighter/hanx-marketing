package com.hzltd.module.erplus.ozon;


import com.hzltd.module.erplus.ApplicationTest;
import com.hzltd.module.erplus.ozon.api.category.CategoryApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApplicationTest.class)
public class CategoryApiTest  {

    @Resource
    private CategoryApi categoryApi;

    @Test
    public void testGetCategoryTree() {
        Object obj = categoryApi.getCategoryTree("DEFAULT");
        log.info("ret: {}", obj);
    }

}
