import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class RBTreeMap implements Map<Integer, Integer> {
    private RBTreeNode rootNode;

    private int size;

    private static final Logger logger = new Logger();

    public RBTreeMap() {
        this.size = 0;
    }

    public enum NodeDirection {
        LEFT, RIGHT
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        logger.info("Checking for key: " + key);
        return Objects.nonNull(this.searchKey(this.rootNode, (Integer) key));
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Integer get(Object key) {
        logger.info("Querying for value with key: " + key);
        return this.searchKey(this.rootNode, (Integer) key);
    }

    @Override
    public Integer put(Integer key, Integer value) {
        assert Objects.nonNull(key) : "Key should not be null";
        assert Objects.nonNull(value) : "Value should not be null";

        this.insertKVPair(key, value, this.rootNode);
        this.size++;
        return value;
    }

    @Override
    public Integer remove(Object key) {
        assert Objects.nonNull(key) : "Key should not be null";

        Stack<RBTreeNode> nodesStack = new Stack<>();
        Integer value = this.deleteKVPair((Integer) key, this.rootNode, nodesStack);
        if (Objects.nonNull(value)) {
            this.size--;
        }
        return value;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Integer> map) {
        map.forEach((key, value) -> {
            if (Objects.nonNull(key) && Objects.nonNull(value)) this.put(key, value);
        });
        this.size += map.size();
    }

    @Override
    public void clear() {
        this.clearAllNodes(this.rootNode);
        logger.info("Cleared all Red-Black tree data");
    }

    @Override
    public Set<Integer> keySet() {
        Set<Integer> keySet = new HashSet<>();
        this.getKeyValues(this.rootNode, keySet, true);
        return keySet;
    }

    @Override
    public Collection<Integer> values() {
        List<Integer> valuesList = new ArrayList<>();
        this.getKeyValues(this.rootNode, valuesList, false);
        return valuesList;
    }

    @Override
    public Set<Entry<Integer, Integer>> entrySet() {
        return Set.of();
    }

    @Override
    public Integer getOrDefault(Object key, Integer defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Override
    public Integer putIfAbsent(Integer key, Integer value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Override
    public boolean replace(Integer key, Integer oldValue, Integer newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Override
    public Integer replace(Integer key, Integer value) {
        return Map.super.replace(key, value);
    }

    /**
     * Searches for a specific key in the Red-Black Tree.
     *
     * @param treeNode node pointer
     * @param key      value to search
     * @return the value of the node if key is found, else null.
     */
    private Integer searchKey(RBTreeNode treeNode, int key) {
        if (Objects.isNull(treeNode)) {
            return null;
        }

        if (key == treeNode.getKey()) {
            return treeNode.getValue();
        } else if (key < treeNode.getKey()) {
            return searchKey(treeNode.getLeftChild(), key);
        } else {
            return searchKey(treeNode.getRightChild(), key);
        }
    }

    /**
     * Inserts a key-value pair into a new node in the Red-Black Tree.
     * Ensures the Red-Black properties during insertion by balancing the tree and adjusting node colors through rotations and recoloring.
     *
     * @param key      The key of the key-value pair to be inserted. The key must be unique within the tree.
     * @param value    The value associated with the key to be inserted. This value will be stored in the node.
     * @param treeNode The node at which to start the insertion. The method will recursively traverse the tree
     *                 *                 and insert the new key-value pair in the correct position.
     */
    private void insertKVPair(Integer key, Integer value, RBTreeNode treeNode) {
        NodeDirection nodeDirection;

        // If the node is null, initialize it as the root node.
        if (Objects.isNull(treeNode)) {
            this.rootNode = new RBTreeNode(key, value, RBTreeNode.NodeColor.BLACK);
            return;
        }

        // Insert node as inserting in a BST
        if (key < treeNode.getKey()) {
            nodeDirection = NodeDirection.LEFT;
            if (Objects.nonNull(treeNode.getLeftChild())) {
                insertKVPair(key, value, treeNode.getLeftChild());
            } else {
                RBTreeNode newNode = new RBTreeNode(key, value);
                newNode.setParent(treeNode);
                treeNode.setLeftChild(newNode);
            }
        } else {
            nodeDirection = NodeDirection.RIGHT;
            if (Objects.nonNull(treeNode.getRightChild())) {
                insertKVPair(key, value, treeNode.getRightChild());
            } else {
                RBTreeNode newNode = new RBTreeNode(key, value);
                newNode.setParent(treeNode);
                treeNode.setRightChild(newNode);
            }
        }

        // Balance Red-Black tree based on the type of imbalance
        if (treeNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
            RBTreeNode parentNode = treeNode.getParent();
            if (nodeDirection == NodeDirection.LEFT && Objects.nonNull(treeNode.getLeftChild()) && treeNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                if (parentNode.getLeftChild() == treeNode) {
                    this.balanceRBTree(parentNode, treeNode, parentNode.getRightChild(), NodeDirection.LEFT, true);
                } else if (parentNode.getRightChild() == treeNode) {
                    this.balanceRBTree(parentNode, treeNode, parentNode.getLeftChild(), NodeDirection.LEFT, false);
                }
            } else if (nodeDirection == NodeDirection.RIGHT && Objects.nonNull(treeNode.getRightChild()) && treeNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                if (parentNode.getRightChild() == treeNode) {
                    this.balanceRBTree(parentNode, treeNode, parentNode.getLeftChild(), NodeDirection.RIGHT, true);
                } else if (parentNode.getLeftChild() == treeNode) {
                    this.balanceRBTree(parentNode, treeNode, parentNode.getRightChild(), NodeDirection.RIGHT, false);
                }
            }
        }
    }

    private Integer deleteKVPair(int key, RBTreeNode treeNode, Stack<RBTreeNode> nodesStack) {
        while (Objects.nonNull(treeNode) && key != treeNode.getKey()) {
            if (key < treeNode.getKey()) {
                nodesStack.add(treeNode);
                treeNode = treeNode.getLeftChild();
            } else if (key > treeNode.getKey()) {
                nodesStack.add(treeNode);
                treeNode = treeNode.getRightChild();
            }
        }

        if (Objects.isNull(treeNode)) {
            return null;
        }

        int value = treeNode.getValue();
        if (Objects.nonNull(treeNode.getLeftChild()) && Objects.nonNull(treeNode.getRightChild())) {
            nodesStack.add(treeNode);
            RBTreeNode itrTreeNode = treeNode.getLeftChild();
            while (Objects.nonNull(itrTreeNode.getRightChild())) {
                nodesStack.add(itrTreeNode);
                itrTreeNode = itrTreeNode.getRightChild();
            }
            treeNode.copyNodeData(itrTreeNode);
            treeNode = itrTreeNode;
        }
        this.deleteNodeAndBalanceRBTree(treeNode, nodesStack);

        return value;
    }

    private void clearAllNodes(RBTreeNode rbTreeNode) {
        if (Objects.isNull(rbTreeNode)) {
            return;
        }
        clearAllNodes(rbTreeNode.getLeftChild());
        clearAllNodes(rbTreeNode.getRightChild());

        rbTreeNode.setLeftChild(null);
        rbTreeNode.setRightChild(null);
    }

    private void getKeyValues(RBTreeNode treeNode, Collection<Integer> collection, boolean insertKeys) {
        if (Objects.isNull(treeNode)) {
            return;
        }
        collection.add((insertKeys) ? treeNode.getKey() : treeNode.getValue());
        getKeyValues(treeNode.getLeftChild(), collection, insertKeys);
        getKeyValues(treeNode.getRightChild(), collection, insertKeys);
    }


    private void balanceRBTree(RBTreeNode parentNode, RBTreeNode treeNode, RBTreeNode siblingNode, NodeDirection nodeDirection, boolean isXXInsertion) {
        if (Objects.nonNull(siblingNode) && siblingNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
            this.XYrInsertion(parentNode, treeNode, siblingNode);
        } else {
            if (isXXInsertion) {
                this.XXbInsertion(parentNode, treeNode, nodeDirection);
            } else {
                this.XYbInsertion(parentNode, treeNode, nodeDirection);
            }
        }
    }

    private void XYrInsertion(RBTreeNode parentNode, RBTreeNode treeNode, RBTreeNode siblingNode) {
        if (Objects.isNull(parentNode.getParent())) {
            parentNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
        } else {
            parentNode.setNodeColor(RBTreeNode.NodeColor.RED);
        }
        treeNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
        siblingNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
    }

    private void XXbInsertion(RBTreeNode parentNode, RBTreeNode treeNode, NodeDirection xNodeDirection) {
        RBTreeNode swapNode = new RBTreeNode(parentNode);
        swapNode.setNodeColor(RBTreeNode.NodeColor.RED);

        parentNode.copyNodeData(treeNode);
        if (xNodeDirection == NodeDirection.LEFT) {
            this.addRBTreeChildNode(parentNode, treeNode.getLeftChild(), NodeDirection.LEFT);
            this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.RIGHT);
            this.addRBTreeChildNode(swapNode, treeNode.getRightChild(), NodeDirection.LEFT);
        } else {
            this.addRBTreeChildNode(parentNode, treeNode.getRightChild(), NodeDirection.RIGHT);
            this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.LEFT);
            this.addRBTreeChildNode(swapNode, treeNode.getLeftChild(), NodeDirection.RIGHT);
        }

        if (Objects.isNull(treeNode.getParent())) {
            treeNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
            this.rootNode = treeNode;
        }
    }

    private void XYbInsertion(RBTreeNode parentNode, RBTreeNode treeNode, NodeDirection xNodeDirection) {
        RBTreeNode swapNode = new RBTreeNode(parentNode);
        swapNode.setNodeColor(RBTreeNode.NodeColor.RED);

        RBTreeNode childNode;
        if (xNodeDirection == NodeDirection.RIGHT) {
            childNode = treeNode.getRightChild();
            parentNode.copyNodeData(childNode);
            this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.RIGHT);
            this.addRBTreeChildNode(swapNode, childNode.getRightChild(), NodeDirection.LEFT);
            this.addRBTreeChildNode(treeNode, childNode.getLeftChild(), NodeDirection.RIGHT);
        } else {
            childNode = treeNode.getLeftChild();
            parentNode.copyNodeData(childNode);
            this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.LEFT);
            this.addRBTreeChildNode(swapNode, childNode.getLeftChild(), NodeDirection.RIGHT);
            this.addRBTreeChildNode(treeNode, childNode.getRightChild(), NodeDirection.LEFT);
        }
    }

    private void deleteNodeAndBalanceRBTree(RBTreeNode treeNode, Stack<RBTreeNode> nodesStack) {
        if (Objects.isNull(treeNode.getRightChild()) && Objects.isNull(treeNode.getLeftChild())) {
            if (Objects.isNull(treeNode.getParent())) {
                this.rootNode = null;
                return;
            }
            this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, null);
        }
        else if (Objects.nonNull(treeNode.getLeftChild())) {
            this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, treeNode.getLeftChild());
        } else if (Objects.nonNull(treeNode.getRightChild())) {
            this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, treeNode.getRightChild());
        }

        if (treeNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
            treeNode.empty();
            return;
        }
    }

    private void replaceRBTreeChildNode(RBTreeNode parentNode, RBTreeNode treeNode, RBTreeNode newNode) {
        if (parentNode.getLeftChild() == treeNode) {
            this.addRBTreeChildNode(parentNode, newNode, NodeDirection.LEFT);
        } else if (parentNode.getRightChild() == treeNode) {
            this.addRBTreeChildNode(parentNode, newNode, NodeDirection.RIGHT);
        }
    }

    private void addRBTreeChildNode(RBTreeNode parentNode, RBTreeNode childNode, NodeDirection childNodeDirection) {
        if (childNodeDirection == NodeDirection.LEFT) {
            parentNode.setLeftChild(childNode);
        } else if (childNodeDirection == NodeDirection.RIGHT) {
            parentNode.setRightChild(childNode);
        }

        if (Objects.nonNull(childNode)) {
            childNode.setParent(parentNode);
        }
    }

    /**
     * Displays the details of the nodes in a Red-Black tree, at their respective height levels.
     * The method performs a level-order traversal of the tree, printing the key, value, and color.
     */
    public void displayRBTree() {
        logger.info("Displaying Red-Black Tree:");

        Queue<AbstractMap.SimpleEntry<RBTreeNode, Integer>> queue = new LinkedList<>();

        int currentLevel = 1;
        queue.add(new AbstractMap.SimpleEntry<>(this.rootNode, 1));
        while (!queue.isEmpty()) {
            System.out.println("Level-" + currentLevel);
            while (!queue.isEmpty() && currentLevel == queue.peek().getValue()) {
                AbstractMap.SimpleEntry<RBTreeNode, Integer> tuple = queue.poll();
                RBTreeNode node = tuple.getKey();
                System.out.println("Key: " + node.getKey() + " Value: " + node.getValue() + " Color: " + node.getNodeColor());

                if (Objects.nonNull(node.getLeftChild())) {
                    queue.add(new AbstractMap.SimpleEntry<>(node.getLeftChild(), currentLevel + 1));
                }
                if (Objects.nonNull(node.getRightChild())) {
                    queue.add(new AbstractMap.SimpleEntry<>(node.getRightChild(), currentLevel + 1));
                }
            }
            currentLevel++;
            System.out.println();
        }
    }
}
