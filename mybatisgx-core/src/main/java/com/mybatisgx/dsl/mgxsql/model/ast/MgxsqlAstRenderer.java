package com.mybatisgx.dsl.mgxsql.model.ast;

import com.mybatisgx.dsl.mgxsql.MgxsqlSyntaxHelper;
import com.mybatisgx.dsl.mgxsql.MgxsqlXmlFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql AST 渲染器：遍历 AST 节点树，产出标准 MyBatis XML 动态标签文本。
 * <p>无状态（无实例可变字段），渲染为 AST 的纯函数。输出格式完全复用 {@link MgxsqlXmlFragment}，
 * 保证与重构前逐字符扫描器逐字一致。OGNL test 生成（{@link #buildTestExpression}）与 guard 处理
 * （{@link #stripParamColons}）按重构前 {@code MgxsqlConditionBodyProcessor} 的规则重实现，避免与旧代码耦合。
 * <p>渲染上下文 {@link RenderContext} 区分范围块（Scope，{@code <}/{@code >} 原样）与条件节点块
 * （ConditionBody，{@code <}/{@code >} 转义为 {@code &lt;}/{@code &gt;}）。
 *
 * @author 薛承城
 * @description mgxsql AST → MyBatis XML 渲染器
 * @date 2026/7/13
 */
public class MgxsqlAstRenderer {

    private static final String IS_NOT_EMPTY = "@com.mybatisgx.utils.ObjectUtils@isNotEmpty(%s)";

    /**
     * 渲染整棵 AST（顶层节点序列）
     */
    public String render(List<MgxsqlNode> root) {
        StringBuilder sb = new StringBuilder();
        if (root != null) {
            for (MgxsqlNode node : root) {
                sb.append(render(node, RenderContext.SCOPE));
            }
        }
        return sb.toString();
    }

    /**
     * 渲染上下文：范围块内 < > 原样；条件节点块内 < > 转义
     */
    private enum RenderContext {
        SCOPE,
        CONDITION_BODY
    }

    private String renderChildren(List<MgxsqlNode> children, RenderContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (MgxsqlNode child : children) {
            sb.append(render(child, ctx));
        }
        return sb.toString();
    }

    private String render(MgxsqlNode node, RenderContext ctx) {
        // ==================== Scope 层（子节点恒为 SCOPE 上下文） ====================
        if (node instanceof WhereScope) {
            WhereScope s = (WhereScope) node;
            return MgxsqlXmlFragment.openWhere() + renderChildren(s.getChildren(), RenderContext.SCOPE) + MgxsqlXmlFragment.closeWhere();
        }
        if (node instanceof SetScope) {
            SetScope s = (SetScope) node;
            return MgxsqlXmlFragment.openSet() + renderChildren(s.getChildren(), RenderContext.SCOPE) + MgxsqlXmlFragment.closeSet();
        }
        if (node instanceof DescentScope) {
            DescentScope s = (DescentScope) node;
            return s.getOpenTag() + renderChildren(s.getChildren(), RenderContext.SCOPE) + s.getCloseTag();
        }
        // ==================== Unit 层 ====================
        if (node instanceof IfUnit) {
            return renderIf((IfUnit) node);
        }
        if (node instanceof ChooseUnit) {
            return renderChoose((ChooseUnit) node);
        }
        if (node instanceof ForeachUnit) {
            ForeachUnit f = (ForeachUnit) node;
            return "in " + MgxsqlXmlFragment.foreachComplex(f.getItemName(), f.getCollectionName(), f.getValueExpr());
        }
        if (node instanceof BindUnit) {
            return renderBind((BindUnit) node);
        }
        // ==================== Expression 层 ====================
        if (node instanceof ParamExpr) {
            return MgxsqlXmlFragment.paramRef(((ParamExpr) node).getParamName());
        }
        if (node instanceof HashParamExpr) {
            return "#{" + ((HashParamExpr) node).getContent() + "}";
        }
        if (node instanceof DollarParamExpr) {
            return "${" + ((DollarParamExpr) node).getContent() + "}";
        }
        if (node instanceof LocalVarExpr) {
            return "#{" + ((LocalVarExpr) node).getVarName() + "}";
        }
        if (node instanceof PassthroughText) {
            return renderPassthrough((PassthroughText) node, ctx);
        }
        throw new IllegalStateException("未支持的 mgxsql AST 节点类型: " + node.getClass().getName());
    }

    private String renderIf(IfUnit unit) {
        String test;
        if (unit.hasCustomGuard()) {
            test = stripParamColons(unit.getGuardExpression());
        } else {
            test = buildTestExpression(collectParamPaths(unit.getBody()));
        }
        String body = renderChildren(unit.getBody(), RenderContext.CONDITION_BODY).trim();
        return MgxsqlXmlFragment.ifTag(test, body);
    }

    private String renderChoose(ChooseUnit choose) {
        StringBuilder sb = new StringBuilder();
        sb.append(MgxsqlXmlFragment.chooseOpen());
        for (WhenUnit when : choose.getWhens()) {
            String test = stripParamColons(when.getGuardExpression());
            String body = renderChildren(when.getBody(), RenderContext.CONDITION_BODY).trim();
            sb.append(MgxsqlXmlFragment.whenTag(test, body));
        }
        if (choose.getOtherwise() != null) {
            OtherwiseUnit o = choose.getOtherwise();
            String body = renderChildren(o.getBody(), RenderContext.CONDITION_BODY).trim();
            sb.append(MgxsqlXmlFragment.otherwiseTag(body));
        }
        sb.append(MgxsqlXmlFragment.chooseClose());
        return sb.toString();
    }

    private String renderBind(BindUnit bind) {
        String result = MgxsqlXmlFragment.bindTag(bind.getBindName(), bind.getBindValue());
        if (bind.isEmitReference()) {
            result = result + MgxsqlXmlFragment.paramRef(bind.getBindName());
        }
        return result;
    }

    private String renderPassthrough(PassthroughText text, RenderContext ctx) {
        if (ctx == RenderContext.CONDITION_BODY) {
            // 条件节点块内 < > 转义（XML 标签的禁用校验在 parse 期完成）
            return text.getText().replace("<", "&lt;").replace(">", "&gt;");
        }
        return text.getText();
    }

    /**
     * 收集条件体直接子级的参数路径（ParamExpr / BindUnit.paramName / ForeachUnit.collectionName）。
     * 嵌套 IfUnit/ChooseUnit 的参数不冒泡（由嵌套条件自己的 <if> 守卫）。
     */
    private List<String> collectParamPaths(List<MgxsqlNode> body) {
        List<String> paths = new ArrayList<String>();
        for (MgxsqlNode child : body) {
            if (child instanceof ParamExpr) {
                paths.add(((ParamExpr) child).getParamName());
            } else if (child instanceof BindUnit) {
                paths.add(((BindUnit) child).getParamName());
            } else if (child instanceof ForeachUnit) {
                paths.add(((ForeachUnit) child).getCollectionName());
            }
        }
        return paths;
    }

    /**
     * 构建 OGNL test 表达式：无参数 → "true"；否则对每个参数路径（含嵌套属性的前缀）生成 isNotEmpty，用 and 连接。
     */
    String buildTestExpression(List<String> paramPaths) {
        if (paramPaths == null || paramPaths.isEmpty()) {
            return "true";
        }
        List<String> testParts = new ArrayList<String>();
        for (String paramPath : paramPaths) {
            String[] parts = paramPath.split("\\.");
            List<String> currentPath = new ArrayList<String>();
            for (int i = 0; i < parts.length; i++) {
                currentPath.add(parts[i]);
                String path = String.join(".", currentPath);
                testParts.add(String.format(IS_NOT_EMPTY, path));
            }
        }
        return String.join(" and ", testParts);
    }

    /**
     * guard 表达式处理：{@code :param} 去冒号 + {@code < >} 转义 + {@code &&}/{@code ||} → {@code and}/{@code or}。
     */
    String stripParamColons(String expr) {
        if (expr == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (c == ':' && i + 1 < expr.length() && MgxsqlSyntaxHelper.isIdentifierStartChar(expr.charAt(i + 1))) {
                if (expr.charAt(i + 1) == ':') {
                    result.append(c);
                    i++;
                    continue;
                }
                i++; // 跳过 :
                while (i < expr.length() && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_' || expr.charAt(i) == '.')) {
                    result.append(expr.charAt(i));
                    i++;
                }
                continue;
            }
            if (c == '<') {
                result.append("&lt;");
                i++;
                continue;
            }
            if (c == '>') {
                result.append("&gt;");
                i++;
                continue;
            }
            if (c == '&' && i + 1 < expr.length() && expr.charAt(i + 1) == '&') {
                result.append("and");
                i += 2;
                continue;
            }
            if (c == '|' && i + 1 < expr.length() && expr.charAt(i + 1) == '|') {
                result.append("or");
                i += 2;
                continue;
            }
            result.append(c);
            i++;
        }
        return result.toString();
    }
}
