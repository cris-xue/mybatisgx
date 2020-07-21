<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${deleteSqlWrapper.namespace}">

    <delete id="${deleteSqlWrapper.methodName}" parameterType="${deleteSqlWrapper.parameterType}">
        delete from ${deleteSqlWrapper.tableName} where id = ${r'#{id}'}
    </delete>

</mapper>