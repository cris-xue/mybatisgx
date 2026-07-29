package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql 嵌入 XML 标签节点：承载用户在 mgxsql 文本中直接书写的原生 MyBatis XML 标签
 * （{@code <if>}/{@code </if>}/{@code <foreach>}/{@code <where>} 等），由 {@code isXmlTagStart} 识别。
 * <p>渲染时 SHALL 原样输出，其 {@code < > &} 不转义。与 {@link SqlText}（转义）由节点类型区分。
 *
 * @author 薛承城
 * @description 嵌入 XML 标签 AST 节点（渲染时原样输出）
 * @date 2026/7/22
 */
public class XmlTagText extends PassthroughText {

    public XmlTagText(String text, int startPosition, int line, int column) {
        super(text, startPosition, line, column);
    }
}
