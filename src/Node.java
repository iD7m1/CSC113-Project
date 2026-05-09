import java.io.Serializable;

public class Node<T> implements Serializable {
    public T data;
    public Node<T> nextNode;

    public Node(T object) {
        this(object, null);
    }

    public Node(T object, Node<T> node) {
        this.data = object;
        this.nextNode = node;
    }

    public T getData() {
        return data;
    }

    public Node<T> getNext() {
        return nextNode;
    }
}
