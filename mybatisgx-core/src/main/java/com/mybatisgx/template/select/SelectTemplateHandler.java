package com.mybatisgx.template.select;

import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.dsl.mgxql.model.HavingExpression;
import com.mybatisgx.dsl.mgxql.model.SelectItem;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.dsl.mgxql.model.WhereClause;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.*;
import com.mybatisgx.template.WhereTemplateHandler;
import com.mybatisgx.template.MgxqlWhereTemplateHandler;
import com.mybatisgx.template.HavingTemplateHandler;
import com.mybatisgx.template.XmlCompiler;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 单表查询模板处理
 *
 * @author ccxuef
 * @date 2025/9/6 14:05
 */
public class SelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    private SelectColumnSqlTemplateHandler selectColumnSqlTemplateHandler = new SelectColumnSqlTemplateHandler();
    private SelectCountSqlTemplateHandler selectCountSqlTemplateHandler = new SelectCountSqlTemplateHandler();
    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();
    private MgxqlWhereTemplateHandler mgxqlWhereTemplateHandler = new MgxqlWhereTemplateHandler();
    private HavingTemplateHandler havingTemplateHandler = new HavingTemplateHandler();
    private OrderByTemplateHandler orderByTemplateHandler = new OrderByTemplateHandler();

    public SelectTemplateHandler(MybatisgxConfiguration configuration) {
    }

    public String execute(MethodInfo methodInfo) {
        return buildSelectXNode(methodInfo);
    }

    private String buildSelectXNode(MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());

        List<Object> selectXmlItemList = new ArrayList();
        MapperInfo mapperInfo = methodInfo.getMapperInfo();

        this.selectItem(methodInfo, selectElement, selectXmlItemList);

        /*SelectItemInfo selectItemInfo = methodInfo.getSelectItemInfo();
        if (selectItemInfo.getSelectItemType() == SelectItemType.COLUMN) {
            selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
            Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
            ColumnEntityRelation columnEntityRelation = mapperInfo.getEntityRelationTree(methodReturnType);
            PlainSelect plainSelect = selectColumnSqlTemplateHandler.buildSimpleSelectSql(columnEntityRelation);
            selectXmlItemList.add(plainSelect.toString());
        }
        if (selectItemInfo.getSelectItemType() == SelectItemType.COUNT) {
            selectElement.addAttribute("resultType", methodInfo.getMethodReturnInfo().getTypeName());
            PlainSelect plainSelect = selectCountSqlTemplateHandler.buildSelectSql(mapperInfo.getEntityInfo());
            selectXmlItemList.add(plainSelect.toString());
        }*/

        Element whereElement = null;
        if (methodInfo.getMgxqlStatement() != null) {
            WhereClause whereClause = methodInfo.getMgxqlStatement().getWhereClause();
            if (whereClause != null) {
                whereElement = mgxqlWhereTemplateHandler.execute(mapperInfo.getEntityInfo(), methodInfo, whereClause.getRootExpression());
                selectXmlItemList.add(whereElement);
            }
        }

        // HAVING 子句渲染
        if (methodInfo.getMgxqlStatement() instanceof SelectStatement) {
            HavingExpression havingExpression = ((SelectStatement) methodInfo.getMgxqlStatement()).getHavingExpression();
            if (havingExpression != null) {
                String havingSql = havingTemplateHandler.execute(havingExpression);
                if (!havingSql.isEmpty()) {
                    selectXmlItemList.add(havingSql);
                }
            }
        }

        List<SelectOrderByInfo> selectOrderByInfoList = methodInfo.getSelectOrderByInfoList();
        if (ObjectUtils.isNotEmpty(selectOrderByInfoList)) {
            String orderBySql = orderByTemplateHandler.execute(selectOrderByInfoList);
            selectXmlItemList.add(orderBySql);
        }

        MethodRowLimitInfo methodRowLimitInfo = methodInfo.getMethodRowLimitInfo();
        if (ObjectUtils.isNotEmpty(methodRowLimitInfo)) {
            LimitTemplateHandler limitTemplateHandler = MybatisgxObjectFactory.get(LimitTemplateHandler.class);
            limitTemplateHandler.execute(selectXmlItemList, methodRowLimitInfo);
        }

        for (Object selectSql : selectXmlItemList) {
            if (selectSql instanceof Element) {
                selectElement.add((Element) selectSql);
            }
            if (selectSql instanceof String) {
                selectElement.addText((String) selectSql);
            }
        }

        // 脱去where标签
        if (!methodInfo.getDynamic() && whereElement != null) {
            XmlCompiler.where(whereElement);
        }

        return document.asXML();
    }

    private void selectItem(MethodInfo methodInfo, Element selectElement, List<Object> selectXmlItemList) {
        MapperInfo mapperInfo = methodInfo.getMapperInfo();
        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
        for (SelectItem selectItem : selectStatement.getSelectItems()) {
            if (selectItem.getType() == SelectItemType.COLUMN_ALL) {
                selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
                Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
                ColumnEntityRelation columnEntityRelation = mapperInfo.getEntityRelationTree(methodReturnType);
                PlainSelect plainSelect = selectColumnSqlTemplateHandler.buildSimpleSelectSql(columnEntityRelation);
                selectXmlItemList.add(plainSelect.toString());
            }
            if (selectItem.getType() == SelectItemType.COLUMN) {
                selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
                Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
                ColumnEntityRelation columnEntityRelation = mapperInfo.getEntityRelationTree(methodReturnType);
                PlainSelect plainSelect = selectColumnSqlTemplateHandler.buildSimpleSelectSql(columnEntityRelation);
                selectXmlItemList.add(plainSelect.toString());
            }
            if (selectItem.getType() == SelectItemType.COUNT) {
                selectElement.addAttribute("resultType", methodInfo.getMethodReturnInfo().getTypeName());
                PlainSelect plainSelect = selectCountSqlTemplateHandler.buildSelectSql(mapperInfo.getEntityInfo());
                selectXmlItemList.add(plainSelect.toString());
            }
        }
    }
}
