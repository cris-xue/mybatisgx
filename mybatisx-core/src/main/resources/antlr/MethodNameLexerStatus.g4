lexer grammar MethodNameLexerStatus;

@lexer::members {
    // 改进的状态跟踪
    private int context = 0; // 0=default, 1=afterSelect, 2=afterBy
    private boolean inCondition = false;

    @Override
    public Token emit() {
        Token token = super.emit();
        // 更新上下文状态
        switch(token.getType()) {
            case SELECT_ACTION:
                context = 1; // 进入 select 上下文
                inCondition = false;
                break;
            case BY:
                context = 2; // 进入 by 上下文
                inCondition = true; // 开始条件部分
                break;
            case AND:
            case FIELD:
            case OR:
                // 逻辑操作符保持 inCondition 状态
                break;
            default:
                if (inCondition) {
                    // 条件中的 token 保持状态
                } else if (context == 1) {
                    // 退出 select 上下文
                    context = 0;
                }
        }
        return token;
    }

    // 检查是否应该忽略特定词
    public boolean shouldIgnoreList() {
        // 只在 select 后且不在条件中时忽略
        return context == 1 && !inCondition;
    }
}