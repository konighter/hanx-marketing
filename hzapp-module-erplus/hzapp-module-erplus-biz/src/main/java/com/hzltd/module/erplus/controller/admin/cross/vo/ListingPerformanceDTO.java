package com.hzltd.module.erplus.controller.admin.cross.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingPerformanceDTO {

    private Integer sales7d;

    private Integer sales30d; // This will represent Units Sold to maintain compatibility

    private Integer orderCount30d;

    private Long gmv30d;

    private Double sales7dGrowth;

    private List<BigDecimal> revenueCurve;
}
