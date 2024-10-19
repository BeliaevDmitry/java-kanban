import java.util.ArrayList;

interface HistoryManager {

    void addTaskInHistory(Task task);

    ArrayList<Task> getHistory();
}