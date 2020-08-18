package com.lc.mybatisx.parse;

import java.util.ArrayList;
import java.util.List;

public class MethodParse {

    private Node root = new Node();

    public void init(List<String> keywordList) {
        for (String keyword : keywordList) {
            char[] chars = keyword.toCharArray();

            Node children = null;
            for (int i = chars.length; i > 0; i--) {
                Node node = new Node();
                node.setCode(chars[i - 1]);
                node.setChildren(children);
                children = node;
            }

        }
    }

    public static void main(String[] args) {
        List<String> unParseKeywordList = new ArrayList<>();
        unParseKeywordList.add("insert");
        unParseKeywordList.add("add");
        unParseKeywordList.add("delete");
        unParseKeywordList.add("update");
        unParseKeywordList.add("find");
        unParseKeywordList.add("select");
        unParseKeywordList.add("query");
        unParseKeywordList.add("By");
        unParseKeywordList.add("Selective");

        MethodParse methodParse = new MethodParse();
        methodParse.init(unParseKeywordList);
    }

}
