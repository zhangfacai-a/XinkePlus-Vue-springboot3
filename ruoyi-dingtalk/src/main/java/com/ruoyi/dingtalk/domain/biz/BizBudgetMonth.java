package com.ruoyi.dingtalk.domain.biz;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 月度预算表 biz_budget_month
 */
@Data
public class BizBudgetMonth {
    private Long id;
    private Integer year;
    private Integer month;
    private Long companySubjectId;
    private Long profitUnitId;
    private String companyName;
    private String subjectName;
    private String profitUnitName;
    private String platform;
    private String brandName;
    private String deptName;
    private String ownerName;
    private BigDecimal gmvBudget;
    private BigDecimal salesBudget;
    private BigDecimal profitBudget;
    private BigDecimal costBudget;
    private BigDecimal adCostBudget;
    private BigDecimal roiBudget;
    private BigDecimal profitRateBudget;
    private String status;
    private String remark;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
