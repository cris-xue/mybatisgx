package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.utils.XmlUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();

    public XNode execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildDeleteXNode(mapperInfo, methodInfo);
    }

    private XNode buildDeleteXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
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

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode(String.format("/mapper/%s", deleteElement.getName()));
        return xNode;
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
