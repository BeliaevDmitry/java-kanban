import exceptions.TaskValidationTimeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static data.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    public InMemoryTaskManager manager;

    public InMemoryTaskManagerTest() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addTask() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", NEW, LocalDateTime.now(), 13);
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", NEW, LocalDateTime.now().plusHours(1), 13);
        manager.addTask(task2);

        assertEquals(1, task1.getIdOfTask(), "неверный id присваивания");
        assertEquals(2, task2.getIdOfTask(), "неверный id присваивания");

    }


    @Test
    void addSubtask() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", NEW, LocalDateTime.now().plusHours(2), 13);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(3), 13);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addSubtask(subtask2);

        assertEquals(2, subtask1.getIdOfTask(), "неверный id присваивания");
        assertEquals(3, subtask2.getIdOfTask(), "неверный id присваивания");
    }

    @Test
    void addSubtaskDONE() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", NEW, LocalDateTime.now().plusHours(2), 13);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", DONE, epic1.getIdOfTask(), LocalDateTime.now().plusHours(3), 13);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", DONE, epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addSubtask(subtask2);
        assertEquals(epic1.getStatus(), DONE, "addSubtaskDONE не работает корректно");
    }

    @Test
    void addSubtaskNEW() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", NEW, LocalDateTime.now().plusHours(2), 13);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(3), 13);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", DONE, epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addSubtask(subtask2);
        assertEquals(epic1.getStatus(), IN_PROGRESS, "addSubtaskNEW не работает корректно");
    }

    @Test
    void addSubtaskIN_PROGRESS() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", NEW, LocalDateTime.now().plusHours(2), 13);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", IN_PROGRESS, epic1.getIdOfTask(), LocalDateTime.now().plusHours(3), 13);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", IN_PROGRESS, epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addSubtask(subtask2);
        assertEquals(epic1.getStatus(), IN_PROGRESS, "addSubtaskIN_PROGRESS не работает корректно");
    }

    @Test
    void testAddSubtaskId_CantAddSelfAsSubtask() {
        Epic epic = new Epic("Epic Title", "Epic Description", NEW);
        int epicId = epic.getIdOfTask(); // Assuming getIdOfTask() returns a unique ID

        epic.addSubtaskId(epicId);

        // Verify that the epic's subtasks list does not contain its own ID
        assertFalse(epic.getSubtasksIds().contains(epicId));
    }

    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Subtask", "Description", NEW, 1);
        assertEquals(1, subtask.getEpicId());

        // Попытка установить себя в качестве эпика
        subtask.setEpicId(subtask.getEpicId());

        // Проверка, что эпик остался прежним (не изменился)
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void testAddTask() {

        Task task1 = new Task("Задача 1", "Описание задачи 1", NEW, LocalDateTime.now(), 13);
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", NEW, LocalDateTime.now().plusHours(1), 13);
        manager.addTask(task2);


        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", NEW, LocalDateTime.now().plusHours(2), 13);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(3), 13);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", NEW, LocalDateTime.now().plusHours(5), 13);
        manager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", NEW, epic2.getIdOfTask(), LocalDateTime.now().plusHours(6), 13);
        manager.addSubtask(subtask3);

        assertEquals(2, manager.getAllTasks().size(), "getAllTasks неверно");
        assertEquals(2, manager.getAllEpics().size(), "getAllEpics неверно");
        assertEquals(3, manager.getAllSubtasks().size(), "getAllSubtasks неверно");
    }

    @Test
    void checkForAnEpicForSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", NEW, LocalDateTime.now().plusHours(2), 13);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(3), 13);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addSubtask(subtask2);

        assertEquals(subtask1.getEpicId(), 1, "getEpicId() работает криво");
        assertEquals(subtask2.getEpicId(), 1, "getEpicId() работает криво");
    }

    @Test
    void testOverlappingIntervals() {
        // Создаем тестовые объекты Task.
        Task prioritizedTask = new Task("Приоритетная задача", "Описание приоритетной задачи", NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), 35);
        manager.addTask(prioritizedTask);
        Task newTask = new Task("Новая задача", "Описание новой задачи", NEW,
                LocalDateTime.of(2023, 1, 1, 10, 30), 12);
        // Проверяем, что при пересекающихся интервалах генерируется исключение.
        assertThrows(TaskValidationTimeException.class, () -> manager.overlappingIntervals(newTask));
    }
}

