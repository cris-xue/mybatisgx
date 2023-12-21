package com.lc.mybatisx.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
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

    private static final Logger logger = LoggerFactory.getLogger(FreeMarkerUtils.class);

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
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    /**
     * 处理模板
     *
     * @param model
     * @param template
     * @return
     */
    public static String processTemplate(Map<String, Object> model, Template template) {
        StringWriter stringWriter = null;
        InputStream inputStream = null;
        try {
            stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String ftlText = stringWriter.toString();
            logger.info(ftlText);
            return ftlText;
        } catch (TemplateException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (stringWriter != null) {
                    stringWriter.flush();
                    stringWriter.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

}
