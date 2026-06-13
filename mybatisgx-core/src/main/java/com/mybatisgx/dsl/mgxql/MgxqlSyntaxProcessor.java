package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.dsl.DslSyntaxErrorListener;
import com.mybatisgx.dsl.mgxql.checker.MgxqlCheckerChain;
import com.mybatisgx.dsl.mgxql.model.ConditionOriginType;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.ModifyStatement;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlLexer;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParser;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.MethodParamInfo;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * mgxql语法解析处理器
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MgxqlSyntaxProcessor {

    private final List<MgxqlSyntaxHandler.SyntaxNodeHandler> handlers = Arrays.asList(
            new MgxqlSyntaxHandler.DeleteStatementHandler(),
            new MgxqlSyntaxHandler.UpdateStatementHandler(),
            new MgxqlSyntaxHandler.SelectStatementHandler(),
            new MgxqlSyntaxHandler.ModifyEntityClauseHandler(),
            new MgxqlSyntaxHandler.SelectItemHandler(),
            new MgxqlSyntaxHandler.SelectFromClauseHandler(),
            new MgxqlSyntaxHandler.WhereClauseHandler(),
            new MgxqlSyntaxHandler.OrderByHandler(),
            new MgxqlSyntaxHandler.LimitHandler(),
            new MgxqlSyntaxHandler.GroupByHandler(),
            new MgxqlSyntaxHandler.HavingHandler()
    );

    private final static Map<String, SqlCommandType> SQL_COMMAND_TYPE_MAP = new HashMap<>();

    static {
        SQL_COMMAND_TYPE_MAP.put("insert", SqlCommandType.INSERT);
        SQL_COMMAND_TYPE_MAP.put("delete", SqlCommandType.DELETE);
        SQL_COMMAND_TYPE_MAP.put("update", SqlCommandType.UPDATE);
        SQL_COMMAND_TYPE_MAP.put("select", SqlCommandType.SELECT);
    }

    public MgxqlSyntaxProcessor() {
        this.handlers.stream()
                .sorted(Comparator.comparingInt(MgxqlSyntaxHandler.SyntaxNodeHandler::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 根据mgxql表达式获取SQL命令类型
     *
     * @param expression mgxql表达式
     * @return SQL命令类型
     */
    public SqlCommandType getSqlCommandType(String expression) {
        for (String key : SQL_COMMAND_TYPE_MAP.keySet()) {
            if (expression.startsWith(key)) {
                return SQL_COMMAND_TYPE_MAP.get(key);
            }
        }
        throw new MybatisgxException("未知的mgxql表达式类型：%s", expression);
    }

    /**
     * 解析mgxql表达式并构建MgxqlStatement模型
     *
     * @param entityInfo          实体信息
     * @param methodInfo          方法信息
     * @param methodParamInfo     方法参数信息
     * @param conditionOriginType 条件来源类型
     * @param expression          mgxql表达式
     * @return MgxqlStatement模型
     */
    private MgxqlStatement execute(EntityInfo entityInfo, MethodInfo methodInfo, MethodParamInfo methodParamInfo, ConditionOriginType conditionOriginType, String expression) {
        CharStream charStream = CharStreams.fromString(expression);
        MgxqlLexer mgxqlLexer = new MgxqlLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(mgxqlLexer);
        MgxqlParser mgxqlParser = new MgxqlParser(tokens);
        this.addErrorListeners(mgxqlLexer, mgxqlParser);
        ParseTree parseTree = mgxqlParser.sql_statement();

        MgxqlStatement mgxqlStatement = methodInfo.getSqlCommandType() == SqlCommandType.SELECT ? new SelectStatement() : new ModifyStatement();
        MgxqlSyntaxHandler.ParserContext parserContext = new MgxqlSyntaxHandler.ParserContext(
                conditionOriginType,
                expression,
                mgxqlStatement
        );
        this.traverseSyntaxTree(parseTree, parserContext);
        return mgxqlStatement;
    }

    /**
     * 解析mgxql表达式并执行语法校验
     *
     * @param entityInfo          实体信息
     * @param methodInfo          方法信息
     * @param methodParamInfo     方法参数信息
     * @param conditionOriginType 条件来源类型
     * @param expression          mgxql表达式
     * @return 经过校验的MgxqlStatement模型
     */
    public MgxqlStatement executeAndCheck(EntityInfo entityInfo, MethodInfo methodInfo, MethodParamInfo methodParamInfo, ConditionOriginType conditionOriginType, String expression) {
        MgxqlStatement mgxqlStatement = this.execute(entityInfo, methodInfo, methodParamInfo, conditionOriginType, expression);
        mgxqlStatement.setDsl(expression);
        MgxqlCheckerChain checkerChain = new MgxqlCheckerChain();
        checkerChain.check(mgxqlStatement, entityInfo);
        return mgxqlStatement;
    }

    /**
     * 添加错误监听
     *
     * @param mgxqlLexer  mgxql词法解析器
     * @param mgxqlParser mgxql语法解析器
     */
    private void addErrorListeners(MgxqlLexer mgxqlLexer, MgxqlParser mgxqlParser) {
        mgxqlLexer.removeErrorListeners();
        mgxqlParser.removeErrorListeners();
        DslSyntaxErrorListener dslSyntaxErrorListener = new DslSyntaxErrorListener();
        mgxqlLexer.addErrorListener(dslSyntaxErrorListener);
        mgxqlParser.addErrorListener(dslSyntaxErrorListener);
    }

    /**
     * 遍历语法树，对每个节点匹配并执行相应的处理器
     *
     * @param node    当前语法树节点
     * @param context 解析上下文
     */
    private void traverseSyntaxTree(ParseTree node, MgxqlSyntaxHandler.ParserContext context) {
        for (MgxqlSyntaxHandler.SyntaxNodeHandler handler : handlers) {
            if (handler.support(node)) {
                handler.handle(node, context);
                break;
            }
        }
        // 递归处理子节点
        for (int i = 0; i < node.getChildCount(); i++) {
            this.traverseSyntaxTree(node.getChild(i), context);
        }
    }
}
