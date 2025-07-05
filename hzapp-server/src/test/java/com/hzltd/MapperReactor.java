package com.hzltd;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Set;

@Slf4j
public class MapperReactor {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("com.hzltd.module.erp");
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(TableName.class);
        log.info("Clazz: size:{}, {}", classSet.size(), classSet);

        while (classSet.iterator().hasNext()) {
           TableInfo tableInfo = SqlHelper.table(classSet.iterator().next());
            log.info("TableName: {}, Fields: {}", tableInfo.getTableName(), tableInfo.getFieldList());
        }












    }
}
