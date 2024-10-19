import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>(10);

    @Override
    public void addTaskInHistory(Task task) {
        if (task == null ) {
            return;
        }
        Task newTask = new Task(task);
        if (history.size()>10) {
            history.removeFirst();
        }
        history.add(newTask);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
