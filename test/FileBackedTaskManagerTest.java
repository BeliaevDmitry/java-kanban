import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import data.Status;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

        private FileBackedTaskManager manager;
        private Path tempFile;

        @Before
        public void setUp() throws IOException {
            tempFile = Files.createTempFile("test", ".csv");
            manager = new FileBackedTaskManager(Path.of(tempFile.toString()));
            Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
            manager.addTask(task1);
            // Добавьте тестовые данные в менеджер
        }



        @Test
        public void testSave() throws IOException {
            // Сохраните данные в файл
            manager.save();

            // Проверьте, что файл был создан
            assertTrue(Files.exists(tempFile),"проблема с сохранением файла");

            // Проверьте содержимое файла
            List<String> lines = Files.readAllLines(tempFile, StandardCharsets.UTF_8);
            assertEquals(lines.getFirst(), "id,type,name,status,description,epic","неправильно сверяет 1 строчку");
            // Проверьте, что данные были сохранены в файл правильно
            for (Task task : manager.getAllTasks()) {
                assertTrue(lines.contains(task.toString()));
            }
            for (Task epic : manager.getAllEpics()) {
                assertTrue(lines.contains(epic.toString()));
            }
            for (Task subtask : manager.getAllSubtasks()) {
                assertTrue(lines.contains(subtask.toString()));
            }
        }
    }
