package hr.smilebacksmile.grpc.calculator.fsm;

import hr.smilebacksmile.grpc.calculator.primes_decomposition.DecomposerGramatics;
import hr.smilebacksmile.grpc.calculator.primes_decomposition.DecomposerMachine;
import hr.smilebacksmile.grpc.calculator.primes_decomposition.DecomposerState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DecomposerStateMachineTest {

    @Test
    public void incrementalyDecomposeOneTest() {

        final Long number = 1L;

        final DecomposerMachine stateMachine = new DecomposerMachine(number);
        final DecomposerState state = stateMachine.doTransition(DecomposerGramatics.getProcessingAction());

        assertTrue(stateMachine.isFinal());
        assertTrue(state.isFinal());

        assertEquals(number, state.getRemainingNumber());

    }

    @Test
    public void incrementalyDecomposeZeroTest() {

        final Long number = 0L;

        final DecomposerMachine stateMachine = new DecomposerMachine(number);
        final DecomposerState state = stateMachine.doTransition(DecomposerGramatics.getProcessingAction());

        assertTrue(stateMachine.isFinal());
        assertTrue(state.isFinal());

        assertEquals(number, state.getRemainingNumber());

    }

    @Test
    public void incrementalyDecomposeNegativeTest() {

        final Long number = -4L;

        final DecomposerMachine stateMachine = new DecomposerMachine(number);
        DecomposerState state = stateMachine.doTransition(DecomposerGramatics.getProcessingAction());

        assertTrue(!state.isFinal() && !state.isInital());
        assertEquals(Long.valueOf(-2L), state.getRemainingNumber());

        state = stateMachine.doTransition(DecomposerGramatics.getProcessingAction());
        assertTrue(!state.isFinal() && !state.isInital());
        assertEquals(Long.valueOf(-1L), state.getRemainingNumber());

        state = stateMachine.doTransition(DecomposerGramatics.getProcessingAction());
        assertTrue(state.isFinal());
        assertEquals(Long.valueOf(-1L), state.getRemainingNumber());

    }

    @Test
    public void incrementalyDecomposePrimeTest() {

        final Long number = 13L;

        final DecomposerMachine stateMachine = new DecomposerMachine(number);
        DecomposerState state = stateMachine.doTransition(DecomposerGramatics.getProcessingAction());

        assertTrue(stateMachine.isFinal());
        assertTrue(state.isFinal());

        assertEquals(number, state.getRemainingNumber());

    }
}
