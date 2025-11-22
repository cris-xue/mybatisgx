package com.mybatisgx.template.select;

import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.model.*;
import com.mybatisgx.template.WhereTemplateHandler;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单表查询模板处理
 * @author ccxuef
 * @date 2025/9/6 14:05
 */
public class SelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();
    private SelectColumnSqlTemplateHandler selectColumnSqlTemplateHandler = new SelectColumnSqlTemplateHandler();
    private SelectCountSqlTemplateHandler selectCountSqlTemplateHandler = new SelectCountSqlTemplateHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildSelectXNode(mapperInfo, methodInfo);
    }

    private String buildSelectXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());

        SelectItemInfo selectItemInfo = methodInfo.getSelectItemInfo();
        if (selectItemInfo.getSelectItemType() == SelectItemType.COLUMN) {
            selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
            Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
            EntityInfo entityInfo = EntityInfoContextHolder.get(methodReturnType);
            PlainSelect plainSelect = selectColumnSqlTemplateHandler.buildSelectSql(entityInfo);
            selectElement.addText(plainSelect.toString());
            whereTemplateHandler.execute(selectElement, entityInfo, methodInfo);
        }
        if (selectItemInfo.getSelectItemType() == SelectItemType.COUNT) {
            selectElement.addAttribute("resultType", "long");
            EntityInfo entityInfo = mapperInfo.getEntityInfo();
            PlainSelect plainSelect = selectCountSqlTemplateHandler.buildSelectSql(entityInfo);
            selectElement.addText(plainSelect.toString());
            whereTemplateHandler.execute(selectElement, entityInfo, methodInfo);
        }
        return document.asXML();
    }
}
