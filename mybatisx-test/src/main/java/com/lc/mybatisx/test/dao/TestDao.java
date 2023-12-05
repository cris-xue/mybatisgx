package com.lc.mybatisx.test.dao;

import com.lc.mybatisx.annotation.BetweenEnd;
import com.lc.mybatisx.annotation.BetweenStart;
import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.dao.QueryDao;
import com.lc.mybatisx.test.model.dto.TestDTO;
import com.lc.mybatisx.test.model.entity.Test;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/11/9 13:19
 */
@Repository
public interface TestDao extends QueryDao<Test, Long> {

    // @MapperMethod(type = MethodType.UPDATE)
    // int updateByIdAndNameOrUserName(Test test);

    // @MapperMethod(type = MethodType.QUERY)
    @Dynamic
    List<TestDTO> findByPayStatusLteqAndPayStatusNotOrXyzAbcLt(
            @Param("payStatus") String payStatus,
            @Param("payStatus") String payStatus1,
            @Param("XyzAbc") String payStatusXyzAbc);

    List<TestDTO> findByIdsIn(@Param("ids") List<Long> ids);

    // @MapperMethod(type = MethodType.QUERY)
    Map<String, Object> findByUsername(@Param("username") String username);

    Map<String, Object> findByIdBetween(
            @Param("start") @BetweenStart("id") Long start,
            @Param("end") @BetweenEnd("id") Long end);

    // Map<String, Object> findTop10ByAgeBetween(@Param("start") Long start, @Param("end") Long end);

    // @MapperMethod(type = MethodType.QUERY)
    // List<Map<String, Object>> findByPayStatusLteqAndPayStatus1NotOrXyzAbc1LtSelective(@Param("payStatus") String payStatus,
    //                                                                                   @Param("payStatus1") String payStatus1,
    //                                                                                   @Param("xyzAbc1") String xyzAbc1);

}
