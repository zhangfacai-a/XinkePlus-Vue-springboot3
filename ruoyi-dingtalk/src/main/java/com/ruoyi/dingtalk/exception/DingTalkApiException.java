package com.ruoyi.dingtalk.exception;

/**
 * 钉钉接口异常。
 *
 * 用运行时异常是为了让若依的 GlobalExceptionHandler 统一处理，
 * controller 不需要每个方法都 try-catch。
 */
public class DingTalkApiException extends RuntimeException {
    public DingTalkApiException(String message) {
        super(message);
    }

    public DingTalkApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
