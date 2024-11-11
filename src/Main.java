public class Main {
    public static void main(String[] args) {
        RBTreeMap rbTreeMap = new RBTreeMap();
        rbTreeMap.put(9, 10);
        rbTreeMap.put(8, 12);
        rbTreeMap.put(7, 13);
        rbTreeMap.put(2, 15);
        rbTreeMap.put(6, 15);
        rbTreeMap.put(1, 2);
        rbTreeMap.put(3, 22);
        rbTreeMap.put(4, 9);

        rbTreeMap.displayRBTree();
    }
}
