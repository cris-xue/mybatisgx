package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql SQL 文本片段节点：承载 SQL 本体、字符串字面量、空白等无需结构化翻译的静态文本。
 * <p>渲染时 SHALL 对 {@code < > &} 做 XML 实体转义（{@code &} 优先），否则产出的 XML 不闭合。
 * 与 {@link XmlTagText}（原样输出）由节点类型区分，转义决策不依赖渲染上下文。
 *
 * @author 薛承城
 * @description SQL 文本片段 AST 节点（渲染时 XML 转义）
 * @date 2026/7/22
 */
public class SqlText extends PassthroughText {

    public SqlText(String text, int startPosition, int line, int column) {
        super(text, startPosition, line, column);
    }
}
