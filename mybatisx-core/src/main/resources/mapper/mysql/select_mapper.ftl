<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <select id="${methodInfo.methodName}" resultMap="${resultMapInfo.id}">
        select
        <trim prefix="" suffix="" suffixOverrides=",">
            <#list tableInfo.columnInfoList as columnInfo>
                ${columnInfo.dbColumnName},
            </#list>
        </trim>
        from ${tableInfo.tableName}
        <where>
            <#if (methodInfo.methodNameInfo)??>
                <#list methodInfo.methodNameInfo.methodNameWhereInfoList as methodNameWhereInfo>
                    ${methodNameWhereInfo.linkOp} ${methodNameWhereInfo.dbColumnName} ${methodNameWhereInfo.op}
                    <@whereParamHandle op=methodNameWhereInfo.op param=methodNameWhereInfo.javaColumnName/>
                </#list>
            </#if>
        </where>
    </select>

</mapper>

<#macro whereParamHandle op param>
    <#switch op>
        <#case "in">
            <foreach item="item" index="index" collection="${param}" open="(" separator="," close=")">
                ${r"#{item}"}
            </foreach>
            <#break>
        <#default>
            ${r'#{'} ${param} ${r'}'}
    </#switch>
</#macro>