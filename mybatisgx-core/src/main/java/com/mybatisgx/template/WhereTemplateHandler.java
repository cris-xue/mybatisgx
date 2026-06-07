package com.mybatisgx.template;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

/**
 * 条件模板处理器。修改和删除的条件只能采用方法名定义，不能使用查询实体作为修改和删除方法的条件。
 *
 * @author ccxuef
 * @date 2025/11/5 19:26
 */
public class WhereTemplateHandler {

    public Element execute(EntityInfo entityInfo, MethodInfo methodInfo) {
        // 如果不存在条件
        if (!methodInfo.getExistCondition()) {
            return null;
        }
        Element whereElement = DocumentHelper.createElement("where");
        ConditionProcessorFactory factory = new ConditionProcessorFactory();
        this.handleConditionGroup(methodInfo, whereElement, methodInfo.getConditionInfoList(), factory);
        this.addOptimisticLockCondition(entityInfo, methodInfo, whereElement);
        this.addLogicDeleteCondition(entityInfo, whereElement);
        return whereElement;
    }

    private void addOptimisticLockCondition(EntityInfo entityInfo, MethodInfo methodInfo, Element whereElement) {
        // 查询不需要乐观锁版本条件
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        ColumnInfo versionColumnInfo = entityInfo.getVersionColumnInfo();
        if (versionColumnInfo != null) {
            // 只有更新的场景才需要乐观锁，逻辑删除不需要乐观锁，因为逻辑删除直接改变逻辑删除字段，因为不管什么操作都一定需要逻辑删除字段
            if (methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
                List<String> argValueCommonPathItemList = new ArrayList<>();
                if (methodInfo.getBatch()) {
                    argValueCommonPathItemList.add(entityParamInfo.getBatchItemName());
                }
                argValueCommonPathItemList.add(versionColumnInfo.getJavaColumnName());
                String valueExpression = StringUtils.join(argValueCommonPathItemList, ".");
                whereElement.addText(String.format(" and %s = #{%s}", versionColumnInfo.getDbColumnName(), valueExpression));
            }
        }
    }

    private void addLogicDeleteCondition(EntityInfo entityInfo, Element whereElement) {
        // 逻辑删除
        ColumnInfo logicDeleteColumnInfo = entityInfo.getLogicDeleteColumnInfo();
        if (logicDeleteColumnInfo != null) {
            LogicDelete logicDelete = logicDeleteColumnInfo.getLogicDelete();
            whereElement.addText(String.format(" and %s = '%s'", logicDeleteColumnInfo.getDbColumnName(), logicDelete.show()));
        }
    }

    /**
     * 处理条件组
     *
     * @param methodInfo
     * @param whereElement
     * @param conditionInfoList
     */
    private void handleConditionGroup(MethodInfo methodInfo, Element whereElement, List<ConditionInfo> conditionInfoList, ConditionProcessorFactory factory) {
        for (ConditionInfo conditionInfo : conditionInfoList) {
            List<ConditionInfo> childConditionInfoList = conditionInfo.getConditionInfoList();
            if (ObjectUtils.isNotEmpty(childConditionInfoList)) {
                // 处理分组的括号
                Element targetElement = this.wrapOptionalIf(whereElement, conditionInfo);
                targetElement.addText(String.format(" %s %s", conditionInfo.getLogicOperator(), conditionInfo.getLeftBracket()));
                this.handleConditionGroup(methodInfo, targetElement, childConditionInfoList, factory);
                targetElement.addText(conditionInfo.getRightBracket());
            } else {
                ColumnInfo columnInfo = conditionInfo.getColumnInfo();
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (logicDelete != null) {
                    continue;
                }
                Element targetElement = this.wrapOptionalIf(whereElement, conditionInfo);
                factory.process(methodInfo, conditionInfo, targetElement);
            }
        }
    }

