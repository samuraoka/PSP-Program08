package psp.program08;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matcher;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static psp.program08.GaussianElimination.lsolve;

/**
 *
 * @author smuraoka
 */
public class ProtoType {

    @Test
    public void sampleClient() {
        int N = 3;
        double[][] A = {{0, 1, 1},
        {2, 4, -2},
        {0, 3, 15}
        };
        double[] b = {4, 2, 36};
        Double[] x = lsolve(A, b);

        List<Matcher<? super Double>> matchers = new ArrayList<>();
        matchers.add(closeTo(-1.0, 0.1));
        matchers.add(closeTo(2.0, 0.1));
        matchers.add(closeTo(2.0, 0.1));

        assertThat(x, is(arrayContaining(matchers)));
    }

    @Test
    public void sampleClient2() {
        int N = 3;
        double[][] A = {{0, 1, 1},
        {2, 4, 2},
        {0, 3, 15}
        };
        double[] b = {4, 2, 36};
        Double[] x = lsolve(A, b);

        List<Matcher<? super Double>> matchers = new ArrayList<>();
        matchers.add(closeTo(-5.0, 0.1));
        matchers.add(closeTo(2.0, 0.1));
        matchers.add(closeTo(2.0, 0.1));

        assertThat(x, is(arrayContaining(matchers)));
    }

    @Test
    public void testCase1() {
        int N = 4;
        double[][] A = {{6, 1670, 355, 149},
        {1670, 641720, 114071, 35495},
        {355, 114071, 46343, 20819},
        {149, 35495, 20819, 10557},};
        double[] b = {138.1, 49225.1, 11202, 4179.4};
        Double[] x = lsolve(A, b);
        double E = 1.0E-11;

        List<Matcher<? super Double>> matchers = new ArrayList<>();
        matchers.add(closeTo(0.5664574696007210, E));
        matchers.add(closeTo(0.0653292546942366, E));
        matchers.add(closeTo(0.0087187361945773, E));
        matchers.add(closeTo(0.1510486476103670, E));

        assertThat(x, is(arrayContaining(matchers)));
    }

    @Test
    public void testCase2() {
        int N = 4;
        double[][] A = {
            {6, 4863, 8761, 654},
            {4863, 4521899, 8519938, 620707},
            {8761, 8519938, 21022091, 905925},
            {654, 620707, 905925, 137902}
        };
        double[] b = {714, 667832, 1265493, 100583};
        Double[] x = lsolve(A, b);
        double E = 1.0E-11;

        List<Matcher<? super Double>> matchers = new ArrayList<>();
        matchers.add(closeTo(6.7013365363875400, E));
        matchers.add(closeTo(0.0783660367338677, E));
        matchers.add(closeTo(0.0150413311993448, E));
        matchers.add(closeTo(0.2460563325801470, E));

        assertThat(x, is(arrayContaining(matchers)));
    }

    @Test
    public void testFormat() {
        final DecimalFormat df4 = new DecimalFormat("0.0000");
        df4.setRoundingMode(RoundingMode.HALF_UP);
        double[] values = new double[]{
            6.7013365363875400,
            0.0783660367338677,
            0.0150413311993448,
            0.2460563325801470
        };
        String formattedString0 = String.format("%s",
                df4.format(values[0]));
        String formattedString1 = String.format("%s",
                df4.format(values[1]));
        String formattedString2 = String.format("%s",
                df4.format(values[2]));
        String formattedString3 = String.format("%s",
                df4.format(values[3]));
        assertThat(formattedString0, is("6.7013"));
        assertThat(formattedString1, is("0.0784"));
        assertThat(formattedString2, is("0.0150"));
        assertThat(formattedString3, is("0.2461"));
    }

    @Test
    public void testFormatDouble() {
        final DecimalFormat df4 = new DecimalFormat("0.0000");
        df4.setRoundingMode(RoundingMode.HALF_UP);
        Double[] values = new Double[]{
            0.5664574696007210,
            0.0653292546942366,
            0.0087187361945773,
            0.1510486476103670
        };
        String formattedString0 = String.format("%s",
                df4.format(values[0]));
        String formattedString1 = String.format("%s",
                df4.format(values[1]));
        String formattedString2 = String.format("%s",
                df4.format(values[2]));
        String formattedString3 = String.format("%s",
                df4.format(values[3]));
        assertThat(formattedString0, is("0.5665"));
        assertThat(formattedString1, is("0.0653"));
        assertThat(formattedString2, is("0.0087"));
        assertThat(formattedString3, is("0.1510"));
    }

}
