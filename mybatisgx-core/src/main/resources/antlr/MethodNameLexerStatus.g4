lexer grammar MethodNameLexerStatus;

@lexer::members {
    private boolean afterSelect = false;
    private boolean afterBy = false;

    @Override
    public Token emit() {
        Token token = super.emit();
        switch(token.getType()) {
            case SELECT_ACTION:
                afterSelect = true;
                afterBy = false;
                break;
            case BY:
                afterSelect = true;
                afterBy = true;
                break;
            default:
                // 其它行为
        }
        return token;
    }

    public boolean isIgnore() {
        return afterSelect && afterBy;
    }
}