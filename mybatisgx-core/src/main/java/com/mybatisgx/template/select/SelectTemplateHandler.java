package com.mybatisgx.template.select;

import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.ColumnEntityRelation;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.template.MgxqlWhereTemplateHandler;
import com.mybatisgx.template.XmlCompiler;
import net.sf.jsqlparser.statement.select.PlainSelect;
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
    private MgxqlSelectColumnTemplateHandler mgxqlSelectColumnTemplateHandler = new MgxqlSelectColumnTemplateHandler();
    private SelectCountSqlTemplateHandler selectCountSqlTemplateHandler = new SelectCountSqlTemplateHandler();
    private MgxqlWhereTemplateHandler mgxqlWhereTemplateHandler = new MgxqlWhereTemplateHandler();
    private HavingTemplateHandler havingTemplateHandler = new HavingTemplateHandler();
    private MgxqlOrderByTemplateHandler mgxqlOrderByTemplateHandler = new MgxqlOrderByTemplateHandler();
    private MgxqlGroupByTemplateHandler mgxqlGroupByTemplateHandler = new MgxqlGroupByTemplateHandler();

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

        MgxqlStatement mgxqlStatement = methodInfo.getMgxqlStatement();
        Element whereElement = null;
        if (mgxqlStatement != null) {
            WhereClause whereClause = mgxqlStatement.getWhereClause();
            if (whereClause != null) {
                whereElement = mgxqlWhereTemplateHandler.execute(mapperInfo.getEntityInfo(), methodInfo, whereClause.getRootExpression());
                selectXmlItemList.add(whereElement);
            }
        }

        // GROUP BY 子句渲染
        if (mgxqlStatement instanceof SelectStatement) {
            GroupByClause groupByClause = ((SelectStatement) mgxqlStatement).getGroupByClause();
            if (groupByClause != null) {
                String groupBySql = mgxqlGroupByTemplateHandler.execute(groupByClause);
                selectXmlItemList.add(groupBySql);
            }
        }

        // HAVING 子句渲染
        if (mgxqlStatement instanceof SelectStatement) {
            HavingExpression havingExpression = ((SelectStatement) mgxqlStatement).getHavingExpression();
            if (havingExpression != null) {
                String havingSql = havingTemplateHandler.execute(havingExpression);
                if (!havingSql.isEmpty()) {
                    selectXmlItemList.add(havingSql);
                }
            }
        }

        // ORDER BY 子句渲染（MGXQL）
        if (mgxqlStatement instanceof SelectStatement) {
            OrderByClause orderByClause = ((SelectStatement) mgxqlStatement).getOrderByClause();
            if (orderByClause != null) {
                String orderBySql = mgxqlOrderByTemplateHandler.execute(orderByClause);
                selectXmlItemList.add(orderBySql);
            }
        }

        // LIMIT 子句渲染（MGXQL）
        if (mgxqlStatement instanceof SelectStatement) {
            LimitClause limitClause = ((SelectStatement) mgxqlStatement).getLimitClause();
            if (limitClause != null) {
                LimitTemplateHandler limitTemplateHandler = MybatisgxObjectFactory.get(LimitTemplateHandler.class);
                limitTemplateHandler.execute(selectXmlItemList, limitClause);
            }
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
        FromClause fromClause = selectStatement.getFromClause();
        if (fromClause != null) {
            // MGXQL 路径：按 FromClause 渲染 FROM/JOIN/ON，按 SelectItem 投影渲染列
            PlainSelect plainSelect = mgxqlSelectColumnTemplateHandler.buildSelectSql(selectStatement);
            selectXmlItemList.add(plainSelect.toString());
            if (mgxqlSelectColumnTemplateHandler.hasAggregate(selectStatement)) {
                selectElement.addAttribute("resultType", methodInfo.getMethodReturnInfo().getTypeName());
            } else {
                selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
            }
            return;
        }
        // 回退：无 FromClause 时按注解 EntityRelationTree 全展开
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
