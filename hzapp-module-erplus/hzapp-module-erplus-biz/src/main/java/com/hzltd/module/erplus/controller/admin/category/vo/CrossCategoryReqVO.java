package com.hzltd.module.erplus.controller.admin.category.vo;

import com.hzltd.module.erplus.system.model.BaseCrossRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class CrossCategoryReqVO extends BaseCrossRequest {

    private String name;

    private List<Long> shopIds;

}
