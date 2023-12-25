<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <select id="findAll" resultMap="${resultMapInfo.id}">
        select
        <trim prefix="" suffix="" suffixOverrides=",">
            <#list resultMapInfo.columnInfoList as columnInfo>
                ${columnInfo.dbColumnName},
            </#list>
        </trim>
        from ${mapperInfo.tableName}
    </select>

</mapper>