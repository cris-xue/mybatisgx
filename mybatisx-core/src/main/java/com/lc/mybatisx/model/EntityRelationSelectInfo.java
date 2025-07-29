package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：实体关系查询
 * @date ：2021/7/9 17:14
 */
public class EntityRelationSelectInfo {

    /**
     * 节点唯一标识，只有在不使用嵌套模式字段时才使用
     */
    private String id;
    /**
     * 查询方法名
     */
    private String select;
    /**
     * 映射到一个外部的标签中，然后通过 id 进行引入
     */
    private String resultMapId;
    /**
     * 类型
     */
    private Class<?> type;
    /**
     * 类型名
     */
    private String typeName;
    /**
     * 容器类型
     */
    private Class<?> ofType;
    /**
     * 容器类型名
     */
    private String ofTypeName;
    /**
     * 关联字段对应的字段信息
     */
    private ColumnInfo columnInfo;
    /**
     * 字段信息列表
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * 关联信息
     */
    private List<EntityRelationSelectInfo> entityRelationSelectInfoList = new ArrayList();


}
