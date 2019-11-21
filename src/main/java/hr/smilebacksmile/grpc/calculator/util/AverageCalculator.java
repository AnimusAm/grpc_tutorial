package hr.smilebacksmile.grpc.calculator.util;

import hr.smilebacksmile.fsm.state.StateType;
import hr.smilebacksmile.grpc.calculator.average.MachineForAverage;
import hr.smilebacksmile.grpc.calculator.average.StateForAverage;

import java.util.stream.Stream;

public class AverageCalculator {

    final MachineForAverage machine = new MachineForAverage();

    public void progress(final Integer number) {
        machine.doTransition((currentState) -> new StateForAverage(StateType.PROCESSING, number));
    }

    public Double avg() {

        Stream<StateForAverage> states =
            machine.doTransitionAndThen((currentState) -> new StateForAverage(StateType.END, null), (newState) -> machine.rewind());

        return states.filter(s -> !s.isFinal() && !s.isInital()).map(StateForAverage::getNumber).mapToInt((n) -> n).summaryStatistics().getAverage();
    }



}
