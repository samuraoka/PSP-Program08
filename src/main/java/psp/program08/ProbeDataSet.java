/*
SUMMARY
Program: PSP Program 08
Name: Satoshi Muraoka
Date: 2017/01/24
Description: 「追加」、「再利用」、「修正」規模と履歴データで多重回帰分析を行い、
             見積値とその70%予測区間を計算する。
 */
package psp.program08;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Probe計算用データセット
 *
 * @author smuraoka
 */
public class ProbeDataSet {

    private final Properties properties; //設定値を保持しているプロパティオブジェクト
    private final File propertyFile; //設定値が記録されたファイル
    private final File historyDataFile; //履歴データが記録されたファイル
    private final int dimension; //履歴データの次元数
    private LinkedList w; //履歴データwの数値一覧
    private LinkedList x; //履歴データxの数値一覧
    private LinkedList y; //履歴データyの数値一覧
    private LinkedList z; //履歴データzの数値一覧
    private Integer numberOfHistoryData; //履歴データの個数

    private Double[] regressionParameters; //多重回帰パラメータ
    private final double[] estimatedProxySizes; //見積プロキシ規模
    private Double improvedEstimation; //多重回帰手法を用いて計算した見積値
    private final double predictionIntervalRate; //予測区間の大きさの値
    private Double predictionInterval; //predictionIntervalRate予測区間

    private final int initialNumberOfSegments; //積分範囲の初期分割数
    private final double acceptableError; //許容誤差

    private Double meanW; //履歴データwの平均値
    private Double meanX; //履歴データxの平均値
    private Double meanY; //履歴データyの平均値
    private LinkedList productWW; //履歴データwの各要素の2乗の数値一覧
    private LinkedList productWX; //履歴データwとxの各要素の積の数値一覧
    private LinkedList productWY; //履歴データwとyの各要素の積の数値一覧
    private LinkedList productWZ; //履歴データwとzの各要素の積の数値一覧
    private LinkedList productXX; //履歴データxの各要素の2乗の数値一覧
    private LinkedList productXY; //履歴データxとyの各要素の積の数値一覧
    private LinkedList productXZ; //履歴データxとzの各要素の積の数値一覧
    private LinkedList productYY; //履歴データyの各要素の2乗の数値一覧
    private LinkedList productYZ; //履歴データyとzの各要素の2乗の数値一覧
    private Double sumProductWW; //履歴データwの各要素の2乗の総和
    private Double sumProductWX; //履歴データwとxの各要素の積の総和
    private Double sumProductWY; //履歴データwとyの各要素の積の総和
    private Double sumProductWZ; //履歴データwとzの各要素の積の総和
    private Double sumProductXX; //履歴データxの各要素の2乗の総和
    private Double sumProductXY; //履歴データxとyの各要素の積の総和
    private Double sumProductXZ; //履歴データxとzの各要素の積の総和
    private Double sumProductYY; //履歴データyの各要素の2乗の総和
    private Double sumProductYZ; //履歴データyとzの各要素の積の総和
    private Double sumW; //履歴データwの各要素の総和
    private Double sumX; //履歴データxの各要素の総和
    private Double sumY; //履歴データyの各要素の総和
    private Double sumZ; //履歴データzの各要素の総和

    private Double xForPredictionInterval; //予測区間の計算に使用する積分範囲（x）
    private Double sigmaForPredictionInterval; //予測区間の計算に使用する標準偏差
    private Double sumForSigma; //予測区間の計算に使用する標準偏差で用いる総和
    private Double thirdTermForPredictionInterval; //予測区間の計算に使用する3番目の項
    private Double sumSquaredDeviationW; //予測区間の計算に使用するwの偏差平方和
    private Double sumSquaredDeviationX; //予測区間の計算に使用するxの偏差平方和
    private Double sumSquaredDeviationY; //予測区間の計算に使用するyの偏差平方和

