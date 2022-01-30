package com.lc.mybatisx.parse;

public enum LinkOp {

    AND("and"), OR("or");

    private String link;

    LinkOp(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static LinkOp get(String linkOp) {
        return LinkOp.valueOf(linkOp);
    }

}
