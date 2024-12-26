package com.lc.mybatisx.utils;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Properties;

public class PropertyPlaceholderUtils {

    private static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER = new PropertyPlaceholderHelper("${", "}");

    public static String replace(String template, Properties properties) {
        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders(template, properties);
    }

}
