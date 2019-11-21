package hr.smilebacksmile.grpc.calculator.util;

import org.junit.Assert;
import org.junit.Test;

public class AverageCalculatorTest {

    @Test
    public void getAverageOfAllPositiveIntegers() {
        final AverageCalculator avgCalculator = new AverageCalculator();

        avgCalculator.progress(1);
        avgCalculator.progress(10);
        avgCalculator.progress(100);
        avgCalculator.progress(1000);

        Assert.assertEquals(277.75, avgCalculator.avg(), 0);

    }


    @Test
    public void getAverageOfPositiveAndNegativeIntegers() {
        final AverageCalculator avgCalculator = new AverageCalculator();

        avgCalculator.progress(1);
        avgCalculator.progress(-1);
        avgCalculator.progress(18);
        avgCalculator.progress(-17);
        avgCalculator.progress(16);
        avgCalculator.progress(-15);
        avgCalculator.progress(874565);
        avgCalculator.progress(-874570);
        avgCalculator.progress(1);

        Assert.assertEquals(-0.222, avgCalculator.avg(), 0.003);

    }
}
