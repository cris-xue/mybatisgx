<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <insert id="insertSelective" keyProperty="id" useGeneratedKeys="true">
        insert into ${mapperInfo.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list tableInfo.columnInfoList as columnInfo>
                <if test="${columnInfo.javaColumnName} != null">
                    ${columnInfo.dbColumnName},
                </if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list tableInfo.columnInfoList as columnInfo>
                <if test="${columnInfo.javaColumnName} != null">
                    ${r'#{'} ${columnInfo.javaColumnName} ${r'}'},
                </if>
            </#list>
        </trim>
    </insert>

</mapper>