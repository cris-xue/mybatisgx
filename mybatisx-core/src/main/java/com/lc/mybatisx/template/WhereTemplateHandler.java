package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.PropertyPlaceholderUtils;
import com.lc.mybatisx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.*;

public class WhereTemplateHandler {

    private final ConditionProcessorFactory processorFactory = new ConditionProcessorFactory();

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
            if ("update".equals(methodInfo.getAction())) {
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
                whereElement.addText(String.format(" %s %s", this.getLogicOp(conditionInfo), conditionInfo.getLeftBracket()));
                this.handleConditionGroup(methodInfo, whereElement, conditionGroupInfo.getConditionInfoList());
                whereElement.addText(conditionInfo.getRightBracket());
            } else {
                ColumnInfo columnInfo = conditionInfo.getColumnInfo();
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (logicDelete != null) {
                    continue;
                }
                ConditionProcessor conditionProcessor = this.processorFactory.getProcessor(columnInfo);
                conditionProcessor.process(methodInfo, conditionInfo, whereElement);
            }
        }
    }

    /**
     * 获取逻辑运算符
     *
     * @param conditionInfo
     * @return
     */
    private String getLogicOp(ConditionInfo conditionInfo) {
        String logicOp = conditionInfo.getLogicOp();
        if (StringUtils.isBlank(logicOp)) {
            return "";
        }
        return logicOp;
    }

    static class ConditionProcessorFactory {

        private final Map<Class<?>, ConditionProcessor> processors = new HashMap<>();

        public ConditionProcessorFactory() {
            processors.put(IdColumnInfo.class, new IdConditionProcessor());
            processors.put(ColumnInfo.class, new CommonConditionProcessor());
            processors.put(RelationColumnInfo.class, new RelationConditionProcessor());
        }

        public ConditionProcessor getProcessor(ColumnInfo columnInfo) {
            return processors.getOrDefault(columnInfo.getClass(), new CommonConditionProcessor());
        }
    }

    interface ConditionProcessor {

        void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);
    }

    static class IdConditionProcessor implements ConditionProcessor {

        @Override
        public void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            IdColumnInfo idColumnInfo = (IdColumnInfo) conditionInfo.getColumnInfo();
            if (ObjectUtils.isEmpty(idColumnInfo.getComposites())) {
                processSimpleId(whereElement, methodInfo, idColumnInfo);
            } else {
                processCompositeId(whereElement, methodInfo, idColumnInfo);
            }
        }

        private void processSimpleId(Element whereElement, MethodInfo methodInfo, IdColumnInfo idColumnInfo) {
            String testExpression = String.format("%s != null", idColumnInfo.getJavaColumnName());
            Element whereOrIfElement = ConditionUtils.createDynamicElementIfNeeded(whereElement, methodInfo.getDynamic(), testExpression);
            whereOrIfElement.addText(String.format("and %s = #{%s}", idColumnInfo.getDbColumnName(), idColumnInfo.getJavaColumnName()));
        }

        private void processCompositeId(Element whereElement, MethodInfo methodInfo, IdColumnInfo idColumnInfo) {
            String testExpression = this.getTestExpression(idColumnInfo);
            Element whereOrIfElement = ConditionUtils.createDynamicElementIfNeeded(whereElement, methodInfo.getDynamic(), testExpression);
            String conditionExpression = this.getConditionExpression(idColumnInfo);
            whereOrIfElement.addText(conditionExpression);
        }

        private String getTestExpression(IdColumnInfo idColumnInfo) {
            List<String> testExpressionList = new ArrayList();
            for (ColumnInfo columnInfo : idColumnInfo.getComposites()) {
                testExpressionList.add(String.format("%s != null and %s.%s != null", idColumnInfo.getJavaColumnName(), idColumnInfo.getJavaColumnName(), columnInfo.getJavaColumnName()));
            }
            return StringUtils.join(testExpressionList, " and ");
        }

        private String getConditionExpression(IdColumnInfo idColumnInfo) {
            List<String> conditionExpressionList = new ArrayList();
            for (ColumnInfo columnInfo : idColumnInfo.getComposites()) {
                conditionExpressionList.add(String.format("and %s = #{%s.%s} ", columnInfo.getDbColumnName(), idColumnInfo.getJavaColumnName(), columnInfo.getJavaColumnName()));
            }
            return StringUtils.join(conditionExpressionList, "");
        }
    }

    static class CommonConditionProcessor implements ConditionProcessor {

        private final Map<String, ConditionHandler> conditionHandlers = new HashMap<>();

        public CommonConditionProcessor() {
            conditionHandlers.put("like", new LikeConditionHandler());
            conditionHandlers.put("in", new InConditionHandler());
            conditionHandlers.put("between", new BetweenConditionHandler());
            conditionHandlers.put("default", new CommonConditionHandler());
        }

        @Override
        public void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            ConditionHandler conditionHandler = conditionHandlers.getOrDefault(conditionInfo.getComparisonOp(), conditionHandlers.get("default"));
            conditionHandler.handle(methodInfo, conditionInfo, whereElement);
        }
    }

    static class RelationConditionProcessor implements ConditionProcessor {

        private final Map<String, ConditionHandler> conditionHandlers = new HashMap<>();

        public RelationConditionProcessor() {
            conditionHandlers.put("like", new LikeConditionHandler());
            conditionHandlers.put("in", new InConditionHandler());
            conditionHandlers.put("between", new BetweenConditionHandler());
            conditionHandlers.put("default", new CommonConditionHandler());
        }

        @Override
        public void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            ConditionHandler conditionHandler = conditionHandlers.getOrDefault(conditionInfo.getComparisonOp(), conditionHandlers.get("default"));
            conditionHandler.handle(methodInfo, conditionInfo, whereElement);
        }
    }

    interface ConditionHandler {

        void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);
    }

    static abstract class AbstractConditionHandler implements ConditionHandler {

        protected Element whereOpDynamic(Boolean dynamic, Element whereElement, String javaColumnName) {
            if (dynamic) {
                String testTemplate = "${test} != null";
                Properties properties = new Properties();
                properties.setProperty("test", javaColumnName);
                String testValue = PropertyPlaceholderUtils.replace(testTemplate, properties);

                Element ifElement = whereElement.addElement("if");
                ifElement.addAttribute("test", testValue);
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

        /**
         * 获取逻辑运算符
         *
         * @param conditionInfo
         * @return
         */
        protected String getLogicOp(ConditionInfo conditionInfo) {
            String logicOp = conditionInfo.getLogicOp();
            if (StringUtils.isBlank(logicOp)) {
                return "";
            }
            return logicOp;
        }
    }

    static class LikeConditionHandler extends AbstractConditionHandler {

        @Override
        public void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getColumnInfo().getJavaColumnName();

            String likeValueTemplate = "'%' + ${like} + '%'";
            Properties properties = new Properties();
            properties.setProperty("like", javaColumnName);
            String likeValue = PropertyPlaceholderUtils.replace(likeValueTemplate, properties);

            Element whereOrIfElement = whereBindDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
            Element bindElement = whereOrIfElement.addElement("bind");
            bindElement.addAttribute("name", javaColumnName);
            bindElement.addAttribute("value", likeValue);

            Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
            String conditionOp = String.format(" %s %s %s #{%s}", this.getLogicOp(conditionInfo), conditionInfo.getDbColumnName(), conditionInfo.getComparisonOp(), javaColumnName);
            trimOrIfElement.addText(conditionOp);
        }
    }

    static class InConditionHandler extends AbstractConditionHandler {

        @Override
        public void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();

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
            foreachElement.addText("#{item}");
        }
    }

    static class BetweenConditionHandler extends AbstractConditionHandler {

        @Override
        public void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();
            Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
            String conditionOp = String.format(" %s %s %s #{%s[0]} and #{%s[1]}", this.getLogicOp(conditionInfo), conditionInfo.getDbColumnName(), conditionInfo.getComparisonOp(), javaColumnName, javaColumnName);
            trimOrIfElement.addText(conditionOp);
        }
    }

    static class CommonConditionHandler extends AbstractConditionHandler {

        @Override
        public void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            ColumnInfo columnInfo = conditionInfo.getColumnInfo();
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
                if (relationColumnInfo.getRelationType() != RelationType.MANY_TO_MANY && mappedByRelationColumnInfo == null) {
                    String tableColumnName = relationColumnInfo.getDbColumnName();
                    List<ForeignKeyColumnInfo> foreignKeyInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo foreignKeyInfo : foreignKeyInfoList) {
                        ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                        String javaColumnName = relationColumnInfo.getJavaColumnName() + "." + referencedColumnInfo.getJavaColumnName();
                        String logicOp = this.getLogicOp(conditionInfo);
                        String comparisonOp = conditionInfo.getComparisonOp();
                        Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
                        String conditionOp = String.format(" %s %s %s #{%s}", logicOp, tableColumnName, comparisonOp, javaColumnName);
                        trimOrIfElement.addText(conditionOp);
                    }
                }
            } else {
                String tableColumnName = conditionInfo.getColumnInfo().getDbColumnName();
                String javaColumnName = conditionInfo.getColumnInfo().getJavaColumnName();
                String logicOp = this.getLogicOp(conditionInfo);
                String comparisonOp = conditionInfo.getComparisonOp();
                Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
                String conditionOp = String.format(" %s %s %s #{%s}", logicOp, tableColumnName, comparisonOp, javaColumnName);
                trimOrIfElement.addText(conditionOp);
            }
        }
    }

    static class ConditionUtils {

        /**
         * 创建动态条件元素（if标签）
         */
        public static Element createDynamicElementIfNeeded(Element parent, boolean dynamic, String testExpression) {
            if (dynamic) {
                Element ifElement = parent.addElement("if");
                ifElement.addAttribute("test", testExpression);
                return ifElement;
            }
            return parent;
        }

        /**
         * 创建带bind的动态元素
         */
        public static Element createBindDynamicElement(Element parent, boolean dynamic, String javaColumnName) {
            if (dynamic) {
                Element ifElement = parent.addElement("if");
                ifElement.addAttribute("test", javaColumnName + " != null");
                return ifElement;
            }
            return parent;
        }
    }
}
