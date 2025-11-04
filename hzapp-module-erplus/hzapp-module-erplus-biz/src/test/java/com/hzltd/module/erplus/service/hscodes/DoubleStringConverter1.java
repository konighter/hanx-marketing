package com.hzltd.module.erplus.service.hscodes;

import cn.idev.excel.converters.doubleconverter.DoubleStringConverter;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import cn.idev.excel.util.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;

public class DoubleStringConverter1 extends DoubleStringConverter {

    @Override
    public Double convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws ParseException {
        try {
            if (StringUtils.isNotEmpty(cellData.getStringValue()) && cellData.getStringValue().contains("%")) {
                return NumberUtils.parseDouble(cellData.getStringValue().replace("%", ""), contentProperty)/100;
            } else {
                return super.convertToJavaData(cellData, contentProperty, globalConfiguration);

            }
        } catch (Exception e) {
            return 0d;
        }

    }

    @Override
    public WriteCellData<?> convertToExcelData(Double value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return super.convertToExcelData(value, contentProperty, globalConfiguration);
    }
}