    private Element wrapOptionalIf(Element whereElement, ConditionInfo conditionInfo) {
        if (conditionInfo.getOptional() != null && conditionInfo.getOptional()) {
            List<String> paramValueCommonPathItemList = conditionInfo.getParamValueCommonPathItemList();
            if (ObjectUtils.isNotEmpty(paramValueCommonPathItemList)) {
                String testExpression = MybatisXmlHelper.getTestExpression(paramValueCommonPathItemList);
                return MybatisXmlHelper.buildIfElement(whereElement, testExpression);
            }
        }
        return whereElement;
    }

    static class ConditionProcessorFactory {

        protected final Map<ComparisonOperator, ConditionHandler> conditionHandlers = new HashMap<>();

        public ConditionProcessorFactory() {
            conditionHandlers.put(ComparisonOperator.LT, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.LT_EQ, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.GT, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.GT_EQ, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.LT, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.IN, new InConditionHandler());
            conditionHandlers.put(ComparisonOperator.EQ, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.EQUAL, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.LIKE, new LikeConditionHandler());
            conditionHandlers.put(ComparisonOperator.STARTING_WITH, new StartingLikeConditionHandler());
            conditionHandlers.put(ComparisonOperator.ENDING_WITH, new EndingLikeConditionHandler());
            conditionHandlers.put(ComparisonOperator.BETWEEN, new BetweenConditionHandler());

            conditionHandlers.put(ComparisonOperator.IS_NULL, new NullConditionHandler());
            conditionHandlers.put(ComparisonOperator.IS_NOT_NULL, new NullConditionHandler());
            conditionHandlers.put(ComparisonOperator.NOT_NULL, new NullConditionHandler());
        }

