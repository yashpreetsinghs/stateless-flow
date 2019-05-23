package com.stateless.flow.repository;

import com.stateless.flow.TransitionHandler;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class StateTransitionRepository<STATE, OPERATION> {

    private Map<STATE, Map<OPERATION, Pair<STATE, TransitionHandler>>> stateTransitionRepository;

    public StateTransitionRepository() {
        if (stateTransitionRepository == null) stateTransitionRepository = new HashMap<>();
    }

    public Map<OPERATION, Pair<STATE, TransitionHandler>> findAllTransitions(STATE from) {
        return stateTransitionRepository.getOrDefault(from, null);
    }

    private Map<OPERATION, Pair<STATE, TransitionHandler>> getAllTransitions(STATE from) {
        return stateTransitionRepository.getOrDefault(from, new HashMap<>());
    }

    public void saveTransition(STATE from, STATE to, OPERATION operation, TransitionHandler transitionHandler) {
        Map<OPERATION, Pair<STATE, TransitionHandler>> transitionMap = getAllTransitions(from);
        Pair<STATE, TransitionHandler> stateTransitionHandlerPair = new Pair<STATE, TransitionHandler>(to, transitionHandler);
        transitionMap.put(operation, stateTransitionHandlerPair);
        stateTransitionRepository.put(from, transitionMap);
    }
}
