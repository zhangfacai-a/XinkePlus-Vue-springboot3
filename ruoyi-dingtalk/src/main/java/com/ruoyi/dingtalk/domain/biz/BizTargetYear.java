package com.ruoyi.dingtalk.domain.biz;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 年度目标表 biz_target_year
 */
@Data
public class BizTargetYear {
    private Long id;
    private Integer year;
    private Long companySubjectId;
    private Long profitUnitId;
    private String companyName;
    private String subjectName;
    private String profitUnitName;
    private String platform;
    private String brandName;
    private String deptName;
    private String ownerName;
    private BigDecimal gmvTarget;
    private BigDecimal salesTarget;
    private BigDecimal profitTarget;
    private BigDecimal costTarget;
    private BigDecimal adCostTarget;
    private BigDecimal roiTarget;
    private BigDecimal profitRateTarget;
    private String status;
    private String remark;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
