package com.mybatisgx.dsl.mgxsql;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

/**
 * mgxsql XML 预处理器，在 MyBatis 解析 XML 之前检测并转换 mgxsql 语法
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class MgxsqlXmlPreprocessor {

    private final MgxsqlScanner scanner = new MgxsqlScanner();

    /**
     * 预处理 XML 文档中的 mgxsql 语法
     * <p>
     * 检测 &lt;select&gt;/&lt;update&gt;/&lt;delete&gt; 标签内的 SQL 文本，
     * 如果包含 mgxsql 语法标记则调用扫描器转换
     *
     * @param xmlContent 原始 XML 文档内容
     * @return 转换后的 XML 文档内容（无 mgxsql 语法则原样返回）
     */
    public String preprocess(String xmlContent) {
        if (StringUtils.isBlank(xmlContent) || !MgxsqlDetector.containsMgxsqlSyntax(xmlContent)) {
            return xmlContent;
        }

        try {
            Document document = DocumentHelper.parseText(xmlContent);
            Element root = document.getRootElement();

            // 处理所有 SQL 语句标签
            this.processStatementElements(root, "select");
            this.processStatementElements(root, "update");
            this.processStatementElements(root, "delete");

            return document.asXML();
        } catch (Exception e) {
            // XML 解析失败，原样返回
            return xmlContent;
        }
    }

    private void processStatementElements(Element root, String tagName) {
        List<Element> elements = root.elements(tagName);
        for (Element element : elements) {
            String sqlText = element.getTextTrim();
            if (MgxsqlDetector.containsMgxsqlSyntax(sqlText)) {
                String converted = this.scanner.process(sqlText);
                // 保留属性，只替换文本内容
                // 清除原有内容
                element.clearContent();
                // 设置转换后的内容
                element.setText(converted);
            }
        }
    }
}
