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
                    <if test="${methodNameWhereInfo.javaColumnName} != null">
                        ${methodNameWhereInfo.linkOp} ${methodNameWhereInfo.dbColumnName} ${methodNameWhereInfo.op} ${r'#{'} ${methodNameWhereInfo.javaColumnName} ${r'}'}
                    </if>
                </#list>
            </#if>
        </where>
    </select>

</mapper>