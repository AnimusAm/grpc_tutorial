package hr.smilebacksmile.fsm.state.impl;

import hr.smilebacksmile.fsm.state.State;
import hr.smilebacksmile.fsm.state.StateType;

public class MachineState implements State {
    protected final StateType type;

    public MachineState(StateType type) {
        this.type = type;
    }

    @Override
    public boolean isFinal() {
        return this.type == StateType.END;
    }

    @Override
    public boolean isInital() {
        return this.type == StateType.INITIAL;
    }
}
