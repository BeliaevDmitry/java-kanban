import data.Status;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import data.Type;
import exceptions.ManagerSaveException;
import static java.lang.Integer.*;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private Path path = Path.of("src", "resources", "savedTasks.csv"); //файл по умолчанию

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    public FileBackedTaskManager() {
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeTask(int idOfTask) {
        super.removeTask(idOfTask);
        save();
    }

    @Override
    public void removeEpic(int idOfTask) {
        super.removeEpic(idOfTask);
        save();
    }

    @Override
    public void removeSubtask(int idOfTask) {
        super.removeSubtask(idOfTask);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        try {
            List<String> lines = Files.readAllLines(file);
            return fromString(String.valueOf(lines));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static FileBackedTaskManager fromString(String value) throws IllegalArgumentException {
        FileBackedTaskManager tm = new FileBackedTaskManager();
        int idMax = 0;
        String[] line = value.split("\n");
        for (String s : line) {
            String[] element = s.split(",");

            try (int id = Integer.parseInt(element[0].substring(1)) ) {
                if (id > idMax) {
                    idMax = id;
                }

                Type type = Type.valueOf(element[1]);
                String title = element[2];
                Status status = Status.valueOf(element[3]);
                String description = element[4];

                switch (type) {
                    case TASK:
                        Task task = new Task(title, description, status);
                        tm.addTask(task);
                        task.setIdOfTask(id);
                        break;
                    case EPIC:
                        Epic epic = new Epic(title, description, status);
                        tm.addEpic(epic);
                        epic.setIdOfTask(id);
                        break;
                    case SUBTASK:
                        int epicId = parseInt(element[5]);
                        Subtask subtask = new Subtask(title, description, status, epicId);
                        tm.addSubtask(subtask);
                        subtask.setIdOfTask(id);
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        tm.setIdOfTasks(idMax);
        return tm;
    }

    public void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (Task task : getAllTasks()) {
                bw.write(task.toString() + "\n");
            }
            for (Task epic : getAllEpics()) {
                bw.write(epic.toString() + "\n");
            }
            for (Task subtask : getAllSubtasks()) {
                bw.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл", e);
        }
    }
}