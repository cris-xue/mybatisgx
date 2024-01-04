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
                    <@dynamicHandle type="string" param=conditionInfo.javaColumnName conditionInfo=conditionInfo />
                </#list>
            </#if>
        </where>
    </select>

</mapper>

<#macro dynamicHandle type param conditionInfo>
    <#switch type>
        <#case "string">
            <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(${conditionInfo.paramName[0]})">
                <@conditionHandle conditionInfo=conditionInfo/>
            </if>
            <#break>
        <#case "list">
            <if test="@org.apache.commons.lang3.ObjectUtils@isNotEmpty(${conditionInfo.paramName[0]})">
                <@conditionHandle conditionInfo=conditionInfo/>
            </if>
            <#break>
        <#default>
            <if test="${conditionInfo.paramName[0]} != null">
                <@conditionHandle conditionInfo=conditionInfo/>
            </if>
    </#switch>
</#macro>

<#macro conditionHandle conditionInfo>
    ${conditionInfo.linkOp} ${conditionInfo.dbColumnName} ${conditionInfo.op}
    <#switch conditionInfo.op>
        <#case "in">
            <foreach item="item" index="index" collection="${conditionInfo.paramName[0]}" open="(" separator="," close=")">
                ${r"#{item}"}
            </foreach>
            <#break>
        <#case "between">
            ${r'#{'} ${conditionInfo.paramName[0]} ${r'}'} and ${r'#{'} ${conditionInfo.paramName[1]} ${r'}'}
            <#break>
        <#default>
            ${r'#{'} ${conditionInfo.paramName[0]} ${r'}'}
    </#switch>
</#macro>