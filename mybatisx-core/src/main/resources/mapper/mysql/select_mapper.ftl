<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <select id="${methodInfo.methodName}" resultMap="${resultMapInfo.id}">
        select
        <trim prefix="" suffix="" suffixOverrides=",">
            <#list resultMapInfo.columnInfoList as columnInfo>
                ${columnInfo.dbColumnName},
            </#list>
        </trim>
        from ${mapperInfo.tableName}
        <where>
            <#if (methodInfo.conditionInfoList)??>
                <#list methodInfo.conditionInfoList as conditionInfo>
                    ${conditionInfo.linkOp} ${conditionInfo.dbColumnName} ${conditionInfo.op}
                    <@conditionHandle op=conditionInfo.op param=conditionInfo.paramName/>
                </#list>
            </#if>
        </where>
    </select>

</mapper>

<#macro conditionHandle op param>
    <#switch op>
        <#case "in">
            <foreach item="item" index="index" collection="${param[0]}" open="(" separator="," close=")">
                ${r"#{item}"}
            </foreach>
            <#break>
        <#case "between">
            ${r'#{'} ${param[0]} ${r'}'} and ${r'#{'} ${param[1]} ${r'}'}
            <#break>
        <#default>
            ${r'#{'} ${param[0]} ${r'}'}
    </#switch>
</#macro>