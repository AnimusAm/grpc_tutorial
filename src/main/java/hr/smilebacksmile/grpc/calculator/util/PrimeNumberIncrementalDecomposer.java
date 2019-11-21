package hr.smilebacksmile.grpc.calculator.util;

import hr.smilebacksmile.grpc.calculator.primes_decomposition.DecomposerGramatics;
import hr.smilebacksmile.grpc.calculator.primes_decomposition.DecomposerMachine;
import hr.smilebacksmile.grpc.calculator.primes_decomposition.DecomposerState;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PrimeNumberIncrementalDecomposer {

    public interface WhenDoneThen {
        Stream<Long> unwrap();
    }

    private final Long input;

    public PrimeNumberIncrementalDecomposer(final Long input) {
        this.input = input;
    }

    public WhenDoneThen until(final Predicate<DecomposerState> untilCondition) {
        return new MachineStopped(this.input, untilCondition);
    }

    private class MachineStopped implements WhenDoneThen {
        private final DecomposerMachine machine;
        private final Predicate<DecomposerState> endingPredicate;


        public MachineStopped(final Long input, final Predicate<DecomposerState> untilCondition) {
            this.machine = new DecomposerMachine(input);
            this.endingPredicate = untilCondition;
        }

        @Override
        public Stream<Long> unwrap() {
            final List<Long> factors = new LinkedList<>();

            final Function<DecomposerState, DecomposerState> transitionFunction = DecomposerGramatics.getProcessingAction();

            DecomposerState state = machine.doTransition(transitionFunction);
            factors.add(state.getFactor());

            while(!endingPredicate.test(state)) {
                state = machine.doTransition(transitionFunction);
                factors.add(state.getFactor());
            }
            return factors.stream().filter(f -> !Long.valueOf(1).equals(f));
        }
    }


}
