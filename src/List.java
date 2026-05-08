import java.io.Serializable;

public class List<V> implements Serializable {
    private Node<V> head;
    private Node<V> tail;
    private String name;

    public List() {
        this("list");
    }

    public List(String name) {
        this.name = name;
        head = tail = null;
    }

    public void insertAtFront(V insertItem) {
        if (isEmpty()) {
            head = tail = new Node<V>(insertItem);
        } else {
            head = new Node<V>(insertItem, head);
        }
    }

    public void insertAtBack(V insertItem) {
        if (isEmpty()) {
            head = tail = new Node<V>(insertItem);
        } else {
            tail = tail.nextNode = new Node<V>(insertItem);
        }
    }

    public V removeFromFront() {
        V removedItem = head.data;
        if (head == tail) {
            head = tail = null;
        } else {
            head = head.nextNode;
        }
        return removedItem;
    }

    public V removeFromBack() {
        V removedItem = tail.data;
        if (head == tail) {
            head = tail = null;
        } else {
            Node<V> current = head;
            while (current.nextNode != tail) {
                current = current.nextNode;
            }
            tail = current;
            current.nextNode = null;
        }
        return removedItem;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void print() {
        if (isEmpty()) {
            System.out.printf("Empty %s%n", name);
            return;
        }
        System.out.printf("The %s is: ", name);
        Node<V> current = head;
        while (current != null) {
            System.out.printf("%s ", current.data);
            current = current.nextNode;
        }
        System.out.println();
    }

    public void insertAt(int index, V insertItem) throws IndexOutOfBoundsException {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        if (index == 0) {
            insertAtFront(insertItem);
            return;
        }
        if (index == size()) {
            insertAtBack(insertItem);
            return;
        }
        Node<V> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.nextNode;
        }
        current.nextNode = new Node<V>(insertItem, current.nextNode);
    }

    public V remove(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        if (index == 0) {
            return removeFromFront();
        }
        if (index == size() - 1) {
            return removeFromBack();
        }
        Node<V> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.nextNode;
        }
        V removedItem = current.nextNode.data;
        current.nextNode = current.nextNode.nextNode;
        return removedItem;
    }

    public V get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        Node<V> current = head;
        for (int i = 0; i < index; i++) {
            current = current.nextNode;
        }
        return current.data;
    }

    public int size() {
        int count = 0;
        Node<V> current = head;
        while (current != null) {
            count++;
            current = current.nextNode;
        }
        return count;
    }
}
