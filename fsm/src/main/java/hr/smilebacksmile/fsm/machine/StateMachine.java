package hr.smilebacksmile.fsm.machine;


import hr.smilebacksmile.fsm.state.State;

import java.util.function.Function;

public class StateMachine<T extends State> {
    protected T currentState;

    public StateMachine(T initialState) {
        this.currentState = initialState;
    }

    public T doTransition(final Function<T, T> action) {
        currentState = action.apply(currentState);
        return this.currentState;
    }

    public <V> V doTransitionAndThen(final Function<T, T> action, Function<T, V> after) {
        currentState = action.apply(currentState);
        return action.andThen(after).apply(currentState);
    }

    public boolean isFinal() {
        return this.currentState.isFinal();
    }
}
