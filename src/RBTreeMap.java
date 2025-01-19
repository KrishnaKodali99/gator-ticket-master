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

public class RBTreeMap implements Map<Integer, Integer> {
    private static final Logger logger = new Logger();

    private RBTreeNode rootNode;

    private int size;

    public RBTreeMap() {
        this.size = 0;
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
        assert Objects.nonNull(key) : "Key should not be null";

        return Objects.nonNull(this.searchKey(this.rootNode, (Integer) key));
    }

    @Override
    public boolean containsValue(Object value) {
        assert Objects.nonNull(value) && value instanceof Integer : "Key should not be null";

        return Objects.nonNull(this.searchValue(this.rootNode, (Integer) value));
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

        if (this.size == 0 || Objects.isNull(this.rootNode)) {
            return null;
        }

        Integer value = this.deleteKVPair((Integer) key, this.rootNode);
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
        Set<Entry<Integer, Integer>> set = new HashSet<>();
        this.addEntriesInOrder(this.rootNode, set);
        return set;
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

    // Searches for a key in the Red-Black tree similar to search in a BST.
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

    // Recursively searches for a value in the Red-Black tree and returns it if found, otherwise null.
    private Integer searchValue(RBTreeNode treeNode, int value) {
        if (Objects.isNull(treeNode)) {
            return null;
        }
        if (value == treeNode.getValue()) {
            return value;
        }

        Integer leftSearch = searchValue(treeNode.getLeftChild(), value);
        if (Objects.nonNull(leftSearch)) {
            return leftSearch;
        }

        return searchValue(treeNode.getRightChild(), value);
    }

    // Inserts a key-value pair into the Red-Black Tree and balances the tree based on the type of insertion through rotations and recoloring.
    private void insertKVPair(Integer key, Integer value, RBTreeNode treeNode) {
        // If the node is null, initialize it as the root node.
        if (Objects.isNull(treeNode)) {
            this.rootNode = new RBTreeNode(key, value, RBTreeNode.NodeColor.BLACK);
            return;
        }

        NodeDirection nodeDirection = null;

        // Insert node as inserting in a BST
        while (Objects.nonNull(treeNode)) {
            if (key < treeNode.getKey()) {
                nodeDirection = NodeDirection.LEFT;
                if (Objects.nonNull(treeNode.getLeftChild())) {
                    treeNode = treeNode.getLeftChild();
                } else {
                    RBTreeNode newNode = new RBTreeNode(key, value);
                    this.addRBTreeChildNode(treeNode, newNode, nodeDirection);
                    break;
                }
            } else {
                nodeDirection = NodeDirection.RIGHT;
                if (Objects.nonNull(treeNode.getRightChild())) {
                    treeNode = treeNode.getRightChild();
                } else {
                    RBTreeNode newNode = new RBTreeNode(key, value);
                    this.addRBTreeChildNode(treeNode, newNode, nodeDirection);
                    break;
                }
            }
        }

        // Balance the Red-Black tree after insertion, based on the type of imbalance. Consider treeNode here as the pp node.
        this.balanceRBTreePostInsert(treeNode, nodeDirection);
    }

    // Deletes a key-value pair if found and balances the Red-Black tree based on the type of deletion.
    private Integer deleteKVPair(int key, RBTreeNode treeNode) {
        while (Objects.nonNull(treeNode) && key != treeNode.getKey()) {
            if (key < treeNode.getKey()) {
                treeNode = treeNode.getLeftChild();
            } else if (key > treeNode.getKey()) {
                treeNode = treeNode.getRightChild();
            }
        }

        if (Objects.isNull(treeNode) || key != treeNode.getKey()) {
            return null;
        }

        int value = treeNode.getValue();
        if (Objects.nonNull(treeNode.getLeftChild()) && Objects.nonNull(treeNode.getRightChild())) {
            RBTreeNode itrTreeNode = treeNode.getLeftChild();
            while (Objects.nonNull(itrTreeNode.getRightChild())) {
                itrTreeNode = itrTreeNode.getRightChild();
            }
            treeNode.copyNodeData(itrTreeNode);
            treeNode = itrTreeNode;
        }

        // Balance the Red-Black tree after deletion, based on the type of imbalance.
        this.balanceRBTreePostDelete(treeNode);

        return value;
    }

    // This method recursively removes all nodes in the subtree rooted at the given rbTreeNode, resetting the tree.
    private void clearAllNodes(RBTreeNode rbTreeNode) {
        if (Objects.isNull(rbTreeNode)) {
            return;
        }
        clearAllNodes(rbTreeNode.getLeftChild());
        clearAllNodes(rbTreeNode.getRightChild());

        rbTreeNode.setLeftChild(null);
        rbTreeNode.setRightChild(null);
    }

    // Recursively traverses the Red-Black tree and adds either keys or values to the given collection.
    private void getKeyValues(RBTreeNode treeNode, Collection<Integer> collection, boolean insertKeys) {
        if (Objects.isNull(treeNode)) {
            return;
        }
        collection.add((insertKeys) ? treeNode.getKey() : treeNode.getValue());
        getKeyValues(treeNode.getLeftChild(), collection, insertKeys);
        getKeyValues(treeNode.getRightChild(), collection, insertKeys);
    }

    // Traverse the Red-Black tree in-order and adds key-value pairs to the given set.
    private void addEntriesInOrder(RBTreeNode treeNode, Set<Entry<Integer, Integer>> set) {
        if (Objects.isNull(treeNode)) {
            return;
        }
        addEntriesInOrder(treeNode.getLeftChild(), set);
        treeNode.getKey();
        set.add(Map.entry(treeNode.getKey(), treeNode.getValue()));
        addEntriesInOrder(treeNode.getRightChild(), set);
    }

    // Balances the Red-Black tree after an insertion, adjusting node colors and performing rotations based on the tree's imbalance.
    private void balanceRBTreePostInsert(RBTreeNode treeNode, NodeDirection nodeDirection) {
        boolean continueLoop = true;

        while (Objects.nonNull(treeNode)) {
            RBTreeNode parentNode = treeNode.getParent();
            if (treeNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
                if (Objects.isNull(parentNode)) {
                    treeNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
                    return;
                }
                if (nodeDirection == NodeDirection.LEFT && Objects.nonNull(treeNode.getLeftChild()) && treeNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                    if (parentNode.getLeftChild() == treeNode) {
                        continueLoop = this.determineInsertType(parentNode, treeNode, parentNode.getRightChild(), NodeDirection.LEFT, true);
                    } else if (parentNode.getRightChild() == treeNode) {
                        continueLoop = this.determineInsertType(parentNode, treeNode, parentNode.getLeftChild(), NodeDirection.RIGHT, false);
                    }
                } else if (nodeDirection == NodeDirection.RIGHT && Objects.nonNull(treeNode.getRightChild()) && treeNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                    if (parentNode.getRightChild() == treeNode) {
                        continueLoop = this.determineInsertType(parentNode, treeNode, parentNode.getLeftChild(), NodeDirection.RIGHT, true);
                    } else if (parentNode.getLeftChild() == treeNode) {
                        continueLoop = this.determineInsertType(parentNode, treeNode, parentNode.getRightChild(), NodeDirection.LEFT, false);
                    }
                }
                if (!continueLoop) {
                    break;
                }
            }
            if (Objects.isNull(parentNode)) {
                return;
            } else if (Objects.isNull(parentNode.getParent())) {
                this.rootNode = parentNode;
                return;
            }

            // If the loop continues, determine the node to balance in the next iteration.
            treeNode = parentNode.getParent();
            if (treeNode.getLeftChild() == parentNode) {
                nodeDirection = NodeDirection.LEFT;
            } else if (treeNode.getRightChild() == parentNode) {
                nodeDirection = NodeDirection.RIGHT;
            }
        }
    }

    // Balances the Red-Black tree after a deletion, adjusting node colors and performing rotations based on the tree's imbalance.
    private void balanceRBTreePostDelete(RBTreeNode treeNode) {
        RBTreeNode.NodeColor deletedNodeColor = treeNode.getNodeColor();
        boolean isExternalNode = false;

        // Check if the node is a 0-degree node or a 1-degree node, and delete accordingly.
        if (Objects.isNull(treeNode.getRightChild()) && Objects.isNull(treeNode.getLeftChild())) {
            if (Objects.isNull(treeNode.getParent())) {
                this.rootNode = null;
                return;
            }
            treeNode = this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, new RBTreeNode());
            isExternalNode = true;
        } else if (Objects.nonNull(treeNode.getLeftChild())) {
            treeNode = this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, treeNode.getLeftChild());
        } else if (Objects.nonNull(treeNode.getRightChild())) {
            treeNode = this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, treeNode.getRightChild());
        }

        // No rebalancing needed if the following conditions are met post-deletion.
        if (deletedNodeColor == RBTreeNode.NodeColor.RED) {
            this.replaceRBTreeChildNode(treeNode.getParent(), treeNode, null);
            return;
        } else if (Objects.nonNull(treeNode) && (treeNode.getNodeColor() == RBTreeNode.NodeColor.RED || Objects.isNull(treeNode.getParent()))) {
            treeNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
            return;
        }

        RBTreeNode siblingNode;
        NodeDirection nodeDirection = null;
        while (isExternalNode || Objects.nonNull(treeNode)) {
            RBTreeNode parentNode = treeNode.getParent();
            siblingNode = null;

            //If root node, return as entire tree is deficient
            if (Objects.isNull(parentNode)) {
                return;
            }

            // Determine whether the node is deleted from the left or right subtree.
            if (parentNode.getLeftChild() == treeNode) {
                nodeDirection = NodeDirection.LEFT;
                siblingNode = parentNode.getRightChild();
            } else if (parentNode.getRightChild() == treeNode) {
                nodeDirection = NodeDirection.RIGHT;
                siblingNode = parentNode.getLeftChild();
            }

            // Remove the existing root of the subtree if it's an external node.
            if (isExternalNode) {
                if (nodeDirection == NodeDirection.LEFT) {
                    parentNode.setLeftChild(null);
                } else {
                    parentNode.setRightChild(null);
                }
                treeNode.empty();
                isExternalNode = false;
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
                        treeNode = treeNode.getParent();
                        continue;
                    }
                } else {
                    this.XbnDeletion(parentNode, siblingNode, nodeDirection, siblingNodeRedEdges == 2);
                }
                return;
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
                    this.XrnDeletion(parentNode, siblingNode, siblingChildNode, nodeDirection, siblingChildRedEdges == 2);
                }
                return;
            }
            treeNode = treeNode.getParent();
        }
    }

    // Determines the insert type based on sibling color and if parent and grandparent are in the same direction (LEFT or RIGHT).
    private boolean determineInsertType(RBTreeNode parentNode, RBTreeNode treeNode, RBTreeNode siblingNode, NodeDirection nodeDirection, boolean isXXInsertion) {
        if (Objects.nonNull(siblingNode) && siblingNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
            this.XYrInsertion(parentNode, treeNode, siblingNode);
            return true;
        }
        if (isXXInsertion) {
            this.XXbInsertion(parentNode, treeNode, nodeDirection);
        } else {
            this.XYbInsertion(parentNode, treeNode, nodeDirection);
        }
        return false;
    }

    // Handles the insertion of type LLr, LRr, RRr and RLr for balancing the Red-Black tree.
    private void XYrInsertion(RBTreeNode parentNode, RBTreeNode treeNode, RBTreeNode siblingNode) {
        if (Objects.isNull(parentNode.getParent())) {
            parentNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
        } else {
            parentNode.setNodeColor(RBTreeNode.NodeColor.RED);
        }
        treeNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
        siblingNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
    }

    // Handles the insertion of type LLb and RRb, for balancing the Red-Black tree.
    private void XXbInsertion(RBTreeNode parentNode, RBTreeNode treeNode, NodeDirection nodeDirection) {
        this.updateGrandParent(parentNode, treeNode);
        parentNode.setNodeColor(RBTreeNode.NodeColor.RED);
        treeNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

        if (nodeDirection == NodeDirection.LEFT) {
            this.addRBTreeChildNode(parentNode, treeNode.getRightChild(), NodeDirection.LEFT);
            this.addRBTreeChildNode(treeNode, parentNode, NodeDirection.RIGHT);
        } else {
            this.addRBTreeChildNode(parentNode, treeNode.getLeftChild(), NodeDirection.RIGHT);
            this.addRBTreeChildNode(treeNode, parentNode, NodeDirection.LEFT);
        }

        this.checkAndUpdateRoot(treeNode);
    }

    // Handles the insertion of type LRb1, LRb2, RLb1 and RLb2, for balancing the Red-Black tree.
    private void XYbInsertion(RBTreeNode parentNode, RBTreeNode treeNode, NodeDirection xNodeDirection) {
        RBTreeNode childNode = null;
        if (xNodeDirection == NodeDirection.LEFT) {
            childNode = treeNode.getRightChild();
            this.updateGrandParent(parentNode, childNode);

            // Perform an LR Rotation by pivoting around the child node.
            this.addRBTreeChildNode(parentNode, childNode.getRightChild(), NodeDirection.LEFT);
            this.addRBTreeChildNode(treeNode, childNode.getLeftChild(), NodeDirection.RIGHT);
            this.addRBTreeChildNode(childNode, treeNode, NodeDirection.LEFT);
            this.addRBTreeChildNode(childNode, parentNode, NodeDirection.RIGHT);
        } else if (xNodeDirection == NodeDirection.RIGHT) {
            childNode = treeNode.getLeftChild();
            this.updateGrandParent(parentNode, childNode);

            // Perform an RL Rotation by pivoting around the child node.
            this.addRBTreeChildNode(parentNode, childNode.getLeftChild(), NodeDirection.RIGHT);
            this.addRBTreeChildNode(treeNode, childNode.getRightChild(), NodeDirection.LEFT);
            this.addRBTreeChildNode(childNode, treeNode, NodeDirection.RIGHT);
            this.addRBTreeChildNode(childNode, parentNode, NodeDirection.LEFT);
        }

        if (Objects.nonNull(childNode)) {
            childNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
            parentNode.setNodeColor(RBTreeNode.NodeColor.RED);
        }
        this.checkAndUpdateRoot(childNode);
    }

    // Handles the deletion of type Lb0 and Rb0, for balancing the Red-Black tree.
    private boolean Xb0Deletion(RBTreeNode parentNode, RBTreeNode siblingNode) {
        siblingNode.setNodeColor(RBTreeNode.NodeColor.RED);

        //Flip color of the nodes and continue to balance.
        if (parentNode.getNodeColor() == RBTreeNode.NodeColor.BLACK) {
            return true;
        }
        if (parentNode.getNodeColor() == RBTreeNode.NodeColor.RED) {
            parentNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
        }
        return false;
    }

    // Handles the deletion of type Lb1, Lb2, Rb1 and Rb2, for balancing the Red-Black tree.
    private void XbnDeletion(RBTreeNode parentNode, RBTreeNode siblingNode, NodeDirection nodeDirection, boolean isXb2Deletion) {
        if (nodeDirection == NodeDirection.LEFT) {
            if (!isXb2Deletion && Objects.nonNull(siblingNode.getRightChild()) && siblingNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                this.updateGrandParent(parentNode, siblingNode);
                siblingNode.getRightChild().setNodeColor(RBTreeNode.NodeColor.BLACK);
                siblingNode.setNodeColor(parentNode.getNodeColor());
                parentNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

                // Perform RR Rotation by pivoting around the siblingChildNode node.
                this.addRBTreeChildNode(parentNode, siblingNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingNode, parentNode, NodeDirection.LEFT);

                this.checkAndUpdateRoot(siblingNode);
            } else if (Objects.nonNull(siblingNode.getLeftChild()) && siblingNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                RBTreeNode siblingChildNode = siblingNode.getLeftChild();
                this.updateGrandParent(parentNode, siblingChildNode);
                siblingChildNode.setNodeColor(parentNode.getNodeColor());
                parentNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

                // Perform RL Rotation by pivoting around the siblingChildNode node.
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(parentNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingChildNode, siblingNode, NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingChildNode, parentNode, NodeDirection.LEFT);

                this.checkAndUpdateRoot(siblingChildNode);
            }
        } else if (nodeDirection == NodeDirection.RIGHT) {
            if (!isXb2Deletion && Objects.nonNull(siblingNode.getLeftChild()) && siblingNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                this.updateGrandParent(parentNode, siblingNode);
                siblingNode.getLeftChild().setNodeColor(RBTreeNode.NodeColor.BLACK);
                siblingNode.setNodeColor(parentNode.getNodeColor());
                parentNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

                // Perform LL Rotation by pivoting around the siblingChildNode node.
                this.addRBTreeChildNode(parentNode, siblingNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingNode, parentNode, NodeDirection.RIGHT);

                this.checkAndUpdateRoot(siblingNode);
            } else if (Objects.nonNull(siblingNode.getRightChild()) && siblingNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                RBTreeNode siblingChildNode = siblingNode.getRightChild();
                this.updateGrandParent(parentNode, siblingChildNode);
                siblingChildNode.setNodeColor(parentNode.getNodeColor());
                parentNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

                // Perform LR Rotation by pivoting around the siblingChildNode node.
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(parentNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingChildNode, siblingNode, NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingChildNode, parentNode, NodeDirection.RIGHT);

                this.checkAndUpdateRoot(siblingChildNode);
            }
        }
    }

    // Handles the deletion of type Lr(0), Rr(0) for balancing the Red-Black tree.
    private void Xr0Deletion(RBTreeNode parentNode, RBTreeNode siblingNode, NodeDirection nodeDirection) {
        this.updateGrandParent(parentNode, siblingNode);
        siblingNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

        if (nodeDirection == NodeDirection.LEFT) {
            if (Objects.nonNull(siblingNode.getLeftChild())) {
                siblingNode.getLeftChild().setNodeColor(RBTreeNode.NodeColor.RED);
            }
            this.addRBTreeChildNode(parentNode, siblingNode.getLeftChild(), NodeDirection.RIGHT);
            this.addRBTreeChildNode(siblingNode, parentNode, NodeDirection.LEFT);
        } else if (nodeDirection == NodeDirection.RIGHT) {
            if (Objects.nonNull(siblingNode.getRightChild())) {
                siblingNode.getRightChild().setNodeColor(RBTreeNode.NodeColor.RED);
            }
            this.addRBTreeChildNode(parentNode, siblingNode.getRightChild(), NodeDirection.LEFT);
            this.addRBTreeChildNode(siblingNode, parentNode, NodeDirection.RIGHT);
        }

        this.checkAndUpdateRoot(siblingNode);
    }

    // Handles the deletion of type Lr(1), Lr(2), Rr(1) and Rr(2), for balancing the Red-Black tree.
    private void XrnDeletion(RBTreeNode parentNode, RBTreeNode siblingNode, RBTreeNode siblingChildNode, NodeDirection nodeDirection, boolean isXr2Deletion) {
        if (nodeDirection == NodeDirection.LEFT) {
            if (!isXr2Deletion && Objects.nonNull(siblingChildNode.getRightChild()) && siblingChildNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                this.updateGrandParent(parentNode, siblingChildNode);
                siblingChildNode.getLeftChild().setNodeColor(RBTreeNode.NodeColor.BLACK);

                this.addRBTreeChildNode(parentNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingChildNode, siblingNode, NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingChildNode, parentNode, NodeDirection.LEFT);

                this.checkAndUpdateRoot(siblingChildNode);
            } else if (Objects.nonNull(siblingChildNode.getLeftChild()) && siblingChildNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                RBTreeNode siblingGrandChildNode = siblingChildNode.getLeftChild();
                this.updateGrandParent(parentNode, siblingGrandChildNode);
                siblingGrandChildNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

                this.addRBTreeChildNode(parentNode, siblingGrandChildNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingChildNode, siblingGrandChildNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingGrandChildNode, siblingNode, NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingGrandChildNode, parentNode, NodeDirection.LEFT);

                this.checkAndUpdateRoot(siblingGrandChildNode);
            }
        } else if (nodeDirection == NodeDirection.RIGHT) {
            if (!isXr2Deletion && Objects.nonNull(siblingChildNode.getLeftChild()) && siblingChildNode.getLeftChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                this.updateGrandParent(parentNode, siblingChildNode);
                siblingChildNode.getLeftChild().setNodeColor(RBTreeNode.NodeColor.BLACK);

                this.addRBTreeChildNode(parentNode, siblingChildNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingNode, siblingChildNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingChildNode, siblingNode, NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingChildNode, parentNode, NodeDirection.RIGHT);

                this.checkAndUpdateRoot(siblingChildNode);
            } else if (Objects.nonNull(siblingChildNode.getRightChild()) && siblingChildNode.getRightChild().getNodeColor() == RBTreeNode.NodeColor.RED) {
                RBTreeNode siblingGrandChildNode = siblingChildNode.getRightChild();
                this.updateGrandParent(parentNode, siblingGrandChildNode);
                siblingGrandChildNode.setNodeColor(RBTreeNode.NodeColor.BLACK);

                this.addRBTreeChildNode(parentNode, siblingGrandChildNode.getRightChild(), NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingChildNode, siblingGrandChildNode.getLeftChild(), NodeDirection.RIGHT);
                this.addRBTreeChildNode(siblingGrandChildNode, siblingNode, NodeDirection.LEFT);
                this.addRBTreeChildNode(siblingGrandChildNode, parentNode, NodeDirection.RIGHT);

                this.checkAndUpdateRoot(siblingGrandChildNode);
            }
        }
    }

    // Replaces a child node in the parent node with a new node, updating the root if necessary.
    private RBTreeNode replaceRBTreeChildNode(RBTreeNode parentNode, RBTreeNode treeNode, RBTreeNode newNode) {
        if (Objects.isNull(parentNode) && Objects.nonNull(newNode)) {
            newNode.setNodeColor(RBTreeNode.NodeColor.BLACK);
            newNode.setParent(null);
            this.rootNode = newNode;
        } else {
            this.addRBTreeChildNode(parentNode, newNode, (parentNode.getLeftChild() == treeNode) ? NodeDirection.LEFT : NodeDirection.RIGHT);
        }
        treeNode.empty();
        return newNode;
    }

    // Updates the parent pointer of the parent node, setting the childNode as its new child node.
    private void updateGrandParent(RBTreeNode parentNode, RBTreeNode childNode) {
        if (Objects.nonNull(parentNode.getParent())) {
            this.addRBTreeChildNode(parentNode.getParent(), childNode, (parentNode.getParent().getLeftChild() == parentNode) ? NodeDirection.LEFT : NodeDirection.RIGHT);
        } else {
            childNode.setParent(parentNode.getParent());
        }
    }

    // Adds a child node to the parent node (left or right) and sets the parent reference for the child.
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

    // Checks if the node is the root and updates it if necessary.
    private void checkAndUpdateRoot(RBTreeNode treeNode) {
        if (Objects.isNull(treeNode)) {
            return;
        }
        if (Objects.isNull(treeNode.getParent())) {
            this.rootNode = treeNode;
        }
    }

    // Performs a level-order traversal of the Red-Black tree, displaying node details (key, value, and color) at each level.
    public void displayRBTree() {
        if (Objects.isNull(this.rootNode)) {
            logger.info("Tree is empty.");
            return;
        }
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

    public enum NodeDirection {
        LEFT, RIGHT
    }
}
