package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
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

        this.setValue(mapperInfo, methodInfo, javaTrimElement);
        return document.asXML();
    }

    private String getKeyProperty(MethodInfo methodInfo) {
        if (methodInfo.getBatch()) {
            MethodParamInfo dataMethodParamInfo = null;
            List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
            for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                if (!methodParamInfo.getBatchSize()) {
                    dataMethodParamInfo = methodParamInfo;
                }
            }
            if (dataMethodParamInfo != null) {
                EntityInfo entityInfo = EntityInfoContextHolder.get(dataMethodParamInfo.getType());
                List<ColumnInfo> idColumnInfoList = entityInfo.getIdColumnInfo().getColumnInfoList();
                List<String> keyPropertyList = new ArrayList();
                for (ColumnInfo idColumnInfo : idColumnInfoList) {
                    String keyProperty = String.format("%s.%s", dataMethodParamInfo.getBatchItemName(), idColumnInfo.getJavaColumnName());
                    keyPropertyList.add(keyProperty);
                }
                return StringUtils.join(keyPropertyList, ",");
            } else {
                throw new RuntimeException("新增批量方法必须有数据列表");
            }
        } else {
            MethodParamInfo dataMethodParamInfo = null;
            List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
            for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                if (!methodParamInfo.getBatchSize()) {
                    dataMethodParamInfo = methodParamInfo;
                }
            }

            EntityInfo entityInfo = EntityInfoContextHolder.get(dataMethodParamInfo.getType());
            IdColumnInfo idColumnInfo = entityInfo.getIdColumnInfo();
            List<ColumnInfo> columnInfoList = idColumnInfo.getColumnInfoList();
            List<String> keyPropertyList = new ArrayList();
            if (ObjectUtils.isNotEmpty(columnInfoList)) {
                for (ColumnInfo columnInfo : columnInfoList) {
                    String keyProperty = String.format("%s", columnInfo.getJavaColumnName());
                    keyPropertyList.add(keyProperty);
                }
            } else {
                String keyProperty = String.format("%s", idColumnInfo.getJavaColumnName());
                keyPropertyList.add(keyProperty);
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
            String javaColumnName = tableColumnInfo.getJavaColumnName();
            String dbColumnName = tableColumnInfo.getDbColumnName();

            LogicDelete logicDelete = tableColumnInfo.getLogicDelete();
            if (logicDelete != null) {
                String javaColumn = String.format("%s,", dbColumnName);
                dbTrimElement.addText(javaColumn);
                continue;
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
    }

    private void setValue(MapperInfo mapperInfo, MethodInfo methodInfo, Element javaTrimElement) {
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
            LogicDelete logicDelete = tableColumnInfo.getLogicDelete();
            if (logicDelete != null) {
                String javaColumn = String.format("'%s'%s,", logicDelete.show(), buildTypeHandler(tableColumnInfo));
                javaTrimElement.addText(javaColumn);
                continue;
            }

            Boolean isBatch = methodInfo.getBatch();
            if (isBatch) {
                this.handleBatchValue(methodInfo, methodParamInfo, tableColumnInfo, javaTrimElement);
            } else {
                Param param = methodParamInfo.getParam();
                if (param == null) {
                    this.handleSingleBusinessObjectParam(methodInfo, methodParamInfo, tableColumnInfo, javaTrimElement);
                } else {
                    this.handleSingleBusinessObjectParamAnnotation(methodInfo, methodParamInfo, tableColumnInfo, javaTrimElement);
                }
            }
        }
    }

    private void handleBatchValue(MethodInfo methodInfo, MethodParamInfo methodParamInfo, ColumnInfo tableColumnInfo, Element javaTrimElement) {
        String batchItemName = methodParamInfo.getBatchItemName();
        Boolean dynamic = methodInfo.getDynamic();
        String javaColumnName = tableColumnInfo.getJavaColumnName();
        if (ColumnInfoHelper.isColumnInfo(tableColumnInfo)) {
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
        } else {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) tableColumnInfo;
            ManyToMany manyToMany = relationColumnInfo.getManyToMany();
            if (manyToMany == null) {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    String referencedColumnName = inverseForeignKeyColumnInfo.getReferencedColumnName();
                    String nestedJavaColumnName = String.format("%s.%s.%s", batchItemName, javaColumnName, referencedColumnName);
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
            } else {

            }
        }
    }

    private void handleSingleBusinessObjectParam(MethodInfo methodInfo, MethodParamInfo methodParamInfo, ColumnInfo tableColumnInfo, Element javaTrimElement) {
        Boolean dynamic = methodInfo.getDynamic();
        String javaColumnName = tableColumnInfo.getJavaColumnName();
        if (ColumnInfoHelper.isColumnInfo(tableColumnInfo)) {
            if (dynamic) {
                Element javaTrimIfElement = javaTrimElement.addElement("if");
                javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(tableColumnInfo));
                javaTrimIfElement.addText(javaColumn);
            } else {
                String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(tableColumnInfo));
                javaTrimElement.addText(javaColumn);
            }
        } else {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) tableColumnInfo;
            ManyToMany manyToMany = relationColumnInfo.getManyToMany();
            if (manyToMany == null) {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    String referencedColumnName = inverseForeignKeyColumnInfo.getReferencedColumnName();
                    String nestedJavaColumnName = javaColumnName + "." + referencedColumnName;
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
            } else {

            }
        }
    }

    private void handleSingleBusinessObjectParamAnnotation(MethodInfo methodInfo, MethodParamInfo methodParamInfo, ColumnInfo tableColumnInfo, Element javaTrimElement) {
        String javaColumnName = tableColumnInfo.getJavaColumnName();
        if (ColumnInfoHelper.isColumnInfo(tableColumnInfo)) {
            Boolean isBatch = methodInfo.getBatch();
            String paramName = isBatch ? methodParamInfo.getBatchItemName() : methodParamInfo.getParamName();
            if (methodInfo.getDynamic()) {
                Element javaTrimIfElement = javaTrimElement.addElement("if");
                javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                String javaColumn = String.format("#{%s.%s%s},", paramName, javaColumnName, buildTypeHandler(tableColumnInfo));
                javaTrimIfElement.addText(javaColumn);
            } else {
                String javaColumn = String.format("#{%s.%s%s},", paramName, javaColumnName, buildTypeHandler(tableColumnInfo));
                javaTrimElement.addText(javaColumn);
            }
        } else {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) tableColumnInfo;
            ManyToMany manyToMany = relationColumnInfo.getManyToMany();
            if (manyToMany == null) {
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                    String referencedColumnName = inverseForeignKeyColumnInfo.getReferencedColumnName();
                    String nestedJavaColumnName = javaColumnName + "." + referencedColumnName;

                    Boolean isBatch = methodInfo.getBatch();
                    String paramName = isBatch ? methodParamInfo.getBatchItemName() : methodParamInfo.getParamName();
                    if (methodInfo.getDynamic()) {
                        Element javaTrimIfElement = javaTrimElement.addElement("if");
                        javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                        String javaColumn = String.format("#{%s.%s%s},", paramName, javaColumnName, buildTypeHandler(tableColumnInfo));
                        javaTrimIfElement.addText(javaColumn);
                    } else {
                        String javaColumn = String.format("#{%s.%s%s},", paramName, javaColumnName, buildTypeHandler(tableColumnInfo));
                        javaTrimElement.addText(javaColumn);
                    }
                }
            } else {

            }
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