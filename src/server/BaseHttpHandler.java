package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.Managers;
import controllers.TaskManager;
import exceptions.TaskValidationTimeException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {

    TaskManager taskManager;
    Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = Managers.getDefaultGson();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String requestMethod = exchange.getRequestMethod();
            String[] splitPath = exchange.getRequestURI().getPath().split("/");
            if (splitPath.length == 0 || splitPath.length > 4) {
                sendNotFound(exchange);
            } else {
                switch (requestMethod) {
                    case "GET" -> readResource(exchange, splitPath);
                    case "POST" -> createResource(exchange, splitPath);
                    case "DELETE" -> deleteResource(exchange, splitPath);
                    default -> sendText(exchange, "Такого метода нет", 405);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readResource(HttpExchange exchange, String[] path) throws IOException {
        switch (path[1]) {
            case "tasks" -> {
                switch (path.length) {
                    case 2 -> sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
                    case 3 -> {
                        if (taskManager.getTaskById(Integer.parseInt(path[2])) == null) {
                            sendNotFound(exchange);
                        } else {
                            sendText(exchange, gson.toJson(taskManager.getTaskById(Integer.parseInt(path[2]))),
                                    200);
                        }
                    }
                    default -> sendNotFound(exchange);
                }
            }
            case "subtasks" -> {
                switch (path.length) {
                    case 2 -> sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
                    case 3 -> {
                        if (taskManager.getSubtaskById(Integer.parseInt(path[2])) == null) {
                            sendNotFound(exchange);
                        } else {
                            sendText(exchange, gson.toJson(taskManager.getSubtaskById(Integer.parseInt(path[2]))),
                                    200);
                        }
                    }
                    default -> sendNotFound(exchange);
                }
            }
            case "epics" -> {
                switch (path.length) {
                    case 2 -> sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                    case 3 -> {
                        if (taskManager.getEpicById(Integer.parseInt(path[2])) == null) {
                            sendNotFound(exchange);
                        } else {
                            sendText(exchange, gson.toJson(taskManager.getEpicById(Integer.parseInt(path[2]))),
                                    200);
                        }
                    }
                    case 4 -> {
                        int id = Integer.parseInt(path[2]);
                        if (taskManager.getEpicById(id) != null) {
                            sendText(exchange,
                                    gson.toJson(taskManager.getSubtaskEpic(taskManager.getEpicById(id))),
                                    200);
                        } else sendNotFound(exchange);
                    }
                    default -> sendNotFound(exchange);
                }
            }
            case "history" -> sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
            case "prioritized" -> sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
            default -> {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
            }
        }
    }

    private void createResource(HttpExchange exchange, String[] path) throws IOException {
        switch (path.length) {
            case 2 -> {
                try {
                    String stringRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                    switch (path[1]) {
                        case "tasks" -> {
                            try {
                                taskManager.addTask(gson.fromJson(stringRequest, Task.class));
                            } catch (TaskValidationTimeException e) {
                                sendHasInteractions(exchange);
                            }
                        }
                        case "subtasks" -> {
                            try {
                                taskManager.addSubtask(gson.fromJson(stringRequest, Subtask.class));
                            } catch (TaskValidationTimeException e) {
                                sendHasInteractions(exchange);
                            }
                        }
                        case "epics" -> {
                            try {
                                taskManager.addEpic(gson.fromJson(stringRequest, Epic.class));
                            } catch (TaskValidationTimeException e) {
                                sendHasInteractions(exchange);
                            }
                        }
                        default -> sendNotFound(exchange);
                    }
                    exchange.sendResponseHeaders(200, 0);
                    exchange.close();
                } catch (IllegalArgumentException e) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                }
            }
            case 3 -> {
                try {
                    String stringRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    int id = Integer.parseInt(path[2]);
                    if (taskManager.getTaskById(id) == null) sendNotFound(exchange);
                    else {
                        switch (path[1]) {
                            case "tasks" -> {
                                try {
                                    taskManager.updateTask(gson.fromJson(stringRequest, Task.class));
                                } catch (TaskValidationTimeException e) {
                                    sendHasInteractions(exchange);
                                }
                            }
                            case "subtasks" -> {
                                try {
                                    taskManager.updateSubtask(gson.fromJson(stringRequest, Subtask.class));
                                } catch (TaskValidationTimeException e) {
                                    sendHasInteractions(exchange);
                                }
                            }
                            case "epics" -> {
                                try {
                                    taskManager.updateEpic(gson.fromJson(stringRequest, Epic.class));
                                } catch (TaskValidationTimeException e) {
                                    sendHasInteractions(exchange);
                                }
                            }
                            default -> sendNotFound(exchange);
                        }
                    }
                    exchange.sendResponseHeaders(200, 0);
                    exchange.close();
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                }
            }
            default -> {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
            }
        }
    }

    private void deleteResource(HttpExchange exchange, String[] path) throws IOException {
        if (path.length == 3) {
            try {
                int id = Integer.parseInt(path[2]);
                switch (path[1]) {
                    case "tasks" -> {
                        if (taskManager.getTaskById(id) == null) sendNotFound(exchange);
                        else {
                            taskManager.removeTask(id);
                        }
                    }
                    case "subtasks" -> {
                        if (taskManager.getSubtaskById(id) == null) sendNotFound(exchange);
                        else {
                            taskManager.removeSubtask(id);
                        }
                    }
                    case "epics" -> {
                        if (taskManager.getEpicById(id) == null) sendNotFound(exchange);
                        else {
                            taskManager.removeEpic(id);
                        }
                    }
                    default -> sendNotFound(exchange);
                }
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void sendText(HttpExchange exchange, String resposeString, int responseCode) throws IOException {
        byte[] resp = resposeString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    private void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }
}
