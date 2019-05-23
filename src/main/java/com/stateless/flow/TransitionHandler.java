package com.stateless.flow;

public interface TransitionHandler<STATE, OPERATION> {
    void execute(STATE from, STATE to, OPERATION operation) throws Exception;
}
