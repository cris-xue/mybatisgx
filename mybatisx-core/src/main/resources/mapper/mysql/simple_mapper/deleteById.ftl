<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <delete id="deleteById">
        delete from ${tableInfo.tableName} where id = ${r'#{id}'}
    </delete>

</mapper>