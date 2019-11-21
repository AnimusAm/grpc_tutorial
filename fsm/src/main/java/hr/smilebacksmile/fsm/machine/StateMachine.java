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

    public <V> T doTransitionAndThen(final Function<T, T> action, Function<? super T, ? extends V> after) {
        currentState = action.apply(currentState);
        action.andThen(after);
        return this.currentState;
    }

    public boolean isFinal() {
        return this.currentState.isFinal();
    }
}
