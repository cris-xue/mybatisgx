package com.lc.mybatisx.annotation.dsl;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/11/13 17:18
 */
public interface TestMapper {

    @QueryDsl(
            table = @Table(
                    name = User.class,
                    where = @Where(value = "id"),
                    leftJoin = @LeftJoin
            )
    )
    void aaaa();

}
