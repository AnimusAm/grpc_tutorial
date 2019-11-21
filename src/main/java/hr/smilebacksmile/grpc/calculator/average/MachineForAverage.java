package hr.smilebacksmile.grpc.calculator.average;

import hr.smilebacksmile.fsm.machine.StateMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MachineForAverage extends StateMachine<StateForAverage> {

    private final Stack<StateForAverage> statesTrace;

    public MachineForAverage() {
        super(new StateForAverage());
        this.statesTrace = new Stack<>();
    }

    @Override
    public StateForAverage doTransition(final Function<StateForAverage, StateForAverage> action) {
       statesTrace.push(super.doTransition(action));
       return statesTrace.peek();
    }

    @Override
    public <V> V doTransitionAndThen(final Function<StateForAverage, StateForAverage> action, final Function<StateForAverage, V> after) {
        return super.doTransitionAndThen(action, after);
    }

    public Stream<StateForAverage> rewind() {
        this.currentState = new StateForAverage();
        final List<StateForAverage> states  = new ArrayList<>(statesTrace);
        this.statesTrace.clear();
        return states.stream();
    }

}
