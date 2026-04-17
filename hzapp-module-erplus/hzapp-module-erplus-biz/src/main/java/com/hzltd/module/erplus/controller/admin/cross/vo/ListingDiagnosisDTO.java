package com.hzltd.module.erplus.controller.admin.cross.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingDiagnosisDTO {

    private Integer score;

    private List<Issue> issues;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Issue {
        private String type; // warning | info | error
        private String label;
        private String description;
    }
}
