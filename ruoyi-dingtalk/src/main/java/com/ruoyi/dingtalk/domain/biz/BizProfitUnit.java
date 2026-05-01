package com.ruoyi.dingtalk.domain.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 利润单元表 biz_profit_unit
 */
@Data
public class BizProfitUnit {

    private Long id;

    @Excel(name = "公司主体ID")
    private Long companySubjectId;

    @Excel(name = "负责会计")
    private String accountantName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始做账日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date accountingStartDate;

    @Excel(name = "利润单元全称")
    private String profitUnitName;

    @Excel(name = "利润单元编码")
    private String profitUnitCode;

    @Excel(name = "平台")
    private String platform;

    @Excel(name = "品牌")
    private String brandName;

    @Excel(name = "品类")
    private String categoryName;

    @Excel(name = "所属部门")
    private String deptName;

    @Excel(name = "部门负责人工号")
    private String deptOwnerJobNo;

    @Excel(name = "部门负责人")
    private String deptOwnerName;

    @Excel(name = "地区")
    private String regionName;

    @Excel(name = "项目组")
    private String projectGroup;

    @Excel(name = "项目负责人")
    private String projectOwnerName;

    @Excel(name = "直接负责人工号")
    private String ownerJobNo;

    @Excel(name = "直接负责人")
    private String ownerName;

    @Excel(name = "预策店铺授权ID")
    private String storeAuthId;

    @Excel(name = "预策店铺编码")
    private String storeCode;

    @Excel(name = "预策店铺")
    private String storeName;

    @Excel(name = "ID地址")
    private String accountId;

    @Excel(name = "成交类型")
    private String dealType;

    @Excel(name = "直播间类型")
    private String liveRoomType;

    @Excel(name = "核算依据")
    private String accountingBasis;

    @Excel(name = "注册手机号")
    private String registerMobile;

    @Excel(name = "注册实名人员")
    private String registerRealName;

    @Excel(name = "实名注册介绍人")
    private String registerReferrer;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "注册日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date registerDate;

    @Excel(name = "保证金金额")
    private BigDecimal depositAmount;

    @Excel(name = "是否启动")
    private String isEnabled;

    @Excel(name = "是否授权")
    private String isAuthorized;

    @Excel(name = "推广千川账户ID")
    private String qianchuanAccountId;

    @Excel(name = "推广千川账户名称")
    private String qianchuanAccountName;

    @Excel(name = "推广千川手机号")
    private String qianchuanMobile;

    @Excel(name = "千川公司主体")
    private String qianchuanCompanySubject;

    @Excel(name = "千川授权")
    private String qianchuanAuth;

    @Excel(name = "验数记录")
    private String verifyRecord;

    @Excel(name = "UID")
    private String uid;

    @Excel(name = "状态")
    private String status;

    @Excel(name = "备注")
    private String remark;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 当前业务里 transfer_date 被当作“结束日期”使用。
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date transferDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "停运日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date stopDate;

    /** 列表查询：开始日期起 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDateBegin;

    /** 列表查询：开始日期止 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDateEnd;

    /** 列表查询：结束日期起 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date transferDateBegin;

    /** 列表查询：结束日期止 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date transferDateEnd;
}
