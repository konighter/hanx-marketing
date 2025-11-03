package com.hzltd.module.erplus.controller.admin.productpotential.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞品数据
 */
@Data
public class CompetitorVO {
    private String platform;
    private String asin;
    private Date listDate;
    private BigDecimal price;
    private Integer monthSales;
    private Integer reviews;
    private Boolean autoMonitor;
}
