package com.ruoyi.dingtalk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 钉钉模块配置。
 *
 * 建议写在 ruoyi-admin/src/main/resources/application.yml：
 *
 * dingtalk:
 *   client-id: 你的AppKey或ClientId
 *   client-secret: 你的AppSecret或ClientSecret
 *   corp-id: 你的CorpId
 *   root-dept-id: 1
 *   ruoyi-root-dept-id: 100
 *   default-password: 123456
 *   missing-threshold: 3
 */
@Data
@Component
@ConfigurationProperties(prefix = "dingtalk")
public class DingTalkProperties {

    /** 钉钉应用 client_id，旧版也叫 appKey */
    private String clientId;

    /** 钉钉应用 client_secret，旧版也叫 appSecret */
    private String clientSecret;

    /** 企业 corpId，新版 token 接口需要 */
    private String corpId;

    /** 从哪个钉钉部门开始同步，根部门通常是 1 */
    private Long rootDeptId = 1L;

    /** 若依里承接钉钉部门的父级部门ID；一般填公司根部门 dept_id，例如 100 */
    private Long ruoyiRootDeptId = 100L;

    /** 同步新增用户时的默认密码，生成 BCrypt 后写入 sys_user.password */
    private String defaultPassword = "123456";

    /** 连续多少次没从钉钉在职接口返回，才停用用户；防止接口权限/分页异常导致误停用 */
    private Integer missingThreshold = 3;
}
