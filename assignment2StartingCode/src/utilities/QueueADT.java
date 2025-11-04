package utilities;

import java.util.NoSuchElementException;

/**
 * Defines the behavior of a generic Queue (FIFO - First In, First Out).
 * @param <T> the type of elements held in this queue.
 */
public interface QueueADT<T> {

    /**
     * Adds an element to the end of this queue.
     * 
     * @param element the element to add.
     * @throws NullPointerException if element is null.
     */
    void enqueue(T element) throws NullPointerException;

    /**
     * Removes and returns the element at the front of this queue.
     * 
     * @return the element removed from the front.
     * @throws NoSuchElementException if the queue is empty.
     */
    T dequeue() throws NoSuchElementException;

    /**
     * Returns (but does not remove) the element at the front of this queue.
     * 
     * @return the element at the front of the queue.
     * @throws NoSuchElementException if the queue is empty.
     */
    T peek() throws NoSuchElementException;

    /**
     * Removes all elements from this queue.
     * After calling clear(), the queue will be empty.
     */
    void clear();

    /**
     * Returns true if the queue contains no elements.
     * 
     * @return true if empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Returns the number of elements currently in the queue.
     * 
     * @return the size of the queue.
     */
    int size();
}
