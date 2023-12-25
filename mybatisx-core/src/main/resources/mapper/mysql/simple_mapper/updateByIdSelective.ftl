<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <update id="updateByIdSelective">
        update ${mapperInfo.tableName}
        <trim prefix="set" suffixOverrides=",">
            <#list tableInfo.columnInfoList as columnInfo>
                <if test="${columnInfo.javaColumnName} != null">
                    ${columnInfo.dbColumnName} = ${r'#{'} ${columnInfo.javaColumnName} ${r'}'},
                </if>
            </#list>
        </trim>
        where id = ${r'#{id}'}
    </update>

</mapper>