package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:16
 */
public class MapperNode {

    private Class<?> InterfaceClass;



    private EntityNode entityNode;

    private List<ActionNode> actionNodeList;

}


/**
 *
 * @Repository
 * public interface TestDao extends QueryDao<Test, Long> {
 *
 *     // @MapperMethod(type = MethodType.UPDATE)
 *     // int updateByIdAndNameOrUserName(Test test);
 *
 *     @MapperMethod(type = MethodType.QUERY)
 *     List<TestDTO> findByPayStatusAndPayStatusXyzAbcSelective(@Param("payStatus") String payStatus, @Param("payStatusXyzAbc") String payStatusXyzAbc);
 *
 *     // @MapperMethod(type = MethodType.QUERY)
 *     // Map<String, Object> findByUsername(@Param("username") String username);
 *
 *     // Map<String, Object> findByIdBetween(@Param("start") @BetweenStart Long start, @Param("end") @BetweenEnd Long end);
 *
 *     // Map<String, Object> findTop10ByAgeBetween(@Param("start") Long start, @Param("end") Long end);
 *
 *     @MapperMethod(type = MethodType.QUERY)
 *     List<Map<String, Object>> findByPayStatusLteqAndPayStatus1NotOrXyzAbc1LtSelective(@Param("payStatus") String payStatus,
 *                                                                                       @Param("payStatus1") String payStatus1,
 *                                                                                       @Param("xyzAbc1") String xyzAbc1);
 *
 * }
 * */
