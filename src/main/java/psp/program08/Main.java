/*
SUMMARY
Program: PSP Program 08
Name: Satoshi Muraoka
Date: 2017/01/24
Description: 「追加」、「再利用」、「修正」規模と履歴データで多重回帰分析を行い、
             見積値とその70%予測区間を計算する。
 */
package psp.program08;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 * 制御処理
 * <p>
 * 各部品を制御して、要求される処理を実行する。
 *
 * @author smuraoka
 */
public class Main {

    //@method_def_start: main
    /**
     * プログラムのエントリーポイント
     * <p>
     * コマンドライン引数の構成
     * <ol>
     * <li>第一引数：プログラムの設定値が記述されたプロパティファイル</li>
     * </ol>
     *
     * @param args コマンドライン引数
     * @throws java.io.IOException 設定ファイルの読み込みに失敗した場合
     */
    public static void main(String[] args) throws IOException {

        //必要最低限の引数が指定されていなければ、即時終了する。
        if (args.length < 1) {
            System.err.println("Bad Argument Number: " + args.length);
            System.exit(-1);
        }

        //プロパティファイルの読込
        final File configFile = new File(args[0]);
        final Properties config = loadProperty(configFile);
        config.setProperty("psp.program8.configurationFile", args[0]);

        //Probe計算用データを作成
        final ProbeDataSet data = new ProbeDataSet(config);

        //結果を表示する。
        printResult(data);
    }
    //@method_def_end

    //@method_def_start: loadProperty
    /**
     * プログラム設定が記述されたプロパティファイルを読み込む
     *
     * @param file プロパティファイル
     * @return プログラム設定
     */
    private static Properties loadProperty(File file) throws IOException {
        final Properties prop = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            prop.load(in);
        }
        return prop;
    }
    //@method_def_end

    //@method_def_start: printResult
    /**
     * プログラムの実行結果を画面に表示する。
     *
     * @param data Probe計算用データセット
     */
    private static void printResult(ProbeDataSet data) throws IOException {

        //小数点数の四捨五入ををするために使用する
        final DecimalFormat df1 = new DecimalFormat("0.0");
        df1.setRoundingMode(RoundingMode.HALF_UP);
        final DecimalFormat df2 = new DecimalFormat("0.00");
        df2.setRoundingMode(RoundingMode.HALF_UP);
        final DecimalFormat df4 = new DecimalFormat("0.0000");
        df4.setRoundingMode(RoundingMode.HALF_UP);

        // あらかじめ計算しておく
        final double LPI
                = data.getImprovedEstimation() - data.getPredictionInterval();
        final double UPI
                = data.getImprovedEstimation() + data.getPredictionInterval();

        //計算結果の出力
        System.out.println("================================================");
        System.out.format("Configuration File: %s%n",
                data.getPropertyFile().getName());
        System.out.format("History Data File: %s%n",
                data.getHistoryDataFile().getName());
        System.out.println("------------------------------------------------");
        System.out.format("Number of History Data: %d%n",
                data.getNumberOfHistoryData());
        System.out.println("Added, Reused, Modified Code Size:");
        System.out.format("    %s, %s, %s%n",
                df1.format(data.getEstimatedProxySizes()[0]),
                df1.format(data.getEstimatedProxySizes()[1]),
                df1.format(data.getEstimatedProxySizes()[2]));
        System.out.println("Regression Parameters (B0, B1, B2, B3):");
        System.out.format("    %s, %s, %s, %s%n",
                df4.format(data.getRegressionParameters()[0]),
                df4.format(data.getRegressionParameters()[1]),
                df4.format(data.getRegressionParameters()[2]),
                df4.format(data.getRegressionParameters()[3]));
        System.out.format("Projected Hours: %s%n",
                df1.format(data.getImprovedEstimation()));
        System.out.format("Prediction Interval Rate: %s%n",
                df2.format(data.getPredictionIntervalRate()));
        System.out.println("Prediction Intervals (LPI, UPI):");
        System.out.format("    %s, %s%n", df1.format(LPI), df1.format(UPI));
        System.out.println("================================================");
        System.out.println();
    }
    //@method_def_end

}
