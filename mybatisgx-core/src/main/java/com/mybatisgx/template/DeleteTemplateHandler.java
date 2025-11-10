package com.mybatisgx.template;

import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildDeleteXNode(mapperInfo, methodInfo);
    }

    private String buildDeleteXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        EntityInfo entityInfo = mapperInfo.getEntityInfo();
        ColumnInfo logicDeleteColumnInfo = entityInfo.getLogicDeleteColumnInfo();

        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");

        Element deleteElement;
        if (logicDeleteColumnInfo == null) {
            deleteElement = delete(mapperElement, entityInfo, methodInfo);
        } else {
            deleteElement = update(mapperElement, entityInfo, methodInfo, logicDeleteColumnInfo);
        }
        whereTemplateHandler.execute(deleteElement, mapperInfo.getEntityInfo(), methodInfo);
        return document.asXML();
    }

    private Element delete(Element mapperElement, EntityInfo entityInfo, MethodInfo methodInfo) {
        Element deleteElement = mapperElement.addElement("delete");
        deleteElement.addAttribute("id", methodInfo.getMethodName());
        deleteElement.addText(String.format("delete from %s", entityInfo.getTableName()));
        return deleteElement;
    }

    private Element update(Element mapperElement, EntityInfo entityInfo, MethodInfo methodInfo, ColumnInfo logicDeleteColumnInfo) {
        String dbColumnName = logicDeleteColumnInfo.getDbColumnName();
        LogicDelete logicDelete = logicDeleteColumnInfo.getLogicDelete();

        Element updateElement = mapperElement.addElement("update");
        updateElement.addAttribute("id", methodInfo.getMethodName());
        updateElement.addText(String.format("update %s", entityInfo.getTableName()));
        Element setElement = updateElement.addElement("set");
        String javaColumn = String.format("%s = '%s'", dbColumnName, logicDelete.hide());
        setElement.addText(javaColumn);
        return updateElement;
    }

}
