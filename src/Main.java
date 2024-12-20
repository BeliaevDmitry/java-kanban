import java.nio.file.Path;

public class Main {


    public static void main(String[] args) {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(Path.of("src/resources/savedTasks.csv"));

        System.out.println("Задачи созданы");
        System.out.println("История задач:");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Запрашиваю задачи");

        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getEpicById(3));
        System.out.println(taskManager.getSubtaskById(6));
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println(taskManager.getSubtaskById(7));
        System.out.println(taskManager.getEpicById(4));

        System.out.println("История задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Приоритетные задачи:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

        System.out.println("Конец программы");

    }
}