    //@method_def_start: ProbeDataSet
    /**
     * プロパティオブジェクトを指定して初期化処理を実行する。
     *
     * @param config 設定値が記録されているプロパティオブジェクト
     */
    public ProbeDataSet(Properties config) {
        //設定値の読み込み
        this.properties = config;
        final String propertyFilePath
                = config.getProperty("psp.program8.configurationFile");
        final String addedCodeSizeLiteral
                = config.getProperty("psp.program8.addedSize");
        final String reusedCodeSizeLiteral
                = config.getProperty("psp.program8.resusedSize");
        final String modifiedCodeSizeLiteral
                = config.getProperty("psp.program8.modifiedSize");
        final String historyDataFilePath
                = config.getProperty("psp.program8.historyDataFile");
        final String dimensionLiteral
                = config.getProperty("psp.program8.historyDataFile.dimension");
        final String predictionIntervalRateLiteral
                = config.getProperty("psp.program8.predictionIntervalRate");
        final String initialNumberOfSegmentsLiteral
                = config.getProperty("psp.program8.initialNumberOfSegment");
        final String acceptableErrorLiteral
                = config.getProperty("psp.program8.acceptableError");
        //文字列をオブジェクトに変換
        this.estimatedProxySizes = new double[]{
            Double.parseDouble(addedCodeSizeLiteral),
            Double.parseDouble(reusedCodeSizeLiteral),
            Double.parseDouble(modifiedCodeSizeLiteral)
        };
        this.propertyFile = new File(propertyFilePath);
        this.historyDataFile = new File(historyDataFilePath);
        this.dimension = Integer.parseInt(dimensionLiteral);
        this.predictionIntervalRate
                = Double.parseDouble(predictionIntervalRateLiteral);
        this.initialNumberOfSegments
                = Integer.parseInt(initialNumberOfSegmentsLiteral);
        this.acceptableError = Double.parseDouble(acceptableErrorLiteral);
    }
    //@method_def_end

