package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskConflictException;
import model.Endpoint;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

class SubtaskHandler extends TaskHandler implements HttpHandler {

    public SubtaskHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String requestMethod = h.getRequestMethod();
        String url = h.getRequestURI().toString();
        String[] urlParts = url.split("/");
        Endpoint endpoint = getEndpoint(requestMethod, url);

        switch (endpoint) {
            case GET_SUBTASKS -> sendText(h, manager.getAllSubTasks().toString());
            case GET_SUBTASK_BY_ID -> {
                try {
                    manager.getSubTask(Integer.parseInt(urlParts[urlParts.length - 1]));
                } catch (NoSuchElementException e) {
                    sendNotFound(h);
                }
                sendText(h, manager.getSubTask(Integer.parseInt(urlParts[urlParts.length - 1])).toString());
            }
            case POST_SUBTASK -> {
                try {
                    InputStream is = h.getRequestBody();
                    String subtaskStr = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    SubTask subTask = gson.fromJson(subtaskStr, SubTask.class);
                    if (urlParts[urlParts.length - 1].equals("subtasks")) {
                        manager.createSubTask(subTask);
                        h.sendResponseHeaders(201, 0);
                    } else {
                        manager.updateSubTask(subTask);
                        h.sendResponseHeaders(201, 0);
                    }
                } catch (TaskConflictException e) {
                    sendHasInteractions(h);
                }
            }
            case DELETE_SUBTASK_BY_ID -> {
                manager.deleteSubTask(Integer.parseInt(urlParts[urlParts.length - 1]));
                sendText(h, gson.toJson("Подзадача с id = " + urlParts[urlParts.length - 1] + " успешно удалена."));
            }
            case UNKNOWN -> h.sendResponseHeaders(500, 0);
            default -> h.sendResponseHeaders(404, 0);
        }
    }

    private Endpoint getEndpoint(String requestMethod, String url) {
        String[] urlParts = url.split("/");

        switch (requestMethod) {
            case "GET":
                if (urlParts[urlParts.length - 1].equals("subtasks")) {
                    return Endpoint.GET_SUBTASKS;
                } else {
                    return Endpoint.GET_SUBTASK_BY_ID;
                }
            case "POST":
                return Endpoint.POST_SUBTASK;
            case "DELETE":
                return Endpoint.DELETE_SUBTASK_BY_ID;
            default:
                return Endpoint.UNKNOWN;
        }
    }
}