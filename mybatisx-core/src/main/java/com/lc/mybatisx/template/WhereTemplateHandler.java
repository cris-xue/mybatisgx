package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.List;

public class WhereTemplateHandler {

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo) {
        Element whereElement = parentElement.addElement("where");
        Element trimElement = whereElement.addElement("trim");
        trimElement.addAttribute("prefix", "");
        trimElement.addAttribute("suffix", "");
        trimElement.addAttribute("prefixOverrides", "AND|OR|and|or");
        methodInfo.getConditionInfoList().forEach(conditionInfo -> {
            ColumnInfo columnInfo = entityInfo.getColumnInfo(conditionInfo.getJavaColumnName());
            Id id = columnInfo.getId();
            Lock lock = columnInfo.getLock();
            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (id != null) {
                processId(trimElement, entityInfo, methodInfo.getDynamic());
                return;
            }
            if (logicDelete != null) {
                return;
            }

            Boolean conditionEntity = conditionInfo.getConditionEntity();
            if (conditionEntity) {
                String op;
                if ("like".equals(conditionInfo.getOp())) {
                    String like = new StringBuilder("%#{").append(conditionInfo.getJavaColumnName()).append("}%").toString();
                    op = String.format(" %s %s %s %s", "and", conditionInfo.getDbColumnName(), conditionInfo.getOp(), like);
                } else {
                    op = String.format(" %s %s %s #{%s}", "and", conditionInfo.getDbColumnName(), conditionInfo.getOp(), conditionInfo.getJavaColumnName());
                }
                if (methodInfo.getDynamic()) {
                    Element ifElement = trimElement.addElement("if");
                    ifElement.addAttribute("test", String.format("%s != null", conditionInfo.getJavaColumnName()));
                    ifElement.addText(op);
                } else {
                    trimElement.addText(op);
                }
                return;
            }

            if (methodInfo.getDynamic()) {
                List<MethodParamInfo> methodParamInfoList = conditionInfo.getMethodParamInfoList();
                Element ifElement = trimElement.addElement("if");
                ifElement.addAttribute("test", String.format("%s != null", methodParamInfoList.get(0).getParamName()));
                buildCondition(ifElement, entityInfo, columnInfo, conditionInfo);
            } else {
                buildCondition(trimElement, entityInfo, columnInfo, conditionInfo);
            }
        });

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
            trimElement.addText(String.format(" and %s = %s", logicDeleteColumnInfo.getDbColumnName(), logicDelete.show()));
        }
    }

    public void buildCondition(Element parentElement, EntityInfo entityInfo, ColumnInfo columnInfo, ConditionInfo conditionInfo) {
        List<MethodParamInfo> methodParamInfoList = conditionInfo.getMethodParamInfoList();
        String op = conditionInfo.getOp();
        parentElement.addText(String.format(" %s %s %s ", conditionInfo.getLinkOp(), conditionInfo.getDbColumnName(), op));
        if ("in".equals(op)) {
            Element foreachElement = parentElement.addElement("foreach");
            foreachElement.addAttribute("item", "item");
            foreachElement.addAttribute("index", "index");
            foreachElement.addAttribute("collection", methodParamInfoList.get(0).getParamName());
            foreachElement.addAttribute("open", "(");
            foreachElement.addAttribute("separator", ",");
            foreachElement.addAttribute("close", ")");
            foreachElement.addText("#{item}");
        } else if ("between".equals(op)) {
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

}
