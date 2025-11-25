package implementations;

import java.util.EmptyStackException;
import utilities.Iterator;
import utilities.StackADT;

public class MyStack<E> implements StackADT<E> {

    private MyArrayList<E> list;

    public MyStack() {
        list = new MyArrayList<>();
    }

    @Override
    public void push(E toAdd) throws NullPointerException {
        if (toAdd == null)
            throw new NullPointerException("Cannot push null onto stack.");
        list.add(toAdd);
    }

    @Override
    public E pop() throws EmptyStackException {
        if (isEmpty())
            throw new EmptyStackException();
        return list.remove(list.size() - 1);
    }

    @Override
    public E peek() throws EmptyStackException {
        if (isEmpty())
            throw new EmptyStackException();
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
        Object[] arr = new Object[list.size()];
        int topIndex = list.size() - 1;

        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(topIndex - i); 
        }
        return arr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray(E[] holder) throws NullPointerException {
        if (holder == null)
            throw new NullPointerException("Holder array cannot be null.");

        int size = list.size();

        if (holder.length < size) {
            holder = (E[]) java.lang.reflect.Array
                    .newInstance(holder.getClass().getComponentType(), size);
        }

        int topIndex = size - 1;

        for (int i = 0; i < size; i++) {
            holder[i] = list.get(topIndex - i);
        }

        if (holder.length > size)
            holder[size] = null;

        return holder;
    }

    @Override
    public boolean contains(E toFind) throws NullPointerException {
        return list.contains(toFind);
    }

    @Override
    public int search(E toFind) {
        if (toFind == null)
            return -1;

        for (int i = list.size() - 1, pos = 1; i >= 0; i--, pos++) {
            if (toFind.equals(list.get(i)))
                return pos;
        }
        return -1;
    }


    @Override
    public Iterator<E> iterator() {
        return new StackIterator();
    }

    private class StackIterator implements Iterator<E> {

        private Object[] snapshot;
        private int index = 0;

        public StackIterator() {
            Object[] normal = list.toArray();
            snapshot = new Object[normal.length];

            int topIndex = normal.length - 1;

            for (int i = 0; i < normal.length; i++) {
                snapshot[i] = normal[topIndex - i];
            }
        }

        @Override
        public boolean hasNext() {
            return index < snapshot.length;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            return (E) snapshot[index++];
        }
    }

    @Override
    public boolean equals(StackADT<E> that) {
        if (that == null || this.size() != that.size()) return false;

        Iterator<E> it1 = this.iterator();
        Iterator<E> it2 = that.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            if (!it1.next().equals(it2.next()))
                return false;
        }

        return true;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean stackOverflow() {
        return false;
    }
}
