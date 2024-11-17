import data.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    public InMemoryTaskManager manager;

    public InMemoryTaskManagerTest() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addTask() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        manager.addTask(task2);

        assertEquals(1, task1.getIdOfTask(), "неверный id присваивания");
        assertEquals(2, task2.getIdOfTask(), "неверный id присваивания");

    }


    @Test
    void addSubtask() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getIdOfTask());
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getIdOfTask());
        manager.addSubtask(subtask2);

        assertEquals(2, subtask1.getIdOfTask(), "неверный id присваивания");
        assertEquals(3, subtask2.getIdOfTask(), "неверный id присваивания");


    }

    @Test
    void testAddSubtaskId_CantAddSelfAsSubtask() {
        Epic epic = new Epic("Epic Title", "Epic Description", Status.NEW);
        int epicId = epic.getIdOfTask(); // Assuming getIdOfTask() returns a unique ID

        epic.addSubtaskId(epicId);

        // Verify that the epic's subtasks list does not contain its own ID
        assertFalse(epic.getSubtasksIds().contains(epicId));
    }

    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, 1);
        assertEquals(1, subtask.getEpicId());

        // Попытка установить себя в качестве эпика
        subtask.setEpicId(subtask.getEpicId());

        // Проверка, что эпик остался прежним (не изменился)
        assertEquals(1, subtask.getEpicId());
    }


    @Test
    void testAddTask() {

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getIdOfTask());
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getIdOfTask());
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.NEW);
        manager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.NEW, epic2.getIdOfTask());
        manager.addSubtask(subtask3);

        assertEquals(2, manager.getAllTasks().size());
        assertEquals(2, manager.getAllEpics().size());
        assertEquals(3, manager.getAllSubtasks().size());


    }
}
