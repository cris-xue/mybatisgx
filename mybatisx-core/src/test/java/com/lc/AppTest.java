package com.lc;

import com.lc.mybatisx.handler.handler.SqlMapperHandler;
import com.lc.mybatisx.model.InterfaceNode;
import com.lc.mybatisx.model.MethodNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppTest {

    @Test
    public void test01() {
        List<String> methodList = Arrays.asList("findByIdEq", "findTop50ByIdAndName", "findByIdAndName", "findByNameAndAgeEqOrderByNameDesc", "findByNameAndInputTimeBetween", "findByNameGroupByName", "findByPayStatusAndPayStatusXyzAbc", "updateByIdSelective");
        Map<String, List<String>> map = new LinkedHashMap<>();

        SqlMapperHandler sqlMapperHandler = new SqlMapperHandler();
        for (String method : methodList) {
            SqlMapperHandler.build(new InterfaceNode(), new MethodNode(), method);
            /*List<String> keyList = KeywordParse.parseMethod(method, null);
            map.put(method, keyList);*/
        }
        System.out.println(map);

        map.forEach((k, v) -> {
            // WhereWrapper whereWrapper = Keyword.buildWhereWrapper(v);
            // System.out.println(whereWrapper);
        });
    }

    @Test
    public void testLinkOp() {
        /*LinkOp linkOp = LinkOp.valueOf("AND");
        System.out.println(linkOp);*/

        // sumTableUserLeftJoinUserByNameAndAgeAndAbcOrCdbGroupByMnbOrderByAbcDesc
    }

}
