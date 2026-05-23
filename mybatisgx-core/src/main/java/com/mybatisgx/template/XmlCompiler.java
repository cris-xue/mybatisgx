package com.mybatisgx.template;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * 提前编译不需要动态处理的xml节点，以提升性能
 * @author 薛承城
 * @date 2026/5/21 18:39
 */
public class XmlCompiler {

    public static void trim(Element trimElement) {
        Element parent = trimElement.getParent();
        if (parent == null) {
            return;
        }

        String prefix = trimElement.attributeValue("prefix");
        String suffix = trimElement.attributeValue("suffix");
        String suffixOverrides = trimElement.attributeValue("suffixOverrides");

        String sql = trimElement.getText();
        if (StringUtils.isBlank(sql)) {
            sql = "";
        }
        sql = sql.trim();

        // 去除尾部分隔符
        if (StringUtils.isNotBlank(suffixOverrides)) {
            while (sql.endsWith(suffixOverrides)) {
                sql = sql.substring(0, sql.length() - suffixOverrides.length()).trim();
            }
        }

        // 添加前缀
        if (StringUtils.isNotBlank(prefix)) {
            sql = String.format(" %s %s", prefix, sql);
        }

        // 添加后缀
        if (StringUtils.isNotBlank(suffix)) {
            sql = String.format("%s %s", sql, suffix);
        }

        int index = parent.indexOf(trimElement);
        parent.remove(trimElement);
        parent.content().add(index, DocumentHelper.createText(sql));
    }

    public static void where(Element whereElement) {
        Element parent = whereElement.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOf(whereElement);
        List<Node> children = new ArrayList<>(whereElement.content());
        parent.remove(whereElement);
        parent.content().add(index++, DocumentHelper.createText(" where "));
        for (Node child : children) {
            parent.content().add(index++, child);
        }
    }
}
