<?xml version="1.0" encoding="UTF-8" ?>
<mapper>

    <select id="findAll">
        select * from ${tableInfo.tableName}
    </select>

</mapper>