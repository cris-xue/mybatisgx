package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.PropertyPlaceholderUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.List;
import java.util.Properties;

public class WhereTemplateHandler {

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo) {
        Element whereElement = parentElement.addElement("where");
        Element trimElement = whereElement.addElement("trim");
        trimElement.addAttribute("prefix", "");
        trimElement.addAttribute("suffix", "");
        trimElement.addAttribute("prefixOverrides", "AND|OR|and|or");
        this.handleConditionGroup(entityInfo, methodInfo, whereElement, trimElement, methodInfo.getConditionInfoList());
        /*methodInfo.getConditionInfoList().forEach(conditionInfo -> {
            ConditionGroupInfo conditionGroupInfo = conditionInfo.getConditionGroupInfo();
            if (conditionGroupInfo != null) {
                List<ConditionInfo> conditionInfoList = conditionGroupInfo.getConditionInfoList();
            } else {
                ColumnInfo columnInfo = entityInfo.getColumnInfo(conditionInfo.getJavaColumnName());
                Id id = columnInfo.getId();
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (id != null) {
                    processId(trimElement, entityInfo, methodInfo.getDynamic());
                    return;
                }
                if (logicDelete != null) {
                    return;
                }
                this.processCondition(methodInfo, conditionInfo, whereElement, trimElement);
            }
        });*/

        // 查询不需要乐观锁版本条件
        ColumnInfo lockColumnInfo = entityInfo.getLockColumnInfo();
        if (lockColumnInfo != null) {
            // 只有更新的场景才需要乐观锁，逻辑删除不需要乐观锁，因为逻辑删除直接改变逻辑删除字段，因为不管什么操作都一定需要逻辑删除字段
            if ("update".equals(methodInfo.getAction())) {
                trimElement.addText(String.format(" and %s = #{%s}", lockColumnInfo.getDbColumnName(), lockColumnInfo.getJavaColumnName()));
            }
        }

        // 逻辑删除
        ColumnInfo logicDeleteColumnInfo = entityInfo.getLogicDeleteColumnInfo();
        if (logicDeleteColumnInfo != null) {
            LogicDelete logicDelete = logicDeleteColumnInfo.getLogicDelete();
            trimElement.addText(String.format(" and %s = '%s'", logicDeleteColumnInfo.getDbColumnName(), logicDelete.show()));
        }
    }

    /**
     * 处理条件组
     *
     * @param entityInfo
     * @param methodInfo
     * @param whereElement
     * @param trimElement
     * @param conditionInfoList
     */
    private void handleConditionGroup(EntityInfo entityInfo, MethodInfo methodInfo, Element whereElement, Element trimElement, List<ConditionInfo> conditionInfoList) {
        for (int i = 0; i < conditionInfoList.size(); i++) {
            ConditionInfo conditionInfo = conditionInfoList.get(i);
            ConditionGroupInfo conditionGroupInfo = conditionInfo.getConditionGroupInfo();
            if (conditionGroupInfo != null) {
                // 处理分组的括号
                trimElement.addText(conditionInfo.getLogicOp());
                trimElement.addText(conditionInfo.getLeftBracket());
                this.handleConditionGroup(entityInfo, methodInfo, whereElement, trimElement, conditionGroupInfo.getConditionInfoList());
                trimElement.addText(conditionInfo.getRightBracket());
            } else {
                ColumnInfo columnInfo = entityInfo.getColumnInfo(conditionInfo.getJavaColumnName());
                Id id = columnInfo.getId();
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (id != null) {
                    processId(trimElement, entityInfo, methodInfo.getDynamic());
                    return;
                }
                if (logicDelete != null) {
                    return;
                }
                this.processCondition(methodInfo, conditionInfo, whereElement, trimElement);
            }
        }
    }

    private void processId(Element trimElement, EntityInfo entityInfo, Boolean dynamic) {
        List<ColumnInfo> idColumnInfoList = entityInfo.getIdColumnInfoList();
        for (int i = 0; i < idColumnInfoList.size(); i++) {
            ColumnInfo idColumnInfo = idColumnInfoList.get(i);
            if (dynamic) {
                Element ifElement = trimElement.addElement("if");
                ifElement.addAttribute("test", String.format("%s != null", idColumnInfo.getJavaColumnName()));
                ifElement.addText(String.format(" %s %s %s #{%s}", "and", idColumnInfo.getDbColumnName(), "=", idColumnInfo.getJavaColumnName()));
            } else {
                trimElement.addText(String.format(" %s %s %s #{%s}", "and", idColumnInfo.getDbColumnName(), "=", idColumnInfo.getJavaColumnName()));
            }
        }
    }

    private void processMethodCondition(EntityInfo entityInfo, MethodInfo methodInfo, ColumnInfo columnInfo, ConditionInfo conditionInfo, Element trimElement) {
        Boolean conditionEntity = conditionInfo.getConditionEntity();
        if (conditionEntity) {
            return;
        }
        if (methodInfo.getDynamic()) {
            List<MethodParamInfo> methodParamInfoList = conditionInfo.getMethodParamInfoList();
            Element ifElement = trimElement.addElement("if");
            ifElement.addAttribute("test", String.format("%s != null", methodParamInfoList.get(0).getParamName()));
            buildMethodCondition(ifElement, entityInfo, columnInfo, conditionInfo);
        } else {
            buildMethodCondition(trimElement, entityInfo, columnInfo, conditionInfo);
        }
    }

    private void buildMethodCondition(Element parentElement, EntityInfo entityInfo, ColumnInfo columnInfo, ConditionInfo conditionInfo) {
        List<MethodParamInfo> methodParamInfoList = conditionInfo.getMethodParamInfoList();
        String comparisonOp = conditionInfo.getComparisonOp();
        parentElement.addText(String.format(" %s %s %s ", conditionInfo.getLogicOp(), conditionInfo.getDbColumnName(), comparisonOp));
        if ("in".equals(comparisonOp)) {
            Element foreachElement = parentElement.addElement("foreach");
            foreachElement.addAttribute("item", "item");
            foreachElement.addAttribute("index", "index");
            foreachElement.addAttribute("collection", methodParamInfoList.get(0).getParamName());
            foreachElement.addAttribute("open", "(");
            foreachElement.addAttribute("separator", ",");
            foreachElement.addAttribute("close", ")");
            foreachElement.addText("#{item}");
        } else if ("between".equals(comparisonOp)) {
            // 把between修改成大于和小于
            // parentElement.addText(String.format("#{%s} and #{%s}", paramNameList.get(0), paramNameList.get(1)));
        } else {
            String typeHandler = columnInfo.getTypeHandler();
            String typeHandlerTemplate = "";
            if (StringUtils.isNotBlank(typeHandler)) {
                typeHandlerTemplate = String.format(", typeHandler=%s", typeHandler);
            }
            parentElement.addText(String.format("#{%s%s}", methodParamInfoList.get(0).getParamName(), typeHandlerTemplate));
        }
    }

    private void processCondition(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement, Element trimElement) {
        String comparisonOp = conditionInfo.getComparisonOp();
        if ("like".equals(comparisonOp)) {
            whereLike(whereElement, trimElement, methodInfo, conditionInfo);
        } else if ("in".equals(comparisonOp)) {
            whereIn(whereElement, trimElement, methodInfo, conditionInfo);
        } else if ("between".equals(comparisonOp)) {
            whereBetween(whereElement, trimElement, methodInfo, conditionInfo);
        } else {
            whereCommon(whereElement, trimElement, methodInfo, conditionInfo);
        }
    }

    private void whereCommon(Element whereElement, Element trimElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
        String dbColumnName = conditionInfo.getDbColumnName();
        String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();
        String logicOp = conditionInfo.getLogicOp();
        String comparisonOp = conditionInfo.getComparisonOp();
        Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), trimElement, javaColumnName);
        String conditionOp = String.format(" %s %s %s #{%s}", logicOp, dbColumnName, comparisonOp, javaColumnName);
        trimOrIfElement.addText(conditionOp);
    }

    /**
     * <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
     * <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
     * and act.client_code like #{likeClientCode}
     * </if>
     */
    private void whereLike(Element whereElement, Element trimElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
        String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();

        String likeValueTemplate = "'%' + ${like} + '%'";
        Properties properties = new Properties();
        properties.setProperty("like", javaColumnName);
        String likeValue = PropertyPlaceholderUtils.replace(likeValueTemplate, properties);

        Element whereOrIfElement = whereBindDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
        Element bindElement = whereOrIfElement.addElement("bind");
        bindElement.addAttribute("name", javaColumnName);
        bindElement.addAttribute("value", likeValue);

        Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), trimElement, javaColumnName);
        String conditionOp = String.format(" %s %s %s #{%s}", conditionInfo.getLogicOp(), conditionInfo.getDbColumnName(), conditionInfo.getComparisonOp(), javaColumnName);
        trimOrIfElement.addText(conditionOp);
    }

    private void whereIn(Element whereElement, Element trimElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
        String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();

        Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), trimElement, javaColumnName);

        List<MethodParamInfo> methodParamInfoList = conditionInfo.getMethodParamInfoList();
        String comparisonOp = conditionInfo.getComparisonOp();
        trimOrIfElement.addText(String.format(" %s %s %s ", conditionInfo.getLogicOp(), conditionInfo.getDbColumnName(), comparisonOp));

        Element foreachElement = trimOrIfElement.addElement("foreach");
        foreachElement.addAttribute("index", "index");
        foreachElement.addAttribute("item", "item");
        foreachElement.addAttribute("collection", javaColumnName);
        foreachElement.addAttribute("open", "(");
        foreachElement.addAttribute("close", ")");
        foreachElement.addAttribute("separator", ",");
        foreachElement.addText("#{item}");
    }

    private void whereBetween(Element whereElement, Element trimElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
        String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();
        Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), trimElement, javaColumnName);
        String conditionOp = String.format(" %s %s %s #{%s[0]} and #{%s[1]}", conditionInfo.getLogicOp(), conditionInfo.getDbColumnName(), conditionInfo.getComparisonOp(), javaColumnName, javaColumnName);
        trimOrIfElement.addText(conditionOp);
    }

    private Element whereOpDynamic(Boolean dynamic, Element trimElement, String javaColumnName) {
        Element dynamicElement = trimElement;
        if (dynamic) {
            String testTemplate = "${test} != null";
            Properties properties = new Properties();
            properties.setProperty("test", javaColumnName);
            String testValue = PropertyPlaceholderUtils.replace(testTemplate, properties);

            Element ifElement = trimElement.addElement("if");
            ifElement.addAttribute("test", testValue);
            dynamicElement = ifElement;
        }
        return dynamicElement;
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
}
