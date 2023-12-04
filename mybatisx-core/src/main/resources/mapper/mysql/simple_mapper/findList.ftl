<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <select id="findList">
        select * from ${tableInfo.tableName}
        <trim prefix="where" prefixOverrides="and">
            <#list tableInfo.columnInfoList as columnInfo>
                <if test="${columnInfo.javaColumnName} != null">
                    AND ${columnInfo.dbColumnName} = ${r'#{'} ${columnInfo.javaColumnName} ${r'}'}
                </if>
            </#list>
        </trim>
    </select>

</mapper>