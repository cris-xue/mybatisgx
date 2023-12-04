<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <select id="findAll" resultType="${methodInfo.methodReturnInfo.typeName}">
        select * from ${tableInfo.tableName}
    </select>

</mapper>