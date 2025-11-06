package utilities;

import exceptions.EmptyStackException;

/**
 * <p>
 * The <code>StackADT</code> interface represents the contract for a Stack abstract data type.
 * Implementations will provide LIFO (Last-In-First-Out) behavior.
 * </p>
 * 
 * @param <E> the type of elements this stack holds.
 */
public interface StackADT<E>
{
    /**
     * Pushes an item onto the top of this stack.
     * 
     * @param toAdd The element to be pushed onto this stack.
     * @return <code>true</code> if the element is pushed successfully.
     * @throws NullPointerException If the specified element is <code>null</code>
     *                              and the stack implementation does not support
     *                              having <code>null</code> elements.
     */
    public boolean push(E toAdd) throws NullPointerException;

    /**
     * Removes the object at the top of this stack and returns that object.
     * 
     * @return The element at the top of this stack.
     * @throws EmptyStackException If this stack is empty.
     */
    public E pop() throws EmptyStackException;

    /**
     * Looks at the object at the top of this stack without removing it.
     * 
     * @return The element at the top of this stack.
     * @throws EmptyStackException If this stack is empty.
     */
    public E peek() throws EmptyStackException;

    /**
     * Removes all of the elements from this stack. This stack will be empty after
     * this call returns.
     */
    public void clear();

    /**
     * Checks if the stack is empty.
     * 
     * @return <code>true</code> if this stack contains no elements.
     */
    public boolean isEmpty();

    /**
     * Returns an array containing all of the elements in this stack in proper
     * sequence (from top to bottom). Obeys the general contract of the
     * <code>java.util.Collection.toArray()</code> method.
     * 
     * @return An array containing all of the elements in this stack in proper
     *         sequence.
     */
    public Object[] toArray();

    /**
     * Returns an array containing all of the elements in this stack in proper
     * sequence (from top to bottom). Obeys the general contract of the
     * <code>java.util.Collection.toArray(Object [])</code> method.
     * 
     * @param toHold The array into which the elements of this stack are to be
     *               stored, if it is big enough; otherwise, a new array of the same
     *               runtime type is allocated for this purpose.
     * @return An array containing the elements of this stack.
     * @throws NullPointerException If the specified array is <code>null</code>.
     */
    public E[] toArray(E[] toHold) throws NullPointerException;

    /**
     * Returns true if this stack contains at least one element
     * <code>e</code> such that <code>toFind.equals(e)</code>.
     * 
     * @param toFind The element whose presence in this stack is to be tested.
     * @return <code>true</code> if this stack contains the specified element.
     * @throws NullPointerException If the specified element is <code>null</code>
     *                              and the stack implementation does not support
     *                              having <code>null</code> elements.
     */
    public boolean contains(E toFind) throws NullPointerException;

    /**
     * Returns the position where an object is on this stack. If the
     * object <code>toFind</code> occurs as an item in this stack, this method
     * returns the distance from the top of the stack of the occurrence nearest
     * the top of the stack. The <code>equals</code> method is used to compare
     * <code>toFind</code> to the items in this stack.
     * 
     * @param toFind The desired object.
     * @return The 1-based position from the top of the stack where the object is
     *         located; the return value <code>-1</code> indicates that the object
     *         is not on the stack.
     * @throws NullPointerException If the specified element is <code>null</code>
     *                              and the stack implementation does not support
     *                              having <code>null</code> elements.
     */
    public int search(E toFind) throws NullPointerException;

    /**
     * Returns an iterator over the elements in this stack in proper sequence
     * (from top to bottom).
     * 
     * @return An iterator over the elements in this stack in proper sequence. NB:
     *         The return is of type <code>utilities.Iterator<E></code>, not
     *         <code>java.util.Iterator</code>.
     */
    public Iterator<E> iterator();

    /**
     * Compares the specified object with this stack for equality. Returns
     * <code>true</code> if and only if the specified object is also a stack, both
     * stacks have the same size, and all corresponding pairs of elements in the
     * two stacks are equal.
     * 
     * @param that The stack to be compared for equality with this stack.
     * @return <code>true</code> if the specified stack is equal to this stack.
     */
    public boolean equals(StackADT<E> that);

    /**
     * The size method will return the current element count contained in the stack.
     * 
     * @return The current element count.
     */
    public int size();

    /**
     * Checks if the stack has reached its maximum capacity. For implementations
     * with no fixed capacity, this method should always return <code>false</code>.
     * 
     * @return <code>true</code> if the stack is full, <code>false</code> otherwise.
     */
    public boolean stackOverflow();
}
