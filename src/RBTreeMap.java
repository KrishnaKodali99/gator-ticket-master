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
        assert Objects.nonNull(key) : "Key should not be null";

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
        logger.info("Cleared all data from the Red-Black tree.");
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
        if (!this.containsKey(key)) {
            this.insertKVPair(key, value, this.rootNode);
            this.size++;
            return value;
        }
        return null;
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
     *                 and insert the new key-value pair in the correct position.
     */
    private void insertKVPair(Integer key, Integer value, RBTreeNode treeNode) {
        // If the node is null, initialize it as the root node.
        if (Objects.isNull(treeNode)) {
            this.rootNode = new RBTreeNode(key, value, RBTreeNode.NodeColor.BLACK);
            return;
        }

        NodeDirection nodeDirection;

        // Insert node as inserting in a BST
        if (key < treeNode.getKey()) {
            nodeDirection = NodeDirection.LEFT;
            if (Objects.nonNull(treeNode.getLeftChild())) {
                insertKVPair(key, value, treeNode.getLeftChild());
            } else {
                RBTreeNode newNode = new RBTreeNode(key, value);
                this.addRBTreeChildNode(treeNode, newNode, NodeDirection.LEFT);
            }
        } else {
            nodeDirection = NodeDirection.RIGHT;
            if (Objects.nonNull(treeNode.getRightChild())) {
                insertKVPair(key, value, treeNode.getRightChild());
            } else {
                RBTreeNode newNode = new RBTreeNode(key, value);
                this.addRBTreeChildNode(treeNode, newNode, NodeDirection.RIGHT);
            }
        }

        // Balance the Red-Black tree after insertion, based on the type of imbalance.
        if (treeNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
            RBTreeNode parentNode = treeNode.getParent();
            if (nodeDirection == NodeDirection.LEFT && Objects.nonNull(treeNode.getLeftChild()) && treeNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                if (parentNode.getLeftChild() == treeNode) {
                    this.balanceRBTreePostInsert(parentNode, treeNode, parentNode.getRightChild(), NodeDirection.LEFT, true);
                } else if (parentNode.getRightChild() == treeNode) {
                    this.balanceRBTreePostInsert(parentNode, treeNode, parentNode.getLeftChild(), NodeDirection.LEFT, false);
                }
            } else if (nodeDirection == NodeDirection.RIGHT && Objects.nonNull(treeNode.getRightChild()) && treeNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                if (parentNode.getRightChild() == treeNode) {
                    this.balanceRBTreePostInsert(parentNode, treeNode, parentNode.getLeftChild(), NodeDirection.RIGHT, true);
                } else if (parentNode.getLeftChild() == treeNode) {
                    this.balanceRBTreePostInsert(parentNode, treeNode, parentNode.getRightChild(), NodeDirection.RIGHT, false);
                }
            }
        }
    }

    /**
     * Deletes a key-value pair from the Red-Black tree while ensuring the tree remains balanced according to Re-Black tree properties
     * The method uses a stack to track visited nodes, instead of recursion, for rebalancing if needed.
     *
     * @param key        The key of the node to be deleted.
     * @param treeNode   The root node of the tree or subtree where the deletion is to take place.
     * @param nodesStack A stack that tracks nodes visited during the deletion process, used for potential rebalancing.
     * @return The value of the deleted node, or null if the node was not found.
     */
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

        if (Objects.isNull(treeNode) || key != treeNode.getKey()) {
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

        // Balance the Red-Black tree after deletion, based on the type of imbalance.
        this.balanceRBTreePostDelete(treeNode, nodesStack);

        return value;
    }

    /**
     * This method recursively removes all nodes in the subtree rooted at the given rbTreeNode, resetting the tree.
     *
     * @param rbTreeNode The root node of the tree or subtree to be cleared.
     */
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

    private void balanceRBTreePostInsert(RBTreeNode parentNode, RBTreeNode treeNode, RBTreeNode siblingNode, NodeDirection nodeDirection, boolean isXXInsertion) {
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

    private void balanceRBTreePostDelete(RBTreeNode treeNode, Stack<RBTreeNode> nodesStack) {
        RBTreeNode.NodeColor deletedNodeColor = treeNode.getNodeColor();

        // Check if the node is a 0-degree node or a 1-degree node, and delete accordingly.
        if (Objects.isNull(treeNode.getRightChild()) && Objects.isNull(treeNode.getLeftChild())) {
            if (Objects.isNull(treeNode.getParent())) {
                this.rootNode = null;
                return;
            }
            treeNode = this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, null);
        } else if (Objects.nonNull(treeNode.getLeftChild())) {
            treeNode = this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, treeNode.getLeftChild());
        } else if (Objects.nonNull(treeNode.getRightChild())) {
            treeNode = this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, treeNode.getRightChild());
        }

        // No rebalancing needed if the following conditions are met post-deletion.
        if (deletedNodeColor == RBTreeNode.NodeColor.RED) {
            return;
        } else if (Objects.nonNull(treeNode) && treeNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
            treeNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
            return;
        } else if (Objects.nonNull(treeNode) && Objects.isNull(treeNode.getParent())) {
            treeNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
            return;
        }

        RBTreeNode siblingNode;
        NodeDirection nodeDirection = null;
        while (!nodesStack.empty()) {
            siblingNode = null;
            RBTreeNode parentNode = treeNode.getParent();

            // Determine whether the node is deleted from the left or right subtree.
            if (parentNode.getLeftChild() == treeNode) {
                nodeDirection = NodeDirection.LEFT;
                siblingNode = parentNode.getRightChild();
            } else if (parentNode.getRightChild() == treeNode) {
                nodeDirection = NodeDirection.RIGHT;
                siblingNode = parentNode.getLeftChild();
            }

            // Sibling node must exist; if it does not, stop the deletion process as there is an issue with the Red-Black tree structure.
            if (Objects.isNull(siblingNode)) {
                return;
            }

            // Check the Red-Black tree imbalance based on the sibling node and perform necessary transformations.
            if (siblingNode.getNodeColor() == RBTreeNode.NodeColor.BLACK) {
                int siblingNodeRedEdges = 0;
                if (Objects.nonNull(siblingNode.getLeftChild()) && siblingNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                    siblingNodeRedEdges++;
                }
                if (Objects.nonNull(siblingNode.getRightChild()) && siblingNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                    siblingNodeRedEdges++;
                }

                if (siblingNodeRedEdges == 0) {
                    siblingNode.setNodeColor(RBTreeNode.NodeColor.RED);
                    boolean continueLoop = this.Xb0Deletion(parentNode, siblingNode);
                    if (continueLoop) {
                        treeNode = nodesStack.pop();
                        continue;
                    }
                    return;
                } else if (siblingNodeRedEdges == 1) {
                    this.Xb1Deletion(parentNode, siblingNode, nodeDirection);
                    return;
                } else {
                    this.Xb2Deletion(parentNode, siblingNode, nodeDirection);
                    return;
                }
            } else if (siblingNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
                RBTreeNode siblingChildNode = (nodeDirection == NodeDirection.LEFT) ? siblingNode.getLeftChild() : siblingNode.getRightChild();

                int siblingChildRedEdges = 0;
                if (Objects.nonNull(siblingChildNode.getLeftChild()) && siblingChildNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                    siblingChildRedEdges++;
                }
                if (Objects.nonNull(siblingChildNode.getRightChild()) && siblingChildNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                    siblingChildRedEdges++;
                }

                if (siblingChildRedEdges == 0) {
                    this.Xr0Deletion(parentNode, siblingNode, nodeDirection);
                } else {
                    this.XrnDeletion(parentNode, siblingNode, siblingChildNode, nodeDirection, siblingChildRedEdges != 1);
                }
                return;
            }

            treeNode = nodesStack.pop();
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

    private boolean Xb0Deletion(RBTreeNode parentNode, RBTreeNode siblingNode) {
        siblingNode.setNodeColor(RBTreeNode.NodeColor.RED);
        if (parentNode.getNodeColor() == RBTreeNode.NodeColor.BLACK) {
            return true;
        } else if (parentNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
            parentNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
        }
        return false;
    }

    private void Xb1Deletion(RBTreeNode parentNode, RBTreeNode siblingNode, NodeDirection nodeDirection) {
        RBTreeNode swapNode = new RBTreeNode(parentNode);
        swapNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

        if (nodeDirection == NodeDirection.LEFT) {
            if (Objects.nonNull(siblingNode.getRightChild()) && siblingNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                parentNode.copyNodeData(siblingNode);
                siblingNode.getRightChild().setNodeColor(RBTreeNode.NodeColor.BLACK);
                this.addRBTreeChildNode(parentNode, siblingNode.getRightChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.LEFT);
                this.addRBTreeChildNode(swapNode, siblingNode.getLeftChild(), NodeDirection.RIGHT);
            } else if (Objects.nonNull(siblingNode.getLeftChild()) && siblingNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                RBTreeNode siblingChildNode = siblingNode.getLeftChild();
                parentNode.copyNodeData(siblingChildNode);
                this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.LEFT);
                this.addRBTreeChildNode(swapNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
            }
        } else if (nodeDirection == NodeDirection.RIGHT) {
            if (Objects.nonNull(siblingNode.getLeftChild()) && siblingNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                parentNode.copyNodeData(siblingNode);
                siblingNode.getLeftChild().setNodeColor(RBTreeNode.NodeColor.BLACK);
                this.addRBTreeChildNode(parentNode, siblingNode.getLeftChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.RIGHT);
                this.addRBTreeChildNode(swapNode, siblingNode.getRightChild(), NodeDirection.LEFT);
            } else if (Objects.nonNull(siblingNode.getRightChild()) && siblingNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                RBTreeNode siblingChildNode = siblingNode.getRightChild();
                parentNode.copyNodeData(siblingChildNode);
                this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.RIGHT);
                this.addRBTreeChildNode(swapNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
            }
        }
    }


    private void Xb2Deletion(RBTreeNode parentNode, RBTreeNode siblingNode, NodeDirection nodeDirection) {
        RBTreeNode swapNode = new RBTreeNode(parentNode);
        swapNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

        if (nodeDirection == NodeDirection.LEFT) {
            RBTreeNode siblingChildNode = siblingNode.getLeftChild();
            parentNode.copyNodeData(siblingChildNode);
            this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.LEFT);
            this.addRBTreeChildNode(swapNode, siblingChildNode.getRightChild(), NodeDirection.RIGHT);
            this.addRBTreeChildNode(siblingNode, siblingChildNode.getLeftChild(), NodeDirection.LEFT);
        } else if (nodeDirection == NodeDirection.RIGHT) {
            RBTreeNode siblingChildNode = siblingNode.getRightChild();
            parentNode.copyNodeData(siblingChildNode);
            this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.RIGHT);
            this.addRBTreeChildNode(swapNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
            this.addRBTreeChildNode(siblingNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
        }
    }

    private void Xr0Deletion(RBTreeNode parentNode, RBTreeNode siblingNode, NodeDirection nodeDirection) {
        RBTreeNode swapNode = new RBTreeNode(parentNode);
        parentNode.copyNodeData(siblingNode);

        if (nodeDirection == NodeDirection.LEFT) {
            this.addRBTreeChildNode(parentNode, siblingNode.getRightChild(), NodeDirection.RIGHT);
            this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.LEFT);
            this.addRBTreeChildNode(swapNode, siblingNode.getLeftChild(), NodeDirection.RIGHT);

            if (Objects.nonNull(siblingNode.getLeftChild())) {
                siblingNode.getLeftChild().setNodeColor(RBTreeNode.NodeColor.RED);
            }
        } else if (nodeDirection == NodeDirection.RIGHT) {
            this.addRBTreeChildNode(parentNode, siblingNode.getLeftChild(), NodeDirection.LEFT);
            this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.RIGHT);
            this.addRBTreeChildNode(swapNode, siblingNode.getRightChild(), NodeDirection.LEFT);

            if (Objects.nonNull(siblingNode.getRightChild())) {
                siblingNode.getRightChild().setNodeColor(RBTreeNode.NodeColor.RED);
            }
        }
    }

    private void XrnDeletion(RBTreeNode parentNode, RBTreeNode siblingNode, RBTreeNode siblingChildNode, NodeDirection nodeDirection, boolean isXR2Deletion) {
        RBTreeNode swapNode = new RBTreeNode(parentNode);
        if (nodeDirection == NodeDirection.LEFT) {
            if (!isXR2Deletion && Objects.nonNull(siblingChildNode.getRightChild()) && siblingChildNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                parentNode.copyNodeData(siblingChildNode);
                this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.LEFT);
                this.addRBTreeChildNode(swapNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
                siblingChildNode.getRightChild().setNodeColor(RBTreeNode.NodeColor.BLACK);
            } else if (Objects.nonNull(siblingChildNode.getLeftChild()) && siblingChildNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                parentNode.copyNodeData(siblingChildNode.getLeftChild());
                this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.LEFT);
                this.addRBTreeChildNode(swapNode, siblingChildNode.getLeftChild().getLeftChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getLeftChild().getRightChild(), NodeDirection.RIGHT);
            }
        } else if (nodeDirection == NodeDirection.RIGHT) {
            if (!isXR2Deletion && Objects.nonNull(siblingChildNode.getLeftChild()) && siblingChildNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                parentNode.copyNodeData(siblingChildNode);
                this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.RIGHT);
                this.addRBTreeChildNode(swapNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
                siblingChildNode.getLeftChild().setNodeColor(RBTreeNode.NodeColor.BLACK);
            } else if (Objects.nonNull(siblingChildNode.getRightChild()) && siblingChildNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                parentNode.copyNodeData(siblingChildNode.getRightChild());
                this.addRBTreeChildNode(parentNode, swapNode, NodeDirection.RIGHT);
                this.addRBTreeChildNode(swapNode, siblingChildNode.getRightChild().getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getRightChild().getLeftChild(), NodeDirection.RIGHT);
            }
        }
    }

    private RBTreeNode replaceRBTreeChildNode(RBTreeNode parentNode, RBTreeNode treeNode, RBTreeNode newNode) {
        if (parentNode.getLeftChild() == treeNode) {
            this.addRBTreeChildNode(parentNode, newNode, NodeDirection.LEFT);
        } else if (parentNode.getRightChild() == treeNode) {
            this.addRBTreeChildNode(parentNode, newNode, NodeDirection.RIGHT);
        }
        treeNode.empty();
        return newNode;
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
