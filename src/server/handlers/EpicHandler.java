package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoint;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class EpicHandler extends TaskHandler implements HttpHandler {

    public EpicHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String requestMethod = h.getRequestMethod();
        String url = h.getRequestURI().toString();
        String[] urlParts = url.split("/");
        Endpoint endpoint = getEndpoint(requestMethod, url);

        switch (endpoint) {
            case GET_EPICS -> sendText(h, gson.toJson(manager.getAllEpics()));
            case GET_EPIC_BY_ID -> {
                try {
                    sendText(h, gson.toJson(manager.getEpic(Integer.parseInt(urlParts[urlParts.length - 1]))));
                } catch (NoSuchElementException e) {
                    sendNotFound(h, gson.toJson("Такого эпика не существует."));
                }
            }
            case GET_EPIC_SUBTASKS -> {
                try {
                    sendText(h,gson.toJson(manager.getSubTasksOfEpic(Integer.parseInt(urlParts[urlParts.length - 2]))));
                } catch (NoSuchElementException e) {
                    sendNotFound(h, gson.toJson("Такого эпика не существует."));
                }
            }
            case POST_EPIC -> {
                InputStream is = h.getRequestBody();
                String epicStr = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                if ((epicStr.isEmpty()) || (epicStr.isBlank())) {
                    sendNotFound(h, gson.toJson("Тело сообщения должно содержать экземпляр 'Epic'"));
                } else {
                    Epic epic = gson.fromJson(epicStr, Epic.class);
                    if (urlParts[urlParts.length - 1].equals("epics")) {
                        manager.createEpic(epic);
                        sendText(h, gson.toJson("Эпик с id = " + epic.getId() + " успешно создан."));
                    } else {
                        manager.updateEpic(epic);
                        sendText(h, gson.toJson("Эпик с id = " + urlParts[urlParts.length - 1] + " успешно обновлен."));
                    }
                }
            }
            case DELETE_EPIC_BY_ID -> {
                manager.deleteEpic(Integer.parseInt(urlParts[urlParts.length - 1]));
                sendText(h, gson.toJson("Эпик с id = " + urlParts[urlParts.length - 1] + " успешно удален."));
            }
            case UNKNOWN -> h.sendResponseHeaders(500, 0);
            default -> h.sendResponseHeaders(404, 0);
        }
    }

    private Endpoint getEndpoint(String requestMethod, String url) {
        String[] urlParts = url.split("/");

        switch (requestMethod) {
            case "GET":
                if (urlParts[urlParts.length - 1].equals("epics")) {
                    return Endpoint.GET_EPICS;
                } else if (urlParts[urlParts.length - 1].equals("subtasks")) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                } else {
                    return Endpoint.GET_EPIC_BY_ID;
                }
            case "POST":
                return Endpoint.POST_EPIC;
            case "DELETE":
                return Endpoint.DELETE_EPIC_BY_ID;
            default:
                return Endpoint.UNKNOWN;
        }
    }
}