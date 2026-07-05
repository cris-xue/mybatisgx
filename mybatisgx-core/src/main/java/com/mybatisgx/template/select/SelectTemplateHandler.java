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

    private MgxqlSelectColumnTemplateHandler mgxqlSelectColumnTemplateHandler = new MgxqlSelectColumnTemplateHandler();
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

        SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();

        // 设置返回结果集或者返回类型
        if (mgxqlSelectColumnTemplateHandler.hasAggregate(selectStatement)) {
            selectElement.addAttribute("resultType", methodInfo.getMethodReturnInfo().getTypeName());
        } else {
            selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
        }

        // 构建上下文参数
        ColumnEntityRelation fullTree = selectStatement.getMgxqlEntityRelationTree();
        AliasContext aliasContext = AliasContext.build(selectStatement, fullTree);

        // 构建 from join
        FromClause fromClause = selectStatement.getFromClause();
        if (fromClause != null) {
            // MGXQL 路径：按 FromClause 渲染 FROM/JOIN/ON，按 SelectItem 投影渲染列
            PlainSelect plainSelect = mgxqlSelectColumnTemplateHandler.buildSelectSql(selectStatement, aliasContext);
            selectXmlItemList.add(plainSelect.toString());
        }

        // 构建查询条件
        Element whereElement = null;
        if (selectStatement != null) {
            WhereClause whereClause = selectStatement.getWhereClause();
            if (whereClause != null) {
                whereElement = mgxqlWhereTemplateHandler.execute(mapperInfo.getEntityInfo(), methodInfo, whereClause.getRootExpression(), aliasContext);
                selectXmlItemList.add(whereElement);
            }
        }

        // GROUP BY 子句渲染
        if (selectStatement instanceof SelectStatement) {
            GroupByClause groupByClause = selectStatement.getGroupByClause();
            if (groupByClause != null) {
                String groupBySql = mgxqlGroupByTemplateHandler.execute(groupByClause, aliasContext);
                selectXmlItemList.add(groupBySql);
            }
        }

        // HAVING 子句渲染
        if (selectStatement instanceof SelectStatement) {
            HavingExpression havingExpression = selectStatement.getHavingExpression();
            if (havingExpression != null) {
                String havingSql = havingTemplateHandler.execute(havingExpression);
                if (!havingSql.isEmpty()) {
                    selectXmlItemList.add(havingSql);
                }
            }
        }

        // ORDER BY 子句渲染（MGXQL）
        if (selectStatement instanceof SelectStatement) {
            OrderByClause orderByClause = selectStatement.getOrderByClause();
            if (orderByClause != null) {
                String orderBySql = mgxqlOrderByTemplateHandler.execute(orderByClause, aliasContext);
                selectXmlItemList.add(orderBySql);
            }
        }

        // LIMIT 子句渲染（MGXQL）
        if (selectStatement instanceof SelectStatement) {
            LimitClause limitClause = selectStatement.getLimitClause();
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
}
