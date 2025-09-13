package com.lc.mybatisx.utils;

import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

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
            return new XPathParser(xml);
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

    public static String writeString(Document document) {
        StringWriter stringWriter = null;
        XMLWriter writer = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            stringWriter = new StringWriter();
            writer = new XMLWriter(stringWriter, format);
            writer.close();
            writer.write(document);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (stringWriter != null) {
                    stringWriter.flush();
                    stringWriter.close();
                }
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
