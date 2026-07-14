package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.model.ast.MgxsqlAstRenderer;
import com.mybatisgx.dsl.mgxsql.model.ast.MgxsqlParser;

/**
 * mgxsql 翻译器：将 mgxsql 简化语法文本转换为标准 MyBatis XML 动态标签文本。
 * <p>对外契约 {@code process(String) → String} 不变。内部走 <b>parse → AST → render</b> 三阶段
 * （见 OpenSpec change {@code mgxsql-ast-refactor}）：
 * <ul>
 *   <li>{@link MgxsqlParser} 将 mgxsql 文本解析为 Scope/Unit/Expression 三层 AST 节点树</li>
 *   <li>{@link MgxsqlAstRenderer} 将 AST 渲染为标准 MyBatis XML 动态标签文本（无状态）</li>
 * </ul>
 * 解析与渲染严格分离，AST 作为可独立检视的中间表示。重构前融合状态机的逐字符扫描 + 条件体拼接逻辑
 * （原 {@code MgxsqlScanner} 状态机 + {@code MgxqlConditionBodyProcessor}）已由上述两层取代并删除。
 *
 * @author 薛承城
 * @date 2026/7/8
 */
public class MgxsqlScanner {

    private final MgxsqlParser parser = new MgxsqlParser();
    private final MgxsqlAstRenderer renderer = new MgxsqlAstRenderer();

    /**
     * 将 mgxsql 文本转换为标准 MyBatis XML 动态标签文本。
     *
     * @param input mgxsql 文本（空白原样返回，对齐重构前行为）
     * @return 标准 MyBatis XML 动态标签文本
     */
    public String process(String input) {
        return this.renderer.render(this.parser.parse(input));
    }
}