    //@method_def_start: getW
    /**
     * 履歴データwの数値データ一覧を返す
     *
     * @return 履歴データwの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getW() throws IOException {
        if (w == null) {
            loadHistoryData();
        }
        return w;
    }
    //@method_def_end

    //@method_def_start: getX
    /**
     * 履歴データxの数値データ一覧を返す
     *
     * @return 履歴データxの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getX() throws IOException {
        if (x == null) {
            loadHistoryData();
        }
        return x;
    }
    //@method_def_end

    //@method_def_start: getY
    /**
     * 履歴データyの数値データ一覧を返す
     *
     * @return 履歴データyの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getY() throws IOException {
        if (y == null) {
            loadHistoryData();
        }
        return y;
    }
    //@method_def_end

    //@method_def_start: getZ
    /**
     * 履歴データzの数値データ一覧を返す
     *
     * @return 履歴データzの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getZ() throws IOException {
        if (z == null) {
            loadHistoryData();
        }
        return z;
    }
    //@method_def_end

    //@method_def_start: getProductWW
    /**
     * w^2の数値データ一覧を返す
     *
     * @return w^2の数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductWW() throws IOException {
        if (productWW == null) {
            productWW = PSPMath.product(getW(), getW());
        }
        return productWW;
    }
    //@method_def_end

    //@method_def_start: getProductWX
    /**
     * w*xの数値データ一覧を返す
     *
     * @return w*xの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductWX() throws IOException {
        if (productWX == null) {
            productWX = PSPMath.product(getW(), getX());
        }
        return productWX;
    }
    //@method_def_end

    //@method_def_start: getProductWY
    /**
     * w*yの数値データ一覧を返す
     *
     * @return w*yの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductWY() throws IOException {
        if (productWY == null) {
            productWY = PSPMath.product(getW(), getY());
        }
        return productWY;
    }
    //@method_def_end

    //@method_def_start: getProductWZ
    /**
     * w*zの数値データ一覧を返す
     *
     * @return w*zの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductWZ() throws IOException {
        if (productWZ == null) {
            productWZ = PSPMath.product(getW(), getZ());
        }
        return productWZ;
    }
    //@method_def_end

    //@method_def_start: getProductXX
    /**
     * x^2の数値データ一覧を返す
     *
     * @return x^2の数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductXX() throws IOException {
        if (productXX == null) {
            productXX = PSPMath.product(getX(), getX());
        }
        return productXX;
    }
    //@method_def_end

    //@method_def_start: getProductXY
    /**
     * x*yの数値データ一覧を返す
     *
     * @return x*yの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductXY() throws IOException {
        if (productXY == null) {
            productXY = PSPMath.product(getX(), getY());
        }
        return productXY;
    }
    //@method_def_end

    //@method_def_start: getProductXZ
    /**
     * x*zの数値データ一覧を返す
     *
     * @return x*zの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductXZ() throws IOException {
        if (productXZ == null) {
            productXZ = PSPMath.product(getX(), getZ());
        }
        return productXZ;
    }
    //@method_def_end

    //@method_def_start: getProductYY
    /**
     * y*yの数値データ一覧を返す
     *
     * @return y*yの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductYY() throws IOException {
        if (productYY == null) {
            productYY = PSPMath.product(getY(), getY());
        }
        return productYY;
    }
    //@method_def_end

    //@method_def_start: getProductYZ
    /**
     * y*zの数値データ一覧を返す
     *
     * @return y*zの数値データ一覧
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public LinkedList getProductYZ() throws IOException {
        if (productYZ == null) {
            productYZ = PSPMath.product(getY(), getZ());
        }
        return productYZ;
    }
    //@method_def_end

    //@method_def_start: getSumW
    /**
     * w数値データ一覧の総和を返す
     *
     * @return w数値データ一覧の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumW() throws IOException {
        if (sumW == null) {
            sumW = PSPMath.sum(getW());
        }
        return sumW;
    }
    //@method_def_end

    //@method_def_start: getSumX
    /**
     * x数値データ一覧の合計値を返す
     *
     * @return x数値データ一覧の合計値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumX() throws IOException {
        if (sumX == null) {
            sumX = PSPMath.sum(getX());
        }
        return sumX;
    }
    //@method_def_end

    //@method_def_start: getSumY
    /**
     * y数値データ一覧の合計値を返す
     *
     * @return y数値データ一覧の合計値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumY() throws IOException {
        if (sumY == null) {
            sumY = PSPMath.sum(getY());
        }
        return sumY;
    }
    //@method_def_end

    //@method_def_start: getSumZ
    /**
     * z数値データ一覧の総和を返す
     *
     * @return z数値データ一覧の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumZ() throws IOException {
        if (sumZ == null) {
            sumZ = PSPMath.sum(getZ());
        }
        return sumZ;
    }
    //@method_def_end

    //@method_def_start: getSumProductWW
    /**
     * w^2数値データ一覧の総和を返す
     *
     * @return w^2数値データ一覧の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductWW() throws IOException {
        if (sumProductWW == null) {
            sumProductWW = PSPMath.sum(getProductWW());
        }
        return sumProductWW;
    }
    //@method_def_end

    //@method_def_start: getSumProductWX
    /**
     * w*x数値データ一覧の総和を返す
     *
     * @return w*x数値データ一覧の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductWX() throws IOException {
        if (sumProductWX == null) {
            sumProductWX = PSPMath.sum(getProductWX());
        }
        return sumProductWX;
    }
    //@method_def_end

    //@method_def_start: getSumProductWY
    /**
     * w*y数値データ一覧の総和を返す
     *
     * @return w*y数値データ一覧の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductWY() throws IOException {
        if (sumProductWY == null) {
            sumProductWY = PSPMath.sum(getProductWY());
        }
        return sumProductWY;
    }
    //@method_def_end

    //@method_def_start: getSumProductWZ
    /**
     * w*z数値データ一覧の総和を返す
     *
     * @return w*z数値データ一覧の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductWZ() throws IOException {
        if (sumProductWZ == null) {
            sumProductWZ = PSPMath.sum(getProductWZ());
        }
        return sumProductWZ;
    }
    //@method_def_end

    //@method_def_start: getSumProductXX
    /**
     * x^2数値データ一覧の合計値を返す
     *
     * @return x^2数値データ一覧の合計値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductXX() throws IOException {
        if (sumProductXX == null) {
            sumProductXX = PSPMath.sum(getProductXX());
        }
        return sumProductXX;
    }
    //@method_def_end

    //@method_def_start: getSumProductXY
    /**
     * x*y数値データ一覧の合計値を返す
     *
     * @return x*y数値データ一覧の合計値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductXY() throws IOException {
        if (sumProductXY == null) {
            sumProductXY = PSPMath.sum(getProductXY());
        }
        return sumProductXY;
    }
    //@method_def_end

    //@method_def_start: getSumProductXZ
    /**
     * x*z数値データ一覧の合計値を返す
     *
     * @return x*z数値データ一覧の合計値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductXZ() throws IOException {
        if (sumProductXZ == null) {
            sumProductXZ = PSPMath.sum(getProductXZ());
        }
        return sumProductXZ;
    }
    //@method_def_end

    //@method_def_start: getSumProductYY
    /**
     * y^2数値データ一覧の合計値を返す
     *
     * @return y^2数値データ一覧の合計値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductYY() throws IOException {
        if (sumProductYY == null) {
            sumProductYY = PSPMath.sum(getProductYY());
        }
        return sumProductYY;
    }
    //@method_def_end

    //@method_def_start: getSumProductYZ
    /**
     * y*z数値データ一覧の総和を返す
     *
     * @return y*z数値データ一覧の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumProductYZ() throws IOException {
        if (sumProductYZ == null) {
            sumProductYZ = PSPMath.sum(getProductYZ());
        }
        return sumProductYZ;
    }
    //@method_def_end

    //@method_def_start: getMeanW
    /**
     * w数値データ一覧の平均値を返す
     *
     * @return w数値データ一覧の平均値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getMeanW() throws IOException {
        if (meanW == null) {
            meanW = PSPMath.mean(getW());
        }
        return meanW;
    }
    //@method_def_end

    //@method_def_start: getMeanX
    /**
     * x数値データ一覧の平均値を返す
     *
     * @return x数値データ一覧の平均値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getMeanX() throws IOException {
        if (meanX == null) {
            meanX = PSPMath.mean(getX());
        }
        return meanX;
    }
    //@method_def_end

    //@method_def_start: getMeanY
    /**
     * y数値データ一覧の平均値を返す
     *
     * @return y数値データ一覧の平均値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getMeanY() throws IOException {
        if (meanY == null) {
            meanY = PSPMath.mean(getY());
        }
        return meanY;
    }
    //@method_def_end

    //@method_def_start: getHistoryDataFile
    /**
     * 履歴データが記録されているファイルを得る
     *
     * @return 履歴データが記録されているファイル
     */
    public File getHistoryDataFile() {
        return historyDataFile;
    }
    //@method_def_end

