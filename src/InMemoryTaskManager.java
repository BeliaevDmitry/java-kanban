import data.Status;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager extends Managers implements TaskManager {
    private final HashMap<Integer, Task> taskCollection = new HashMap<>();
    private final HashMap<Integer, Epic> epicCollection = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskCollection = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private int idOfTasks = 0;

    @Override
    public void addTask(Task task) {
        task.setIdOfTask(calculateId());
        taskCollection.put(task.getIdOfTask(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (taskCollection.containsKey(task.getIdOfTask())) {
            taskCollection.put(task.getIdOfTask(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicCollection.containsKey(epic.getIdOfTask())) {
            calculateEpicStatus(epic);
            epicCollection.put(epic.getIdOfTask(), epic);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setIdOfTask(calculateId());
        epicCollection.put(epic.getIdOfTask(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setIdOfTask(calculateId());
        subtaskCollection.put(subtask.getIdOfTask(), subtask);

        if (epicCollection.containsKey(subtask.getEpicId())) {
            Epic epic = epicCollection.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtaskId(subtask.getIdOfTask());
                calculateEpicStatus(epic);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskCollection.containsKey(subtask.getIdOfTask())) {
            subtaskCollection.put(subtask.getIdOfTask(), subtask);
            calculateEpicStatus(epicCollection.get(subtask.getEpicId()));
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskCollection.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicCollection.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskCollection.values());
    }

    @Override
    public void removeAllTasks() {
        for (int id : taskCollection.keySet()) {
            historyManager.remove(id);
        }
        taskCollection.clear();
    }

    @Override
    public void removeAllEpics() {
        for (int id : epicCollection.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtaskCollection.keySet()) {
            historyManager.remove(id);
        }
        subtaskCollection.clear();
        epicCollection.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (int id : subtaskCollection.keySet()) {
            historyManager.remove(id);
        }
        subtaskCollection.clear();
        for (Epic epic : epicCollection.values()) {
            epic.getSubtasksIds().clear();
            epic.setStatus(Status.NEW);
        }

    }

    @Override
    public Task getTaskById(int idOfTask) {
        historyManager.addTaskInHistory(taskCollection.get(idOfTask));
        return taskCollection.get(idOfTask);
    }

    @Override
    public Epic getEpicById(int idOfTask) {
        historyManager.addTaskInHistory(epicCollection.get(idOfTask));
        return epicCollection.get(idOfTask);
    }

    @Override
    public Subtask getSubtaskById(int idOfTask) {
        historyManager.addTaskInHistory(subtaskCollection.get(idOfTask));
        return subtaskCollection.get(idOfTask);
    }

    @Override
    public void removeTask(int idOfTask) {
        taskCollection.remove(idOfTask);
        historyManager.remove(idOfTask);
    }

    @Override
    public void removeEpic(int idOfTask) {
        if (epicCollection.containsKey(idOfTask)) {
            for (int keyOfSubtask : epicCollection.get(idOfTask).getSubtasksIds()) {
                subtaskCollection.remove(keyOfSubtask);
                historyManager.remove(keyOfSubtask);
            }
            epicCollection.remove(idOfTask);
            historyManager.remove(idOfTask);
        }

    }

    @Override
    public void removeSubtask(int idOfTask) {
        if (subtaskCollection.containsKey(idOfTask)) {
            int idOfEpic = subtaskCollection.get(idOfTask).getEpicId();
            subtaskCollection.remove(idOfTask);
            historyManager.remove(idOfTask);
            epicCollection.get(idOfEpic).removeSubtaskId(idOfTask);
            calculateEpicStatus(epicCollection.get(idOfEpic));
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>(epic.getSubtasksIds().size());
        for (Integer keySubtask : epic.getSubtasksIds()) {
            subtasksOfEpic.add(subtaskCollection.get(keySubtask));
        }
        return subtasksOfEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int calculateId() {
        return ++idOfTasks;
    }

    private void calculateEpicStatus(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Status status = Status.NEW;
        for (Integer idOfSubtask : epic.getSubtasksIds()) {
            if (subtaskCollection.get(idOfSubtask).getStatus() != Status.NEW) {
                status = Status.DONE;
                break;
            }
        }
        if (status == Status.NEW) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Integer idOfSubtask : epic.getSubtasksIds()) {
            if (subtaskCollection.get(idOfSubtask).getStatus() != Status.DONE) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        epic.setStatus(Status.DONE);
    }
}