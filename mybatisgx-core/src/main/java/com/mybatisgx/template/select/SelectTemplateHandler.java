package com.mybatisgx.template.select;

import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.model.*;
import com.mybatisgx.template.WhereTemplateHandler;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 单表查询模板处理
 * @author ccxuef
 * @date 2025/9/6 14:05
 */
public class SelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    private SelectColumnSqlTemplateHandler selectColumnSqlTemplateHandler = new SelectColumnSqlTemplateHandler();
    private SelectCountSqlTemplateHandler selectCountSqlTemplateHandler = new SelectCountSqlTemplateHandler();
    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();
    private OrderByTemplateHandler orderByTemplateHandler = new OrderByTemplateHandler();
    private LimitTemplateHandler limitTemplateHandler = new LimitTemplateHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildSelectXNode(mapperInfo, methodInfo);
    }

    private String buildSelectXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());

        EntityInfo entityInfo = null;
        SelectItemInfo selectItemInfo = methodInfo.getSelectItemInfo();
        if (selectItemInfo.getSelectItemType() == SelectItemType.COLUMN) {
            selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
            Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
            entityInfo = EntityInfoContextHolder.get(methodReturnType);
            PlainSelect plainSelect = selectColumnSqlTemplateHandler.buildSelectSql(entityInfo);
            selectElement.addText(plainSelect.toString());
        }
        if (selectItemInfo.getSelectItemType() == SelectItemType.COUNT) {
            selectElement.addAttribute("resultType", methodInfo.getMethodReturnInfo().getTypeName());
            entityInfo = mapperInfo.getEntityInfo();
            PlainSelect plainSelect = selectCountSqlTemplateHandler.buildSelectSql(entityInfo);
            selectElement.addText(plainSelect.toString());
        }

        whereTemplateHandler.execute(selectElement, entityInfo, methodInfo);

        List<SelectOrderByInfo> selectOrderByInfoList = methodInfo.getSelectOrderByInfoList();
        if (ObjectUtils.isNotEmpty(selectOrderByInfoList)) {
            String orderBySql = orderByTemplateHandler.execute(selectOrderByInfoList);
            selectElement.addText(orderBySql);
        }

        SelectPageInfo selectPageInfo = methodInfo.getSelectPageInfo();
        if (ObjectUtils.isNotEmpty(selectPageInfo)) {
            String limitSql = limitTemplateHandler.execute(selectPageInfo);
            selectElement.addText(limitSql);
        }

        return document.asXML();
    }
}
