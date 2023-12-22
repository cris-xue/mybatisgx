<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <select id="findList" resultMap="${resultMapInfo.id}">
        select * from ${tableInfo.tableName}
        <where>
            <trim prefixOverrides="and">
                <#list tableInfo.columnInfoList as columnInfo>
                    <if test="${columnInfo.javaColumnName} != null">
                        and ${columnInfo.dbColumnName} = ${r'#{'} ${columnInfo.javaColumnName} ${r'}'}
                    </if>
                </#list>
            </trim>
        </where>
    </select>

</mapper>