import java.util.ArrayList;
import java.util.List;

interface TaskManager {

    void addTask(Task task);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int idOfTask);

    Epic getEpicById(int idOfTask);

    Subtask getSubtaskById(int idOfTask);

    void removeTask(int idOfTask);

    void removeEpic(int idOfTask);

    void removeSubtask(int idOfTask);

    List<Task> getHistory();

    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    List<Subtask> getSubtaskEpic(Epic epic);

    List<Task> getPrioritizedTasks();

}