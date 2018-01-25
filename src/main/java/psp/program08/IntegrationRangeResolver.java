/*
SUMMARY
Program: PSP Program 08
Name: Satoshi Muraoka
Date: 2017/01/24
Description: 「追加」、「再利用」、「修正」規模と履歴データで多重回帰分析を行い、
             見積値とその70%予測区間を計算する。
 */
package psp.program08;

import java.util.Properties;

/**
 * 初期化時に指定された条件に基づいて、積分範囲の探索処理を実行する。
 *
 * @author smuraoka
 */
public class IntegrationRangeResolver {

    //探索処理開始前に初期化する。
    private final double target; //期待する積分値
    private final TDistribution tDistribution; //数値積分対象のt分布関数
    private final double acceptableError; //許容誤差
    private final int initialNumberOfSegments; //積分範囲の初期分割数

    //探索処理終了後に値が設定される。
    private Double answer = null; //探索処理で得られた解（積分範囲）
    private Double actual = null; //探索処理が終了した時点の積分値

    //@method_def_start: IntegrationRangeResolver_Properties
    /**
     * 設定ファイルに記述されている内容に従って、探索処理を初期化する。
     *
     * @param props 設定ファイル
     */
    public IntegrationRangeResolver(Properties props) {
        this.target = Double.parseDouble(
                props.getProperty("psp.program6.predictionValue"));
        this.tDistribution = new TDistribution(Integer.parseInt(
                props.getProperty("psp.program6.degreeOfFreedom")));
        this.acceptableError = Double.parseDouble(
                props.getProperty("psp.program6.acceptableError"));
        this.initialNumberOfSegments = Integer.parseInt(
                props.getProperty("psp.program6.initialSegment"));
    }
    //@method_def_end

    //@method_def_start: IntegrationRangeResolver_double_int_double_int
    /**
     * 初期化処理
     *
     * @param target 期待する積分値
     * @param degreeOfFreedom t分布関数の自由度
     * @param error 数値積分処理及び積分範囲の探索に使用する許容誤差
     * @param segments 数値積分処理に使用する積分範囲の初期分割数
     */
    public IntegrationRangeResolver(
            double target, int degreeOfFreedom, double error, int segments) {
        this.target = target;
        this.tDistribution = new TDistribution(degreeOfFreedom);
        this.acceptableError = error;
        this.initialNumberOfSegments = segments;
    }
    //@method_def_end

    //@method_def_start: resolve
    /**
     * 積分範囲の探索処理
     * <p>
     * 実行結果は属性値として保存されるので、この処理の実行後に、属性値を取得する操作を使用して、 結果を得ることができる。
     * </p>
     */
    public void resolve() {
        double trialX = 1.0; //試しの値
        double deltaX = trialX; //増減値
        boolean isIncreasing = true; //増減傾向

        //増減値の絶対値が許容誤差の１０分の１以上の間ループする。
        while (Math.abs(deltaX) >= acceptableError / 10) {

            //試しの値を更新する。
            if (actual != null) {
                if (isIncreasing && actual < target) {
                    trialX += deltaX;
                } else if (isIncreasing && target <= actual) {
                    deltaX /= 2;
                    trialX -= deltaX;
                    isIncreasing = false;
                } else if (!isIncreasing && actual <= target) {
                    deltaX /= 2;
                    trialX += deltaX;
                    isIncreasing = true;
                } else if (!isIncreasing && target < actual) {
                    trialX -= deltaX;
                }
            }

            //試しの値を用いて積分値を計算する。
            actual = PSPMath.integrate(tDistribution, trialX,
                    initialNumberOfSegments, acceptableError);
        }

        //解を設定する。
        answer = trialX;
    }
    //@method_def_end

    //@method_def_start: getAnswer
    /**
     * 探索処理で得られた解（積分範囲）を得る。
     *
     * @return 探索処理で得られた解（積分範囲）
     */
    public Double getAnswer() {
        return answer;
    }
    //@method_def_end

    //@method_def_start: getActual
    /**
     * 探索処理が終了した時点の積分値を得る。
     *
     * @return 探索処理が終了した時点の積分値
     */
    public Double getActual() {
        return actual;
    }
    //@method_def_end

    //@method_def_start: getTarget
    /**
     * 期待する積分値を得る。
     *
     * @return 期待する積分値
     */
    public double getTarget() {
        return target;
    }
    //@method_def_end

    //@method_def_start: gettDistribution
    /**
     * 数値積分対象のt分布関数を得る。
     *
     * @return 数値積分対象のt分布関数
     */
    public TDistribution gettDistribution() {
        return tDistribution;
    }
    //@method_def_end

    //@method_def_start: getAcceptableError
    /**
     * 数値積分処理および積分範囲探索に使用する許容誤差を得る。
     *
     * @return 許容誤差
     */
    public double getAcceptableError() {
        return acceptableError;
    }
    //@method_def_end

    //@method_def_start: getInitialNumberOfSegments
    /**
     * 数値積分処理で使用する積分範囲の初期分割数を得る。
     *
     * @return 初期分割数
     */
    public int getInitialNumberOfSegments() {
        return initialNumberOfSegments;
    }
    //@method_def_end

}
