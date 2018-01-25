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
 * 数値データを保持するクラス
 *
 * <p>
 * 数値データの一覧はリンクリストとして実装される。そのため、 各 {@link Node} インスタンスは次の {@link Node}
 * への参照を保持している。
 * </p>
 *
 * @author smuraoka
 */
public class Node {

    private final Double value;
    private Node nextNode;

    //@method_def_start: Node
    /**
     * 数値データを指定してインスタンスを作成します。
     *
     * @param value 数値データ
     */
    public Node(Double value) {
        this.value = value;
    }
    //@method_def_end

    //@method_def_start: getValue
    /**
     * 数値データを取得します。
     *
     * @return 数値データ
     */
    public double getValue() {
        return value;
    }
    //@method_def_end

    //@method_def_start: getNextNode
    /**
     * 次の数値データを取得します。
     *
     * @return 次の数値データ
     */
    public Node getNextNode() {
        return nextNode;
    }
    //@method_def_end

    //@method_def_start: setNextNode
    /**
     * 次の数値データを設定します。
     *
     * @param nextNode 次の数値データ
     */
    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
    //@method_def_end

}
