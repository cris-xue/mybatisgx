package com.mybatisgx.template;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(InsertTemplateHandler.class);

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return simpleTemplateHandle(mapperInfo, methodInfo);
    }

    private String simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildInsertSelectiveXNode(mapperInfo, methodInfo);
    }

    private String buildInsertSelectiveXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element insertElement = mapperElement.addElement("insert");
        insertElement.addAttribute("id", methodInfo.getMethodName());
        String keyProperty = this.getKeyProperty(methodInfo);
        insertElement.addAttribute("keyProperty", keyProperty);
        insertElement.addAttribute("useGeneratedKeys", "true");
        insertElement.addText(String.format("insert into %s", mapperInfo.getEntityInfo().getTableName()));

        Element dbTrimElement = insertElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "(");
        dbTrimElement.addAttribute("suffix", ")");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        this.setColumn(methodInfo, dbTrimElement);

        Element javaTrimElement = insertElement.addElement("trim");
        javaTrimElement.addAttribute("prefix", "values (");
        javaTrimElement.addAttribute("suffix", ")");
        javaTrimElement.addAttribute("suffixOverrides", ",");

        this.setValue(methodInfo, javaTrimElement);
        return document.asXML();
    }

    private String getKeyProperty(MethodInfo methodInfo) {
        if (methodInfo.getBatch()) {
            List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
            List<String> keyPropertyList = new ArrayList();
            for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                if (methodParamInfo.getBatchData()) {
                    EntityInfo entityInfo = EntityInfoContextHolder.get(methodParamInfo.getType());
                    IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
                    List<ColumnInfo> idColumnComposites = idColumnInfo.getComposites();
                    if (ObjectUtils.isEmpty(idColumnComposites)) {
                        String keyProperty = String.format("%s.%s", methodParamInfo.getBatchItemName(), idColumnInfo.getJavaColumnName());
                        keyPropertyList.add(keyProperty);
                    } else {
                        for (ColumnInfo idColumnComposite : idColumnComposites) {
                            String keyProperty = String.format("%s.%s.%s", methodParamInfo.getBatchItemName(), idColumnInfo.getJavaColumnName(), idColumnComposite.getJavaColumnName());
                            keyPropertyList.add(keyProperty);
                        }
                    }
                }
            }
            return StringUtils.join(keyPropertyList, ",");
        } else {
            List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
            List<String> keyPropertyList = new ArrayList();
            for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                EntityInfo entityInfo = EntityInfoContextHolder.get(methodParamInfo.getType());
                IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
                List<ColumnInfo> idColumnComposites = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(idColumnComposites)) {
                    String keyProperty = String.format("%s", idColumnInfo.getJavaColumnName());
                    keyPropertyList.add(keyProperty);
                } else {
                    for (ColumnInfo idColumnComposite : idColumnComposites) {
                        String javaColumnName = String.format("%s.%s", idColumnInfo.getJavaColumnName(), idColumnComposite.getJavaColumnName());
                        String keyProperty = String.format("%s", javaColumnName);
                        keyPropertyList.add(keyProperty);
                    }
                }
            }
            return StringUtils.join(keyPropertyList, ",");
        }
    }

    private void setColumn(MethodInfo methodInfo, Element dbTrimElement) {
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo.getType());
        for (ColumnInfo tableColumnInfo : tableColumnInfoList) {
            if (TypeUtils.typeEquals(tableColumnInfo, IdColumnInfo.class)) {
                IdColumnInfo idColumnInfo = (IdColumnInfo) tableColumnInfo;
                List<ColumnInfo> columnInfoList = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(columnInfoList)) {
                    this.setColumn(methodInfo, tableColumnInfo, dbTrimElement);
                } else {
                    for (ColumnInfo columnInfo : columnInfoList) {
                        String javaColumnName = String.format("%s.%s", idColumnInfo.getJavaColumnName(), columnInfo.getJavaColumnName());
                        ColumnInfo composite = new ColumnInfo.Builder().columnInfo(columnInfo).javaColumnName(javaColumnName).build();
                        this.setColumn(methodInfo, composite, dbTrimElement);
                    }
                }
            }
            if (TypeUtils.typeEquals(tableColumnInfo, ColumnInfo.class)) {
                this.setColumn(methodInfo, tableColumnInfo, dbTrimElement);
            }
            if (TypeUtils.typeEquals(tableColumnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) tableColumnInfo;
                RelationType relationType = relationColumnInfo.getRelationType();
                if (relationType == RelationType.MANY_TO_MANY) {
                    continue;
                }
                ColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
                if (mappedByRelationColumnInfo == null) {
                    List<ForeignKeyColumnInfo> inverseForeignKeyInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyInfo : inverseForeignKeyInfoList) {
                        ColumnInfo columnInfo = inverseForeignKeyInfo.getColumnInfo();
                        this.setColumn(methodInfo, columnInfo, dbTrimElement);
                    }
                } else {
                    this.setColumn(methodInfo, tableColumnInfo, dbTrimElement);
                }
            }
        }
    }

    private void setColumn(MethodInfo methodInfo, ColumnInfo columnInfo, Element dbTrimElement) {
        String javaColumnName = columnInfo.getJavaColumnName();
        String dbColumnName = columnInfo.getDbColumnName();

        LogicDelete logicDelete = columnInfo.getLogicDelete();
        if (logicDelete != null) {
            String javaColumn = String.format("%s,", dbColumnName);
            dbTrimElement.addText(javaColumn);
            return;
        }

        if (methodInfo.getDynamic()) {
            Element javaTrimIfElement = dbTrimElement.addElement("if");
            javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
            String dbColumn = String.format("%s,", dbColumnName);
            javaTrimIfElement.addText(dbColumn);
        } else {
            String dbColumn = String.format("%s,", dbColumnName);
            dbTrimElement.addText(dbColumn);
        }
    }

    private void setValue(MethodInfo methodInfo, Element javaTrimElement) {
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo.getType());
        if (ObjectUtils.isEmpty(tableColumnInfoList)) {
            throw new RuntimeException("实体表字段不存在");
        }
        for (ColumnInfo tableColumnInfo : tableColumnInfoList) {
            if (TypeUtils.typeEquals(tableColumnInfo, IdColumnInfo.class)) {
                IdColumnInfo idColumnInfo = (IdColumnInfo) tableColumnInfo;
                List<ColumnInfo> columnInfoList = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(columnInfoList)) {
                    this.handleMethodParam(methodInfo, entityParamInfo, idColumnInfo, javaTrimElement);
                } else {
                    for (ColumnInfo columnInfo : columnInfoList) {
                        String javaColumnName = String.format("%s.%s", idColumnInfo.getJavaColumnName(), columnInfo.getJavaColumnName());
                        ColumnInfo composite = new ColumnInfo.Builder().columnInfo(columnInfo).javaColumnName(javaColumnName).build();
                        this.handleMethodParam(methodInfo, entityParamInfo, composite, javaTrimElement);
                    }
                }
            }
            if (TypeUtils.typeEquals(tableColumnInfo, ColumnInfo.class)) {
                this.handleMethodParam(methodInfo, entityParamInfo, tableColumnInfo, javaTrimElement);
            }
            if (TypeUtils.typeEquals(tableColumnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) tableColumnInfo;
                RelationType relationType = relationColumnInfo.getRelationType();
                if (relationType == RelationType.MANY_TO_MANY) {
                    continue;
                }
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                    if (TypeUtils.typeEquals(referencedColumnInfo, IdColumnInfo.class)) {
                        IdColumnInfo idColumnInfo = (IdColumnInfo) referencedColumnInfo;
                        List<ColumnInfo> idColumnInfoComposites = idColumnInfo.getComposites();
                        if (ObjectUtils.isEmpty(idColumnInfoComposites)) {
                            String javaColumnName = String.format("%s.%s", relationColumnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnName());
                            ColumnInfo composite = new ColumnInfo.Builder().columnInfo(foreignKeyColumnInfo).javaColumnName(javaColumnName).build();
                            this.handleMethodParam(methodInfo, entityParamInfo, composite, javaTrimElement);
                        } else {
                            for (ColumnInfo idColumnInfoComposite : idColumnInfoComposites) {
                                String javaColumnName = String.format("%s.%s.%s", relationColumnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnName(), idColumnInfoComposite.getJavaColumnName());
                                ColumnInfo composite = new ColumnInfo.Builder().columnInfo(foreignKeyColumnInfo).javaColumnName(javaColumnName).build();
                                this.handleMethodParam(methodInfo, entityParamInfo, composite, javaTrimElement);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleMethodParam(MethodInfo methodInfo, MethodParamInfo methodParamInfo, ColumnInfo columnInfo, Element javaTrimElement) {
        LogicDelete logicDelete = columnInfo.getLogicDelete();
        if (logicDelete != null) {
            String javaColumn = String.format("'%s'%s,", logicDelete.show(), buildTypeHandler(columnInfo));
            javaTrimElement.addText(javaColumn);
            return;
        }

        Boolean isBatch = methodInfo.getBatch();
        if (isBatch) {
            // int insertBatch(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);
            String batchItemName = methodParamInfo.getBatchItemName();
            String javaColumnName = columnInfo.getJavaColumnName();
            List<String> paramValuePathList = Arrays.asList(batchItemName, javaColumnName);
            this.handleEntityParam(methodInfo, columnInfo, javaTrimElement, paramValuePathList);
        } else {
            String javaColumnName = columnInfo.getJavaColumnName();
            List<String> paramValuePathList = Arrays.asList(javaColumnName);
            this.handleEntityParam(methodInfo, columnInfo, javaTrimElement, paramValuePathList);
        }
    }

    private void handleEntityParam(MethodInfo methodInfo, ColumnInfo tableColumnInfo, Element javaTrimElement, List<String> paramValuePathList) {
        String paramValuePath = StringUtils.join(paramValuePathList, ".");
        if (methodInfo.getDynamic()) {
            Element javaTrimIfElement = javaTrimElement.addElement("if");
            javaTrimIfElement.addAttribute("test", buildTestNotNull(paramValuePath));
            String javaColumn = String.format("#{%s%s},", paramValuePath, buildTypeHandler(tableColumnInfo));
            javaTrimIfElement.addText(javaColumn);
        } else {
            String javaColumn = String.format("#{%s%s},", paramValuePath, buildTypeHandler(tableColumnInfo));
            javaTrimElement.addText(javaColumn);
        }
    }

    private String buildTypeHandler(ColumnInfo tableColumnInfo) {
        String typeHandler = tableColumnInfo.getTypeHandler();
        if (StringUtils.isNotBlank(typeHandler)) {
            return String.format(", typeHandler=%s", typeHandler);
        }
        return "";
    }

    private String buildTestNotNull(String javaColumnName) {
        return String.format("%s != null", javaColumnName);
    }

    private List<ColumnInfo> getTableColumnInfoList(Class<?> entityClass) {
        EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
        return entityInfo.getTableColumnInfoList();
    }
}