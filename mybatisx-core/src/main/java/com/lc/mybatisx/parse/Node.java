package com.lc.mybatisx.parse;

import java.util.List;

public class Node {

    private char code;
    private Node parent;
    private Node children;
    private List<Integer> index;
    private String keyword;

    public char getCode() {
        return code;
    }

    public void setCode(char code) {
        this.code = code;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getChildren() {
        return children;
    }

    public void setChildren(Node children) {
        this.children = children;
    }

    public List<Integer> getIndex() {
        return index;
    }

    public void setIndex(List<Integer> index) {
        this.index = index;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
