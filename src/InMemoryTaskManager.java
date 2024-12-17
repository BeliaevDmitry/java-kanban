import data.Status;
import exceptions.TaskValidationTimeException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager extends Managers implements TaskManager {
    private final HashMap<Integer, Task> taskCollection = new HashMap<>();
    private final HashMap<Integer, Epic> epicCollection = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskCollection = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public void setIdOfTasks(int idOfTasks) {
        this.idOfTasks = idOfTasks;
    }

    private int idOfTasks = 0;

    @Override
    public void addTask(Task task) {
        overlappingIntervals(task);
        task.setIdOfTask(calculateId());
        taskCollection.put(task.getIdOfTask(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        overlappingIntervals(task);
        if (taskCollection.containsKey(task.getIdOfTask())) {
            taskCollection.put(task.getIdOfTask(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicCollection.containsKey(epic.getIdOfTask())) {
            calculateEpicStatus(epic);
            epicCollection.put(epic.getIdOfTask(), epic);
            prioritizedTasks.add(epic);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setIdOfTask(calculateId());
        epicCollection.put(epic.getIdOfTask(), epic);
        prioritizedTasks.add(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        overlappingIntervals(subtask);
        subtask.setIdOfTask(calculateId());
        subtaskCollection.put(subtask.getIdOfTask(), subtask);

        if (epicCollection.containsKey(subtask.getEpicId())) {
            Epic epic = epicCollection.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtaskId(subtask.getIdOfTask());
                calculateEpicStatus(epic);
                epicStartTimeAndDuration(epic);
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskCollection.containsKey(subtask.getIdOfTask())) {
            subtaskCollection.put(subtask.getIdOfTask(), subtask);
            calculateEpicStatus(epicCollection.get(subtask.getEpicId()));
            epicStartTimeAndDuration(epicCollection.get(subtask.getEpicId()));
            overlappingIntervals(subtask);
            prioritizedTasks.add(subtask);
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
            prioritizedTasks.remove(getTaskById(id));
        }
        taskCollection.clear();
    }

    @Override
    public void removeAllEpics() {
        for (int id : epicCollection.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getEpicById(id));
        }
        for (int id : subtaskCollection.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getSubtaskById(id));
        }
        subtaskCollection.clear();
        epicCollection.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (int id : subtaskCollection.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getSubtaskById(id));
        }
        subtaskCollection.clear();
        for (Epic epic : epicCollection.values()) {
            epic.getSubtasksIds().clear();
            epic.setStatus(Status.NEW);
            epicStartTimeAndDuration(epic);
        }

    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        if (taskCollection.containsKey(idOfTask)) {
            prioritizedTasks.remove(getTaskById(idOfTask));
            taskCollection.remove(idOfTask);
            historyManager.remove(idOfTask);
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    @Override
    public void removeEpic(int idOfTask) {
        if (epicCollection.containsKey(idOfTask)) {
            for (int keyOfSubtask : epicCollection.get(idOfTask).getSubtasksIds()) {
                subtaskCollection.remove(keyOfSubtask);
                historyManager.remove(keyOfSubtask);
                prioritizedTasks.remove(getEpicById(idOfTask));
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
            epicStartTimeAndDuration(epicCollection.get(idOfEpic));
            prioritizedTasks.remove(getSubtaskById(idOfTask));
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        return (ArrayList<Subtask>) epic.getSubtasksIds().stream()
                .map(subtaskCollection::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public List<Subtask> getSubtaskEpic(Epic epic) {
        return epic.getSubtasksIds().stream().map(subtaskCollection::get).toList();
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

    public void epicStartTimeAndDuration(Epic epic) {
        LocalDateTime startTime = getSubtasksOfEpic(epic).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        epic.setStartTime(startTime);

        try {
            prioritizedTasks.add(epic);
        } catch (NullPointerException e) {
            System.err.println("Ошибка при добавлении эпоса: " + epic + " - значение равно null");
            // Возможно, стоит предпринять дополнительные действия по обработке ошибки
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            // Возможно, стоит предпринять дополнительные действия по обработке ошибки
        }


        LocalDateTime endTime = getSubtaskEpic(epic).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setEndTime(endTime);

        Duration duration = getSubtaskEpic(epic).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(duration);
    }

    public void printPrioritizedTasks() {
        System.out.println("Приоритетные задачи:");
        for (Task task : prioritizedTasks) {
            System.out.println(task);
        }
    }

    public void overlappingIntervals(Task task) {
        if (prioritizedTasks.isEmpty()) return;
        for (Task newTask : prioritizedTasks) {
            if (task.getStartTime().isBefore(newTask.getEndTime())
                    && task.getEndTime().isAfter(newTask.getStartTime())) {
                throw new TaskValidationTimeException("Временные интервалы для задач пересекаются");
            }
        }
    }
}