import data.Status;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {


    public static void main(String[] args) {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(Path.of("src/resources/savedTasks.csv"));

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW, LocalDateTime.now().plusHours(1), 13);
        taskManager.addTask(task2);


        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(3), 13);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.NEW, LocalDateTime.now().plusHours(5), 13);
        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.NEW, epic2.getIdOfTask(), LocalDateTime.now().plusHours(6), 13);
        taskManager.addSubtask(subtask3);

        System.out.println("Задачи созданы");
        System.out.println("История задач:");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Запрашиваю задачи");

        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getEpicById(3));
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println(taskManager.getEpicById(6));

        System.out.println("История задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Удаляю задачу 2");
        taskManager.removeTask(2);

        System.out.println("История задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Удаляю эпик 1");
        taskManager.removeEpic(3);

        System.out.println("История задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

       taskManager.printPrioritizedTasks();

        System.out.println("Конец программы");

    }
}