import data.Status;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;


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
        ArrayList<Task> result = manager.getHistory();
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
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        manager.addTaskInHistory(task1);
        manager.addTaskInHistory(task2);
        ArrayList<Task> history = manager.getHistory();
        history.add(new Task("Task 3", "Description 3", Status.NEW));
        assertEquals(2, manager.getHistory().size());
        assertEquals(3, history.size());
    }

    @Test
    public void testHistoryManagerSavesPreviousTaskState() {
        TaskManager taskManager = Managers.getDefault();
        Task task3 = new Task("task3", "task3 description", Status.NEW);
        taskManager.addTask(task3);
        taskManager.getTaskById(1).setStatus(Status.IN_PROGRESS);
        taskManager.getTaskById(1);
        ArrayList<Task> history = taskManager.getHistory();
       assertNotEquals(Status.NEW , history.get(1).getStatus());
    }
}