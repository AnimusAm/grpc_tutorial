package hr.smilebacksmile.grpc.calculator.primes_decomposition;

import com.google.common.primitives.UnsignedInteger;
import hr.smilebacksmile.fsm.state.StateType;

import java.util.Optional;
import java.util.function.Function;

public class DecomposerGramatics {

    private final static int BEGIN_WITH = 2;

    private static Optional<Long> getNextFactor(final Long number) {
        Long k = (long) BEGIN_WITH;
        Long result = null;
        Long remain = null;
        while(!Long.valueOf(0).equals(remain)) {
            remain = number % k;
            if (Long.valueOf(0).equals(remain)) { // k is the factor => next is found
                result = k;
                break;
            } else {
                k++;
            }
        }
        return Optional.ofNullable(result);
    }

    public static Function<DecomposerState, DecomposerState> getProcessingAction() {
        return (currentState) -> {

            final DecomposerState newState;
            if (!currentState.isFinal()) {
                final Long number = currentState.getRemainingNumber();

                if (Long.valueOf(1).equals(number) || Long.valueOf(0).equals(number) || Long.valueOf(-1).equals(number)) {
                    newState = new DecomposerState(StateType.END, number, 1L);
                } else {

                    final Optional<Long> nextFactor = getNextFactor(currentState.getRemainingNumber());

                    if (nextFactor.isPresent()) {
                        final Long factor = nextFactor.get();
                        if (factor.equals(currentState.getRemainingNumber())) {
                            newState = new DecomposerState(StateType.END, number, Math.abs(factor));
                        } else {
                            newState = new DecomposerState(StateType.PROCESSING, number/factor,  Math.abs(factor));
                        }
                    } else {
                        newState = new DecomposerState(StateType.END, number, null);
                    }
                }
            } else {
                newState = currentState;
            }
            return newState;
        };
    }
}
