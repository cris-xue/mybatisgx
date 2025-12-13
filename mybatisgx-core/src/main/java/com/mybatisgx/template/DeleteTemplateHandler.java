package com.mybatisgx.template;

import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.annotation.LogicDeleteId;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除模板逻辑
 * @author 薛承城
 * @date 2025/12/13 17:25
 */
public class DeleteTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildDeleteXNode(mapperInfo, methodInfo);
    }

    private String buildDeleteXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");

        EntityInfo entityInfo = mapperInfo.getEntityInfo();
        Element deleteElement = this.getDeleteElement(entityInfo, methodInfo);
        mapperElement.add(deleteElement);

        Element whereElement = whereTemplateHandler.execute(mapperInfo.getEntityInfo(), methodInfo);
        deleteElement.add(whereElement);
        return document.asXML();
    }

    private Element getDeleteElement(EntityInfo entityInfo, MethodInfo methodInfo) {
        if (entityInfo.getLogicDeleteColumnInfo() == null) {
            return delete(entityInfo, methodInfo);
        } else {
            return logicDelete(entityInfo, methodInfo);
        }
    }

    private Element delete(EntityInfo entityInfo, MethodInfo methodInfo) {
        Element deleteElement = DocumentHelper.createElement("delete");
        deleteElement.addAttribute("id", methodInfo.getMethodName());
        deleteElement.addText(String.format("delete from %s", entityInfo.getTableName()));
        return deleteElement;
    }

    private Element logicDelete(EntityInfo entityInfo, MethodInfo methodInfo) {
        Element updateElement = DocumentHelper.createElement("update");
        updateElement.addAttribute("id", methodInfo.getMethodName());
        updateElement.addText(String.format("update %s", entityInfo.getTableName()));
        Element setElement = updateElement.addElement("set");

        List<String> expressionList = this.getExpressionList(entityInfo);
        setElement.addText(StringUtils.join(expressionList, ", "));
        return updateElement;
    }

    private List<String> getExpressionList(EntityInfo entityInfo) {
        ColumnInfo logicDeleteColumnInfo = entityInfo.getLogicDeleteColumnInfo();
        ColumnInfo logicDeleteIdColumnInfo = entityInfo.getLogicDeleteIdColumnInfo();
        LogicDelete logicDelete = logicDeleteColumnInfo.getLogicDelete();
        LogicDeleteId logicDeleteId = logicDeleteIdColumnInfo.getLogicDeleteId();

        List<String> expressionList = new ArrayList<>();
        String logicDeleteColumnValueExpression = String.format("%s = '%s'", logicDeleteColumnInfo.getDbColumnName(), logicDelete.hide());
        expressionList.add(logicDeleteColumnValueExpression);
        if (logicDeleteId != null) {
            String logicDeleteIdColumnColumnValueExpression = String.format("%s = #{%s}", logicDeleteIdColumnInfo.getDbColumnName(), logicDeleteIdColumnInfo.getJavaColumnName());
            expressionList.add(logicDeleteIdColumnColumnValueExpression);
        }
        return expressionList;
    }
}
