package com.mybatisgx.template;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

import java.util.List;

/**
 * 提前编译不需要动态处理的xml节点，以提升性能
 * @author 薛承城
 * @date 2026/5/21 18:39
 */
public class XmlCompiler {

    public static String compile(Element trimElement) {
        String prefix = trimElement.attributeValue("prefix");
        String suffix = trimElement.attributeValue("suffix");
        String suffixOverrides = trimElement.attributeValue("suffixOverrides");

        StringBuilder contentBuilder = new StringBuilder();
        List<Node> nodes = trimElement.content();

        for (Node node : nodes) {
            if (node instanceof Text) {
                contentBuilder.append(node.getText());
            }
            if (node instanceof Element) {
                contentBuilder.append(node.getText());
            }
        }

        String content = contentBuilder.toString().trim();

        // 去掉最后的 suffixOverrides
        if (suffixOverrides != null && !suffixOverrides.isEmpty()) {
            while (content.endsWith(suffixOverrides)) {
                content = content.substring(0, content.length() - suffixOverrides.length()).trim();
            }
        }

        StringBuilder sql = new StringBuilder();
        if (prefix != null) {
            sql.append(prefix);
        }
        sql.append(content);
        if (suffix != null) {
            sql.append(suffix);
        }

        return sql.toString();
    }
}
