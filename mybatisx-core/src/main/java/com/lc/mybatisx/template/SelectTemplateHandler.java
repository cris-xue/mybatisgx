package com.lc.mybatisx.template;

import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.ResultMapInfo;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildSelectXNode(mapperInfo, methodInfo);
    }

    private String buildSelectXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());
        selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
        // OselectElement.addText("select");

        /*Element dbTrimElement = selectElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "");
        dbTrimElement.addAttribute("suffix", "");
        dbTrimElement.addAttribute("suffixOverrides", ",");*/

        Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
        ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(methodReturnType);
        EntityInfo entityInfo = resultMapInfo.getEntityInfo();

        SelectSqlTemplateHandler selectSqlTemplateHandler = new SelectSqlTemplateHandler();
        PlainSelect plainSelect = selectSqlTemplateHandler.buildEntityMainSelect(entityInfo);

        selectElement.addText(plainSelect.toString());

        /*List<ColumnInfo> tableColumnInfoList = entityInfo.getTableColumnInfoList();
        for (ColumnInfo columnInfo : tableColumnInfoList) {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            ColumnRelationInfo columnRelationInfo = columnInfo.getColumnInfoAnnotationInfo();
            if (columnRelationInfo == null) {
                dbTrimElement.addText(String.format("%s, ", columnInfo.getDbColumnName()));
            } else {
                ManyToMany manyToMany = columnRelationInfo.getManyToMany();
                if (manyToMany == null) {
                    // 只有一对一、一对多、多对一的时候关联字段才需要作为表字段。多对多存在中间表，关联字段在中间中表，不需要作为实体表字段
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        dbTrimElement.addText(String.format("%s, ", inverseForeignKeyColumnInfo.getName()));
                    }
                }
            }
        }*/
        // selectElement.addText(String.format("from %s", entityInfo.getTableName()));
        whereTemplateHandler.execute(selectElement, entityInfo, methodInfo);
        return document.asXML();
    }
}
