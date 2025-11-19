package com.mybatisgx.template.select;

import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.SelectType;
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
    private GeneralSelectSqlTemplateHandler generalSelectSqlTemplateHandler = new GeneralSelectSqlTemplateHandler();
    private AggregateSelectSqlTemplateHandler aggregateSelectSqlTemplateHandler = new AggregateSelectSqlTemplateHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildSelectXNode(mapperInfo, methodInfo);
    }

    private String buildSelectXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());
        selectElement.addAttribute("resultMap", methodInfo.getResultMapId());

        if (methodInfo.getSelectType() == SelectType.GENERAL) {
            Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
            EntityInfo entityInfo = EntityInfoContextHolder.get(methodReturnType);
            PlainSelect plainSelect = generalSelectSqlTemplateHandler.buildSelectSql(entityInfo);
            selectElement.addText(plainSelect.toString());
            whereTemplateHandler.execute(selectElement, entityInfo, methodInfo);
        }
        if (methodInfo.getSelectType() == SelectType.AGGREGATE) {
            EntityInfo entityInfo = mapperInfo.getEntityInfo();
            PlainSelect plainSelect = aggregateSelectSqlTemplateHandler.buildSelectSql();
            selectElement.addText(plainSelect.toString());
            whereTemplateHandler.execute(selectElement, entityInfo, methodInfo);
        }
        return document.asXML();
    }
}
