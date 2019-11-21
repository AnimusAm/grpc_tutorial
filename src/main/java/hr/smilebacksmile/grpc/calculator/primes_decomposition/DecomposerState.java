package hr.smilebacksmile.grpc.calculator.primes_decomposition;

import hr.smilebacksmile.fsm.state.StateType;
import hr.smilebacksmile.fsm.state.impl.MachineState;

public class DecomposerState extends MachineState {

    protected final Long remainingNumber;
    protected final Long factor;

    public DecomposerState(final StateType type, final Long remainingNumber, final Long factor) {
        super(type);
        this.remainingNumber = remainingNumber;
        this.factor = factor;
    }

    public Long getRemainingNumber() {
        return remainingNumber;
    }

    public Long getFactor() {
        return factor;
    }
}
