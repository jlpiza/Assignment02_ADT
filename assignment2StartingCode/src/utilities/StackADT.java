package utilities;

import java.util.NoSuchElementException;

/**
 * Defines the behavior of a generic Stack (LIFO - Last In, First Out).
 * @param <T> the type of elements held in this stack.
 */
public interface StackADT<T> {

    /**
     * Pushes an element onto the top of this stack.
     * 
     * @param element the element to be added.
     * @throws NullPointerException if element is null.
     */
    void push(T element) throws NullPointerException;

    /**
     * Removes and returns the element from the top of this stack.
     * 
     * @return the element removed from the top of the stack.
     * @throws NoSuchElementException if the stack is empty.
     */
    T pop() throws NoSuchElementException;

    /**
     * Returns (but does not remove) the element on the top of this stack.
     * 
     * @return the top element in the stack.
     * @throws NoSuchElementException if the stack is empty.
     */
    T peek() throws NoSuchElementException;

    /**
     * Removes all elements from this stack.
     * After calling clear(), the stack will be empty.
     */
    void clear();

    /**
     * Returns true if the stack contains no elements.
     * 
     * @return true if empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Returns the number of elements currently in the stack.
     * 
     * @return the size of the stack.
     */
    int size();
}
