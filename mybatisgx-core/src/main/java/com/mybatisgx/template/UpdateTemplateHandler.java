package com.mybatisgx.template;

import com.mybatisgx.annotation.Lock;
import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
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

    private SimpleUpdateHandler simpleUpdateHandler = new SimpleUpdateHandler();
    private BatchUpdateHandler batchUpdateHandler = new BatchUpdateHandler();

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

        AbstractUpdateHandler abstractUpdateHandler = this.getAbstractUpdateHandler(methodInfo);
        abstractUpdateHandler.setValue(methodInfo, setTrimElement);
        abstractUpdateHandler.setWhere(updateElement, entityInfo, methodInfo);
        return document.asXML();
    }

    private AbstractUpdateHandler getAbstractUpdateHandler(MethodInfo methodInfo) {
        if (methodInfo.getBatch()) {
            return batchUpdateHandler;
        } else {
            return simpleUpdateHandler;
        }
    }

    private static abstract class AbstractUpdateHandler {

        protected WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();

        public void setValue(MethodInfo methodInfo, Element setTrimElement) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo);
            if (ObjectUtils.isEmpty(tableColumnInfoList)) {
                throw new RuntimeException("实体表字段不存在" + entityParamInfo.getType());
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

                if (TypeUtils.typeEquals(tableColumnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    if (methodInfo.getDynamic()) {
                        Element setTrimIfElement = setTrimElement.addElement("if");
                        setTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                        String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                        setTrimIfElement.addText(String.format("%s = %s", dbColumnName, javaColumn));
                    } else {
                        String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                        setTrimElement.addText(String.format("%s = %s", dbColumnName, javaColumn));
                    }
                }
                if (TypeUtils.typeEquals(tableColumnInfo, RelationColumnInfo.class)) {
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

    private static class SimpleUpdateHandler extends AbstractUpdateHandler {

    }

    private static class BatchUpdateHandler extends AbstractUpdateHandler {

    }
}
