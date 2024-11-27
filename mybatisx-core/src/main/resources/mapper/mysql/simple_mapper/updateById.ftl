<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <update id="updateById">
        update ${mapperInfo.tableInfo.tableName}
        <trim prefix="set" suffixOverrides=",">
            <#list resultMapInfo.columnInfoList as columnInfo>
                ${columnInfo.dbColumnName} = ${r'#{'} ${columnInfo.javaColumnName} ${r'}'},
            </#list>
        </trim>
        where id = ${r'#{id}'}
    </update>

</mapper>