package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
                        String keyProperty = String.format("%s", idColumnInfo.getJavaColumnName());
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
        List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
        for (MethodParamInfo methodParamInfo : methodParamInfoList) {
            if (methodParamInfo.getBatchSize()) {
                continue;
            }
            if (methodParamInfo.getBasicType()) {
                continue;
            }
            this.setColumn(methodInfo, methodParamInfo, dbTrimElement);
        }
    }

    private void setColumn(MethodInfo methodInfo, MethodParamInfo methodParamInfo, Element dbTrimElement) {
        List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(methodParamInfo.getType());
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
            } else if (TypeUtils.typeEquals(tableColumnInfo, ColumnInfo.class)) {
                this.setColumn(methodInfo, tableColumnInfo, dbTrimElement);
            } else if (TypeUtils.typeEquals(tableColumnInfo, RelationColumnInfo.class)) {
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
        Boolean isBatch = methodInfo.getBatch();
        List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
        for (MethodParamInfo methodParamInfo : methodParamInfoList) {
            if (isBatch && methodParamInfo.getBatchSize()) {
                continue;
            }
            Boolean isBasicType = methodParamInfo.getBasicType();
            if (isBasicType) {
                throw new RuntimeException("新增方法参数不支持定义基础类型");
            }

            this.handleMethodParam(methodInfo, methodParamInfo, javaTrimElement);
        }
    }

    private void handleMethodParam(MethodInfo methodInfo, MethodParamInfo methodParamInfo, Element javaTrimElement) {
        List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(methodParamInfo.getType());
        if (ObjectUtils.isEmpty(tableColumnInfoList)) {
            throw new RuntimeException("实体表字段不存在");
        }
        for (ColumnInfo tableColumnInfo : tableColumnInfoList) {
            if (TypeUtils.typeEquals(tableColumnInfo, IdColumnInfo.class)) {
                IdColumnInfo idColumnInfo = (IdColumnInfo) tableColumnInfo;
                List<ColumnInfo> columnInfoList = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(columnInfoList)) {
                    this.handleMethodParam(methodInfo, methodParamInfo, idColumnInfo, javaTrimElement);
                } else {
                    for (ColumnInfo columnInfo : columnInfoList) {
                        String javaColumnName = String.format("%s.%s", idColumnInfo.getJavaColumnName(), columnInfo.getJavaColumnName());
                        ColumnInfo composite = new ColumnInfo.Builder().columnInfo(columnInfo).javaColumnName(javaColumnName).build();
                        this.handleMethodParam(methodInfo, methodParamInfo, composite, javaTrimElement);
                    }
                }
            } else if (TypeUtils.typeEquals(tableColumnInfo, ColumnInfo.class)) {
                this.handleMethodParam(methodInfo, methodParamInfo, tableColumnInfo, javaTrimElement);
            } else if (TypeUtils.typeEquals(tableColumnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) tableColumnInfo;
                RelationType relationType = relationColumnInfo.getRelationType();
                if (relationType == RelationType.MANY_TO_MANY) {
                    continue;
                }
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyColumnInfo.getColumnInfo();
                    ColumnInfo referencedColumnInfo = inverseForeignKeyColumnInfo.getReferencedColumnInfo();
                    String javaColumnName = String.format("%s.%s", relationColumnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnPath());
                    ColumnInfo composite = new ColumnInfo.Builder().columnInfo(foreignKeyColumnInfo).javaColumnName(javaColumnName).build();
                    this.handleMethodParam(methodInfo, methodParamInfo, composite, javaTrimElement);
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
            this.handleBatchValue(methodInfo, methodParamInfo, columnInfo, javaTrimElement);
            return;
        }

        this.handleSingleBusinessObjectParam(methodInfo, columnInfo, javaTrimElement);
    }

    private void handleBatchValue(MethodInfo methodInfo, MethodParamInfo methodParamInfo, ColumnInfo tableColumnInfo, Element javaTrimElement) {
        String batchItemName = methodParamInfo.getBatchItemName();
        Boolean dynamic = methodInfo.getDynamic();
        String javaColumnName = tableColumnInfo.getJavaColumnName();

        String nestedJavaColumnName = String.format("%s.%s", batchItemName, javaColumnName);
        if (dynamic) {
            Element javaTrimIfElement = javaTrimElement.addElement("if");
            javaTrimIfElement.addAttribute("test", buildTestNotNull(nestedJavaColumnName));
            String javaColumn = String.format("#{%s%s},", nestedJavaColumnName, buildTypeHandler(tableColumnInfo));
            javaTrimIfElement.addText(javaColumn);
        } else {
            String javaColumn = String.format("#{%s%s},", nestedJavaColumnName, buildTypeHandler(tableColumnInfo));
            javaTrimElement.addText(javaColumn);
        }
    }

    private void handleSingleBusinessObjectParam(MethodInfo methodInfo, ColumnInfo tableColumnInfo, Element javaTrimElement) {
        String javaColumnName = tableColumnInfo.getJavaColumnName();
        if (methodInfo.getDynamic()) {
            Element javaTrimIfElement = javaTrimElement.addElement("if");
            javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
            String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(tableColumnInfo));
            javaTrimIfElement.addText(javaColumn);
        } else {
            String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(tableColumnInfo));
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