package com.lc.mybatisx.utils;

import org.apache.ibatis.parsing.XPathParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XmlUtils {

    /**
     * 处理xml字符串
     *
     * @param xml
     * @return
     */
    public static XPathParser processXml(String xml) {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(xml.getBytes());
            // 把xml字符串转换成Document
            return new XPathParser(inputStream);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

}
