package com.mybatisgx.dsl.mgxsql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 扫描上下文，维护状态、位置、括号层级、输出缓冲区（v2）
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class MgxsqlContext {

    /**
     * 当前扫描状态
     */
    private MgxsqlState state = MgxsqlState.NORMAL;

    /**
     * 当前扫描位置
     */
    private int position = 0;

    /**
     * 当前括号层级（用于匹配 #(expr)(sql) 中的括号）
     */
    private int parenDepth = 0;

    /**
     * 输出缓冲区
     */
    private StringBuilder output = new StringBuilder();

    /**
     * 状态栈（支持嵌套状态恢复）
     */
    private List<MgxsqlState> stateStack = new ArrayList<MgxsqlState>();

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

    public MgxsqlState getState() {
        return state;
    }

    public void setState(MgxsqlState state) {
        this.state = state;
    }

    public void pushState(MgxsqlState newState) {
        this.stateStack.add(this.state);
        this.state = newState;
    }

    public MgxsqlState popState() {
        if (this.stateStack.isEmpty()) {
            return MgxsqlState.NORMAL;
        }
        this.state = this.stateStack.remove(this.stateStack.size() - 1);
        return this.state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int advance() {
        char c = this.charAt(this.position);
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

    public int getParenDepth() {
        return parenDepth;
    }

    public void setParenDepth(int parenDepth) {
        this.parenDepth = parenDepth;
    }

    public void incrementParenDepth() {
        this.parenDepth++;
    }

    public void decrementParenDepth() {
        this.parenDepth--;
    }

    public StringBuilder getOutput() {
        return output;
    }

    public void appendOutput(String text) {
        this.output.append(text);
    }

    public void appendOutput(char c) {
        this.output.append(c);
    }

    public String getOutputString() {
        return this.output.toString();
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
     * 获取当前位置的友好位置描述（行号:列号 + 附近文本）
     */
    public String getPositionInfo() {
        int pos = this.position;
        int radius = 10;
        int start = Math.max(0, pos - radius);
        int end = Math.min(this.input.length(), pos + radius + 1);
        return "行" + this.lineNumber + ":列" + this.columnNumber + "，附近文本: ..." + this.input.substring(start, end) + "...";
    }
}
