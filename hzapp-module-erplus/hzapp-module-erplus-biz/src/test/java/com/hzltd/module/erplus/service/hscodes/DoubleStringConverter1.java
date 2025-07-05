package com.hzltd.module.erplus.service.hscodes;

import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.converters.doubleconverter.DoubleStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.NumberUtils;
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
