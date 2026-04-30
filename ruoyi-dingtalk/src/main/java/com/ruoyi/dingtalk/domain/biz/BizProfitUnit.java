package com.ruoyi.dingtalk.domain.biz;

import lombok.Data;
import java.util.Date;

/**
 * 利润单元表 biz_profit_unit
 */
@Data
public class BizProfitUnit {

    private Long id;

    private Long companySubjectId;

    private String profitUnitName;
    private String profitUnitCode;

    private String platform;
    private String brandName;

    private String deptName;
    private String deptOwnerName;   // 新增

    private String ownerName;

    private String categoryName;    // 新增

    private String storeAuthId;     // 新增
    private String storeCode;
    private String storeName;

    private String accountId;
    private String dealType;
    private String liveRoomType;
    private String isEnabled;
    private String isAuthorized;

    private String status;
    private String remark;

    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private Date startDate;
    private Date transferDate;
    private String accountantName;
    private Date accountingStartDate;
    private String deptOwnerJobNo;
    private String regionName;
    private String projectGroup;
    private String projectOwnerName;
    private String ownerJobNo;

    private String accountingBasis;
    private String registerMobile;
    private String registerRealName;
    private String registerReferrer;
    private Date registerDate;
    private java.math.BigDecimal depositAmount;

    private Date stopDate;
    private String qianchuanAccountId;
    private String qianchuanAccountName;
    private String qianchuanMobile;
    private String qianchuanCompanySubject;
    private String qianchuanAuth;
    private String verifyRecord;
    private String uid;
}