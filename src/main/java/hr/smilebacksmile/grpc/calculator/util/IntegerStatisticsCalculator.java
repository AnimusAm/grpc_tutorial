package hr.smilebacksmile.grpc.calculator.util;

import hr.smilebacksmile.fsm.state.StateType;
import hr.smilebacksmile.grpc.calculator.average.StatisticsMachine;
import hr.smilebacksmile.grpc.calculator.average.StatisticsState;

import java.util.IntSummaryStatistics;
import java.util.function.Function;
import java.util.stream.Stream;

public class IntegerStatisticsCalculator {

    final StatisticsMachine<Integer> machine = new StatisticsMachine<>();

    public void progress(final Integer number) {
        machine.doTransition((currentState) -> new StatisticsState<>(StateType.PROCESSING, number));
    }

    private IntSummaryStatistics getStatistics(final boolean withRewinding) {
        final Function<StatisticsState<Integer>, Stream<StatisticsState<Integer>>> after;

        if (withRewinding) {
            after = (newState) -> machine.rewind();
        } else {
            after = (newState) -> machine.takeSnapshot();
        }

        Stream<StatisticsState<Integer>> states =
                machine.doTransitionAndThen((currentState) -> new StatisticsState<>(StateType.END, null), after);

        return states.filter(s -> !s.isFinal() && !s.isInital()).map(StatisticsState::getNumber).mapToInt((n) -> n).summaryStatistics();
    }

    public Double avg() {
        return this.getStatistics(true).getAverage();
    }

    public Integer currentMaximum() {
        return this.getStatistics(false).getMax();
    }

    public void reset() {
        machine.rewind();
    }
}
