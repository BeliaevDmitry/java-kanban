import data.Status;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        taskManager = FileBackedTaskManager.loadFromFile(Path.of("src/resources/savedTasks.csv"));

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        taskManager.addTask(task2);


        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getIdOfTask());
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getIdOfTask());
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.NEW);
        taskManager.addEpic(epic2);

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
        System.out.println(taskManager.getTaskById(1));

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
        taskManager.removeEpic(1);

        System.out.println("История задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Конец программы");

    }
}