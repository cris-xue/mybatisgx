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

    SimpleInsertHandler simpleInsertHandler = new SimpleInsertHandler();
    BatchInsertHandler batchInsertHandler = new BatchInsertHandler();

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
        String keyProperty;
        if (methodInfo.getBatch()) {
            keyProperty = batchInsertHandler.getKeyProperty(methodInfo);
        } else {
            keyProperty = simpleInsertHandler.getKeyProperty(methodInfo);
        }
        insertElement.addAttribute("keyProperty", keyProperty);
        insertElement.addAttribute("useGeneratedKeys", "true");
        insertElement.addText(String.format("insert into %s", mapperInfo.getEntityInfo().getTableName()));

        Element dbTrimElement = insertElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "(");
        dbTrimElement.addAttribute("suffix", ")");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        if (methodInfo.getBatch()) {
            batchInsertHandler.setColumn(methodInfo, dbTrimElement);
        } else {
            simpleInsertHandler.setColumn(methodInfo, dbTrimElement);
        }

        Element javaTrimElement = insertElement.addElement("trim");
        javaTrimElement.addAttribute("prefix", "values (");
        javaTrimElement.addAttribute("suffix", ")");
        javaTrimElement.addAttribute("suffixOverrides", ",");

        if (methodInfo.getBatch()) {
            batchInsertHandler.setValue(methodInfo, javaTrimElement);
        } else {
            simpleInsertHandler.setValue(methodInfo, javaTrimElement);
        }
        return document.asXML();
    }

    private static abstract class AbstractInsertHandler {

        abstract String getKeyProperty(MethodInfo methodInfo);

        public void setColumn(MethodInfo methodInfo, Element dbTrimElement) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo.getType());
            for (ColumnInfo columnInfo : tableColumnInfoList) {
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        this.setColumn(methodInfo, entityParamInfo, columnInfo, null, dbTrimElement);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            String javaColumnName = String.format("%s.%s", columnInfo.getJavaColumnName(), columnInfoComposite.getJavaColumnName());
                            ColumnInfo composite = new ColumnInfo.Builder().columnInfo(columnInfo).javaColumnName(javaColumnName).build();
                            this.setColumn(methodInfo, entityParamInfo, columnInfo, columnInfoComposite, dbTrimElement);
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
                        List<ForeignKeyColumnInfo> inverseForeignKeyInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                        for (ForeignKeyColumnInfo inverseForeignKeyInfo : inverseForeignKeyInfoList) {
                            ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                            this.setColumn(methodInfo, entityParamInfo, foreignKeyColumnInfo, columnInfo, dbTrimElement);
                        }
                    }
                }
            }
        }

        private void setColumn(MethodInfo methodInfo, MethodParamInfo entityParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite, Element dbTrimElement) {
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

        public void setValue(MethodInfo methodInfo, Element javaTrimElement) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(entityParamInfo.getType());
            if (ObjectUtils.isEmpty(tableColumnInfoList)) {
                throw new RuntimeException("实体表字段不存在");
            }
            for (ColumnInfo columnInfo : tableColumnInfoList) {
                if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                    List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
                    if (ObjectUtils.isEmpty(columnInfoComposites)) {
                        this.setValue(methodInfo, entityParamInfo, columnInfo, null, javaTrimElement);
                    } else {
                        for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                            // String javaColumnName = String.format("%s.%s", idColumnInfo.getJavaColumnName(), columnInfoComposite.getJavaColumnName());
                            // ColumnInfo composite = new ColumnInfo.Builder().columnInfo(columnInfo).javaColumnName(javaColumnName).build();
                            this.setValue(methodInfo, entityParamInfo, columnInfo, columnInfoComposite, javaTrimElement);
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
                            if (TypeUtils.typeEquals(referencedColumnInfo, IdColumnInfo.class)) {
                                List<ColumnInfo> referencedColumnComposites = referencedColumnInfo.getComposites();
                                if (ObjectUtils.isEmpty(referencedColumnComposites)) {
                                    String javaColumnName = String.format("%s.%s", relationColumnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnName());
                                    ColumnInfo composite = new ColumnInfo.Builder().columnInfo(foreignKeyColumnInfo).javaColumnName(javaColumnName).build();
                                    this.setValue(methodInfo, entityParamInfo, relationColumnInfo, referencedColumnInfo, javaTrimElement);
                                } else {
                                    for (ColumnInfo referencedColumnComposite : referencedColumnComposites) {
                                        String javaColumnName = String.format("%s.%s.%s", relationColumnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnName(), referencedColumnComposite.getJavaColumnName());
                                        ColumnInfo composite = new ColumnInfo.Builder().columnInfo(foreignKeyColumnInfo).javaColumnName(javaColumnName).build();
                                        this.setValue(methodInfo, entityParamInfo, relationColumnInfo, referencedColumnInfo, javaTrimElement);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private void setValue(MethodInfo methodInfo, MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite, Element javaTrimElement) {
            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (logicDelete != null) {
                String javaColumn = String.format("'%s'%s,", logicDelete.show(), buildTypeHandler(columnInfo));
                javaTrimElement.addText(javaColumn);
                return;
            }
            List<String> paramValuePathItemList = this.getParamValuePathItemList(methodParamInfo, columnInfo, columnInfoComposite);
            String paramValuePath = StringUtils.join(paramValuePathItemList, ".");
            if (methodInfo.getDynamic()) {
                Element javaTrimIfElement = javaTrimElement.addElement("if");
                javaTrimIfElement.addAttribute("test", buildTestNotNull(paramValuePath));
                String javaColumn = String.format("#{%s%s},", paramValuePath, buildTypeHandler(columnInfo));
                javaTrimIfElement.addText(javaColumn);
            } else {
                String javaColumn = String.format("#{%s%s},", paramValuePath, buildTypeHandler(columnInfo));
                javaTrimElement.addText(javaColumn);
            }
        }

        protected String buildTypeHandler(ColumnInfo columnInfo) {
            String typeHandler = columnInfo.getTypeHandler();
            if (StringUtils.isNotBlank(typeHandler)) {
                return String.format(", typeHandler=%s", typeHandler);
            }
            return "";
        }

        protected String buildTestNotNull(String javaColumnName) {
            return String.format("%s != null", javaColumnName);
        }

        protected List<ColumnInfo> getTableColumnInfoList(Class<?> entityClass) {
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
            return entityInfo.getTableColumnInfoList();
        }

        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.getParamValuePathItemList(methodParamInfo, null, columnInfo, columnInfoComposite);
        }

        abstract protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo relationColumnInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite);
    }

    private static class SimpleInsertHandler extends AbstractInsertHandler {

        @Override
        String getKeyProperty(MethodInfo methodInfo) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<String> keyPropertyList = new ArrayList();
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityParamInfo.getType());
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
            return StringUtils.join(keyPropertyList, ",");
        }

        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo relationColumnInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = new ArrayList<>();
            if (methodParamInfo.getWrapper()) {
                paramValuePathItemList.add(methodParamInfo.getParamName());
            }
            if (relationColumnInfo != null) {
                paramValuePathItemList.add(relationColumnInfo.getJavaColumnName());
            }
            if (columnInfo != null) {
                paramValuePathItemList.add(columnInfo.getJavaColumnName());
            }
            if (columnInfoComposite != null) {
                paramValuePathItemList.add(columnInfoComposite.getJavaColumnName());
            }
            return paramValuePathItemList;
        }
    }

    private static class BatchInsertHandler extends AbstractInsertHandler {

        @Override
        String getKeyProperty(MethodInfo methodInfo) {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            List<String> keyPropertyList = new ArrayList();
            if (entityParamInfo.getBatchData()) {
                EntityInfo entityInfo = EntityInfoContextHolder.get(entityParamInfo.getType());
                IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
                List<ColumnInfo> idColumnComposites = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(idColumnComposites)) {
                    String keyProperty = String.format("%s.%s", entityParamInfo.getBatchItemName(), idColumnInfo.getJavaColumnName());
                    keyPropertyList.add(keyProperty);
                } else {
                    for (ColumnInfo idColumnComposite : idColumnComposites) {
                        String keyProperty = String.format("%s.%s.%s", entityParamInfo.getBatchItemName(), idColumnInfo.getJavaColumnName(), idColumnComposite.getJavaColumnName());
                        keyPropertyList.add(keyProperty);
                    }
                }
            }
            return StringUtils.join(keyPropertyList, ",");
        }

        protected List<String> getParamValuePathItemList(MethodParamInfo methodParamInfo, ColumnInfo relationColumnInfo, ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            // int insertBatch(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);
            String batchItemName = methodParamInfo.getBatchItemName();
            String javaColumnName = columnInfo.getJavaColumnName();
            return Arrays.asList(batchItemName, javaColumnName);
        }
    }
}