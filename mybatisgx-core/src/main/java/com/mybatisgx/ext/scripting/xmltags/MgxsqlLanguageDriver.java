package com.mybatisgx.ext.scripting.xmltags;

import com.mybatisgx.dsl.mgxsql.MgxsqlDetector;
import com.mybatisgx.dsl.mgxsql.MgxsqlScanner;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * mgxsql 语言驱动，将 mgxsql 简化语法转换为标准 MyBatis 动态 SQL
 * <p>
 * 继承 XMLLanguageDriver，在 createSqlSource 时自动检测 mgxsql 语法标记，
 * 有则通过 {@link MgxsqlScanner} 转换为标准 MyBatis XML 动态标签文本，
 * 再交给父类解析为 DynamicSqlSource；无则原样透传给父类。
 * <p>
 * 使用方式：
 * <ul>
 *   <li>方法级：{@code @Lang(MgxsqlLanguageDriver.class) @Select("...")}</li>
 *   <li>全局配置：mybatis.configuration.default-scripting-language=com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver</li>
 * </ul>
 *
 * @author 薛承城
 * @date 2026/7/9
 */
public class MgxsqlLanguageDriver extends XMLLanguageDriver {

    private final MgxsqlScanner scanner = new MgxsqlScanner();

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        if (MgxsqlDetector.containsMgxsqlSyntax(script)) {
            String converted = this.scanner.process(script);
            // 转换后的文本包含 <where>/<if>/<foreach> 等动态标签，
            // 需要包裹 <script> 标签才能被 XMLLanguageDriver 解析为 DynamicSqlSource
            String wrapped = "<script>" + converted + "</script>";
            return super.createSqlSource(configuration, wrapped, parameterType);
        }
        return super.createSqlSource(configuration, script, parameterType);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        String originScript = getOriginScript(script.getNode());
        if (MgxsqlDetector.containsMgxsqlSyntax(originScript)) {
            String converted = this.scanner.process(originScript);
            // XNode 不可变，将转换后的文本包裹 <script> 标签后交给父类的 String 重载方法处理
            String wrapped = "<script>" + converted + "</script>";
            return super.createSqlSource(configuration, wrapped, parameterType);
        }
        return super.createSqlSource(configuration, script, parameterType);
    }

    private String getOriginScript(Node node) {
        StringBuilder originScript = new StringBuilder();
        parse(node, originScript);
        return originScript.toString();
    }

    private void parse(Node node, StringBuilder sql) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeType()) {
                case Node.TEXT_NODE:
                case Node.CDATA_SECTION_NODE:
                    sql.append(child.getTextContent());
                    break;
                case Node.ELEMENT_NODE:
                    this.buildElementNode(child, sql);
                    break;
            }
        }
    }

    private void buildElementNode(Node child, StringBuilder sql) {
        sql.append('<').append(child.getNodeName());
        NamedNodeMap attrs = child.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            sql.append(' ').append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append('"');
        }
        sql.append('>');
        parse(child, sql);
        sql.append("</").append(child.getNodeName()).append('>');
    }
}
