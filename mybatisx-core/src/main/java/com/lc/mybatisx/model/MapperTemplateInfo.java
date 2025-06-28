package com.lc.mybatisx.model;

import org.apache.ibatis.parsing.XNode;

import java.util.Map;

public class MapperTemplateInfo {

    private String namespace;

    private Map<String, XNode> curdTemplateMap;

    private Map<String, XNode> resultMapTemplateMap;

    private Map<String, XNode> associationSelectTemplateMap;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Map<String, XNode> getCurdTemplateMap() {
        return curdTemplateMap;
    }

    public void setCurdTemplateMap(Map<String, XNode> curdTemplateMap) {
        this.curdTemplateMap = curdTemplateMap;
    }

    public Map<String, XNode> getResultMapTemplateMap() {
        return resultMapTemplateMap;
    }

    public void setResultMapTemplateMap(Map<String, XNode> resultMapTemplateMap) {
        this.resultMapTemplateMap = resultMapTemplateMap;
    }

    public Map<String, XNode> getAssociationSelectTemplateMap() {
        return associationSelectTemplateMap;
    }

    public void setAssociationSelectTemplateMap(Map<String, XNode> associationSelectTemplateMap) {
        this.associationSelectTemplateMap = associationSelectTemplateMap;
    }

    @Override
    public String toString() {
        return "MapperTemplateInfo{" +
                "namespace='" + namespace + '\'' +
                ", curdTemplateMap=" + curdTemplateMap +
                ", resultMapTemplateMap=" + resultMapTemplateMap +
                ", associationSelectTemplateMap=" + associationSelectTemplateMap +
                '}';
    }
}
