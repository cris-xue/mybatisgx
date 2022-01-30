package com.lc.mybatisx.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/12/5 18:13
 */
public class FreeMarkerUtils {

    private static final Logger log = LoggerFactory.getLogger(FreeMarkerUtils.class);

    /**
     * 获取模板
     *
     * @param templateName
     * @return
     */
    public static Template getTemplate(String templateName) {
        Reader reader = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource(templateName);
            reader = new InputStreamReader(classPathResource.getInputStream());
            return new Template("mapper", reader, null, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 处理模板
     *
     * @param model
     * @param template
     * @return
     */
    public static XPathParser processTemplate(Map<String, Object> model, Template template) {
        StringWriter stringWriter = null;
        InputStream inputStream = null;
        try {
            stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String methodXml = stringWriter.toString();
            log.info(methodXml);
            inputStream = new ByteArrayInputStream(methodXml.getBytes());

            // 把xml字符串转换成Document
            return new XPathParser(inputStream);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (stringWriter != null) {
                    stringWriter.flush();
                }
                if (stringWriter != null) {
                    stringWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
