package implementations;

/**
 * Node class for MyDLL (Doubly Linked List)
 * @param <E> the type of element stored in the node
 */
public class MyDLLNode<E> {
    private E element;
    private MyDLLNode<E> next;
    private MyDLLNode<E> previous;
    
    /**
     * Constructor for creating a node with element
     * @param element the element to store in this node
     */
    public MyDLLNode(E element) {
        this.element = element;
        this.next = null;
        this.previous = null;
    }
    
    /**
     * Constructor for creating a node with element and links
     * @param element the element to store
     * @param previous reference to previous node
     * @param next reference to next node
     */
    public MyDLLNode(E element, MyDLLNode<E> previous, MyDLLNode<E> next) {
        this.element = element;
        this.previous = previous;
        this.next = next;
    }
    
    // Getters and Setters
    public E getElement() {
        return element;
    }
    
    public void setElement(E element) {
        this.element = element;
    }
    
    public MyDLLNode<E> getNext() {
        return next;
    }
    
    public void setNext(MyDLLNode<E> next) {
        this.next = next;
    }
    
    public MyDLLNode<E> getPrevious() {
        return previous;
    }
    
    public void setPrevious(MyDLLNode<E> previous) {
        this.previous = previous;
    }
}