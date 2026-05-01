package com.ruoyi.dingtalk.domain.biz;

import lombok.Data;
import java.util.Date;

/**
 * 公司主体表 biz_company_subject
 */
@Data
public class BizCompanySubject {

    private Long id;

    /** 负责会计 */
    private String accountantName;

    /** 开始日期 */
    private Date startDate;

    /** 转交日期 */
    private Date transferDate;

    /** 公司主体 */
    private String companyName;

    /** 主体名称（兜底 = companyName） */
    private String subjectName;

    /** 税号 */
    private String taxNo;

    /** 法人 */
    private String legalPerson;

    /** 成立时间 */
    private Date establishDate;

    /** 注册资本 */
    private String registeredCapital;

    /** 注册地址 */
    private String registeredAddress;

    /** 银行账号 */
    private String bankAccount;

    /** 开户行 */
    private String bankName;

    /** 品牌（可选） */
    private String brandName;

    /** 备注 */
    private String remark;

    /** 状态 */
    private String status;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新人 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;
}