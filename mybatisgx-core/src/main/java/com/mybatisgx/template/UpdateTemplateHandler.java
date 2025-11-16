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

import java.util.Collections;
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
            for (ColumnInfo columnInfo : tableColumnInfoList) {
                String javaColumnName = columnInfo.getJavaColumnName();
                String dbColumnName = columnInfo.getDbColumnName();
                String typeHandler = columnInfo.getTypeHandler();
                Lock lock = columnInfo.getLock();
                LogicDelete logicDelete = columnInfo.getLogicDelete();

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

                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, null);
                        String testExpression = this.getTestExpression(paramValuePathItemList);
                        String valueExpression = this.getValueExpression(paramValuePathItemList, typeHandler);
                        Element trimOrIfElement = this.buildTrimOrIfElement(methodInfo.getDynamic(), setTrimElement, testExpression);
                        trimOrIfElement.addText(String.format("%s = %s", dbColumnName, valueExpression));
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, columnInfo, columnInfoComposite);
                            String testExpression = this.getTestExpression(paramValuePathItemList);
                            String valueExpression = this.getValueExpression(paramValuePathItemList, typeHandler);
                            Element trimOrIfElement = this.buildTrimOrIfElement(methodInfo.getDynamic(), setTrimElement, testExpression);
                            trimOrIfElement.addText(String.format("%s = %s", dbColumnName, valueExpression));
                        }
                    }
                }
                if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                    RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                    if (relationColumnInfo.getRelationType() == RelationType.MANY_TO_MANY) {
                        continue;
                    }
                    ColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
                    if (mappedByRelationColumnInfo == null) {
                        for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                            ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                            ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                            List<ColumnInfo> referencedColumnInfoComposites = referencedColumnInfo.getComposites();
                            if (ObjectUtils.isEmpty(referencedColumnInfoComposites)) {
                                List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, referencedColumnInfo, null, relationColumnInfo);
                                String testExpression = this.getTestExpression(paramValuePathItemList);
                                String valueExpression = this.getValueExpression(paramValuePathItemList, typeHandler);
                                Element trimOrIfElement = this.buildTrimOrIfElement(methodInfo.getDynamic(), setTrimElement, testExpression);
                                trimOrIfElement.addText(String.format("%s = %s", foreignKeyColumnInfo.getDbColumnName(), valueExpression));
                            } else {
                                for (ColumnInfo referencedColumnInfoComposite : referencedColumnInfoComposites) {
                                    List<String> paramValuePathItemList = this.getParamValuePathItemList(entityParamInfo, referencedColumnInfo, referencedColumnInfoComposite, relationColumnInfo);
                                    String testExpression = this.getTestExpression(paramValuePathItemList);
                                    String valueExpression = this.getValueExpression(paramValuePathItemList, typeHandler);
                                    Element trimOrIfElement = this.buildTrimOrIfElement(methodInfo.getDynamic(), setTrimElement, testExpression);
                                    trimOrIfElement.addText(String.format("%s = %s", foreignKeyColumnInfo.getDbColumnName(), valueExpression));
                                }
                            }
                        }
                    }
                }
            }
        }

        private void setWhere(Element updateElement, EntityInfo entityInfo, MethodInfo methodInfo) {
            whereTemplateHandler.execute(updateElement, entityInfo, methodInfo);
        }

        private Element buildTrimOrIfElement(Boolean dynamic, Element parentElement, String testExpression) {
            if (dynamic) {
                Element ifElement = parentElement.addElement("if");
                ifElement.addAttribute("test", testExpression);
                return ifElement;
            }
            return parentElement;
        }

        private String buildTypeHandler(String typeHandler) {
            if (StringUtils.isNotBlank(typeHandler)) {
                return String.format(", typeHandler=%s", typeHandler);
            }
            return "";
        }

        protected String getTestExpression(List<String> pathItemList) {
            String[] paths = pathItemList.toArray(new String[pathItemList.size()]);
            if (paths.length == 1) {
                return String.format("%1$s != null", paths);
            }
            if (paths.length == 2) {
                return String.format("%1$s != null and %1$s.%2$s != null", paths);
            }
            if (paths.length == 3) {
                return String.format("%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null", paths);
            }
            return "";
        }

        protected String getValueExpression(List<String> pathItemList, String typeHandler) {
            String valuePath = StringUtils.join(pathItemList, ".");
            return String.format("#{%s%s},", valuePath, buildTypeHandler(typeHandler));
        }

        private List<ColumnInfo> getTableColumnInfoList(MethodParamInfo methodParamInfo) {
            Class<?> entityClass = methodParamInfo.getType();
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
            if (entityInfo != null) {
                return entityInfo.getTableColumnInfoList();
            }
            return methodParamInfo.getColumnInfoList();
        }

        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.getParamValuePathItemList(methodParamInfo, columnInfo, columnInfoComposite, null);
        }

        abstract protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite, ColumnInfo relationColumnInfo);
    }

    private static class SimpleUpdateHandler extends AbstractUpdateHandler {

        @Override
        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite, ColumnInfo relationColumnInfo) {
            return Collections.emptyList();
        }
    }

    private static class BatchUpdateHandler extends AbstractUpdateHandler {

        @Override
        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite, ColumnInfo relationColumnInfo) {
            return Collections.emptyList();
        }
    }
}
