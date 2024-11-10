public class RBTreeNode {
    public enum NodeColor {
        RED,
        BLACK
    }

    private int key;

    private int value;

    private RBTreeNode leftChild;

    private RBTreeNode rightChild;

    private NodeColor color;

    public RBTreeNode() {
    }

    public RBTreeNode(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public RBTreeNode getLeftChild() {
        return this.leftChild;
    }

    public void setLeftChild(RBTreeNode leftChild) {
        this.leftChild = leftChild;
    }

    public RBTreeNode getRightChild() {
        return this.rightChild;
    }

    public void setRightChild(RBTreeNode rightChild) {
        this.rightChild = rightChild;
    }

    public NodeColor getColor() {
        return this.color;
    }

    public void setColor(NodeColor color) {
        this.color = color;
    }
}
