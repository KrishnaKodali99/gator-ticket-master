public class RBTreeNode {
    public enum NodeColor {
        RED, BLACK
    }

    private Integer key;

    private Integer value;

    private RBTreeNode parent;

    private RBTreeNode leftChild;

    private RBTreeNode rightChild;

    private NodeColor nodeColor;

    public RBTreeNode(RBTreeNode rbTreeNode) {
        this.key = rbTreeNode.getKey();
        this.value = rbTreeNode.getValue();
        this.parent = rbTreeNode.getParent();
        this.leftChild = rbTreeNode.getLeftChild();
        this.rightChild = rbTreeNode.getRightChild();
        this.nodeColor = rbTreeNode.getNodeColor();
    }

    public RBTreeNode(int key, int value) {
        this.key = key;
        this.value = value;
        this.nodeColor = NodeColor.RED;
    }

    public RBTreeNode(int key, int value, NodeColor nodeColor) {
        this.key = key;
        this.value = value;
        this.nodeColor = nodeColor;
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

    public RBTreeNode getParent() {
        return parent;
    }

    public void setParent(RBTreeNode parent) {
        this.parent = parent;
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

    public NodeColor getNodeColor() {
        return this.nodeColor;
    }

    public void setNodeColor(NodeColor nodeColor) {
        this.nodeColor = nodeColor;
    }

    public void copyNodeData(RBTreeNode rbTreeNode) {
        this.key = rbTreeNode.getKey();
        this.value = rbTreeNode.getValue();
    }

    public void empty() {
        this.key = null;
        this.value = null;
        this.parent = null;
        this.leftChild = null;
        this.rightChild = null;
        this.nodeColor = null;
    }
}
