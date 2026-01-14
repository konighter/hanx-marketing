package com.hzltd.module.erplus.model.product;

import lombok.Data;

import java.util.List;

@Data
public class IssueModel {
    private String code;

    private String message;

    private String severity;

    private List<String> enforcementActions;
}
