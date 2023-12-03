package com.lc.mybatisx.handler.handler;

import com.lc.mybatisx.annotation.BetweenEnd;
import com.lc.mybatisx.annotation.BetweenStart;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.EntityNode;
import com.lc.mybatisx.model.InterfaceNode;
import com.lc.mybatisx.model.MethodNode;
import com.lc.mybatisx.model.MethodParamNode;
import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import com.lc.mybatisx.wrapper.LogicDeleteWrapper;
import com.lc.mybatisx.wrapper.QuerySqlWrapper;
import com.lc.mybatisx.wrapper.SqlWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/14 15:48
 */
public class SqlMapperHandler {

    private static SqlMapperHandler instance = new SqlMapperHandler();
    private InterfaceNode interfaceNode;
    private MethodNode methodNode;

    public static SqlWrapper build(InterfaceNode interfaceNode, MethodNode methodNode, String methodName) {
        ParseTree parseTree = instance.getParseTree(methodName);
        return instance.buildSqlWrapper(interfaceNode, methodNode, parseTree);
    }

    private ParseTree getParseTree(String methodName) {
        CharStream input = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(input);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        ParseTree sqlStatementContext = methodNameParser.sql_statement();
        return sqlStatementContext;
    }

    private SqlWrapper buildSqlWrapper(InterfaceNode interfaceNode, MethodNode methodNode, ParseTree parseTree) {
        this.interfaceNode = interfaceNode;
        this.methodNode = methodNode;

        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);

            buildInsert(parseTreeChild);
            buildDelete(parseTreeChild);
            buildUpdate(parseTreeChild);

