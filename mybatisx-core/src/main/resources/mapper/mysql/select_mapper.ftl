<?xml version="1.0" encoding="UTF-8" ?>
<mapper>
    <#if methodInfo.dynamic>
    <select id="${methodInfo.methodName}" resultType="${methodInfo.methodReturnInfo.typeName}">
        select
        <trim prefix="" suffix="" suffixOverrides=",">
            <#list tableInfo.columnInfoList as columnInfo>
                ${columnInfo.dbColumnName} as ${columnInfo.javaColumnName},
            </#list>
        </trim>
        from ${tableInfo.tableName}
        <where>
            <#if (methodInfo.methodNameInfo)??>
                <trim prefix="where" prefixOverrides="and">
                    <#list methodInfo.methodNameInfo.methodNameWhereInfoList as methodNameWhereInfo>
                        <if test="${methodNameWhereInfo.javaColumnName} != null">
                            ${methodNameWhereInfo.linkOp} ${methodNameWhereInfo.dbColumnName} ${methodNameWhereInfo.op} ${r'#{'} ${methodNameWhereInfo.javaColumnName} ${r'}'}
                        </if>
                    </#list>
                </trim>
            </#if>
        </where>
    </select>
    </#if>

    <#if !methodInfo.dynamic>
        <select id="${methodInfo.methodName}" resultType="${methodInfo.methodReturnInfo.typeName}">
            select
            <trim prefix="" suffix="" suffixOverrides=",">
                <#list tableInfo.columnInfoList as columnInfo>
                    ${columnInfo.dbColumnName} as ${columnInfo.javaColumnName},
                </#list>
            </trim>
            from ${tableInfo.tableName}
            <where>
                <#if (methodInfo.methodNameInfo)??>
                    <trim prefix="where" prefixOverrides="and">
                        <#list methodInfo.methodNameInfo.methodNameWhereInfoList as methodNameWhereInfo>
                            ${methodNameWhereInfo.linkOp} ${methodNameWhereInfo.dbColumnName} ${methodNameWhereInfo.op} ${r'#{'} ${methodNameWhereInfo.javaColumnName} ${r'}'}
                        </#list>
                    </trim>
                </#if>
            </where>
        </select>
    </#if>
</mapper>