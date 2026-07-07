package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxql.model.MgxqlSourceType;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.parsing.XNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mgxsql 处理器：将 @SimpleSql 注解中的 mgxsql 文本转换为 MyBatis XNode
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class MgxsqlProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MgxsqlProcessor.class);

    private final MgxsqlScanner scanner = new MgxsqlScanner();

    /**
     * 处理 @SimpleSql 方法，将 mgxsql 文本转换为 XNode
     *
     * @param methodInfo 方法信息
     * @return 生成的 XNode，如果不是 mgxsql 方法则返回 null
     */
    public XNode process(MethodInfo methodInfo) {
        String mgxsqlText = methodInfo.getMgxsqlText();
        if (StringUtils.isBlank(mgxsqlText)) {
            return null;
        }

        // 使用扫描器转换 mgxsql 文本为 MyBatis XML 文本
        String mybatisXml = this.scanner.process(mgxsqlText);
        LOGGER.debug("mgxsql [{}] -> [{}]", mgxsqlText, mybatisXml);

        // 包装为完整的 mapper XML 文档
        String fullXml = this.wrapAsMapperXml(methodInfo, mybatisXml);

        // 解析为 XNode
        try {
            XNode xPathParser = XmlUtils.processXml(fullXml).evalNode("/mapper/select|/mapper/insert|/mapper/update|/mapper/delete");
            return xPathParser;
        } catch (Exception e) {
            throw new MybatisgxException("mgxsql 解析失败 [%s]: %s", methodInfo.getMethodName(), e.getMessage());
        }
    }

    /**
     * 将转换后的 SQL 文本包装为完整的 mapper XML 文档
     */
    private String wrapAsMapperXml(MethodInfo methodInfo, String sqlBody) {
        String namespace = methodInfo.getMapperInfo().getNamespace();
        String methodName = methodInfo.getMethodName();
        SqlCommandType commandType = methodInfo.getSqlCommandType();

        String tagName;
        if (commandType == SqlCommandType.SELECT) {
            tagName = "select";
        } else if (commandType == SqlCommandType.INSERT) {
            tagName = "insert";
        } else if (commandType == SqlCommandType.UPDATE) {
            tagName = "update";
        } else if (commandType == SqlCommandType.DELETE) {
            tagName = "delete";
        } else {
            throw new MybatisgxException("不支持的 SqlCommandType: %s", commandType.name());
        }

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
        xml.append("<mapper namespace=\"").append(namespace).append("\">");
        xml.append("<").append(tagName).append(" id=\"").append(methodName).append("\"");
        // resultType 由 MyBatis 自动处理或用户通过 XML 指定
        xml.append(">");
        xml.append(sqlBody);
        xml.append("</").append(tagName).append(">");
        xml.append("</mapper>");

        return xml.toString();
    }
}