    //@method_def_start: getNumberOfHistoryData
    /**
     * 履歴データの個数を得る
     *
     * @return 履歴データの個数
     * @throws java.io.IOException 履歴データの読み込みに失敗した場合
     */
    public Integer getNumberOfHistoryData() throws IOException {
        if (numberOfHistoryData == null) {
            loadHistoryData();
        }
        return numberOfHistoryData;
    }
    //@method_def_end

    //@method_def_start: loadHistoryData
    /**
     * 履歴データファイルよりデータを読み込む処理
     */
    private void loadHistoryData() throws IOException {
        //履歴データファイルの読込
        final LinkedList wValues = new LinkedList();
        final LinkedList xValues = new LinkedList();
        final LinkedList yValues = new LinkedList();
        final LinkedList zValues = new LinkedList();;
        try (BufferedReader in
                = new BufferedReader(new FileReader(getHistoryDataFile()))) {
            String line = in.readLine();
            while (line != null) {
                final String[] values = line.split("\\t");
                if (values.length != getDimension()) {
                    throw new IOException(
                            "Illegal format of history data file");
                }
                final Node wValue = new Node(Double.parseDouble(values[0]));
                final Node xValue = new Node(Double.parseDouble(values[1]));
                final Node yValue = new Node(Double.parseDouble(values[2]));
                final Node zValue = new Node(Double.parseDouble(values[3]));
                wValues.add(wValue);
                xValues.add(xValue);
                yValues.add(yValue);
                zValues.add(zValue);
                line = in.readLine();
            }
        }

        //チェック
        if ((wValues.getCount() != xValues.getCount())
                || (wValues.getCount() != yValues.getCount())
                || (wValues.getCount() != zValues.getCount())) {
            throw new IllegalStateException("bat history data: "
                    + "w=" + wValues.getCount()
                    + ": x=" + xValues.getCount()
                    + ": y=" + yValues.getCount()
                    + ": z=" + zValues.getCount());
        }

        //値の設定
        w = wValues;
        x = xValues;
        y = yValues;
        z = zValues;
        numberOfHistoryData = xValues.getCount();
    }
    //@method_def_end

