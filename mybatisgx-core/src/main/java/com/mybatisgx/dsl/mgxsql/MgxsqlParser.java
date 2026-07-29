package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 文本解析器：将 mgxsql 文本解析为 {@link MgxsqlNode} AST（Scope/Unit/Expression 三层）。
 * <p>复用 {@link MgxsqlContext}（逐字符读取 + 位置/行号）与 {@link MgxsqlSyntaxHelper}（关键字/词边界检测）
 * 的识别能力，产物是 AST 节点（不拼 XML）。解析与渲染严格分离（design Decision 1）。
 *
 * <p>识别逻辑自重构前的 {@code MgxsqlScanner}（scope 层，ctx 基）与 {@code MgxsqlConditionBodyProcessor}
 * （body 层，String 基）原样搬迁，仅把"拼字符串"换成"构造节点"。
 *
 * <p><b>未实现</b>：原生三标签下沉 {@code <where>/<set>/<trim>}（DescentScope）遇之抛
 * {@link UnsupportedOperationException}，留待后续。
 *
 * @author 薛承城
 * @description mgxsql 文本 → AST 解析器
 * @date 2026/7/13
 */
public class MgxsqlParser {

    public List<MgxsqlNode> parse(String input) {
        List<MgxsqlNode> root = new ArrayList();
        if (input == null) {
            return root;
        }
        if (input.trim().isEmpty()) {
            root.add(new SqlText(input, 0, 1, 1));
            return root;
        }
        MgxsqlContext ctx = new MgxsqlContext(input.trim());
        BindRegistry bindRegistry = new BindRegistry();
        MgxqlBodyParser bodyParser = new MgxqlBodyParser(bindRegistry);
        MgxsqlScopeParser scopeParser = new MgxsqlScopeParser(ctx, bindRegistry, bodyParser);
        scopeParser.parse(root);
        return root;
    }
}
