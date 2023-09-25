package co.xiaowangzi.debug.utils;

public class Pair<L, R> {

    private L left;
    private R right;

    private L key;
    private R value;

    public static <L, R> Pair<L, R> of(L left, R right){
        Pair<L, R> pair = new Pair<>();
        pair.key = left;
        pair.left = left;
        pair.value = right;
        pair.right = right;
        return pair;
    }

    public boolean equals(Pair<L, R> o1, Pair<L, R> o2){
        return o1.getLeft().equals(o2.getLeft()) && o1.getRight().equals(o2.getRight());
    }

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public L getKey() {
        return key;
    }

    public void setKey(L key) {
        this.key = key;
    }

    public R getValue() {
        return value;
    }

    public void setValue(R value) {
        this.value = value;
    }

}
