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
                    <@dynamicParamHandle type="string" param=methodNameWhereInfo.javaColumnName methodNameWhereInfo=methodNameWhereInfo />
                </#list>
            </#if>
        </where>
    </select>

</mapper>

<#macro dynamicParamHandle type param methodNameWhereInfo>
    <#switch type>
        <#case "string">
            <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(${param})">
                <@whereParamHandle methodNameWhereInfo=methodNameWhereInfo/>
            </if>
            <#break>
        <#case "list">
            <if test="@org.apache.commons.lang3.ObjectUtils@isNotEmpty(${param})">
                <@whereParamHandle methodNameWhereInfo=methodNameWhereInfo/>
            </if>
            <#break>
        <#default>
            <if test="${param} != null">
                <@whereParamHandle methodNameWhereInfo=methodNameWhereInfo/>
            </if>
    </#switch>
</#macro>

<#macro whereParamHandle methodNameWhereInfo>
    ${methodNameWhereInfo.linkOp} ${methodNameWhereInfo.dbColumnName} ${methodNameWhereInfo.op}
    <#switch methodNameWhereInfo.op>
        <#case "in">
            <foreach item="item" index="index" collection="${methodNameWhereInfo.javaColumnName}" open="(" separator="," close=")">
                ${r"#{item}"}
            </foreach>
            <#break>
        <#default>
            ${r'#{'} ${methodNameWhereInfo.javaColumnName} ${r'}'}
    </#switch>
</#macro>