package com.ruoyi.dingtalk.domain.biz;

import lombok.Data;
import java.util.Date;

/**
 * 店铺授权表 biz_store_auth
 */
@Data
public class BizStoreAuth {
    private Long id;
    private Long companySubjectId;
    private Long profitUnitId;
    private String platform;
    private String storeCode;
    private String storeName;
    private String authAccount;
    private String authSubject;
    private String authStatus;
    private Date openTime;
    private Date expireTime;
    private String status;
    private String remark;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
