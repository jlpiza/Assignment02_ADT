package implementations;

import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

public class MyArrayList<E> implements ListADT<E> {

    private static final int DEFAULT_CAPACITY = 10;

    /** backing array */
    private E[] elements;

    /** number of stored items */
    private int size;

    @SuppressWarnings("unchecked")
    public MyArrayList() {
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /** make sure array is big enough */
    @SuppressWarnings("unchecked")
    private void ensureCapacity(int minCapacity) {
        if (elements.length >= minCapacity) return;

        int newCapacity = elements.length * 2;
        if (newCapacity < minCapacity) newCapacity = minCapacity;

        E[] newArr = (E[]) new Object[newCapacity];
        System.arraycopy(elements, 0, newArr, 0, size);
        elements = newArr;
    }

    /** index check for get/set/remove */
    private void checkElementIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }

    /** index check for add at position */
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) elements[i] = null;
        size = 0;
    }

    @Override
    public boolean add(int index, E toAdd) {
        if (toAdd == null) throw new NullPointerException("cannot add null");
        checkPositionIndex(index);

        ensureCapacity(size + 1);

        // shift right to open space
        for (int i = size; i > index; i--)
            elements[i] = elements[i - 1];

        elements[index] = toAdd;
        size++;
        return true;
    }

    @Override
    public boolean add(E toAdd) {
        if (toAdd == null) throw new NullPointerException("cannot add null");

        ensureCapacity(size + 1);
        elements[size++] = toAdd;
        return true;
    }

    @Override
    public boolean addAll(ListADT<? extends E> toAdd) {
        if (toAdd == null) throw new NullPointerException("list cannot be null");

        Iterator<? extends E> it = toAdd.iterator();
        boolean changed = false;

        while (it.hasNext()) {
            add(it.next());
            changed = true;
        }
        return changed;
    }

    @Override
    public E get(int index) {
        checkElementIndex(index);
        return elements[index];
    }

    @Override
    public E remove(int index) {
        checkElementIndex(index);

        E removed = elements[index];

        // shift left to fill the gap
        for (int i = index; i < size - 1; i++)
            elements[i] = elements[i + 1];

        elements[size - 1] = null;
        size--;
        return removed;
    }

    @Override
    public E remove(E toRemove) {
        if (toRemove == null) throw new NullPointerException("cannot remove null");

        for (int i = 0; i < size; i++) {
            if (toRemove.equals(elements[i]))
                return remove(i);
        }
        return null; // not found
    }

    @Override
    public E set(int index, E toChange) {
        if (toChange == null) throw new NullPointerException("cannot set null");
        checkElementIndex(index);

        E old = elements[index];
        elements[index] = toChange;
        return old;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E toFind) {
        if (toFind == null) throw new NullPointerException("cannot search for null");

        for (int i = 0; i < size; i++) {
            if (toFind.equals(elements[i])) return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray(E[] toHold) {
        if (toHold == null) throw new NullPointerException("array cannot be null");

        if (toHold.length < size) {
            Class<?> type = toHold.getClass().getComponentType();
            E[] newArr = (E[]) java.lang.reflect.Array.newInstance(type, size);
            for (int i = 0; i < size; i++) newArr[i] = elements[i];
            return newArr;
        }

        for (int i = 0; i < size; i++) toHold[i] = elements[i];
        if (toHold.length > size) toHold[size] = null;
        return toHold;
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        System.arraycopy(elements, 0, arr, 0, size);
        return arr;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayListIterator();
    }

    /** iterator over a fixed snapshot of the list */
    private class ArrayListIterator implements Iterator<E> {

        private Object[] snapshot;
        private int idx = 0;

        private ArrayListIterator() {
            snapshot = toArray();
        }

        @Override
        public boolean hasNext() {
            return idx < snapshot.length;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            return (E) snapshot[idx++];
        }
    }
}
