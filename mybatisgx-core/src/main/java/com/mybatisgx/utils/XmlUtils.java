package com.mybatisgx.utils;

import org.apache.ibatis.parsing.XPathParser;

public class XmlUtils {

    /**
     * 把xml字符串转换成Document
     *
     * @param xml
     * @return
     */
    public static XPathParser processXml(String xml) {
        return new XPathParser(xml);
    }
}
