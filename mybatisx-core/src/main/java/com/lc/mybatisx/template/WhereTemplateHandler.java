package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.PropertyPlaceholderUtils;
import com.lc.mybatisx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class WhereTemplateHandler {

    private final ConditionProcessorFactory conditionProcessorFactory = new ConditionProcessorFactory();

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo) {
        this.execute(parentElement, entityInfo, methodInfo, methodInfo.getConditionInfoList());
    }

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo, List<ConditionInfo> conditionInfoList) {
        Element whereElement = parentElement.addElement("where");
        this.handleConditionGroup(methodInfo, whereElement, conditionInfoList);
        this.addOptimisticLockCondition(entityInfo, methodInfo, whereElement);
        this.addLogicDeleteCondition(entityInfo, whereElement);
    }

    private void addOptimisticLockCondition(EntityInfo entityInfo, MethodInfo methodInfo, Element whereElement) {
        // 查询不需要乐观锁版本条件
        ColumnInfo lockColumnInfo = entityInfo.getLockColumnInfo();
        if (lockColumnInfo != null) {
            // 只有更新的场景才需要乐观锁，逻辑删除不需要乐观锁，因为逻辑删除直接改变逻辑删除字段，因为不管什么操作都一定需要逻辑删除字段
            if (methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
                whereElement.addText(String.format(" and %s = #{%s}", lockColumnInfo.getDbColumnName(), lockColumnInfo.getJavaColumnName()));
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
    private void handleConditionGroup(MethodInfo methodInfo, Element whereElement, List<ConditionInfo> conditionInfoList) {
        for (ConditionInfo conditionInfo : conditionInfoList) {
            ConditionGroupInfo conditionGroupInfo = conditionInfo.getConditionGroupInfo();
            if (conditionGroupInfo != null) {
                // 处理分组的括号
                whereElement.addText(String.format(" %s %s", conditionInfo.getLogicOperator(), conditionInfo.getLeftBracket()));
                this.handleConditionGroup(methodInfo, whereElement, conditionGroupInfo.getConditionInfoList());
                whereElement.addText(conditionInfo.getRightBracket());
            } else {
                ColumnInfo columnInfo = conditionInfo.getColumnInfo();
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (logicDelete != null) {
                    continue;
                }
                conditionProcessorFactory.process(methodInfo, conditionInfo, whereElement);
            }
        }
    }

    static class ConditionProcessorFactory {

        protected final Map<String, ConditionHandler> conditionHandlers = new HashMap<>();

        public ConditionProcessorFactory() {
            conditionHandlers.put("like", new LikeConditionHandler());
            conditionHandlers.put("in", new InConditionHandler());
            conditionHandlers.put("between", new BetweenConditionHandler());
            conditionHandlers.put("default", new CommonConditionHandler());
        }

        public void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            ConditionHandler conditionHandler = conditionHandlers.getOrDefault(conditionInfo.getComparisonOperator().getValue(), conditionHandlers.get("default"));
            conditionHandler.handle(methodInfo, conditionInfo, whereElement);
        }
    }

    interface ConditionHandler {

        void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        void singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        void multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);
    }

    static abstract class AbstractConditionHandler implements ConditionHandler {

        @Override
        public void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            int paramCount = methodInfo.getMethodParamInfoList().size();
            if (paramCount == 1) {
                singleParamHandle(methodInfo, conditionInfo, whereElement);
            } else {
                multiParamHandle(methodInfo, conditionInfo, whereElement);
            }
        }

        /**
         *
         * @param logicOperator
         * @param comparisonOperator
         * @param columnInfo
         * @param paramValueExpression #{userId} or #{user.userId}
         * @return and user_id = #{userId} or and user_id = #{user.userId}
         */
        protected String getConditionExpression(LogicOperator logicOperator, ComparisonOperator comparisonOperator, ColumnInfo columnInfo, String paramValueExpression) {
            return String.format(
                    " %s %s %s #{%s}",
                    logicOperator.getValue(),
                    columnInfo.getDbColumnName(),
                    comparisonOperator.getValue(),
                    paramValueExpression
            );
        }

        protected void buildWhereItem(Element whereElement, Boolean dynamic, String testExpression, String conditionExpression) {
            Element whereOrIfElement = whereOpDynamic(dynamic, whereElement, testExpression);
            whereOrIfElement.addText(conditionExpression);
        }

        protected Element whereOpDynamic(Boolean dynamic, Element whereElement, String testExpression) {
            if (dynamic) {
                Element ifElement = whereElement.addElement("if");
                ifElement.addAttribute("test", testExpression);
                return ifElement;
            }
            return whereElement;
        }

        protected Element whereBindDynamic(Boolean dynamic, Element whereElement, String javaColumnName) {
            Element dynamicElement = whereElement;
            if (dynamic) {
                String testTemplate = "${test} != null";
                Properties properties = new Properties();
                properties.setProperty("test", javaColumnName);
                String testValue = PropertyPlaceholderUtils.replace(testTemplate, properties);

                Element ifElement = whereElement.addElement("if");
                ifElement.addAttribute("test", testValue);
                dynamicElement = ifElement;
            }
            return dynamicElement;
        }
    }

    static class LikeConditionHandler extends AbstractConditionHandler {

        @Override
        public void singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            String javaColumnName = null;// conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getColumnInfo().getJavaColumnName();

            String likeValueTemplate = "'%' + ${like} + '%'";
            Properties properties = new Properties();
            properties.setProperty("like", javaColumnName);
            String likeValue = PropertyPlaceholderUtils.replace(likeValueTemplate, properties);

            Element whereOrIfElement = whereBindDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
            Element bindElement = whereOrIfElement.addElement("bind");
            bindElement.addAttribute("name", javaColumnName);
            bindElement.addAttribute("value", likeValue);

            Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
            String conditionOp = String.format(" %s %s %s #{%s}", conditionInfo.getLogicOperator().getValue(), conditionInfo.getColumnName(), conditionInfo.getComparisonOperator().getValue(), javaColumnName);
            trimOrIfElement.addText(conditionOp);
        }

        @Override
        public void multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {

        }
    }

    static class InConditionHandler extends AbstractConditionHandler {

        @Override
        public void singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            /*String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();

            Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);

            List<MethodParamInfo> methodParamInfoList = conditionInfo.getMethodParamInfoList();
            String comparisonOp = conditionInfo.getComparisonOp();
            trimOrIfElement.addText(String.format(" %s %s %s ", this.getLogicOp(conditionInfo), conditionInfo.getDbColumnName(), comparisonOp));

            Element foreachElement = trimOrIfElement.addElement("foreach");
            foreachElement.addAttribute("index", "index");
            foreachElement.addAttribute("item", "item");
            foreachElement.addAttribute("collection", javaColumnName);
            foreachElement.addAttribute("open", "(");
            foreachElement.addAttribute("close", ")");
            foreachElement.addAttribute("separator", ",");
            foreachElement.addText("#{item}");*/
        }

        @Override
        public void multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {

        }
    }

    static class BetweenConditionHandler extends AbstractConditionHandler {

        @Override
        public void singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            /*String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();
            Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
            String conditionOp = String.format(" %s %s %s #{%s[0]} and #{%s[1]}", this.getLogicOp(conditionInfo), conditionInfo.getDbColumnName(), conditionInfo.getComparisonOp(), javaColumnName, javaColumnName);
            trimOrIfElement.addText(conditionOp);*/
        }

        @Override
        public void multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {

        }
    }

    static class CommonConditionHandler extends AbstractConditionHandler {

        @Override
        public void singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            ColumnInfo columnInfo = conditionInfo.getColumnInfo();
            MethodParamInfo methodParamInfo = conditionInfo.getMethodParamInfo();
            LogicOperator logicOperator = conditionInfo.getLogicOperator();
            ComparisonOperator comparisonOperator = conditionInfo.getComparisonOperator();
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getBasicType()) {
                    // findById(Long id) findById(@Param("id") Long id)
                    String testExpression = String.format("%1s != null", methodParamInfo.getArgName());
                    String paramValueExpression = String.format("%1s", methodParamInfo.getArgName());
                    String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
                    this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                } else {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            String testExpression = String.format("%1s != null", columnInfo.getJavaColumnName());
                            String paramValueExpression = String.format("%1s", columnInfo.getJavaColumnName());
                            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
                            this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                        } else {
                            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
                                String testExpression = String.format("%1s != null and %1s.%2s != null", columnInfo.getJavaColumnName(), columnInfo.getJavaColumnName(), columnInfoComposite.getJavaColumnName());
                                String paramValueExpression = String.format("%1s.%2s", columnInfo.getJavaColumnName(), columnInfoComposite.getJavaColumnName());
                                String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
                                this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                            }
                        }
                    } else {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            String testExpression = String.format("%1s.%2s != null", methodParamInfo.getArgName(), columnInfo.getJavaColumnName());
                            String paramValueExpression = String.format("%1s.%2s", methodParamInfo.getArgName(), columnInfo.getJavaColumnName());
                            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
                            this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                        } else {
                            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
                                String testExpression = String.format(
                                        "%1s != null and %1s.%2s != null and %1s.%2s.%3s != null",
                                        methodParamInfo.getArgName(),
                                        columnInfo.getJavaColumnName(),
                                        columnInfoComposite.getJavaColumnName()
                                );
                                String paramValueExpression = String.format(
                                        "%1s.%2s.%3s",
                                        methodParamInfo.getArgName(),
                                        columnInfo.getJavaColumnName(),
                                        columnInfoComposite.getJavaColumnName()
                                );
                                String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
                                this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                            }
                        }
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo foreignKeyInfo : inverseForeignKeyColumnInfoList) {
                        ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                        String testExpression = String.format(
                                "%1s != null and %1s.%2s != null",
                                columnInfo.getJavaColumnName(),
                                referencedColumnInfo.getJavaColumnName()
                        );
                        String paramValueExpression = String.format("%1s.%2s", columnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnName());
                        String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
                        this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                    }
                }
            }
        }

        @Override
        public void multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            MethodParamInfo methodParamInfo = conditionInfo.getMethodParamInfo();
            ColumnInfo columnInfo = conditionInfo.getColumnInfo();
            LogicOperator logicOperator = conditionInfo.getLogicOperator();
            ComparisonOperator comparisonOperator = conditionInfo.getComparisonOperator();
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getBasicType()) {
                    // findByIdAndNameLike(Long id, String name) findByIdAndNameLike(@Param("id") Long id, @Param("name") String name)
                    String testExpression = String.format("%1s != null", methodParamInfo.getArgName());
                    String paramValueExpression = String.format("%1s", methodParamInfo.getArgName());
                    String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
                    this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                } else {
                    // findByIdAndNameLike(MultiId id) findByIdAndNameLike(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            String testExpression = String.format(
                                    "%1s != null and %1s.%2s != null",
                                    methodParamInfo.getArgName(),
                                    columnInfo.getJavaColumnName()
                            );
                            String paramValueExpression = String.format(
                                    "%1s.%2s",
                                    methodParamInfo.getArgName(),
                                    columnInfo.getJavaColumnName()
                            );
                            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
                            this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                        } else {
                            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
                                String testExpression = String.format(
                                        "%1s != null and %1s.%2s != null and %1s.%2s.%3s != null",
                                        methodParamInfo.getArgName(),
                                        columnInfo.getJavaColumnName(),
                                        columnInfoComposite.getJavaColumnName()
                                );
                                String paramValueExpression = String.format(
                                        "%1s.%2s.%3s",
                                        methodParamInfo.getArgName(),
                                        columnInfo.getJavaColumnName(),
                                        columnInfoComposite.getJavaColumnName()
                                );
                                String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
                                this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                            }
                        }
                    } else {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            String testExpression = String.format(
                                    "%1s != null and %1s.%2s != null",
                                    methodParamInfo.getArgName(),
                                    columnInfo.getJavaColumnName()
                            );
                            String paramValueExpression = String.format(
                                    "%1s.%2s",
                                    methodParamInfo.getArgName(),
                                    columnInfo.getJavaColumnName()
                            );
                            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
                            this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                        } else {
                            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
                                String testExpression = String.format(
                                        "%1s != null and %1s.%2s != null and %1s.%2s.%3s != null",
                                        methodParamInfo.getArgName(),
                                        columnInfo.getJavaColumnName(),
                                        columnInfoComposite.getJavaColumnName()
                                );
                                String paramValueExpression = String.format(
                                        "%1s.%2s.%3s",
                                        methodParamInfo.getArgName(),
                                        columnInfo.getJavaColumnName(),
                                        columnInfoComposite.getJavaColumnName()
                                );
                                String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
                                this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                            }
                        }
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    for (ForeignKeyColumnInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                        ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                        String testExpression = String.format(
                                "%1s != null and %1s.%2s != null and %1s.%2s.%3s != null",
                                methodParamInfo.getArgName(),
                                columnInfo.getJavaColumnName(),
                                referencedColumnInfo.getJavaColumnName()
                        );
                        String paramValueExpression = String.format(
                                "%1s.%2s.%3s",
                                methodParamInfo.getArgName(),
                                columnInfo.getJavaColumnName(),
                                referencedColumnInfo.getJavaColumnName()
                        );
                        String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
                        this.buildWhereItem(whereElement, methodInfo.getDynamic(), testExpression, conditionExpression);
                    }
                }
            }
        }
    }
}
