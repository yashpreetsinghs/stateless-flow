package com.stateless.flow;

import com.stateless.flow.exceptions.InvalidOperationException;
import com.stateless.flow.exceptions.InvalidStateException;
import com.stateless.flow.exceptions.NoSuchTransitionException;
import com.stateless.flow.repository.StateTransitionRepository;
import javafx.util.Pair;

import java.util.Map;
import java.util.Set;

import static com.stateless.flow.common.Constants.*;

public class StateMachine<STATE, OPERATION> {

    public static StateMachine builder() {
        return new StateMachine();
    }

    public StateMachine build() {
        return this;
    }

    private class StateTransitions {
        StateTransitionRepository stateTransitionRepository = new StateTransitionRepository();

        private void addTransition(STATE from, STATE to, OPERATION operation, TransitionHandler transitionHandler) {
            stateTransitionRepository.saveTransition(from, to, operation, transitionHandler);
        }

        private void addTransition(STATE from, STATE to, OPERATION operation) throws Exception {
            addTransition(from, to, operation, null);
        }

        private Boolean hasTransition(STATE state, OPERATION operation) {
            Map<OPERATION, Pair<STATE, TransitionHandler>> transitionMap = stateTransitionRepository.findAllTransitions(state);
            return transitionMap == null ? Boolean.FALSE : (transitionMap.getOrDefault(operation, null) == null ? Boolean.FALSE : Boolean.TRUE);
        }

        private void transit(STATE state, OPERATION operation) throws Exception {
            Pair<STATE, TransitionHandler> nextState = getTransitionStateAndHandler(state, operation);
            TransitionHandler stateTransitionHandler = nextState.getValue();
            if (stateTransitionHandler == null) {
                throw new NoSuchTransitionException(String.format(NO_TRANSITION_HANDLER_DEFINED, state, operation));
            }
            stateTransitionHandler.execute(state, nextState.getKey(), operation);
        }

        private STATE getTransition(STATE state, OPERATION operation) throws NoSuchTransitionException {
            Pair<STATE, TransitionHandler> nextState = getTransitionStateAndHandler(state, operation);
            return nextState.getKey();
        }

        private Pair<STATE, TransitionHandler> getTransitionStateAndHandler(STATE state, OPERATION operation) throws NoSuchTransitionException {
            Map<OPERATION, Pair<STATE, TransitionHandler>> transitionMap = stateTransitionRepository.findAllTransitions(state);
            if (transitionMap == null) {
                throw new NoSuchTransitionException(String.format(NOT_TRANSITION_CONFIGURED, state));
            }
            Pair<STATE, TransitionHandler> nextState = transitionMap.getOrDefault(operation, null);
            if (nextState == null) {
                throw new NoSuchTransitionException(String.format(NOT_A_VALID_TRANSITION, state, operation));
            }
            return nextState;
        }
    }

    private Set<STATE> availableStates;
    private Set<OPERATION> availableOperations;
    private StateTransitions stateTransitions;

    private Boolean isValidState(STATE state) {
        return availableStates.contains(state);
    }

    private Boolean isValidOperation(OPERATION OPERATION) {
        return availableOperations.contains(OPERATION);
    }

    public StateMachine states(Set<STATE> availableStates) {
        this.availableStates = availableStates;
        return this;
    }

    public StateMachine operations(Set<OPERATION> availableOperations) {
        this.availableOperations = availableOperations;
        return this;
    }

    private StateMachine setStateTransitions() {
        if (stateTransitions == null)
            stateTransitions = new StateTransitions();
        return this;
    }

    private void isValidTransition(STATE from, STATE to, OPERATION OPERATION) throws Exception {
        if (!isValidState(from)) {
            throw new InvalidStateException(String.format(INVALID_STATE, from));
        }
        if (!isValidState(to)) {
            throw new InvalidStateException(String.format(INVALID_STATE, to));
        }
        if (!isValidOperation(OPERATION)) {
            throw new InvalidOperationException(String.format(INVALID_OPERATION, OPERATION.toString()));
        }
    }

    public StateMachine transition(STATE from, STATE to, OPERATION operation, TransitionHandler transitionHandler) throws Exception {
        setStateTransitions();
        stateTransitions.addTransition(from, to, operation, transitionHandler);
        return this;
    }

    public StateMachine transition(STATE from, STATE to, OPERATION operation) throws Exception {
        setStateTransitions();
        stateTransitions.addTransition(from, to, operation);
        return this;
    }

    public Boolean hasNext(STATE state, OPERATION operation) {
        setStateTransitions();
        return stateTransitions.hasTransition(state, operation);
    }

    public STATE next(STATE state, OPERATION operation) throws NoSuchTransitionException {
        setStateTransitions();
        return stateTransitions.getTransition(state, operation);
    }

    public STATE moveToNext(STATE state, OPERATION operation) throws Exception {
        setStateTransitions();
        stateTransitions.transit(state, operation);
        return stateTransitions.getTransition(state, operation);
    }
}