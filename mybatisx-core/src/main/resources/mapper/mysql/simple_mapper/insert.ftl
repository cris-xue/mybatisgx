<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <insert id="insert" keyProperty="id" useGeneratedKeys="true">
        insert into ${tableInfo.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list tableInfo.columnInfoList as columnInfo>
                ${columnInfo.dbColumnName},
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list tableInfo.columnInfoList as columnInfo>
                ${r'#{'} ${columnInfo.javaColumnName} ${r'}'},
            </#list>
        </trim>
    </insert>

</mapper>