    //@method_def_start: getInitialNumberOfSegments
    /**
     * 数値積分で使用する積分範囲の初期分割数を得る
     *
     * @return 積分範囲の初期分割数
     */
    public int getInitialNumberOfSegments() {
        return initialNumberOfSegments;
    }
    //@method_def_end

    //@method_def_start: getAcceptableError
    /**
     * 数値積分処理及び積分範囲の探索に使用する許容誤差を得る
     *
     * @return 許容誤差
     */
    public double getAcceptableError() {
        return acceptableError;
    }
    //@method_def_end

    //@method_def_start: getEstimatedProxySizes
    /**
     * 見積プロキシ規模を得る
     * <p>
     * 配列の構成
     * <ol>
     * <li> 追加コード規模
     * <li> 再利用コード規模
     * <li> 修正コード規模
     * </ol>
     *
     * @return 見積プロキシ規模
     */
    public double[] getEstimatedProxySizes() {
        return estimatedProxySizes;
    }
    //@method_def_end

    //@method_def_start: getPredictionIntervalRate
    /**
     * 予測区間の大きさを得る
     *
     * @return 予測区間の大きさ
     */
    public double getPredictionIntervalRate() {
        return predictionIntervalRate;
    }
    //@method_def_end

    //@method_def_start: getPredictionInterval
    /**
     * 予測区間を得る
     *
     * @return 予測区間
     * @throws java.io.IOException 履歴データの読み込みに失敗した場合
     */
    public Double getPredictionInterval() throws IOException {
        if (predictionInterval == null) {
            final double term1 = getxForPredictionInterval();
            final double term2 = getSigmaForPredictionInterval();
            final double term3 = getThirdTermForPredictionInterval();
            predictionInterval = term1 * term2 * term3;
        }
        return predictionInterval;
    }
    //@method_def_end

    //@method_def_start: getxForPredictionInterval
    /**
     * 予測区間の計算に使用するxの値を得る
     *
     * @return 予測区間の計算に使用するxの値
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getxForPredictionInterval() throws IOException {
        if (xForPredictionInterval == null) {
            xForPredictionInterval
                    = PSPMath.calculateXForPredictionInterval(this);
        }
        return xForPredictionInterval;
    }
    //@method_def_end

    //@method_def_start: getSigmaForPredictionInterval
    /**
     * 予測区間の計算に使用する標準偏差を得る
     *
     * @return 予測区間の計算に使用する標準偏差
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSigmaForPredictionInterval() throws IOException {
        if (sigmaForPredictionInterval == null) {
            sigmaForPredictionInterval
                    = PSPMath.calculateSigmaForPredictionInterval(this);
        }
        return sigmaForPredictionInterval;
    }
    //@method_def_end

    //@method_def_start: getSumForSigma
    /**
     * 予測区間の計算に使用する標準偏差で用いる総和を得る
     *
     * @return 予測区間の計算に使用する標準偏差で用いる総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumForSigma() throws IOException {
        if (sumForSigma == null) {
            sumForSigma = PSPMath.calculateSumForSigma(this);
        }
        return sumForSigma;
    }
    //@method_def_end

    //@method_def_start: getThirdTermForPredictionInterval
    /**
     * 予測区間の計算に使用する3番目の項を得る
     *
     * @return 予測区間の計算に使用する3番目の項
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getThirdTermForPredictionInterval() throws IOException {
        if (thirdTermForPredictionInterval == null) {
            thirdTermForPredictionInterval
                    = PSPMath.calculateThirdTermForPredictionInterval(this);
        }
        return thirdTermForPredictionInterval;
    }
    //@method_def_end

    //@method_def_start: getProperties
    /**
     * 設定値を保持しているプロパティを得る
     *
     * @return プロパティ
     */
    public Properties getProperties() {
        return properties;
    }
    //@method_def_end

