package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UpdateTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildUpdateXNode(mapperInfo, methodInfo);
    }

    private String buildUpdateXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        EntityInfo entityInfo = mapperInfo.getEntityInfo();

        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element updateElement = mapperElement.addElement("update");
        updateElement.addAttribute("id", methodInfo.getMethodName());
        updateElement.addText(String.format("update %s", entityInfo.getTableName()));

        Element setElement = updateElement.addElement("set");
        Element setTrimElement = setElement.addElement("trim");
        setTrimElement.addAttribute("suffixOverrides", ",");

        this.setValue(entityInfo, methodInfo, setTrimElement);
        this.setWhere(updateElement, entityInfo, methodInfo);
        return document.asXML();
    }

    /**
     * @param entityInfo
     * @param methodInfo
     * @param setTrimElement
     */
    private void setValue(EntityInfo entityInfo, MethodInfo methodInfo, Element setTrimElement) {
        List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
        for (MethodParamInfo methodParamInfo : methodParamInfoList) {
            if (methodParamInfo.getBasicType()) {
                continue;
            }
            this.setValue(methodInfo, methodParamInfo, setTrimElement);
        }
    }

    private void setValue(MethodInfo methodInfo, MethodParamInfo methodParamInfo, Element setTrimElement) {
        List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(methodParamInfo);
        if (ObjectUtils.isEmpty(tableColumnInfoList)) {
            throw new RuntimeException("实体表字段不存在");
        }
        for (ColumnInfo tableColumnInfo : tableColumnInfoList) {
            String javaColumnName = tableColumnInfo.getJavaColumnName();
            String dbColumnName = tableColumnInfo.getDbColumnName();
            String typeHandler = tableColumnInfo.getTypeHandler();
            Lock lock = tableColumnInfo.getLock();
            LogicDelete logicDelete = tableColumnInfo.getLogicDelete();

            if (lock != null) {
                String javaColumn = String.format("%s = #{%s} + %s, ", dbColumnName, javaColumnName, lock.increment());
                setTrimElement.addText(javaColumn);
                continue;
            }

            if (logicDelete != null) {
                String javaColumn = String.format("%s = '%s', ", dbColumnName, logicDelete.show());
                setTrimElement.addText(javaColumn);
                continue;
            }

            if (!(tableColumnInfo instanceof RelationColumnInfo)) {
                if (methodInfo.getDynamic()) {
                    Element setTrimIfElement = setTrimElement.addElement("if");
                    setTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                    String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                    setTrimIfElement.addText(String.format("%s = %s", dbColumnName, javaColumn));
                } else {
                    String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                    setTrimElement.addText(String.format("%s = %s", dbColumnName, javaColumn));
                }
            } else {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) tableColumnInfo;
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    String referencedColumnName = inverseForeignKeyColumnInfo.getReferencedColumnName();
                    String nestedJavaColumnName = javaColumnName + "." + referencedColumnName;
                    if (methodInfo.getDynamic()) {
                        Element setTrimIfElement = setTrimElement.addElement("if");
                        setTrimIfElement.addAttribute("test", buildTestNotNull(nestedJavaColumnName));
                        String nestedJavaColumn = String.format("#{%s%s},", nestedJavaColumnName, buildTypeHandler(typeHandler));
                        setTrimIfElement.addText(String.format("%s = %s", dbColumnName, nestedJavaColumn));
                    } else {
                        String nestedJavaColumn = String.format("#{%s%s},", nestedJavaColumnName, buildTypeHandler(typeHandler));
                        setTrimElement.addText(String.format("%s = %s", dbColumnName, nestedJavaColumn));
                    }
                }
            }
        }
    }

    private void setWhere(Element updateElement, EntityInfo entityInfo, MethodInfo methodInfo) {
        whereTemplateHandler.execute(updateElement, entityInfo, methodInfo);
    }

    private String buildTypeHandler(String typeHandler) {
        if (StringUtils.isNotBlank(typeHandler)) {
            return String.format(", typeHandler=%s", typeHandler);
        }
        return "";
    }

    private String buildTestNotNull(String javaColumnName) {
        return String.format("%s != null", javaColumnName);
    }

    private List<ColumnInfo> getTableColumnInfoList(MethodParamInfo methodParamInfo) {
        Class<?> entityClass = methodParamInfo.getType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
        if (entityInfo != null) {
            return entityInfo.getTableColumnInfoList();
        }
        return methodParamInfo.getColumnInfoList();
    }
}
