package com.mybatisgx.syntax;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

public abstract class MethodNameParserBase extends Parser {

    public MethodNameParserBase(TokenStream input) {
        super(input);
    }

    /**
     * 判断 business semantic 是否结束
     */
    protected boolean isBusinessSemanticEnd() {
        int nextToken = _input.LA(1);
        return nextToken == MethodNameLexer.LIMIT_TOP
                || nextToken == MethodNameLexer.BY
                || nextToken == MethodNameLexer.ORDER_BY
                || nextToken == Token.EOF;
    }
}