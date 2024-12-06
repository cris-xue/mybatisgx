package com.lc.mybatisx.template;

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
            if (methodInfo.getDynamic()) {
                List<MethodParamInfo> methodParamInfoList = conditionInfo.getMethodParamInfoList();
                Element ifElement = trimElement.addElement("if");
                ifElement.addAttribute("test", String.format("%s != null", methodParamInfoList.get(0).getParamName()));
                buildCondition(ifElement, columnInfo, conditionInfo);
            } else {
                buildCondition(trimElement, columnInfo, conditionInfo);
            }
        });

        // 逻辑删除
        entityInfo.getColumnInfoList().forEach(columnInfo -> {
            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (logicDelete != null) {
                trimElement.addText(String.format(" and %s = %s", columnInfo.getDbColumnName(), logicDelete.show()));
            }
        });
    }

    public void buildCondition(Element parentElement, ColumnInfo columnInfo, ConditionInfo conditionInfo) {
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

}