    //@method_def_start: getPropertyFile
    /**
     * 設定値が記録されたファイルを得る
     *
     * @return 設定値が記録されたファイル
     */
    public File getPropertyFile() {
        return propertyFile;
    }
    //@method_def_end

    //@method_def_start: getRegressionParameters
    /**
     * 重回帰パラメータを得る
     * <p>
     * 配列の構成
     * <ol>
     * <li> B0
     * <li> B1
     * <li> B2
     * <li> B3
     * </ol>
     *
     * @return 重回帰パラメータ
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double[] getRegressionParameters() throws IOException {
        if (regressionParameters == null) {
            final int dim = getDimension();
            //行列Aの初期化
            final double[][] A = new double[dim][dim];
            A[0][0] = getNumberOfHistoryData();
            A[0][1] = getSumW();
            A[0][2] = getSumX();
            A[0][3] = getSumY();
            A[1][0] = getSumW();
            A[1][1] = getSumProductWW();
            A[1][2] = getSumProductWX();
            A[1][3] = getSumProductWY();
            A[2][0] = getSumX();
            A[2][1] = getSumProductWX();
            A[2][2] = getSumProductXX();
            A[2][3] = getSumProductXY();
            A[3][0] = getSumY();
            A[3][1] = getSumProductWY();
            A[3][2] = getSumProductXY();
            A[3][3] = getSumProductYY();
            //ベクトルbの初期化
            final double[] b = new double[dim];
            b[0] = getSumZ();
            b[1] = getSumProductWZ();
            b[2] = getSumProductXZ();
            b[3] = getSumProductYZ();
            regressionParameters = PSPMath.resolveEquation(A, b);
        }
        return regressionParameters;
    }
    //@method_def_end

    //@method_def_start: getImprovedEstimation
    /**
     * 見積プロキシ規模と多重回帰パラメータより計算された見積値を得る
     *
     * @return 見積プロキシ規模と多重回帰パラメータより計算された見積値
     * @throws java.io.IOException 履歴データの読み込みに失敗した場合
     */
    public Double getImprovedEstimation() throws IOException {
        if (improvedEstimation == null) {
            improvedEstimation = PSPMath.estimate(this);
        }
        return improvedEstimation;
    }
    //@method_def_end

    //@method_def_start: getDimension
    /**
     * 履歴データの次元数を得る
     *
     * @return 履歴データの次元数
     */
    public int getDimension() {
        return dimension;
    }
    //@method_def_end

    //@method_def_start: getSumSquaredDeviationW
    /**
     * 予測区間の計算に使用する3番目の項で用いるwの偏差平方の総和を得る
     *
     * @return 予測区間の計算に使用する3番目の項で用いるwの偏差平方の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumSquaredDeviationW() throws IOException {
        if (sumSquaredDeviationW == null) {
            sumSquaredDeviationW
                    = PSPMath.sumSquaredDeviation(getW(), getMeanW());
        }
        return sumSquaredDeviationW;
    }
    //@method_def_end

    //@method_def_start: getSumSquaredDeviationX
    /**
     * 予測区間の計算に使用する3番目の項で用いるxの偏差平方の総和を得る
     *
     * @return 予測区間の計算に使用する3番目の項で用いるxの偏差平方の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumSquaredDeviationX() throws IOException {
        if (sumSquaredDeviationX == null) {
            sumSquaredDeviationX
                    = PSPMath.sumSquaredDeviation(getX(), getMeanX());
        }
        return sumSquaredDeviationX;
    }
    //@method_def_end

    //@method_def_start: getSumSquaredDeviationY
    /**
     * 予測区間の計算に使用する3番目の項で用いるyの偏差平方の総和を得る
     *
     * @return 予測区間の計算に使用する3番目の項で用いるyの偏差平方の総和
     * @throws java.io.IOException 履歴データファイルの読み込みに失敗した場合
     */
    public Double getSumSquaredDeviationY() throws IOException {
        if (sumSquaredDeviationY == null) {
            sumSquaredDeviationY
                    = PSPMath.sumSquaredDeviation(getY(), getMeanY());
        }
        return sumSquaredDeviationY;
    }
    //@method_def_end

}
