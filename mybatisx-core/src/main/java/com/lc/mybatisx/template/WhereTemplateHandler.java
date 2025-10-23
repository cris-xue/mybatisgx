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

        // 查询不需要乐观锁版本条件
        ColumnInfo lockColumnInfo = entityInfo.getLockColumnInfo();
        if (lockColumnInfo != null) {
            // 只有更新的场景才需要乐观锁，逻辑删除不需要乐观锁，因为逻辑删除直接改变逻辑删除字段，因为不管什么操作都一定需要逻辑删除字段
            if ("update".equals(methodInfo.getAction())) {
                whereElement.addText(String.format(" and %s = #{%s}", lockColumnInfo.getDbColumnName(), lockColumnInfo.getJavaColumnName()));
            }
        }

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

                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class)) {
                    this.processId(whereElement, methodInfo, conditionInfo);
                }
                if (TypeUtils.typeEquals(columnInfo, ColumnInfo.class)) {
                    this.processCondition(methodInfo, conditionInfo, whereElement);
                }
                if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                    RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                    RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
                    if (mappedByRelationColumnInfo == null) {
                        this.processCondition(methodInfo, conditionInfo, whereElement);
                    }
                }
            }
        }
    }

    private void processId(Element whereElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
        IdColumnInfo idColumnInfo = (IdColumnInfo) conditionInfo.getColumnInfo();
        List<ColumnInfo> composites = idColumnInfo.getComposites();
        if (ObjectUtils.isEmpty(composites)) {
            List<String> javaColumnPathList = Arrays.asList(idColumnInfo.getJavaColumnName());
            ColumnInfo composite = new ColumnInfo.Builder().columnInfo(idColumnInfo).javaColumnPathList(javaColumnPathList).build();
            this.processIdCondition(whereElement, methodInfo, composite);
        } else {
            for (ColumnInfo columnInfo : composites) {
                List<String> javaColumnPathList = Arrays.asList(conditionInfo.getConditionName(), columnInfo.getJavaColumnName());
                ColumnInfo composite = new ColumnInfo.Builder().columnInfo(columnInfo).javaColumnPathList(javaColumnPathList).build();
                this.processIdCondition(whereElement, methodInfo, composite);
            }
        }
    }

    private void processIdCondition(Element whereElement, MethodInfo methodInfo, ColumnInfo columnInfo) {
        List<String> javaColumnPathList = columnInfo.getJavaColumnPathList();
        String javaColumnName = StringUtils.join(javaColumnPathList, ".");
        String tableColumnName = columnInfo.getDbColumnName();
        if (methodInfo.getDynamic()) {
            List<String> testExpressionList = new ArrayList();
            List<String> previousJavaColumnPath = new ArrayList();
            for (String javaColumnPath : javaColumnPathList) {
                previousJavaColumnPath.add(javaColumnPath);
                javaColumnName = StringUtils.join(previousJavaColumnPath, ".");
                String testExpression = String.format("%s != null", javaColumnName);
                testExpressionList.add(testExpression);
            }
            String testExpressionString = StringUtils.join(testExpressionList, " and ");

            Element ifElement = whereElement.addElement("if");
            ifElement.addAttribute("test", testExpressionString);
            ifElement.addText(String.format(" %s %s %s #{%s}", "and", tableColumnName, "=", javaColumnName));
        } else {
            whereElement.addText(String.format(" %s %s %s #{%s}", "and", tableColumnName, "=", javaColumnName));
        }
    }

    private void processCondition(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
        String comparisonOp = conditionInfo.getComparisonOp();
        if ("like".equals(comparisonOp)) {
            whereLike(whereElement, methodInfo, conditionInfo);
        } else if ("in".equals(comparisonOp)) {
            whereIn(whereElement, methodInfo, conditionInfo);
        } else if ("between".equals(comparisonOp)) {
            whereBetween(whereElement, methodInfo, conditionInfo);
        } else {
            whereCommon(whereElement, methodInfo, conditionInfo);
        }
    }

    private void whereCommon(Element whereElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
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

    /**
     * <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
     * <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
     * and act.client_code like #{likeClientCode}
     * </if>
     */
    private void whereLike(Element whereElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
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

    private void whereIn(Element whereElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
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

    private void whereBetween(Element whereElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
        String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();
        Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
        String conditionOp = String.format(" %s %s %s #{%s[0]} and #{%s[1]}", this.getLogicOp(conditionInfo), conditionInfo.getDbColumnName(), conditionInfo.getComparisonOp(), javaColumnName, javaColumnName);
        trimOrIfElement.addText(conditionOp);
    }

    private Element whereOpDynamic(Boolean dynamic, Element whereElement, String javaColumnName) {
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

    private Element whereBindDynamic(Boolean dynamic, Element whereElement, String javaColumnName) {
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

        }

        private void processId(Element whereElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
            IdColumnInfo idColumnInfo = (IdColumnInfo) conditionInfo.getColumnInfo();
            List<ColumnInfo> composites = idColumnInfo.getComposites();
            if (ObjectUtils.isEmpty(composites)) {
                List<String> javaColumnPathList = Arrays.asList(idColumnInfo.getJavaColumnName());
                ColumnInfo composite = new ColumnInfo.Builder().columnInfo(idColumnInfo).javaColumnPathList(javaColumnPathList).build();
                this.processIdCondition(whereElement, methodInfo, composite);
            } else {
                for (ColumnInfo columnInfo : composites) {
                    List<String> javaColumnPathList = Arrays.asList(conditionInfo.getConditionName(), columnInfo.getJavaColumnName());
                    ColumnInfo composite = new ColumnInfo.Builder().columnInfo(columnInfo).javaColumnPathList(javaColumnPathList).build();
                    this.processIdCondition(whereElement, methodInfo, composite);
                }
            }
        }

        private void processIdCondition(Element whereElement, MethodInfo methodInfo, ColumnInfo columnInfo) {
            List<String> javaColumnPathList = columnInfo.getJavaColumnPathList();
            String javaColumnName = StringUtils.join(javaColumnPathList, ".");
            String tableColumnName = columnInfo.getDbColumnName();
            if (methodInfo.getDynamic()) {
                List<String> testExpressionList = new ArrayList();
                List<String> previousJavaColumnPath = new ArrayList();
                for (String javaColumnPath : javaColumnPathList) {
                    previousJavaColumnPath.add(javaColumnPath);
                    javaColumnName = StringUtils.join(previousJavaColumnPath, ".");
                    String testExpression = String.format("%s != null", javaColumnName);
                    testExpressionList.add(testExpression);
                }
                String testExpressionString = StringUtils.join(testExpressionList, " and ");

                Element ifElement = whereElement.addElement("if");
                ifElement.addAttribute("test", testExpressionString);
                ifElement.addText(String.format(" %s %s %s #{%s}", "and", tableColumnName, "=", javaColumnName));
            } else {
                whereElement.addText(String.format(" %s %s %s #{%s}", "and", tableColumnName, "=", javaColumnName));
            }
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

        }

        private void processCondition(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            String comparisonOp = conditionInfo.getComparisonOp();
            if ("like".equals(comparisonOp)) {
                whereLike(whereElement, methodInfo, conditionInfo);
            } else if ("in".equals(comparisonOp)) {
                whereIn(whereElement, methodInfo, conditionInfo);
            } else if ("between".equals(comparisonOp)) {
                whereBetween(whereElement, methodInfo, conditionInfo);
            } else {
                whereCommon(whereElement, methodInfo, conditionInfo);
            }
        }
    }

    static class RelationConditionProcessor implements ConditionProcessor {

        @Override
        public void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {

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
}
