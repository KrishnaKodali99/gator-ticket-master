import java.util.Arrays;

public class GatorTicketMaster {
    public static void main(String[] args) {
//        FileIOProcessor fileIOProcessor = new FileIOProcessor();
//        String filePath = "/Users/krishnakodali/Documents/university-of-florida/academics/sem-1/ADS/project/tests/test-1.txt";
//        fileIOProcessor.processFile(filePath);

        BinaryMinHeap binaryMinHeap = new BinaryMinHeap(5);
        binaryMinHeap.insert(9);
        binaryMinHeap.insert(3);
        binaryMinHeap.insert(5);
        binaryMinHeap.insert(7);
        binaryMinHeap.insert(2);

//        binaryMinHeap.removeElement(3);

        System.out.println(Arrays.toString(binaryMinHeap.getHeapArray()));

//        RBTreeMap rbTreeMap = new RBTreeMap();
//        rbTreeMap.put(9, 10);
//        rbTreeMap.put(8, 12);
//        rbTreeMap.put(7, 13);
//        rbTreeMap.put(2, 15);
//        rbTreeMap.put(6, 16);
//        rbTreeMap.put(1, 2);
//        rbTreeMap.put(3, 22);
//        rbTreeMap.put(4, 9);
//        System.out.println(rbTreeMap.remove(2));

//        rbTreeMap.displayRBTree();
    }
}
