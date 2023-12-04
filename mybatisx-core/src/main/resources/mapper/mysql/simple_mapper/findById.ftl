<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <select id="findById" resultType="${methodInfo.methodReturnInfo.typeName}">
        select * from ${tableInfo.tableName} where id = ${r'#{id}'}
    </select>

</mapper>