/*
SUMMARY
Program: PSP Program 08
Name: Satoshi Muraoka
Date: 2017/01/24
Description: 「追加」、「再利用」、「修正」規模と履歴データで多重回帰分析を行い、
             見積値とその70%予測区間を計算する。
 */
package psp.program08;

import java.io.IOException;

/**
 * 数値計算処理部
 *
 * <p>
 * PSPで使用する数値計算の実装するクラス
 * </p>
 *
 * @author smuraoka
 */
public class PSPMath {

    //@method_def_start: sum
    /**
     * 数値データ一覧の合計値を計算する。
     *
     * @param list 数値データ一覧
     * @return 数値データ一覧の合計値
     */
    public static Double sum(LinkedList list) {
        double sum = 0.0;
        Node n = list.getHeadNode();
        while (n != null) {
            sum += n.getValue();
            n = n.getNextNode();
        }
        return sum;
    }
    //@method_def_end

    //@method_def_start: mean
    /**
     * 数値データ一覧の平均値を計算する。
     *
     * @param list 数値データ一覧
     * @return 数値データ一覧の平均値
     */
    public static Double mean(LinkedList list) {
        if (list.getCount() == 0) {
            throw new IllegalArgumentException("number of data is 0");
        }
        return PSPMath.sum(list) / list.getCount();
    }
    //@method_def_end

    //@method_def_start: stddev
    /**
     * 数値データ一覧の標準偏差を計算する。
     *
     * @param list 数値データの一覧
     * @return 数値データ一覧の標準偏差
     */
    public static Double stddev(LinkedList list) {
        final double mean = PSPMath.mean(list);

        double sum_delta_squared = 0.0;
        Node n = list.getHeadNode();
        while (n != null) {
            final double value = n.getValue();
            sum_delta_squared += Math.pow(value - mean, 2.0);
            n = n.getNextNode();
        }

        return Math.sqrt(sum_delta_squared / (list.getCount() - 1));
    }
    //@method_def_end

    //@method_def_start: product
    /**
     * 2つの数値データ一覧の各要素の積の数値データ一覧を作成する。
     *
     * @param xValues 数値データ一覧
     * @param yValues 数値データ一覧
     * @return 各要素の積の数値データ一覧
     */
    public static LinkedList product(LinkedList xValues, LinkedList yValues) {
        if (xValues.getCount() != yValues.getCount()) {
            throw new IllegalArgumentException("Number of element not match: "
                    + "x=" + xValues.getCount() + ": y=" + yValues.getCount());
        }

        Node xValue = xValues.getHeadNode();
        Node yValue = yValues.getHeadNode();
        LinkedList xyValues = new LinkedList();
        while (xValue != null && yValue != null) {
            Node xyValue = new Node(xValue.getValue() * yValue.getValue());
            xyValues.add(xyValue);
            xValue = xValue.getNextNode();
            yValue = yValue.getNextNode();
        }

        return xyValues;
    }
    //@method_def_end

    //@method_def_start: divide
    /**
     * 2つの数値データ一覧の各要素の商の一覧を作成する。
     *
     * @param xValues 数値データ一覧
     * @param yValues 数値データ一覧
     * @return 各要素の商の数値データ一覧
     */
    public static LinkedList divide(LinkedList xValues, LinkedList yValues) {
        if (xValues.getCount() != yValues.getCount()) {
            throw new IllegalArgumentException("Number of element not match: "
                    + "x=" + xValues.getCount() + ": y=" + yValues.getCount());
        }

        Node xValue = xValues.getHeadNode();
        Node yValue = yValues.getHeadNode();
        final LinkedList resultValues = new LinkedList();
        while (xValue != null && yValue != null) {
            Node resultValue = new Node(xValue.getValue() / yValue.getValue());
            resultValues.add(resultValue);
            xValue = xValue.getNextNode();
            yValue = yValue.getNextNode();
        }

        return resultValues;
    }
    //@method_def_end

    //@method_def_start: ln
    /**
     * 数値データ一覧の各要素の自然対数値の一覧を作成する。
     *
     * @param values 数値データ一覧
     * @return 各要素の自然対数値の数値データ一覧
     */
    public static LinkedList ln(LinkedList values) {
        Node value = values.getHeadNode();
        final LinkedList resultValues = new LinkedList();
        while (value != null) {
            Node resultValue = new Node(Math.log(value.getValue()));
            resultValues.add(resultValue);
            value = value.getNextNode();
        }

        return resultValues;
    }
    //@method_def_end

