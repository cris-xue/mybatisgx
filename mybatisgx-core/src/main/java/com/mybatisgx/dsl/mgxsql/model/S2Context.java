package com.mybatisgx.dsl.mgxsql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 扫描上下文，维护状态、位置、括号层级、输出缓冲区
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class S2Context {

    /**
     * 当前扫描状态
     */
    private S2State state = S2State.NORMAL;

    /**
     * 当前扫描位置
     */
    private int position = 0;

    /**
     * 当前括号层级（用于匹配 ?(expr)(condition) 中的括号）
     */
    private int parenDepth = 0;

    /**
     * 输出缓冲区
     */
    private StringBuilder output = new StringBuilder();

    /**
     * 状态栈（支持嵌套状态恢复）
     */
    private List<S2State> stateStack = new ArrayList<>();

    /**
     * 上一个逻辑连接词（and/or），用于 ? 条件的 and/or 放入 <if> 内部
     */
    private String lastLogicConnector;

    /**
     * 当前扫描的 SQL 文本
     */
    private String input;

    public S2Context(String input) {
        this.input = input;
    }

    public S2State getState() {
        return state;
    }

    public void setState(S2State state) {
        this.state = state;
    }

    public void pushState(S2State newState) {
        this.stateStack.add(this.state);
        this.state = newState;
    }

    public S2State popState() {
        if (this.stateStack.isEmpty()) {
            return S2State.NORMAL;
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
        return ++this.position;
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

    public String getLastLogicConnector() {
        return lastLogicConnector;
    }

    public void setLastLogicConnector(String lastLogicConnector) {
        this.lastLogicConnector = lastLogicConnector;
    }

    public String getInput() {
        return input;
    }

    public int getInputLength() {
        return this.input.length();
    }

    /**
     * 从指定位置开始读取到字符串结束或遇到指定字符
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
}
