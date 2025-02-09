package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskConflictException;
import model.Endpoint;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

class TaskHandler extends BaseHttpHandler implements HttpHandler {

    protected final TaskManager manager;
    protected final Gson gson;

    public TaskHandler(Gson gson, TaskManager manager) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String requestMethod = h.getRequestMethod();
        String url = h.getRequestURI().toString();
        String[] urlParts = url.split("/");
        Endpoint endpoint = getEndpoint(requestMethod, url);

        switch (endpoint) {
            case GET_TASKS -> sendText(h, manager.getAllTasks().toString());
            case GET_TASK_BY_ID -> {
                try {
                    manager.getTask(Integer.parseInt(urlParts[urlParts.length - 1]));
                } catch (NoSuchElementException e) {
                    sendNotFound(h);
                }
                sendText(h, gson.toJson(manager.getTask(Integer.parseInt(urlParts[urlParts.length - 1]))));
            }
            case POST_TASK -> {
                try {
                    InputStream is = h.getRequestBody();
                    String taskStr = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(taskStr, Task.class);
                    if (urlParts[urlParts.length - 1].equals("tasks")) {
                        manager.create(task);
                        h.sendResponseHeaders(201, 0);
                    } else {
                        manager.update(task);
                        h.sendResponseHeaders(201, 0);
                    }
                } catch (TaskConflictException e) {
                    sendHasInteractions(h);
                }
            }
            case DELETE_TASK_BY_ID -> {
                manager.delete(Integer.parseInt(urlParts[urlParts.length - 1]));
                sendText(h, gson.toJson("Задача с id = " + urlParts[urlParts.length - 1] + " успешно удалена."));
            }
            case UNKNOWN -> h.sendResponseHeaders(500, 0);
            default -> h.sendResponseHeaders(404, 0);
        }
    }

    private Endpoint getEndpoint(String requestMethod, String url) {
        String[] urlParts = url.split("/");

        switch (requestMethod) {
            case "GET":
                if (urlParts[urlParts.length - 1].equals("tasks")) {
                    return Endpoint.GET_TASKS;
                } else {
                    return Endpoint.GET_TASK_BY_ID;
                }
            case "POST":
                return Endpoint.POST_TASK;
            case "DELETE":
                return Endpoint.DELETE_TASK_BY_ID;
            default:
                return Endpoint.UNKNOWN;
        }
    }
}