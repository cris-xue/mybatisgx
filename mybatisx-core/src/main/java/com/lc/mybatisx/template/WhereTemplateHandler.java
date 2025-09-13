package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.PropertyPlaceholderUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.List;
import java.util.Properties;

public class WhereTemplateHandler {

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo) {
        this.execute(parentElement, entityInfo, methodInfo, methodInfo.getConditionInfoList());
    }

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo, List<ConditionInfo> conditionInfoList) {
        Element whereElement = parentElement.addElement("where");
        this.handleConditionGroup(entityInfo, methodInfo, whereElement, conditionInfoList);

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
     * @param entityInfo
     * @param methodInfo
     * @param whereElement
     * @param conditionInfoList
     */
    private void handleConditionGroup(EntityInfo entityInfo, MethodInfo methodInfo, Element whereElement, List<ConditionInfo> conditionInfoList) {
        for (ConditionInfo conditionInfo : conditionInfoList) {
            ConditionGroupInfo conditionGroupInfo = conditionInfo.getConditionGroupInfo();
            if (conditionGroupInfo != null) {
                // 处理分组的括号
                whereElement.addText(String.format(" %s %s", this.getLogicOp(conditionInfo), conditionInfo.getLeftBracket()));
                this.handleConditionGroup(entityInfo, methodInfo, whereElement, conditionGroupInfo.getConditionInfoList());
                whereElement.addText(conditionInfo.getRightBracket());
            } else {
                // ColumnInfo columnInfo = entityInfo.getColumnInfo(conditionInfo.getJavaColumnName());
                ColumnInfo columnInfo = conditionInfo.getColumnInfo();
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (columnInfo instanceof IdColumnInfo) {
                    processId(whereElement, entityInfo, columnInfo, methodInfo.getDynamic());
                    return;
                }
                if (logicDelete != null) {
                    return;
                }
                this.processCondition(methodInfo, conditionInfo, whereElement);
            }
        }
    }

    private void processId(Element whereElement, EntityInfo entityInfo, ColumnInfo columnInfo, Boolean dynamic) {
        if (!(columnInfo instanceof IdColumnInfo)) {
            this.processIdCondition(whereElement, columnInfo.getJavaColumnName(), columnInfo.getDbColumnName(), dynamic);
            /*if (dynamic) {
                Element ifElement = whereElement.addElement("if");
                ifElement.addAttribute("test", String.format("%s != null", columnInfo.getJavaColumnName()));
                ifElement.addText(String.format(" %s %s %s #{%s}", "and", columnInfo.getDbColumnName(), "=", columnInfo.getJavaColumnName()));
            } else {
                whereElement.addText(String.format(" %s %s %s #{%s}", "and", columnInfo.getDbColumnName(), "=", columnInfo.getJavaColumnName()));
            }*/
        } else {
            List<ColumnInfo> idColumnInfoList = ((IdColumnInfo) columnInfo).getColumnInfoList();
            for (ColumnInfo idColumnInfo : idColumnInfoList) {
                String javaColumnName = String.format("%s.%s", columnInfo.getJavaColumnName(), idColumnInfo.getJavaColumnName());
                this.processIdCondition(whereElement, javaColumnName, idColumnInfo.getDbColumnName(), dynamic);
                // this.processId(whereElement, entityInfo, idColumnInfo, dynamic);
                /*if (dynamic) {
                    Element ifElement = whereElement.addElement("if");
                    ifElement.addAttribute("test", String.format("%s != null", idColumnInfo.getJavaColumnName()));
                    ifElement.addText(String.format(" %s %s %s #{%s}", "and", idColumnInfo.getDbColumnName(), "=", idColumnInfo.getJavaColumnName()));
                } else {
                    whereElement.addText(String.format(" %s %s %s #{%s}", "and", idColumnInfo.getDbColumnName(), "=", idColumnInfo.getJavaColumnName()));
                }*/
            }
        }
    }

    private void processIdCondition(Element whereElement, String javaColumnName, String tableColumnName, Boolean dynamic) {
        if (dynamic) {
            Element ifElement = whereElement.addElement("if");
            ifElement.addAttribute("test", String.format("%s != null", javaColumnName));
            ifElement.addText(String.format(" %s %s %s #{%s}", "and", tableColumnName, "=", javaColumnName));
        } else {
            whereElement.addText(String.format(" %s %s %s #{%s}", "and", tableColumnName, "=", javaColumnName));
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
        /*String dbColumnName = conditionInfo.getDbColumnName();
        String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();*/
        String tableColumnName = conditionInfo.getColumnInfo().getDbColumnName();
        String javaColumnName = conditionInfo.getColumnInfo().getJavaColumnName();
        String logicOp = this.getLogicOp(conditionInfo);
        String comparisonOp = conditionInfo.getComparisonOp();
        Element trimOrIfElement = whereOpDynamic(methodInfo.getDynamic(), whereElement, javaColumnName);
        String conditionOp = String.format(" %s %s %s #{%s}", logicOp, tableColumnName, comparisonOp, javaColumnName);
        trimOrIfElement.addText(conditionOp);
    }

    /**
     * <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
     * <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
     * and act.client_code like #{likeClientCode}
     * </if>
     */
    private void whereLike(Element whereElement, MethodInfo methodInfo, ConditionInfo conditionInfo) {
        String javaColumnName = conditionInfo.getConditionEntity() ? conditionInfo.getConditionEntityJavaColumnName() : conditionInfo.getJavaColumnName();

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
}
