package com.ruoyi.dingtalk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 钉钉应用配置。
 *
 * 配置位置：ruoyi-admin/src/main/resources/application.yml 或 application-druid.yml 等配置文件。
 *
 * 示例：
 * dingtalk:
 *   client-id: xxxxxx
 *   client-secret: xxxxxx
 *   corp-id: dingxxxxxx
 *   root-dept-id: 1
 */
@Data
@Component
@ConfigurationProperties(prefix = "dingtalk")
public class DingTalkProperties {

    /**
     * 钉钉应用的 client_id，也就是旧版常说的 appKey。
     */
    private String clientId;

    /**
     * 钉钉应用的 client_secret，也就是旧版常说的 appSecret。
     */
    private String clientSecret;

    /**
     * 企业 corpId。
     * 新版 token 接口需要放到 URL 路径里。
     */
    private String corpId;

    /**
     * 从哪个部门开始递归拉取。
     * 钉钉根部门通常是 1。
     */
    private Long rootDeptId = 1L;
}
