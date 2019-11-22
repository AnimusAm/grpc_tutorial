package hr.smilebacksmile.grpc.calculator.average;

import hr.smilebacksmile.fsm.machine.StateMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Stream;

public class StatisticsMachine<T> extends StateMachine<StatisticsState<T>> {

    private final Stack<StatisticsState<T>> statesTrace;

    public StatisticsMachine() {
        super(new StatisticsState<>());
        this.statesTrace = new Stack<>();
    }

    @Override
    public StatisticsState<T> doTransition(final Function<StatisticsState<T>, StatisticsState<T>> action) {
       statesTrace.push(super.doTransition(action));
       return statesTrace.peek();
    }

    @Override
    public <V> V doTransitionAndThen(final Function<StatisticsState<T>, StatisticsState<T>> action, final Function<StatisticsState<T>, V> after) {
        return super.doTransitionAndThen(action, after);
    }

    public Stream<StatisticsState<T>> rewind() {
        this.currentState = new StatisticsState<>();
        final List<StatisticsState<T>> states  = new ArrayList<>(statesTrace);
        this.statesTrace.clear();
        return states.stream();
    }

    public Stream<StatisticsState<T>> takeSnapshot() {
        return this.statesTrace.stream();
    }

}
