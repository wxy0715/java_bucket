package com.wxy.arthas.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetProjectBudgetData {
    private String organizationCode;
    private String projectCode;
    private String keyWord;
    private String projectType;
    private String personCode;
    private String fundCode;
    private Integer pageNO = 1;
    private Integer pageSize = 1400;
    private String keyID;
}