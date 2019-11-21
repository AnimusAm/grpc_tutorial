package hr.smilebacksmile.grpc.calculator.primes_decomposition;

import hr.smilebacksmile.fsm.machine.StateMachine;
import hr.smilebacksmile.fsm.state.StateType;

import java.util.function.Function;

public class DecomposerMachine extends StateMachine<DecomposerState> {

    public DecomposerMachine(final Long input) {
        super(new DecomposerState(StateType.INITIAL, input, 1L));
    }

    @Override
    public DecomposerState doTransition(final Function<DecomposerState, DecomposerState> action) {
       return super.doTransition(action);
    }




}
