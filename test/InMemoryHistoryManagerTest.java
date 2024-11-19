import data.Status;
import org.junit.jupiter.api.Test;

import java.util.List;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    public InMemoryHistoryManager manager;

    public InMemoryHistoryManagerTest() {
        manager = new InMemoryHistoryManager();
    }


    @Test
    void addTaskInHistory() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        manager.addTaskInHistory(task1);
        List<Task> result = manager.getHistory();
        assertEquals(1, result.size());
        Task firstResult = result.getFirst();
        assertEquals(task1, firstResult);
    }

    @Test
    void addTaskInHistory_nullTask_doesNothing() {
        manager.addTaskInHistory(null);
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void addTaskInHistory_newTask_addsToHistory() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        manager.addTaskInHistory(task);
        assertEquals(1, manager.getHistory().size());
        assertEquals(task, manager.getHistory().get(0));
    }


    @Test
    void getHistory_returnsCopyOfHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setIdOfTask(0);
        manager.addTaskInHistory(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setIdOfTask(1);
        manager.addTaskInHistory(task2);
        List<Task> history = manager.getHistory();
        history.add(new Task("Task 3", "Description 3", Status.NEW));
        assertEquals(2, manager.getHistory().size(), "размер manager.getHistory().size() не равен 2");
        assertEquals(3, history.size(), "размер history.size() не равен 3");
    }

    @Test
    public void testHistoryManagerSavesPreviousTaskState() {
        TaskManager taskManager = Managers.getDefault();
        Task task3 = new Task("task3", "task3 description", Status.NEW);
        taskManager.addTask(task3);
        taskManager.getTaskById(1).setStatus(Status.IN_PROGRESS);
        List<Task> history = taskManager.getHistory();
        assertNotEquals(Status.NEW, history.get(0).getStatus());
    }

    @Test
    void shouldUpdateHistoryWhenTaskViewedAgain() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        manager.addTaskInHistory(task1);
        manager.addTaskInHistory(task1);

        List<Task> history = manager.getHistory();

        assertEquals(1, history.size(), "История должна содержать только одну запись для "
                + "уникальной задачи");
        assertEquals(task1, history.getFirst(), "Запись в истории должна быть task1");
    }
}