    //@method_def_start: estimate
    /**
     * 見積プロキシ規模と多重回帰パラメータより改善された見積値を計算する
     *
     * @param data Probe計算用データセット
     * @return 改善された見積値
     * @throws java.io.IOException 履歴データの読み込みに失敗した場合
     */
    public static double estimate(ProbeDataSet data)
            throws IOException {
        //見積プロキシ規模
        final double w = data.getEstimatedProxySizes()[0];
        final double x = data.getEstimatedProxySizes()[1];
        final double y = data.getEstimatedProxySizes()[2];
        //多重回帰パラメータ
        final double b0 = data.getRegressionParameters()[0];
        final double b1 = data.getRegressionParameters()[1];
        final double b2 = data.getRegressionParameters()[2];
        final double b3 = data.getRegressionParameters()[3];
        //改善された見積値を計算して返す
        return b0 + (w * b1) + (x * b2) + (y * b3);
    }
    //@method_def_end

    //@method_def_start: gamma
    /**
     * ガンマ関数
     *
     * @param x 関数に代入する値
     * @return 結果
     */
    public static double gamma(double x) {
        double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
        double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1)
                + 24.01409822 / (x + 2) - 1.231739516 / (x + 3)
                + 0.00120858003 / (x + 4) - 0.00000536382 / (x + 5);
        return Math.exp(tmp + Math.log(ser * Math.sqrt(2 * Math.PI)));
    }
    //@method_def_end

    //@method_def_start: integrate
    /**
     * シンプソンの公式を用いて数値積分を行う。積分範囲は0から{@code x}までとする。
     * 積分範囲の分割数を増加させながら、許容誤差{@code error}を満たすまで計算を続行する。
     *
     * @param f 積分対象の関数
     * @param x 積分範囲、{@code  x > 0}
     * @param segment 積分範囲の分割数の初期値
     * @param error 許容誤差
     * @return 結果
     */
    public static double integrate(
            Function f, double x, int segment, double error) {
        Double p0 = null; //前回の計算結果
        Double p1 = null; //最新の計算結果

        while (p0 == null || p1 == null || Math.abs(p1 - p0) >= error) {
            p0 = p1; //前回の計算結果を保存
            final double step = x / segment; //積分領域を分割した際の幅

            /*
             * シンプソンの公式による数値計算
             */
            //1/3 terms
            double sum = 1.0 / 3.0 * (f.apply(0) + f.apply(x));
            //4/3 terms
            for (int i = 1; i < segment; i += 2) {
                sum += 4.0 / 3.0 * f.apply(i * step);
            }
            //2/3 terms
            for (int i = 2; i < segment; i += 2) {
                sum += 2.0 / 3.0 * f.apply(i * step);
            }

            p1 = sum * step; //最新の計算結果を保存
            segment *= 2; //分割数を２倍にする
        }

        return p1;
    }
    //@method_def_end

    //@method_def_start: calculateXForPredictionInterval
    /**
     * 予測区間の計算に使用する積分範囲xの値を計算する
     *
     * @param data Probe計算用データセット
     * @return 予測区間の計算に使用するxの値
     * @throws IOException 履歴データファイルの読み込みに失敗した場合
     */
    public static Double calculateXForPredictionInterval(ProbeDataSet data)
            throws IOException {
        final double target = data.getPredictionIntervalRate() / 2.0;
        final int degreeOfFreedom
                = data.getNumberOfHistoryData() - data.getDimension();
        final double error = data.getAcceptableError() / 100.0;
        final int segments = data.getInitialNumberOfSegments();
        final IntegrationRangeResolver resolver = new IntegrationRangeResolver(
                target, degreeOfFreedom, error, segments);
        resolver.resolve();
        return resolver.getAnswer();
    }
    //@method_def_end

    //@method_def_start: calculateSigmaForPredictionInterval
    /**
     * 予測区間の計算に使用する標準偏差を計算する
     *
     * @param data Probe計算用データセット
     * @return 予測区間の計算に使用する標準偏差
     * @throws IOException 履歴データファイルの読み込みに失敗した場合
     */
    public static Double calculateSigmaForPredictionInterval(
            ProbeDataSet data) throws IOException {
        final double sum = data.getSumForSigma();
        final double degreeOfFreedom
                = data.getNumberOfHistoryData() - data.getDimension();
        return Math.sqrt(sum / degreeOfFreedom);
    }
    //@method_def_end

    //@method_def_start: calculateSumForSigma
    /**
     * 予測区間の計算に使用する標準偏差で用いる総和を計算する
     *
     * @param data Probe計算用データセット
     * @return 予測区間の計算に使用する標準偏差で用いる総和
     * @throws java.io.IOException 履歴データの読み込みに失敗した場合
     */
    public static Double calculateSumForSigma(ProbeDataSet data)
            throws IOException {
        //数値一覧の取得
        final LinkedList wValues = data.getW();
        final LinkedList xValues = data.getX();
        final LinkedList yValues = data.getY();
        final LinkedList zValues = data.getZ();

        //多重回帰パラメータの取得
        final double b0 = data.getRegressionParameters()[0];
        final double b1 = data.getRegressionParameters()[1];
        final double b2 = data.getRegressionParameters()[2];
        final double b3 = data.getRegressionParameters()[3];

        //総和の計算処理
        double sum = 0.0;
        Node wValue = wValues.getHeadNode();
        Node xValue = xValues.getHeadNode();
        Node yValue = yValues.getHeadNode();
        Node zValue = zValues.getHeadNode();
        while (wValue != null && xValue != null
                && yValue != null && zValue != null) {
            final double w = wValue.getValue();
            final double x = xValue.getValue();
            final double y = yValue.getValue();
            final double z = zValue.getValue();
            sum += Math.pow(z - b0 - (b1 * w) - (b2 * x) - (b3 * y), 2);
            wValue = wValue.getNextNode();
            xValue = xValue.getNextNode();
            yValue = yValue.getNextNode();
            zValue = zValue.getNextNode();
        }

        return sum;
    }
    //@method_def_end

    //@method_def_start: calculateThirdTermForPredictionInterval
    /**
     * 予測区間の計算に使用する3番目の項を計算する
     *
     * @param data Probe計算用データセット
     * @return 予測区間の計算に使用する3番目の項
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public static Double calculateThirdTermForPredictionInterval(
            ProbeDataSet data) throws IOException {
        final double numberOfHistoryData = data.getNumberOfHistoryData();
        final double w = data.getEstimatedProxySizes()[0];
        final double x = data.getEstimatedProxySizes()[1];
        final double y = data.getEstimatedProxySizes()[2];
        final double meanW = data.getMeanW();
        final double meanX = data.getMeanX();
        final double meanY = data.getMeanY();
        final double sumSquaredDeviationW = data.getSumSquaredDeviationW();
        final double sumSquaredDeviationX = data.getSumSquaredDeviationX();
        final double sumSquaredDeviationY = data.getSumSquaredDeviationY();
        final double tempW = Math.pow(w - meanW, 2);
        final double tempX = Math.pow(x - meanX, 2);
        final double tempY = Math.pow(y - meanY, 2);
        return Math.sqrt(1.0
                + (1.0 / numberOfHistoryData)
                + (tempW / sumSquaredDeviationW)
                + (tempX / sumSquaredDeviationX)
                + (tempY / sumSquaredDeviationY));
    }
    //@method_def_end

    //@method_def_start: resolveEquation
    /**
     * 連立方程式の解を求める処理。
     * <p>
     * Ax=b
     * <p>
     * となる。xを計算する。
     *
     * @param A 行列Aを表す2次元行列
     * @param b ベクトルbを表す1次元配列
     * @return 方程式の解を表す1次元配列
     */
    public static Double[] resolveEquation(double[][] A, double[] b) {
        final double EPSILON = 1e-10; //非正則行列の判断基準
        final int N = b.length;

        for (int pivot = 0; pivot < N; pivot++) {
            //ピボットの探索
            int max = pivot;
            for (int i = pivot + 1; i < N; i++) {
                if (Math.abs(A[i][pivot]) > Math.abs(A[max][pivot])) {
                    max = i;
                }
            }
            //行の交換
            double[] temp = A[pivot];
            A[pivot] = A[max];
            A[max] = temp;
            double t = b[pivot];
            b[pivot] = b[max];
            b[max] = t;

            //非正則行列の場合は、解なしのため例外を投げる
            if (Math.abs(A[pivot][pivot]) <= EPSILON) {
                throw new RuntimeException(
                        "Matrix is singular or nearly singular");
            }

            //対角化の実施
            for (int i = pivot + 1; i < N; i++) {
                double alpha = A[i][pivot] / A[pivot][pivot];
                b[i] -= alpha * b[pivot];
                for (int j = pivot; j < N; j++) {
                    A[i][j] -= alpha * A[pivot][j];
                }
            }
        }

        // 逆順に解を求めていく
        final Double[] x = new Double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }
    //@method_def_end

    //@method_def_start: sumSquaredDeviation
    /**
     * 偏差の平方和を求める処理
     *
     * @param list 数値一覧
     * @param mean 数値一覧の平均値
     * @return 数値一覧の偏差の平方和
     */
    public static Double sumSquaredDeviation(LinkedList list, double mean) {
        double sum = 0.0;
        Node value = list.getHeadNode();
        while (value != null) {
            sum += Math.pow(value.getValue() - mean, 2);
            value = value.getNextNode();
        }
        return sum;
    }
    //@method_def_end

}