        public void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            ConditionHandler conditionHandler = conditionHandlers.get(conditionInfo.getComparisonOperator());
            conditionHandler.handle(methodInfo, conditionInfo, whereElement);
        }
    }

    interface ConditionHandler {

        void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo);

        WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleRelationSimpleTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo);

        WhereItemContext handleRelationComplexTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo, ColumnInfo columnInfo);
    }

    static abstract class AbstractConditionHandler implements ConditionHandler {

        protected ConditionInfo conditionInfo;
        protected ColumnInfo columnInfo;
        protected Integer columnInfoCompositeIndex;
        protected MethodParamInfo methodParamInfo;
        protected LogicOperator logicOperator;
        protected ComparisonOperator comparisonOperator;

        @Override
        public void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            this.conditionInfo = conditionInfo;
            this.columnInfo = conditionInfo.getColumnInfo();
            this.columnInfoCompositeIndex = 0;
            this.methodParamInfo = conditionInfo.getMethodParamInfo();
            this.logicOperator = conditionInfo.getLogicOperator();
            this.comparisonOperator = conditionInfo.getComparisonOperator();

            List<WhereItemContext> whereItemContextList = new ArrayList();
            this.conditionParamHandle(whereItemContextList);
            this.processWhereItem(whereElement, methodInfo.getDynamic(), whereItemContextList);
        }

        /**
         * 处理条件，有特殊情况的可以在子类中重写
         *
         * @param whereElement
         * @param dynamic
         * @param whereItemContextList
         */
        protected void processWhereItem(Element whereElement, Boolean dynamic, List<WhereItemContext> whereItemContextList) {
            if (ObjectUtils.isEmpty(whereItemContextList)) {
                return;
            }
            for (WhereItemContext whereItemContext : whereItemContextList) {
                String testExpression = whereItemContext.getTestExpression();
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                for (Object content : whereItemContext.getContentList()) {
                    if (content instanceof String) {
                        whereOrIfElement.addText((String) content);
                    }
                    if (content instanceof Element) {
                        whereOrIfElement.add((Element) content);
                    }
                }
            }
        }

        /**
         * 条件参数处理
         *
         * @param whereItemContextList
         */
        public void conditionParamHandle(List<WhereItemContext> whereItemContextList) {
            // 条件不需要参数
            if (this.conditionInfo.getComparisonOperator().isNullComparisonOperator()) {
                WhereItemContext whereItemContext = this.handleSimpleTypeParam(columnInfo);
                whereItemContextList.add(whereItemContext);
                return;
            }
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                TypeCategory typeCategory = this.getParamTypeCategory();
                if (typeCategory == TypeCategory.SIMPLE) {
                    WhereItemContext whereItemContext = this.handleSimpleTypeParam(columnInfo);
                    whereItemContextList.add(whereItemContext);
                }
                if (typeCategory == TypeCategory.OBJECT) {
                    for (ColumnInfo columnInfoComposite : methodParamInfo.getColumnInfoList()) {
                        this.columnInfoCompositeIndex++;
                        WhereItemContext whereItemContext = this.handleComplexTypeParam(columnInfo, columnInfoComposite);
                        whereItemContextList.add(whereItemContext);
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    for (ForeignKeyInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                        this.columnInfoCompositeIndex++;
                        ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                        if (ObjectUtils.isEmpty(referencedColumnInfo.getComposites())) {
                            WhereItemContext whereItemContext = this.handleRelationSimpleTypeParam(relationColumnInfo, foreignKeyInfo);
                            whereItemContextList.add(whereItemContext);
                        }
                        if (ObjectUtils.isNotEmpty(referencedColumnInfo.getComposites())) {
                            for (ColumnInfo composite : referencedColumnInfo.getComposites()) {
                                WhereItemContext whereItemContext = this.handleRelationComplexTypeParam(relationColumnInfo, foreignKeyInfo, composite);
                                whereItemContextList.add(whereItemContext);
                            }
                        }
                    }
                }
            }
        }

        private TypeCategory getParamTypeCategory() {
            if (methodParamInfo.getTypeCategory() == TypeCategory.SIMPLE) {
                // findById(Long id) findById(@Param("id") Long id)
                return TypeCategory.SIMPLE;
            }
            if (methodParamInfo.getTypeCategory() == TypeCategory.OBJECT) {
                // findById(MultiId id) findById(@Param("id") MultiId id)
                if (ObjectUtils.isEmpty(methodParamInfo.getColumnInfoList())) {
                    return TypeCategory.SIMPLE;
                } else {
                    return TypeCategory.OBJECT;
                }
            }
            throw new MybatisgxException("columnInfoClassCategory is null");
        }

        protected String getParamValueExpression(List<String> pathItemList) {
            return String.format("#{%1$s}", StringUtils.join(pathItemList, "."));
        }

        /**
         *
         * @param columnInfo
         * @param paramValueExpression #{userId} or #{user.userId}
         * @return and user_id = #{userId} or and user_id = #{user.userId}
         */
        protected String getConditionExpression(ColumnInfo columnInfo, String paramValueExpression) {
            // 复合对象从第二个字段开始全部用and连接
            LogicOperator logicOperator = this.logicOperator;
            if (this.columnInfoCompositeIndex >= 2) {
                logicOperator = LogicOperator.AND;
            }
            List<String> expressionItemList = Lists.newArrayList(
                    logicOperator.getValue(),
                    columnInfo.getDbColumnName(),
                    comparisonOperator.getValue(),
                    paramValueExpression
            );
            ComparisonOperator comparisonNotOperator = this.conditionInfo.getComparisonNotOperator();
            if (comparisonNotOperator != null) {
                expressionItemList.add(2, comparisonNotOperator.getValue());
            }
            return StringUtils.SPACE + StringUtils.join(expressionItemList, StringUtils.SPACE);
        }

        protected Element buildWhereOrIfElement(Element whereElement, Boolean dynamic, String testExpression) {
            if (this.comparisonOperator.isNullComparisonOperator()) {
                return whereElement;
            }
            return dynamic ? MybatisXmlHelper.buildIfElement(whereElement, testExpression) : whereElement;
        }

        /**
         * 获取参数值路径
         * <code>
         *     id1 = #{multiId.id1}
         *     code = #{code}
         * </code>
         * @param columnInfoComposite
         * @return
         */
        protected List<String> getParamValuePathItemList(ColumnInfo columnInfoComposite) {
            List<String> javaColumnNamePathList = new ArrayList<>();
            if (columnInfoComposite != null) {
                javaColumnNamePathList.add(columnInfoComposite.getJavaColumnName());
            }
            return this.getPathItemList(javaColumnNamePathList);
        }

        /**
         * 获取关联对象复合类型参数值路径
         * and user_id1 = #{user.multiId.id1}
         * @param relationColumnComposite
         * @return
         */
        protected List<String> getRelationComplexTypeParamValuePathItemList(ColumnInfo relationColumnComposite) {
            List<String> javaColumnNamePathList = new ArrayList<>();
            if (relationColumnComposite != null && relationColumnComposite.getJavaColumnNamePathList() != null) {
                javaColumnNamePathList.addAll(relationColumnComposite.getJavaColumnNamePathList());
            }
            return this.getPathItemList(javaColumnNamePathList);
        }

        private List<String> getPathItemList(List<String> javaColumnNamePathList) {
            List<String> argValueCommonPathItemList = Lists.newArrayList(methodParamInfo.getArgValueCommonPathItemList());
            argValueCommonPathItemList.addAll(javaColumnNamePathList);
            return argValueCommonPathItemList;
        }
    }

    /**
     * <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
     * <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
     * and act.client_code like #{likeClientCode}
     * </if>
     *
     * @author ccxuef
     * @date 2025/10/28 18:39
     */
    static class LikeConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(null);
            return this.buildWhereItemContext(columnInfo, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfoComposite);
            return this.buildWhereItemContext(columnInfoComposite, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationSimpleTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            throw new UnsupportedOperationException("多对多关系字段不支持模糊查询");
        }

        @Override
        public WhereItemContext handleRelationComplexTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo, ColumnInfo columnInfo) {
            throw new UnsupportedOperationException("多对多关系字段不支持模糊查询");
        }

        /**
         * 构建WhereItemContext
         *
         * @param columnInfo             数据库列字段信息
         * @param paramValuePathItemList 参数值路径
         * @return
         */
        private WhereItemContext buildWhereItemContext(ColumnInfo columnInfo, List<String> paramValuePathItemList) {
            String testExpression = MybatisXmlHelper.getTestExpression(paramValuePathItemList);
            String bindKey = this.getBindKey(paramValuePathItemList);
            String bindValuePath = this.getBindValuePath(paramValuePathItemList);
            Element likeBindElement = this.buildLikeBindElement(bindKey, bindValuePath);
            String paramValueExpression = this.getParamValueExpression(Arrays.asList(bindKey));
            String conditionExpression = this.getConditionExpression(columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        private String getBindKey(List<String> pathItemList) {
            return StringUtils.join(pathItemList, "_");
        }

        private String getBindValuePath(List<String> pathItemList) {
            return StringUtils.join(pathItemList, ".");
        }

        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = "'%'+" + bindValuePath + "+'%'";
            return MybatisXmlHelper.buildBindElement(bindKey, likeExpression);
        }
    }

    static class StartingLikeConditionHandler extends LikeConditionHandler {

        @Override
        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = "'%'+" + bindValuePath;
            return MybatisXmlHelper.buildBindElement(bindKey, likeExpression);
        }
    }

    static class EndingLikeConditionHandler extends LikeConditionHandler {

        @Override
        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = bindValuePath + "+'%'";
            return MybatisXmlHelper.buildBindElement(bindKey, likeExpression);
        }
    }

    /**
     * <if test="terminal == @com.iss.dtg.idms.constant.Terminal@EBANK">
     * and act.client_code in
     * <foreach item="item" index="index" collection="unDraftClientCodeList" open="(" separator="," close=")">
     * #{item}
     * </foreach>
     * </if>
     *
     * @author ccxuef
     * @date 2025/10/28 18:41
     */
    static class InConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(null);
            return this.buildWhereItemContext(columnInfo, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfoComposite);
            return this.buildWhereItemContext(columnInfoComposite, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationSimpleTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationComplexTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo, ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getRelationComplexTypeParamValuePathItemList(columnInfo);
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        /**
         * 构建WhereItemContext
         *
         * @param columnInfo             数据库列字段信息
         * @param paramValuePathItemList 参数值路径
         * @return
         */
        private WhereItemContext buildWhereItemContext(ColumnInfo columnInfo, List<String> paramValuePathItemList) {
            String testExpression = MybatisXmlHelper.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(columnInfo, "");
            Element foreachElement = MybatisXmlHelper.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }
    }

    static class BetweenConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(null);
            return this.buildWhereItemContext(columnInfo, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfoComposite);
            return this.buildWhereItemContext(columnInfoComposite, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationSimpleTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationComplexTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo, ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getRelationComplexTypeParamValuePathItemList(columnInfo);
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        @Override
        protected String getParamValueExpression(List<String> pathItemList) {
            String path = StringUtils.join(pathItemList, ".");
            return String.format("#{%1$s[0]} and #{%1$s[1]}", path);
        }

        /**
         * 构建WhereItemContext
         *
         * @param columnInfo             数据库列字段信息
         * @param paramValuePathItemList 参数值路径
         * @return
         */
        private WhereItemContext buildWhereItemContext(ColumnInfo columnInfo, List<String> paramValuePathItemList) {
            String testExpression = MybatisXmlHelper.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }
    }

    static class CommonConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(null);
            return this.buildWhereItemContext(columnInfo, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfoComposite);
            return this.buildWhereItemContext(columnInfoComposite, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationSimpleTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationComplexTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo, ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getRelationComplexTypeParamValuePathItemList(columnInfo);
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        /**
         * 构建WhereItemContext
         *
         * @param columnInfo             数据库列字段信息
         * @param paramValuePathItemList 参数值路径
         * @return
         */
        private WhereItemContext buildWhereItemContext(ColumnInfo columnInfo, List<String> paramValuePathItemList) {
            String testExpression = MybatisXmlHelper.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }
    }

    static class NullConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            String conditionExpression = this.getConditionExpression(columnInfo, "");
            return new WhereItemContext(null, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String conditionExpression = this.getConditionExpression(columnInfoComposite, "");
            return new WhereItemContext(null, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleRelationSimpleTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            throw new UnsupportedOperationException("不支持单参数关系字段");
        }

        @Override
        public WhereItemContext handleRelationComplexTypeParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo, ColumnInfo columnInfo) {
            throw new UnsupportedOperationException("不支持单参数关系字段");
        }
    }

    static class TemplateParamContext {

        private ConditionInfo conditionInfo;
        private ColumnInfo columnInfo;
        private ColumnInfo columnInfoComposite;
        private MethodParamInfo methodParamInfo;
        private LogicOperator logicOperator;
        private ComparisonOperator comparisonOperator;

        public TemplateParamContext(ConditionInfo conditionInfo) {
            this.conditionInfo = conditionInfo;
            this.columnInfo = conditionInfo.getColumnInfo();
            this.methodParamInfo = conditionInfo.getMethodParamInfo();
            this.logicOperator = conditionInfo.getLogicOperator();
            this.comparisonOperator = conditionInfo.getComparisonOperator();
        }

        public ColumnInfo getColumnInfo() {
            return columnInfo;
        }

        public MethodParamInfo getMethodParamInfo() {
            return methodParamInfo;
        }
    }

    static class WhereItemContext {

        private String testExpression;

        private List<Object> contentList;

        public WhereItemContext(String testExpression, List<Object> contentList) {
            this.testExpression = testExpression;
            this.contentList = contentList;
        }

        public String getTestExpression() {
            return testExpression;
        }

        public List<Object> getContentList() {
            return contentList;
        }
    }
}
