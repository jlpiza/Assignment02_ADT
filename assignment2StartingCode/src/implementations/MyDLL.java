package implementations;

import utilities.ListADT;
import utilities.Iterator;
import java.util.NoSuchElementException;

/**
 * MyDLL - Doubly Linked List implementation of ListADT
 * @param <E> the type of elements in this list
 */
public class MyDLL<E> implements ListADT<E> {
    private MyDLLNode<E> head;
    private MyDLLNode<E> tail;
    private int size;
    
    /**
     * Default constructor
     */
    public MyDLL() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
    
    @Override
    public boolean add(int index, E toAdd) throws NullPointerException, IndexOutOfBoundsException {
        // Check for null element
        if (toAdd == null) {
            throw new NullPointerException("Cannot add null element to the list");
        }
        
        // Check for valid index
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        // Case 1: Adding to empty list
        if (isEmpty()) {
            MyDLLNode<E> newNode = new MyDLLNode<>(toAdd);
            head = newNode;
            tail = newNode;
        }
        // Case 2: Adding at the beginning
        else if (index == 0) {
            MyDLLNode<E> newNode = new MyDLLNode<>(toAdd, null, head);
            head.setPrevious(newNode);
            head = newNode;
        }
        // Case 3: Adding at the end
        else if (index == size) {
            MyDLLNode<E> newNode = new MyDLLNode<>(toAdd, tail, null);
            tail.setNext(newNode);
            tail = newNode;
        }
        // Case 4: Adding in the middle
        else {
            MyDLLNode<E> current = getNodeAt(index);
            MyDLLNode<E> previous = current.getPrevious();
            MyDLLNode<E> newNode = new MyDLLNode<>(toAdd, previous, current);
            
            previous.setNext(newNode);
            current.setPrevious(newNode);
        }
        
        size++;
        return true;
    }
    
    @Override
    public boolean add(E toAdd) throws NullPointerException {
        if (toAdd == null) {
            throw new NullPointerException("Cannot add null element");
        }
        return add(size, toAdd);
    }
    
    @Override
    public boolean addAll(ListADT<? extends E> toAdd) throws NullPointerException {
        if (toAdd == null) {
            throw new NullPointerException("Cannot add null list");
        }
        
        Iterator<? extends E> iterator = toAdd.iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();
            add(element); 
        }
        
        return true;
    }
    
    @Override
    public E get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        MyDLLNode<E> node = getNodeAt(index);
        return node.getElement();
    }
    
    @Override
    public E remove(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        E removedElement;
        
        // Case 1: Removing the only element
        if (size == 1) {
            removedElement = head.getElement();
            head = null;
            tail = null;
        }
        // Case 2: Removing from the beginning
        else if (index == 0) {
            removedElement = head.getElement();
            head = head.getNext();
            head.setPrevious(null);
        }
        // Case 3: Removing from the end
        else if (index == size - 1) {
            removedElement = tail.getElement();
            tail = tail.getPrevious();
            tail.setNext(null);
        }
        // Case 4: Removing from the middle
        else {
            MyDLLNode<E> toRemove = getNodeAt(index);
            removedElement = toRemove.getElement();
            
            MyDLLNode<E> previous = toRemove.getPrevious();
            MyDLLNode<E> next = toRemove.getNext();
            
            previous.setNext(next);
            next.setPrevious(previous);
        }
        
        size--;
        return removedElement;
    }
    
    @Override
    public E remove(E toRemove) throws NullPointerException {
        if (toRemove == null) {
            throw new NullPointerException("Cannot remove null element");
        }
        
        // Find the node containing the element
        MyDLLNode<E> current = head;
        int index = 0;
        
        while (current != null) {
            if (toRemove.equals(current.getElement())) {
                return remove(index);
            }
            current = current.getNext();
            index++;
        }
        
        // Element not found
        return null;
    }
    
    @Override
    public E set(int index, E toChange) throws NullPointerException, IndexOutOfBoundsException {
        if (toChange == null) {
            throw new NullPointerException("Cannot set null element");
        }
        
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        MyDLLNode<E> node = getNodeAt(index);
        E oldElement = node.getElement();
        node.setElement(toChange);
        
        return oldElement;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public boolean contains(E toFind) throws NullPointerException {
        if (toFind == null) {
            throw new NullPointerException("Cannot search for null element");
        }
        
        MyDLLNode<E> current = head;
        while (current != null) {
            if (toFind.equals(current.getElement())) {
                return true;
            }
            current = current.getNext();
        }
        
        return false;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray(E[] toHold) throws NullPointerException {
        if (toHold == null) {
            throw new NullPointerException("Array cannot be null");
        }
        
        // If the array is too small, create a new one
        if (toHold.length < size) {
            toHold = (E[]) java.lang.reflect.Array.newInstance(
                toHold.getClass().getComponentType(), size);
        }
        
        MyDLLNode<E> current = head;
        int i = 0;
        while (current != null) {
            toHold[i++] = current.getElement();
            current = current.getNext();
        }
        
        // Set remaining elements to null if array was larger
        for (int j = size; j < toHold.length; j++) {
            toHold[j] = null;
        }
        
        return toHold;
    }
    
    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        MyDLLNode<E> current = head;
        int i = 0;
        
        while (current != null) {
            array[i++] = current.getElement();
            current = current.getNext();
        }
        
        return array;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new DLLIterator();
    }
    
    /**
     * Helper method to get node at specific index
     * @param index the index of the node to retrieve
     * @return the node at the specified index
     */
    private MyDLLNode<E> getNodeAt(int index) {
        // Optimize: traverse from head if index is in first half, from tail if in second half
        if (index < size / 2) {
            // Traverse from head
            MyDLLNode<E> current = head;
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
            return current;
        } else {
            // Traverse from tail
            MyDLLNode<E> current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.getPrevious();
            }
            return current;
        }
    }
    
    /**
     * Iterator implementation for MyDLL
     */
    private class DLLIterator implements Iterator<E> {
        private MyDLLNode<E> current;
        private int currentIndex;
        
        public DLLIterator() {
            this.current = head;
            this.currentIndex = 0;
        }
        
        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }
        
        @Override
        public E next() throws NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the list");
            }
            
            E element = current.getElement();
            current = current.getNext();
            currentIndex++;
            
            return element;
        }
    }
}