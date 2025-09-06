package com.lc.mybatisx.template.select;

import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.ResultMapInfo;
import com.lc.mybatisx.template.WhereTemplateHandler;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();
    private SelectSqlTemplateHandler selectSqlTemplateHandler = new SelectSqlTemplateHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildSelectXNode(mapperInfo, methodInfo);
    }

    private String buildSelectXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());
        selectElement.addAttribute("resultMap", methodInfo.getResultMapId());

        Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
        ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(methodReturnType);
        EntityInfo entityInfo = resultMapInfo.getEntityInfo();

        PlainSelect plainSelect = selectSqlTemplateHandler.buildSelectSql(entityInfo);
        selectElement.addText(plainSelect.toString());

        whereTemplateHandler.execute(selectElement, entityInfo, methodInfo);
        return document.asXML();
    }
}
