package hr.smilebacksmile.grpc.calculator.average;

import hr.smilebacksmile.fsm.state.StateType;
import hr.smilebacksmile.fsm.state.impl.MachineState;

public class StateForAverage extends MachineState {

    protected final Integer number;

    public StateForAverage(final StateType type, final Integer number) {
        super(type);
        this.number = number;
    }

    public StateForAverage() {
        super(StateType.INITIAL);
        this.number = null;
    }

    public Integer getNumber() {
        return number;
    }
}
