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
 * 関数であることを示すマーカーインターフェース
 *
 * @author smuraoka
 */
public interface Function {

    //@method_def_start: apply
    /**
     * 関数に{@code x}を代入して結果を得る。
     *
     * @param x 関数に代入する値
     * @return 結果
     */
    public double apply(double x);
    //@method_def_end

}
