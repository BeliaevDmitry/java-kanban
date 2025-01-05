import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import controllers.TaskManager;
import data.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DurationAdapter;
import server.HttpTaskServer;
import server.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    HttpTaskServer taskServer = new HttpTaskServer();
    TaskManager manager = taskServer.getTaskManager();
    Gson gson;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        this.gson = gsonBuilder.create();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("Тест добавления Task")
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("Тест добавления эпика и сабтаски")
    @Test
    public void testAddSubtaskAndEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        String epicJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtask = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW,
                epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        String subtaskJson = gson.toJson(subtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals(epic1.getTitle(), epicsFromManager.get(0).getTitle(), "Некорректное имя задачи");

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals(subtask.getTitle(), subtasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
        assertEquals(manager.getAllSubtasks().get(0).getEpicId(),
                subtasksFromManager.get(0).getEpicId(), "Сабтаск не привязался эпику через Http");
    }

    @DisplayName("Тест чтения тасок")
    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);
        manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonArray().get(0);

        Task task = gson.fromJson(jsonElement, Task.class);

        assertEquals(1, task.getIdOfTask(), "Некорректный id");
        assertEquals(task1.getIdOfTask(), task.getIdOfTask(), "Некорректное имя задачи");
        assertEquals(task1.getDescription(), task.getDescription(), "Некорректное имя задачи");
        assertEquals(task1.getDuration(), task.getDuration(), "Некорректное имя задачи");

    }

    @DisplayName("Тест чтения тасок по id")
    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);
        manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "ответ сервера при запросе таски с ошибкой");

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonObject();

        Task task = gson.fromJson(jsonElement, Task.class);

        assertEquals(1, task.getIdOfTask(), "Некорректный id");
        assertEquals(task1.getIdOfTask(), task.getIdOfTask(), "Некорректное имя задачи");
        assertEquals(task1.getDescription(), task.getDescription(), "Некорректное имя задачи");
        assertEquals(task1.getDuration(), task.getDuration(), "Некорректное имя задачи");

        url = URI.create("http://localhost:8080/tasks/0");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("Тест чтения сабтасок и эпиков")
    @Test
    public void testGetSubtaskAndEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        Subtask subtask1 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW,
                epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonArray().get(0);
        Epic epic = gson.fromJson(jsonElement, Epic.class);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        jsonElement = JsonParser.parseString(response.body()).getAsJsonArray().get(0);
        Subtask subtask = gson.fromJson(jsonElement, Subtask.class);

        assertEquals(epic1.getIdOfTask(), epic.getIdOfTask(), "Некорректный id");
        assertEquals(epic1.getTitle(), epic.getTitle(), "Некорректное имя задачи");
        assertEquals(epic1.getDescription(), epic.getDescription(), "Некорректное имя задачи");

        assertEquals(subtask1.getIdOfTask(), subtask.getIdOfTask(), "Некорректный id");
        assertEquals(subtask1.getTitle(), subtask.getTitle(), "Некорректное имя задачи");
        assertEquals(subtask1.getDescription(), subtask.getDescription(), "Некорректное имя задачи");
        assertEquals(subtask1.getDuration(), subtask.getDuration(), "Некорректня продолжительность подзадачи");

    }

    @DisplayName("Тест чтения сабтасок и эпиков по id")
    @Test
    public void testGetSubtaskAndEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        Subtask subtask1 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW,
                epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonObject();
        Epic epic = gson.fromJson(jsonElement, Epic.class);

        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        jsonElement = JsonParser.parseString(response.body()).getAsJsonObject();
        Subtask subtask = gson.fromJson(jsonElement, Subtask.class);

        assertEquals(epic1.getIdOfTask(), epic.getIdOfTask(), "Некорректный id");
        assertEquals(epic1.getTitle(), epic.getTitle(), "Некорректное имя задачи");
        assertEquals(epic1.getDescription(), epic.getDescription(), "Некорректное имя задачи");

        assertEquals(subtask1.getIdOfTask(), subtask.getIdOfTask(), "Некорректный id");
        assertEquals(subtask1.getTitle(), subtask.getTitle(), "Некорректное имя задачи");
        assertEquals(subtask1.getDescription(), subtask.getDescription(), "Некорректное имя задачи");
        assertEquals(subtask1.getDuration(), subtask.getDuration(), "Некорректная продолжительность подзадачи");

        url = URI.create("http://localhost:8080/epics/3");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        url = URI.create("http://localhost:8080/subtasks/4");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("Тест удаления тасок по id")
    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);
        manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }

    @DisplayName("Тест удаления сабтасок и эпиков по id")
    @Test
    public void testDeleteSubtaskAndEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW,
                epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addSubtask(subtask1);
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).DELETE().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "неудачное удаление Сабтаски");

        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "неудачное удаление Епика");

        List<Task> epicsFromManager = manager.getAllTasks();
        List<Task> subtasksFromManager = manager.getAllTasks();
        assertEquals(0, epicsFromManager.size(), "Задача не удалена");
        assertEquals(0, subtasksFromManager.size(), "Задача не удалена");
    }

    @DisplayName("Тест чтения сабтасок эпика по id эпика")
    @Test
    public void testGetSubtasksByEpicId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW,
                epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonArray().get(0);
        Subtask subtask = gson.fromJson(jsonElement, Subtask.class);

        assertEquals(subtask1.getIdOfTask(), subtask.getIdOfTask(), "Некорректный id");
        assertEquals(subtask1.getTitle(), subtask.getTitle(), "Некорректное имя задачи");
        assertEquals(subtask1.getDescription(), subtask.getDescription(), "Некорректное имя задачи");
        assertEquals(subtask1.getDuration(), subtask.getDuration(), "Некорректная продолжительность подзадачи");
    }

    @DisplayName("Тест чтения списка истории")
    @Test
    public void testGetHistoryManager() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);
        manager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        Subtask subtask1 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW,
                epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.getTaskById(task1.getIdOfTask());
        manager.getTaskById(epic1.getIdOfTask());
        manager.getTaskById(subtask1.getIdOfTask());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonArray asJsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        List<Task> taskList = gson.fromJson(asJsonArray, new TypeToken<List<Task>>() {
        }.getType());

        assertArrayEquals(manager.getHistory().toArray(), taskList.toArray(), "Списки не совпадают");

    }

    @DisplayName("Тест чтения prioritized списка")
    @Test
    public void testGetPrioritizedTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 13);
        manager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, LocalDateTime.now().plusHours(2), 13);
        Subtask subtask1 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW,
                epic1.getIdOfTask(), LocalDateTime.now().plusHours(4), 13);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonArray asJsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        List<Task> taskList = gson.fromJson(asJsonArray, new TypeToken<List<Task>>() {
        }.getType());
        assertArrayEquals(manager.getPrioritizedTasks().toArray(), taskList.toArray(), "Списки не совпадают");
    }
}
