import controllers.FileBackedTaskManager;

import java.nio.file.Path;

public class Main {


    public static void main(String[] args) {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(Path.of("src/resources/savedTasks.csv"));
    }
}