            QuerySqlWrapper querySqlWrapper = parseSelectStatement(parseTreeChild);
            if (querySqlWrapper != null) {
                return querySqlWrapper;
            }
        }

        return null;
    }

    private void buildInsert(ParseTree parseTree) {
        if (parseTree instanceof MethodNameParser.Insert_statementContext) {
            int childCount = parseTree.getChildCount();
            for (int i = 0; i < childCount; i++) {
                buildInsert(parseTree.getChild(i));
            }
        }
        if (!(parseTree instanceof MethodNameParser.Insert_clauseContext)) {
            return;
        }
    }

    private void buildDelete(ParseTree parseTree) {
        if (parseTree instanceof MethodNameParser.Delete_statementContext) {
            int childCount = parseTree.getChildCount();
            for (int i = 0; i < childCount; i++) {
                buildDelete(parseTree.getChild(i));
            }
        }
        if (!(parseTree instanceof MethodNameParser.Delete_clauseContext)) {
            return;
        }
    }

    private void buildUpdate(ParseTree parseTree) {
        if (parseTree instanceof MethodNameParser.Update_statementContext) {
            int childCount = parseTree.getChildCount();
            for (int i = 0; i < childCount; i++) {
                buildUpdate(parseTree.getChild(i));
            }
        }
        if (!(parseTree instanceof MethodNameParser.Update_clauseContext)) {
            return;
        }
    }

    private QuerySqlWrapper parseSelectStatement(ParseTree parseTree) {
        boolean isSelectStatement = parseTree instanceof MethodNameParser.Select_statementContext;
        if (!isSelectStatement) {
            return null;
        }

        QuerySqlWrapper querySqlWrapper = new QuerySqlWrapper();
        querySqlWrapper.setNamespace(this.interfaceNode.getInterfaceName());
        EntityNode entityTypeParamNode = interfaceNode.getEntityNode();
        Table table = entityTypeParamNode.getTable();
        querySqlWrapper.setTableName(table.name());

        LogicDelete logicDelete = entityTypeParamNode.getLogicDelete();
        if (logicDelete != null) {
            LogicDeleteWrapper logicDeleteWrapper = new LogicDeleteWrapper();
            logicDeleteWrapper.setValue(logicDelete.delete());
            logicDeleteWrapper.setNotValue(logicDelete.notDelete());
            querySqlWrapper.setLogicDeleteWrapper(logicDeleteWrapper);
        }

        querySqlWrapper.setMethodName(methodNode.getMethodName());

        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            parseSelectClause(parseTreeChild);

            WhereWrapper whereWrapper = parseWhereClause(parseTreeChild);
            if (whereWrapper != null) {
                querySqlWrapper.setWhereWrapper(whereWrapper);
            }
        }

        return querySqlWrapper;
    }

    private void parseSelectClause(ParseTree parseTree) {
        boolean isSelectClause = parseTree instanceof MethodNameParser.Select_clauseContext;
        if (!isSelectClause) {
            return;
        }

        // 构建动作
        buildAction(parseTree);
    }

    private void buildAction(ParseTree parseTree) {
        if (parseTree instanceof MethodNameParser.Insert_clauseContext) {

        } else if (parseTree instanceof MethodNameParser.Select_clauseContext) {

        }
    }

    private WhereWrapper parseWhereClause(ParseTree parseTree) {
        boolean isWhereClause = parseTree instanceof MethodNameParser.Where_clauseContext;
        if (!isWhereClause) {
            return null;
        }

        List<WhereWrapper> whereWrapperList = new ArrayList<>();
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            parseWhereItem(whereWrapperList, parseTreeChild);
        }

        // 方法名条件和方法参数做匹配
        matchField(whereWrapperList);

        int whereSize = whereWrapperList.size();
        WhereWrapper ww1 = null;
        WhereWrapper ww2 = null;
        for (int i = 0; i < whereSize; i++) {
            WhereWrapper ww = whereWrapperList.get(i);
            if (ww1 == null) {
                ww1 = ww;
                ww2 = ww;
            } else {
                ww2.setWhereWrapper(ww);
                ww2 = ww;
            }
        }
        return ww1;
    }

    private void parseWhereItem(List<WhereWrapper> whereWrapperList, ParseTree parseTree) {
        boolean isWhereItem = parseTree instanceof MethodNameParser.Where_itemContext;
        if (!isWhereItem) {
            return;
        }

        WhereWrapper whereWrapper = new WhereWrapper();
        int count = parseTree.getChildCount();
        for (int i = 0; i < count; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String tokens = parseTreeChild.getText();

            if (parseTreeChild instanceof MethodNameParser.Where_link_op_clauseContext) {
                whereWrapper.setLinkOp(tokens);
            } else if (parseTreeChild instanceof MethodNameParser.Where_op_clauseContext) {
                whereWrapper.setOp(tokens);
            }
            String field = parseFieldClause(parseTreeChild);
            if (StringUtils.isNoneBlank(field)) {
                whereWrapper.setDbColumn(field);
            }
        }
        whereWrapperList.add(whereWrapper);
    }

    private void matchField(List<WhereWrapper> whereWrapperList) {
        List<MethodParamNode> methodParamNodeList = methodNode.getMethodParamNodeList();
        for (WhereWrapper whereWrapper : whereWrapperList) {
            matchField(whereWrapper, methodParamNodeList);
        }
        // throw new RuntimeException("方法名和参数条件不匹配");
    }

    private void matchField(WhereWrapper whereWrapper, List<MethodParamNode> methodParamNodeList) {
        List<String> javaColumnList = new ArrayList<>();

        for (MethodParamNode methodParamNode : methodParamNodeList) {
            String dbColumn = whereWrapper.getDbColumn();
            String methodParamName = methodParamNode.getParamName();
            Boolean isUse = methodParamNode.getUse();

            Annotation annotation = isBetween(methodParamNode);
            if ("between".equalsIgnoreCase(whereWrapper.getOp())) {
                String value = (String) AnnotationUtils.getValue(annotation);
                if (!isUse && value.equalsIgnoreCase(dbColumn)) {
                    methodParamNode.setUse(true);
                    javaColumnList.add(methodParamName);
                }
            } else if ("in".equalsIgnoreCase(whereWrapper.getOp())) {
                Class<?> containerType = methodParamNode.getContainerType();
                if (containerType != Collection.class) {
                    throw new RuntimeException("in查询参数必须是Collection类型");
                }
                if (!isUse && dbColumn.equalsIgnoreCase(methodParamName)) {
                    methodParamNode.setUse(true);
                    javaColumnList.add(methodParamName);
                    break;
                }
            } else {
                if (!isUse && dbColumn.equalsIgnoreCase(methodParamName)) {
                    methodParamNode.setUse(true);
                    javaColumnList.add(methodParamName);
                    break;
                }
            }
        }

        if (ObjectUtils.isEmpty(javaColumnList)) {
            throw new RuntimeException("方法名中条件和方法参数无匹配！" + whereWrapper.getDbColumn());
        }

        whereWrapper.setJavaColumn(javaColumnList);
    }

    private Annotation isBetween(MethodParamNode methodParamNode) {
        Annotation[] annotations = methodParamNode.getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            if (annotation.annotationType() == BetweenStart.class || annotation.annotationType() == BetweenEnd.class) {
                return annotation;
            }
        }
        return null;
    }

    private String parseFieldClause(ParseTree parseTree) {
        boolean isFieldClause = parseTree instanceof MethodNameParser.Field_clauseContext;
        return isFieldClause ? parseTree.getText() : null;
    }

}
