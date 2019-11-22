package hr.smilebacksmile.grpc.calculator.util;

import org.junit.Assert;
import org.junit.Test;

public class StatisticsCalculatorTest {

    @Test
    public void getAverageOfAllPositiveIntegers() {
        final IntegerStatisticsCalculator avgCalculator = new IntegerStatisticsCalculator();

        avgCalculator.progress(1);
        avgCalculator.progress(10);
        avgCalculator.progress(100);
        avgCalculator.progress(1000);

        Assert.assertEquals(277.75, avgCalculator.avg(), 0);

    }


    @Test
    public void getAverageOfPositiveAndNegativeIntegers() {
        final IntegerStatisticsCalculator avgCalculator = new IntegerStatisticsCalculator();

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

    @Test
    public void getCurrentMaximumOfPositiveAndNegativeIntegers() {
        final IntegerStatisticsCalculator maxCalculator = new IntegerStatisticsCalculator();

        maxCalculator.progress(12);

        Assert.assertEquals(Integer.valueOf(12), maxCalculator.currentMaximum());

        maxCalculator.progress(-1);
        maxCalculator.progress(5);

        Assert.assertEquals(Integer.valueOf(12), maxCalculator.currentMaximum());

        maxCalculator.progress(14);

        Assert.assertEquals(Integer.valueOf(14), maxCalculator.currentMaximum());

    }
}
