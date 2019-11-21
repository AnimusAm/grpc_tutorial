package hr.smilebacksmile.grpc.calculator.util;

import hr.smilebacksmile.fsm.state.impl.MachineState;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;

public class CustomDecomposerTest {

    @Test
    public void incrementalyDecomposeOneTest() {

        final Long number = 1L;

        final List<Long> factors = new PrimeNumberIncrementalDecomposer(number)
                .until(MachineState::isFinal)
                .unwrap()
                .collect(Collectors.toList());

        assertThat(factors, IsEmptyIterable.emptyIterable());
        System.out.println("incrementalyDecomposeOneTest: " + factors.toString());
    }

    @Test
    public void incrementalyDecomposeZeroTest() {

        final Long number = 0L;

        final List<Long> factors = new PrimeNumberIncrementalDecomposer(number)
                .until(MachineState::isFinal)
                .unwrap()
                .collect(Collectors.toList());

        assertThat(factors, IsEmptyIterable.emptyIterable());
        System.out.println("incrementalyDecomposeZeroTest: " + factors.toString());

    }

    @Test
    public void incrementalyDecomposeNegativeTest() {

        final Long number = -4L;

        final List<Long> factors = new PrimeNumberIncrementalDecomposer(number)
                .until(MachineState::isFinal)
                .unwrap()
                .collect(Collectors.toList());

        assertThat(factors, IsIterableWithSize.iterableWithSize(2));
        assertThat(factors, IsIterableContainingInOrder.contains(2L, 2L));
        System.out.println("incrementalyDecomposeNegativeTest: " + factors.toString());
    }

    @Test
    public void incrementalyDecomposePrimeTest() {

        final Long number = 13L;

        final List<Long> factors = new PrimeNumberIncrementalDecomposer(number)
                .until(MachineState::isFinal)
                .unwrap()
                .collect(Collectors.toList());

        assertThat(factors, IsIterableWithSize.iterableWithSize(1));
        assertThat(factors, IsIterableContainingInOrder.contains(13L));
        System.out.println("incrementalyDecomposePrimeTest: " + factors.toString());

    }

    @Test
    public void incrementalyDecomposeLargeTest() {

        final Long number = 1453679L;

        final List<Long> factors = new PrimeNumberIncrementalDecomposer(number)
                .until(MachineState::isFinal)
                .unwrap()
                .collect(Collectors.toList());

        assertThat(factors, IsIterableWithSize.iterableWithSize(2));
        assertThat(factors, IsIterableContainingInOrder.contains(79L, 18401L));
        System.out.println("incrementalyDecomposeLargeTest: " + factors.toString());

    }

    @Test
    public void incrementalyDecomposeUsualTest() {

        final Long number = 120L;

        final List<Long> factors = new PrimeNumberIncrementalDecomposer(number)
                .until(MachineState::isFinal)
                .unwrap()
                .collect(Collectors.toList());

        assertThat(factors, IsIterableWithSize.iterableWithSize(5));
        assertThat(factors, IsIterableContainingInOrder.contains(2L, 2L, 2L, 3L, 5L));
        System.out.println("incrementalyDecomposeUsualTest: " + factors.toString());

    }
}
