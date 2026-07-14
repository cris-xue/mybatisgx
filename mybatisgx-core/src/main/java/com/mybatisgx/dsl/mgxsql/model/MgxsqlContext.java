package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql 解析上下文，维护输入文本、扫描位置、行号/列号。
 * <p>重构后仅保留 {@link com.mybatisgx.dsl.mgxsql.model.ast.MgxsqlParser} 所需的逐字符读取与位置能力；
 * 原状态机的隐式状态（{@code state}/{@code stateStack}/{@code parenDepth}/{@code descentCloseTags}/{@code output}）
 * 已由 AST 节点的显式父子关系取代并移除。
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class MgxsqlContext {

    /**
     * 当前扫描位置
     */
    private int position = 0;

    /**
     * 当前行号（从1开始）
     */
    private int lineNumber = 1;

    /**
     * 当前列号（从1开始）
     */
    private int columnNumber = 1;

    /**
     * 当前扫描的 SQL 文本
     */
    private String input;

    public MgxsqlContext(String input) {
        this.input = input;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int advance() {
        char c = charAt(this.position);
        this.position++;
        if (c == '\n') {
            this.lineNumber++;
            this.columnNumber = 1;
        } else {
            this.columnNumber++;
        }
        return this.position;
    }

    public char charAt(int pos) {
        if (pos < 0 || pos >= this.input.length()) {
            return '\0';
        }
        return this.input.charAt(pos);
    }

    public char currentChar() {
        return charAt(this.position);
    }

    public char peekChar(int offset) {
        return charAt(this.position + offset);
    }

    public boolean hasMore() {
        return this.position < this.input.length();
    }

    public String getInput() {
        return input;
    }

    public int getInputLength() {
        return this.input.length();
    }

    /**
     * 从指定位置开始读取子串
     */
    public String substring(int start, int end) {
        if (start < 0) {
            start = 0;
        }
        if (end > this.input.length()) {
            end = this.input.length();
        }
        return this.input.substring(start, end);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * 获取当前位置的友好位置描述（行号:列号 + 附近文本），用于语法错误信息
     */
    public String getPositionInfo() {
        int pos = this.position;
        int radius = 10;
        int start = Math.max(0, pos - radius);
        int end = Math.min(this.input.length(), pos + radius + 1);
        return "行" + this.lineNumber + ":列" + this.columnNumber + "，附近文本: ..." + this.input.substring(start, end) + "...";
    }

    /**
     * 判断指定位置开始的文本是否等于指定字符串（不区分大小写）
     */
    public boolean startsWithAt(String target, int pos) {
        if (target == null || pos + target.length() > this.input.length()) {
            return false;
        }
        for (int i = 0; i < target.length(); i++) {
            if (Character.toLowerCase(this.input.charAt(pos + i)) != Character.toLowerCase(target.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
