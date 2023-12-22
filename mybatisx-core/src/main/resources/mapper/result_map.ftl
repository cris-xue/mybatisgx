<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <resultMap id="${resultMapInfo.id}" type="${resultMapInfo.type}">
        <#list resultMapInfo.columnInfoList as columnInfo>
            <#if columnInfo.primaryKey=="YES">
                <id property="${columnInfo.javaColumnName}" column="${columnInfo.dbColumnName}" />
            </#if>
            <#if columnInfo.primaryKey=="NO">
                <result property="${columnInfo.javaColumnName}" column="${columnInfo.dbColumnName}" />
            </#if>
        </#list>
    </resultMap>

</mapper>