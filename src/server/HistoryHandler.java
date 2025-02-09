package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoint;
import service.TaskManager;

import java.io.IOException;

class HistoryHandler extends TaskHandler implements HttpHandler {

    public HistoryHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String requestMethod = h.getRequestMethod();
        Endpoint endpoint = getEndpoint(requestMethod);

        switch (endpoint) {
            case GET_HISTORY -> sendText(h, manager.getHistory().toString());
            case UNKNOWN -> h.sendResponseHeaders(500, 0);
            default -> h.sendResponseHeaders(404, 0);
        }
    }

    private Endpoint getEndpoint(String requestMethod) {
        if (requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }
        return Endpoint.UNKNOWN;
    }
}