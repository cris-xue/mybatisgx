package com.lc.mybatisx.model;

import java.util.List;

// SELECT * from runoob_tbl WHERE runoob_author='菜鸟教程';
// UPDATE runoob_tbl SET runoob_title='学习 C++' WHERE runoob_id=3;
// DELETE FROM runoob_tbl WHERE runoob_id=3;
// INSERT INTO runoob_tbl (runoob_title, runoob_author, submission_date) VALUES ("学习 MySQL", "菜鸟教程", NOW());
public class SqlModel {

    private String jdbcColumn;

    private Symbol symbol;

    private List<String> javaColumn;

    private Class<?> type;

}
