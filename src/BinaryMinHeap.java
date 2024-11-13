import java.util.NoSuchElementException;

public class BinaryMinHeap<K extends Comparable<K>> {
    private final K[] heapArray;

    private int currentSize;

    @SuppressWarnings("unchecked")
    public BinaryMinHeap(int size) {
        this.heapArray = (K[]) new Comparable[size];
        this.currentSize = 0;
    }

    /**
     * Inserts a new value into the Binary Min Heap while maintaining the min-heap property.
     *
     * @param value The integer value to be inserted into the heap.
     * @return {@code true} if the value was successfully inserted into the heap,
     * {@code false} if the insertion failed (e.g., due to insufficient space or invalid input).
     */
    public boolean insert(K value) {
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
    public K extractMin() {
        if (this.currentSize <= 0) {
            throw new NoSuchElementException("Cannot extract minimum: Queue is empty.");
        }
        K minValue = this.heapArray[0];

        this.currentSize--;
        this.heapArray[0] = this.heapArray[this.currentSize];
        heapifyDown(0);
        this.heapArray[currentSize] = null;

        return minValue;
    }

    public K removeElement(K element) {
        K removedElement;
        if (element.equals(this.heapArray[0])) {
            return this.extractMin();
        }
        if (element.equals(this.heapArray[this.currentSize - 1])) {
            removedElement = this.heapArray[this.currentSize - 1];
            this.heapArray[this.currentSize - 1] = null;
            this.currentSize--;
            return removedElement;
        }

        for (int index = 1; index < this.currentSize - 1; index++) {
            if (element.equals(this.heapArray[index])) {
                removedElement = this.heapArray[index];

                this.heapArray[index] = this.heapArray[this.currentSize - 1];
                this.currentSize--;

                int parentIndex = (index - 1) / 2;
                if (this.heapArray[index].compareTo(this.heapArray[parentIndex]) > 0) {
                    this.heapifyDown(index);
                } else {
                    this.heapifyUp(index);
                }

                this.heapArray[this.currentSize] = null;
                return removedElement;
            }
        }
        return null;
    }

    /**
     * Returns the minimum value (root) of the Binary Min Heap without removing it.
     *
     * @return The minimum value (root) of the heap, or {@code null} if the heap is empty.
     */
    public K peek() {
        if (this.currentSize <= 0) {
            return null;
        }
        return this.heapArray[0];
    }

    /**
     * @return current size of the heap i.e. number of elements.
     */
    public int size() {
        return this.currentSize;
    }

    /**
     *
     * @return `true` if the heap is empty, `false` otherwise.
     */
    public boolean isEmpty() {
        return this.currentSize == 0;
    }

    private void heapifyUp(int index) {
        int heapifyIndex = index;
        int parentIndex = (index - 1) / 2;

        if (parentIndex < 0) {
            return;
        }

        if (this.heapArray[parentIndex].compareTo(this.heapArray[heapifyIndex]) > 0) {
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

        if (leftChildIndex <= this.currentSize && this.heapArray[leftChildIndex].compareTo(this.heapArray[heapifyIndex]) <= 0) {
            heapifyIndex = leftChildIndex;
        }
        if (rightChildIndex <= this.currentSize && this.heapArray[rightChildIndex].compareTo(this.heapArray[heapifyIndex]) <= 0) {
            heapifyIndex = rightChildIndex;
        }
        if (heapifyIndex == index) {
            return;
        }
        this.swapElements(heapifyIndex, index);
        heapifyDown(heapifyIndex);
    }

    private void swapElements(int index1, int index2) {
        K temp = this.heapArray[index1];
        this.heapArray[index1] = this.heapArray[index2];
        this.heapArray[index2] = temp;
    }
}
