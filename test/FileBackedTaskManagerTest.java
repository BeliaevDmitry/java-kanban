import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import data.Status;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    public void testSave() throws IOException {
        Path tempFile = Files.createTempFile("test", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(Path.of(tempFile.toString()));
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);
        manager.addTask(task1);
        assertTrue(Files.exists(tempFile), "проблема с сохранением файла");
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(Path.of(tempFile.toString()));
        assertEquals(manager.getAllTasks().size(), manager2.getAllTasks().size(), "Количество задач не совпадает"); // Сравнение количества задач
        assertEquals(manager.getAllTasks().getFirst().getTitle(), manager2.getAllTasks().getFirst().getTitle(), "Имена задач не совпадают"); // Сравнение имени первой задачи
        assertEquals(manager.getAllTasks().getFirst().getDescription(), manager2.getAllTasks().getFirst().getDescription(), "Описания задач не совпадают"); // Сравнение описания первой задачи
        assertEquals(manager.getAllTasks().getFirst().getStatus(), manager2.getAllTasks().getFirst().getStatus(), "Статусы задач не совпадают"); // Сравнение статуса первой задачи
    }

    @Test
    public void testSave1() {
        // Проверяем, что исключение не выброшено, если указан допустимый путь к файлу.
        assertDoesNotThrow(() -> new FileBackedTaskManager(Path.of("src", "resources", "savedTasks.csv")),
                "проблема с путём к файлу");
    }
}
