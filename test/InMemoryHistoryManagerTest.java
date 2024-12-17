import data.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;


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
        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.NEW, LocalDateTime.now(), 13);
        taskManager.addTask(task3);
        taskManager.getTaskById(1).setStatus(Status.IN_PROGRESS);
        List<Task> history = taskManager.getHistory();
        assertNotEquals(Status.NEW, history.get(0).getStatus(), "проверка setStatus завалена");
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

    @Test
    void emptyHistory() {
        List<Task> history = manager.getHistory();

        assertEquals(0, history.size(), "История должна содержать 0 записей");

    }

    @Test
    void deleteTaskInHistory() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);
        task1.setIdOfTask(0);
        manager.addTaskInHistory(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW, LocalDateTime.now().plusHours(1), 13);
        task2.setIdOfTask(1);
        manager.addTaskInHistory(task2);


        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        epic1.setIdOfTask(2);
        manager.addTaskInHistory(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(3), 13);
        subtask1.setIdOfTask(3);
        manager.addTaskInHistory(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        subtask2.setIdOfTask(4);
        manager.addTaskInHistory(subtask2);

       assertEquals(5, manager.getHistory().size(), "История должна содержать только 5 записей");

       manager.remove(0);
       assertEquals(manager.getHistory().getFirst(),task2,"После удаления 1 записи 2 запись не стала первой");

        manager.remove(5);
        assertEquals(manager.getHistory().getLast(),subtask2,"После удаления последней записи 5 запись не стала последней");

        manager.remove(2);
        assertEquals(manager.getHistory().getLast(),subtask2,"После удаления средней записи 4 запись не стала последней");

    }
}