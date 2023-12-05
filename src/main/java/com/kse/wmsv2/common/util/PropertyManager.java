package com.kse.wmsv2.common.util;

import java.text.MessageFormat;
import java.util.*;

public class PropertyManager {

    private static final String[] propertyList = {"database","message_jp","message_kr","message_eng","message_ch"};
    private static final String propertyPath = "properties.%s";
    private static final Locale[] localeList = {Locale.KOREA,Locale.JAPAN,Locale.ENGLISH,Locale.CHINESE};
    private static final Locale defaultLocale = Locale.JAPAN;

    private static PropertyManager pManager;
    private static Map<String, List<ResourceBundle>> resourceMap;

    public PropertyManager() {}

    static {
        pManager = new PropertyManager();
        pManager.initManager();
    }

    private void initManager() {
        resourceMap = new HashMap<>();
        for(Locale locale : localeList) {
            List<ResourceBundle> resourceList = new ArrayList<ResourceBundle>();
            for(String resourceName : propertyList) {
                resourceList.add(ResourceBundle.getBundle(String.format(propertyPath, resourceName), locale));
            }
            resourceMap.put(locale.getLanguage(), resourceList);
        }
    }

    /**
     * プロパティ値を取得する
     *
     * @param key キー
     * @return 値
     */
    public static String getProperty(final String key) {
        return getProperty(key, "");
    }

    /**
     * プロパティ値を取得する
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return キーが存在しない場合、デフォルト値
     *          存在する場合、値
     */
    public static String getProperty(final String key, final String defaultValue) {
        return getProperty(key, defaultLocale, defaultValue);
    }

    /**
     * プロパティ値を取得する
     *
     * @param key キー
     * @param locale ロケール
     * @param defaultValue デフォルト値
     * @return キーが存在しない場合、デフォルト値
     *          存在する場合、値
     */
    public static String getProperty(final String key, final Locale locale , final String defaultValue) {
        for (ResourceBundle resource : resourceMap.get(locale.getLanguage())) {
            if(resource.containsKey(key))
                return resource.getString(key);
        }
        return defaultValue;
    }


}
