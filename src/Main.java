import data.Status;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

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

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.NEW, epic2.getIdOfTask());
        taskManager.addSubtask(subtask3);

        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.IN_PROGRESS);

        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        System.out.println();
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getEpicById(3));
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println(taskManager.getEpicById(6));
        System.out.println(taskManager.getSubtaskById(7));

        System.out.println();
        System.out.println(taskManager.getSubtasksOfEpic(taskManager.getEpicById(3)));

        taskManager.removeTask(2);
        taskManager.removeEpic(3);
        taskManager.removeSubtask(7);

        System.out.println(taskManager.getAllTasks());

        System.out.println(taskManager.getEpicById(6));

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();

        System.out.println("Задачи:");
        for (Object o : taskManager.getAllTasks()) {
            System.out.println(o);
        }
        for (Object o : taskManager.getAllEpics()) {
            System.out.println(o);
        }
        for (Object o : taskManager.getAllSubtasks()) {
            System.out.println(o);
        }
        for (Subtask subtask : taskManager.getSubtasksOfEpic(epic1)) {
            System.out.println(subtask);
        }
        for (Subtask subtask : taskManager.getSubtasksOfEpic(epic2)) {
            System.out.println(subtask);
        }
    }
}