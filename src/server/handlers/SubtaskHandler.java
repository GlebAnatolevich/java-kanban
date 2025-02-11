package server.handlers;

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

public class SubtaskHandler extends TaskHandler implements HttpHandler {

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
            case GET_SUBTASKS -> sendText(h, gson.toJson(manager.getAllSubTasks()));
            case GET_SUBTASK_BY_ID -> {
                try {
                    sendText(h, gson.toJson(manager.getSubTask(Integer.parseInt(urlParts[urlParts.length - 1]))));
                } catch (NoSuchElementException e) {
                    sendNotFound(h, gson.toJson("Такой подзадачи не существует."));
                }
            }
            case POST_SUBTASK -> {
                try {
                    InputStream is = h.getRequestBody();
                    String subtaskStr = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    if ((subtaskStr.isEmpty()) || (subtaskStr.isBlank())) {
                        sendNotFound(h, gson.toJson("Тело сообщения должно содержать экземпляр 'SubTask'"));
                    } else {
                        SubTask subTask = gson.fromJson(subtaskStr, SubTask.class);
                        if (urlParts[urlParts.length - 1].equals("subtasks")) {
                            manager.createSubTask(subTask);
                            sendText(h, gson.toJson("Подзадача с id = " + subTask.getId() + " успешно создана."));
                        } else {
                            manager.updateSubTask(subTask);
                            sendText(h, gson.toJson("Подзадача с id = " + urlParts[urlParts.length - 1] +
                                    " успешно обновлена."));
                        }
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