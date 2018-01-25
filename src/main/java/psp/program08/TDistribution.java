/*
SUMMARY
Program: PSP Program 08
Name: Satoshi Muraoka
Date: 2017/01/24
Description: 「追加」、「再利用」、「修正」規模と履歴データで多重回帰分析を行い、
             見積値とその70%予測区間を計算する。
 */
package psp.program08;

/**
 * t分布関数を実装する
 *
 * @author smuraoka
 */
public class TDistribution implements Function {

    private final Double degreeOfFreedom;
    private Double coefficient = null;
    private Double exponent = null;

    //@method_def_start: TDistribution
    /**
     * t分布関数の自由度を設定して初期化する。
     *
     * @param degreeOfFreedum t分布関数の自由度
     */
    public TDistribution(int degreeOfFreedum) {
        this.degreeOfFreedom = new Double(degreeOfFreedum);
    }
    //@method_def_end

    //@method_def_start: apply
    /**
     * t分布関数に{@code x}を代入して結果を得る。
     *
     * @param x 関数に代入する値
     * @return　結果
     */
    @Override
    public double apply(double x) {
        final double temp1 = 1 + (Math.pow(x, 2) / getDegreeOfFreedom());
        final double temp2 = Math.pow(temp1, getExponent());
        return getCoefficient() * temp2;
    }
    //@method_def_end

    //@method_def_start: getCoefficient
    /**
     * t分布関数の計算に用いる係数の値を取得する。
     *
     * @return t分布関数の計算に用いる係数の値
     */
    public double getCoefficient() {
        if (coefficient == null) {
            //分子を計算
            final double temp11 = (getDegreeOfFreedom() + 1.0) / 2.0;
            final double temp12 = PSPMath.gamma(temp11);

            //分母を計算
            final double temp21 = getDegreeOfFreedom() * Math.PI;
            final double temp22 = Math.pow(temp21, 0.5);
            final double temp23 = PSPMath.gamma(getDegreeOfFreedom() / 2.0);
            final double temp24 = temp22 * temp23;

            //係数を設定
            coefficient = temp12 / temp24;

        }
        return coefficient;
    }
    //@method_def_end

    //@method_def_start: getDegreeOfFreedom
    /**
     * t分布関数の計算に用いる自由度の値を取得する。
     *
     * @return t分布関数の計算に用いる自由度の値
     */
    public double getDegreeOfFreedom() {
        return degreeOfFreedom;
    }
    //@method_def_end

    //@method_def_start: getExponent
    /**
     * t分布関数の計算に用いる指数の値を取得する。
     *
     * @return t分布関数の計算に用いる指数の値
     */
    public double getExponent() {
        if (exponent == null) {
            exponent = -1.0 * (getDegreeOfFreedom() + 1.0) / 2.0;
        }
        return exponent;
    }
    //@method_def_end

}
