package com.kse.wmsv2.common.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageUtility {

    /** 業務メッセージソース */
    @Autowired
    public MessageSource messageSource;

    /**
     * このクラスのインスタンス生成を禁止します。
     */
    public MessageUtility() {
    }

    /**
     * メッセージを取得します。
     */
    public String getMessage(String code) {
        return  messageSource.getMessage(code, null, Locale.JAPANESE);
    }

    /**
     * メッセージを取得します。
     */
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.JAPANESE);
    }
}