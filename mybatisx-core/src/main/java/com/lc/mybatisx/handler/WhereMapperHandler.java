package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.BetweenEnd;
import com.lc.mybatisx.annotation.BetweenStart;
import com.lc.mybatisx.parse.Keyword;
import com.lc.mybatisx.parse.KeywordType;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
import org.antlr.v4.runtime.Token;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WhereMapperHandler {

    private Map<String, Keyword> keywordMap;

    public WhereMapperHandler(Map<String, Keyword> keywordMap) {
        this.keywordMap = keywordMap;
    }

    public WhereWrapper build(Map<Class<?>, List<Token>> methodKeywordList) {
        return null;
    }

    public WhereWrapper build(Method method, List<String> keywordList, List<ModelWrapper> modelWrapperList) {
        WhereWrapper head = new WhereWrapper();
        WhereWrapper tail = new WhereWrapper();
        head = tail;

        WhereWrapper whereWrapper = null;
        boolean isWhere = false;
        for (int i = 0; i < keywordList.size(); i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);

            WhereWrapper whereWrapperTemp = createWhereWrapper(keyword);
            if (whereWrapperTemp != null) {
                whereWrapper = whereWrapperTemp;
                isWhere = true;
                continue;
            }

            if (isWhere && keyword == null) {
                String methodDbColumn = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, kw);
                String methodJavaColumn = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, kw);
                for (ModelWrapper modelWrapper : modelWrapperList) {
                    // 方法名字段和模型字段做校验
                    String modelDbColumn = modelWrapper.getDbColumn();
                    String modelJavaColumn = modelWrapper.getJavaColumn();
                    if (modelDbColumn.equals(methodDbColumn)) {
                        whereWrapper.setDbColumn(modelDbColumn);
                        whereWrapper.setJavaColumn(Arrays.asList(modelJavaColumn));
                    }
                }
            }

            // 获取对应的条件关键字参数
            if (keyword == Keyword.BETWEEN) {
                List<String> betweenList = new ArrayList<>();
                Parameter[] parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    Param param = parameter.getAnnotation(Param.class);
                    BetweenStart betweenStart = parameter.getAnnotation(BetweenStart.class);
                    BetweenEnd betweenEnd = parameter.getAnnotation(BetweenEnd.class);

                    if (betweenStart != null) {
                        String methodJavaColumn = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, param.value());
                        betweenList.add(0, methodJavaColumn);
                    }
                    if (betweenEnd != null) {
                        String methodJavaColumn = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, param.value());
                        betweenList.add(1, methodJavaColumn);
                    }
                }
                whereWrapper.setJavaColumn(betweenList);
            }

            boolean isSetOp = setOp(keyword, whereWrapper);
            if (isSetOp) {
                tail.setWhereWrapper(whereWrapper);
                tail = whereWrapper;
                isWhere = false;
                continue;
            }

            boolean isSetDefaultOp = setDefaultOp(keywordList, i, isWhere, whereWrapper);
            if (isSetDefaultOp) {
                tail.setWhereWrapper(whereWrapper);
                tail = whereWrapper;
                isWhere = false;
                continue;
            }
        }

        return head.getWhereWrapper();
    }

    private Map<String, Parameter> getParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Param param = parameter.getAnnotation(Param.class);
            BetweenStart betweenStart = parameter.getAnnotation(BetweenStart.class);
            BetweenEnd betweenEnd = parameter.getAnnotation(BetweenEnd.class);
            /*if (param.value().equals(methodJavaColumn)) {

            }*/
        }
        return null;
    }

    private WhereWrapper createWhereWrapper(Keyword keyword) {
        WhereWrapper whereWrapper = null;

        if (keyword != null && keyword.getKeywordType() == KeywordType.LINK) {
            whereWrapper = new WhereWrapper();
            whereWrapper.setLinkOp(keyword.getSql());
        }

        return whereWrapper;
    }

    private boolean setOp(Keyword keyword, WhereWrapper whereWrapper) {
        if (keyword != null && keyword.getKeywordType() == KeywordType.WHERE) {
            whereWrapper.setOp(keyword.getSql());
            whereWrapper.setSql(keyword.getSql(whereWrapper));
            whereWrapper.setTest(keyword.getTest(whereWrapper));
            return true;
        }

        return false;
    }

    private boolean setDefaultOp(List<String> keywordList, int index, boolean isWhere, WhereWrapper whereWrapper) {
        Keyword nextkeyword = null;
        int nextIndex = index + 1;
        if (nextIndex < keywordList.size()) {
            String nextKeyword = keywordList.get(nextIndex);
            nextkeyword = keywordMap.get(nextKeyword);
        }
        if (isWhere && (nextkeyword == null || nextkeyword.getKeywordType() != KeywordType.WHERE)) {
            Keyword keyword = Keyword.EQ;
            whereWrapper.setOp(keyword.getSql());
            whereWrapper.setSql(keyword.getSql(whereWrapper));
            whereWrapper.setTest(keyword.getTest(whereWrapper));
            return true;
        }

        return false;
    }

}
