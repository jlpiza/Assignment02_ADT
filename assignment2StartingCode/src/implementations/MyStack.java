package implementations;

import java.util.EmptyStackException;
import utilities.Iterator;
import utilities.StackADT;

/*
 * MyStack<E> – Stack implementation backed by a dynamic MyArrayList.
 * This class provides a LIFO (Last-In, First-Out) stack structure where
 * elements are pushed and popped from the end of the underlying
 * MyArrayList. 
 * The implementation supports push, pop, peek, clear, contains, search,
 * iteration (top to bottom), and array conversion.
 */

public class MyStack<E> implements StackADT<E> {

    private MyArrayList<E> list;

    public MyStack() {
        list = new MyArrayList<>();
    }

    @Override
    public void push(E toAdd) throws NullPointerException {
        if (toAdd == null) {
            throw new NullPointerException("Cannot push null onto stack.");
        }
        list.add(toAdd); 
    }

    @Override
    public E pop() throws EmptyStackException {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.remove(list.size() - 1); 
    }

    @Override
    public E peek() throws EmptyStackException {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.get(list.size() - 1); 
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Object[] toArray() {
        // Return in stack order (top to bottom) - LIFO
        Object[] array = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(list.size() - 1 - i);
        }
        return array;
    }

    /**
     * Returns an array containing all elements in this collection in proper sequence.
     * The runtime type of the returned array is that of the specified array.
     * 
     * @param holder the array into which elements are to be stored
     * @return an array containing all elements in this collection
     * @throws NullPointerException if the specified array is null
     */
    @Override
    @SuppressWarnings("unchecked")
    // This cast is safe because:
    // 1. We create the array using Array.newInstance() with the exact component type
    // 2. All elements added to the array are of type E (enforced by the collection)
    // 3. The array is populated only with elements from this collection
    // 4. The collection's generic type ensures type safety of stored elements
    public E[] toArray(E[] holder) throws NullPointerException {
        if (holder == null) {
            throw new NullPointerException("Holder array cannot be null.");
        }
        
        int size = list.size();
        if (holder.length < size) {
            holder = (E[]) java.lang.reflect.Array.newInstance(
                holder.getClass().getComponentType(), size);
        }
        
        // Fill in stack order (top to bottom) - LIFO
        for (int i = 0; i < size; i++) {
            holder[i] = list.get(size - 1 - i);
        }
        
        // Set remaining to null if array was larger
        if (holder.length > size) {
            for (int i = size; i < holder.length; i++) {
                holder[i] = null;
            }
        }
        
        return holder;
    }

    @Override
    public boolean contains(E toFind) throws NullPointerException {
        if (toFind == null) {
            throw new NullPointerException("Cannot search for null element.");
        }
        return list.contains(toFind);
    }

    @Override
    public int search(E toFind) {
        if (toFind == null) {
            return -1;
        }
        
        // Search from top (end of list) to bottom (start of list)
        // Returns 1-based position from top
        for (int i = list.size() - 1; i >= 0; i--) {
            if (toFind.equals(list.get(i))) {
                return list.size() - i;
            }
        }
        return -1;
    }

    @Override
    public Iterator<E> iterator() {
        return new StackIterator();
    }

    @Override
    public boolean equals(StackADT<E> that) {
        if (that == null || this.size() != that.size()) {
            return false;
        }

        Iterator<E> it1 = this.iterator();
        Iterator<E> it2 = that.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            E elem1 = it1.next();
            E elem2 = it2.next();
            
            // Handle null elements
            if (elem1 == null && elem2 == null) {
                continue;
            }
            if (elem1 == null || elem2 == null || !elem1.equals(elem2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean stackOverflow() {
        return false; // ArrayList-based stack never overflows
    }

    /**
     * Iterator for stack (top to bottom order - LIFO)
     */
    private class StackIterator implements Iterator<E> {
        private int currentIndex;
        
        public StackIterator() {
            this.currentIndex = list.size() - 1;
        }
        
        @Override
        public boolean hasNext() {
            return currentIndex >= 0;
        }
        
        @Override
        public E next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException("No more elements in stack");
            }
            return list.get(currentIndex--);
        }
    }
}