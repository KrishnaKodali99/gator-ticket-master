import java.util.PriorityQueue;

public class BinaryMinHeap extends PriorityQueue<Integer> {
    int[] heapArray;

    int currentSize;

    public BinaryMinHeap(int size) {
        this.heapArray = new int[size];
        this.currentSize = 0;
    }

    public boolean insert(int value) {
        if(this.currentSize >= this.heapArray.length) {
            return false;
        }
        this.heapArray[this.currentSize] = value;
        return true;
    }

    public Integer extractMin() {
        return this.heapArray[0];
    }

    public Integer peekMin() {
        if(this.currentSize <= 0) {
            return null;
        }
        return this.heapArray[0];
    }
}
