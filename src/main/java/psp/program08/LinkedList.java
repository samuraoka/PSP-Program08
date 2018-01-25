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
 * 数値データの一覧を表すクラス
 *
 * <p>
 * 数値データの一覧はリンクリストとして実装される。そのため、{@link LinkedList}インストタンスは先頭の {@link Node}
 * への参照を保持している。<br>
 * リンクリストに保持されている数値データの件数は記録されているため、{@link #getCount()}メソッドより取得できます。
 * </p>
 *
 * @author smuraoka
 */
public class LinkedList {

    private int count = 0;
    private Node headNode = null;

    //@method_def_start: add
    /**
     * このリンクリストに数値データを追加します。
     *
     * @param node このリンクリストに追加する数値データ
     */
    public void add(Node node) {
        node.setNextNode(headNode);
        headNode = node;
        ++count;
    }
    //@method_def_end

    //@method_def_start: getCount
    /**
     * 数値データの件数を取得します。
     *
     * @return 数値データの件数
     */
    public int getCount() {
        return count;
    }
    //@method_def_end

    //@method_def_start: getHeadNode
    /**
     * リンクリストを構成している先頭の数値データへの参照を返します。
     *
     * @return 先頭の数値データへの参照
     */
    public Node getHeadNode() {
        return headNode;
    }
    //@method_def_end

}
