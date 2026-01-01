package com.mybatisgx.relation.select.batch.onetomany.test;

import com.mybatisgx.relation.select.batch.onetomany.dao.OrgDao;
import com.mybatisgx.relation.select.batch.onetomany.entity.Org;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrgDaoTest {

    private static int count = 10;
    private static OrgDao orgDao;

    private static List<Org> orgList = new ArrayList();

    private static int counter = 0;

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.batch.onetomany.entity"},
                new String[]{"com.mybatisgx.relation.select.batch.onetomany.dao"}
        );
        orgDao = sqlSession.getMapper(OrgDao.class);

        buildRoot();
    }

    private static void buildRoot() {
        Org org = new Org(counter, UUID.randomUUID().toString());
        List<Org> orgList = buildData(1, org);
        orgDao.insertBatch(orgList, 100);
    }

    private static List<Org> buildData(int level, Org parentOrg) {
        if (level > 3) {
            return null;
        }
        List<Org> orgList = new ArrayList();
        for (int i = 0; i < 3; i++) {
            Org org = new Org(++counter, UUID.randomUUID().toString());
            org.setParent(parentOrg);
            orgList.add(org);

            List<Org> childrenOrgList = buildData(++level, org);
            if (ObjectUtils.isNotEmpty(childrenOrgList)) {
                orgList.addAll(childrenOrgList);
            }
        }
        return orgList;
    }

    @Test
    public void testFindTreeList() {
        Org parent = new Org();
        parent.setId(0);
        Org org = new Org();
        org.setParent(parent);
        List<Org> dbOrgList = orgDao.findList(org);
        System.out.println(dbOrgList);
    }
}
