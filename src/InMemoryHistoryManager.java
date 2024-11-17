import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> history = new HashMap<>();

    private static class Node {
        private final Task task;
        private Node prev;
        private Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    @Override
    public void addTaskInHistory(Task task) {
        if (task == null) {
            return;
        }
        if (history.containsKey(task.getIdOfTask())) {
            removeNode(history.get(task.getIdOfTask()));
        }
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        history.put(task.getIdOfTask(), newNode);
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        history.remove(node.task.getIdOfTask());
    }

    private ArrayList<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return (ArrayList<Task>) tasks;
    }
}
