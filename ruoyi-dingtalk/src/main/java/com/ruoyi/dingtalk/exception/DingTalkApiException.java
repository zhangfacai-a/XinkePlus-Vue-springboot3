package com.ruoyi.dingtalk.exception;

/**
 * 钉钉接口异常。
 *
 * 统一抛这个异常，方便后面你接入全局异常处理或者日志记录。
 */
public class DingTalkApiException extends RuntimeException {

    public DingTalkApiException(String message) {
        super(message);
    }

    public DingTalkApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
