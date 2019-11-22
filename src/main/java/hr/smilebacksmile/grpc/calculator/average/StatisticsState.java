package hr.smilebacksmile.grpc.calculator.average;

import hr.smilebacksmile.fsm.state.StateType;
import hr.smilebacksmile.fsm.state.impl.MachineState;

public class StatisticsState<T> extends MachineState {

    protected final T number;

    public StatisticsState(final StateType type, final T number) {
        super(type);
        this.number = number;
    }

    public StatisticsState() {
        super(StateType.INITIAL);
        this.number = null;
    }

    public T getNumber() {
        return number;
    }
}
