import java.util.ArrayList;
import java.util.List;

interface HistoryManager {
    void addTaskInHistory(Task task);
    List<Task> getHistory();
    void  remove(int id);
}