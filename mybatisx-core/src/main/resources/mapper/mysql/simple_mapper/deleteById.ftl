<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

    <delete id="deleteById" parameterType="java.lang.Long">
        update ${tableName} set delete_flag=1 where id = ${r'#{id}'}
        <#if columnMap['version']??>
            and version=${r'#{version'}${r'}'}
        </#if>
    </delete>

</mapper>