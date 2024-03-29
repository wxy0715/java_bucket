package com.wxy.arthas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/**
 * rpc,项目额度信息
 * @author wxy 2022/4/28 12:56
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectControlData {
    private String projectCode;
    private String projectName;
    private String departCode;
    private String departName;
    private String fixedProperty0;
    private String fundCode;
    private String fundName;
    private String templateCode;
    private String templateName;
    private String chargeNo;
    private String chargeName;
    private String dLeaderPersonNo;
    private String dLeaderPersonName;
    private String firstChargeNo;
    private String firstChargeName;
    private String secondChargeNo;
    private String secondChargeName;
    private String leaderPersonNo;
    private String leaderPersonName;
    private BigDecimal usedAmount;
    private BigDecimal appropAmount;
    private BigDecimal freezeAmount;
    private BigDecimal outFreezeAmount;
    private BigDecimal usedSubjects;
    private BigDecimal notUsedSubjects;
    private BigDecimal subjectCode;
    private BigDecimal loanAmount;
}
