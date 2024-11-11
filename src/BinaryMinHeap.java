import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

public class BinaryMinHeap extends PriorityQueue<Integer> {
    private final int[] heapArray;

    private int currentSize;

    public BinaryMinHeap(int size) {
        this.heapArray = new int[size];
        this.currentSize = 0;
    }

    /**
     * Inserts a new value into the Binary Min Heap while maintaining the min-heap property.
     *
     * @param value The integer value to be inserted into the heap.
     * @return {@code true} if the value was successfully inserted into the heap,
     * {@code false} if the insertion failed (e.g., due to insufficient space or invalid input).
     */
    public boolean insert(int value) {
        if (this.currentSize >= this.heapArray.length) {
            return false;
        }

        this.heapArray[this.currentSize] = value;
        this.heapifyUp(this.currentSize);
        this.currentSize++;

        return true;
    }

    /**
     * Removes and returns the minimum value (root) from the Binary Min Heap.
     *
     * @return The minimum value (root) of the heap.
     * @throws NoSuchElementException if the heap is empty and there is no minimum value to extract.
     */
    public Integer extractMin() {
        if (this.currentSize <= 0) {
            throw new NoSuchElementException("Cannot extract minimum: Queue is empty.");
        }
        int minValue = this.heapArray[0];

        this.currentSize--;
        this.heapArray[0] = this.heapArray[this.currentSize];
        heapifyDown(0);

        return minValue;
    }

    /**
     * Returns the minimum value (root) of the Binary Min Heap without removing it.
     *
     * @return The minimum value (root) of the heap, or {@code null} if the heap is empty.
     */
    public Integer peek() {
        if (this.currentSize <= 0) {
            return null;
        }
        return this.heapArray[0];
    }

    public int size() {
        return this.currentSize;
    }

    public int[] getHeapArray() {
        return Arrays.copyOfRange(this.heapArray, 0, this.currentSize);
    }

    private void heapifyUp(int index) {
        int heapifyIndex = index;
        int parentIndex = (index - 1) / 2;

        if (parentIndex < 0) {
            return;
        }

        if (this.heapArray[parentIndex] > this.heapArray[heapifyIndex]) {
            this.swapElements(parentIndex, heapifyIndex);
            heapifyIndex = parentIndex;
        }

        if (heapifyIndex == index) {
            return;
        }
        this.heapifyUp(heapifyIndex);
    }

    private void heapifyDown(int index) {
        int heapifyIndex = index;
        int leftChildIndex = (2 * index) + 1;
        int rightChildIndex = (2 * index) + 2;

        if (leftChildIndex <= this.currentSize && this.heapArray[leftChildIndex] <= this.heapArray[heapifyIndex]) {
            heapifyIndex = leftChildIndex;
        }
        if (rightChildIndex <= this.currentSize && this.heapArray[rightChildIndex] <= this.heapArray[heapifyIndex]) {
            heapifyIndex = rightChildIndex;
        }
        if (heapifyIndex == index) {
            return;
        }
        this.swapElements(heapifyIndex, index);
        heapifyDown(heapifyIndex);
    }

    private void swapElements(int index1, int index2) {
        int temp = this.heapArray[index1];
        this.heapArray[index1] = this.heapArray[index2];
        this.heapArray[index2] = temp;
    }
}
