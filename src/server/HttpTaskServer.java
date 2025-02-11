package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import server.adapter.DurationTypeAdapter;
import server.adapter.LocalDateTimeTypeAdapter;
import server.handlers.*;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import static service.Managers.getDefault;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        Gson gson = getGson();

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(gson, manager));
        server.createContext("/epics", new EpicHandler(gson, manager));
        server.createContext("/subtasks", new SubtaskHandler(gson, manager));
        server.createContext("/history", new HistoryHandler(gson, manager));
        server.createContext("/prioritized", new PrioritizedHandler(gson, manager));
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(getDefault());
        httpTaskServer.startServer();
        httpTaskServer.stopServer();
    }

    public void startServer() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public void stopServer() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }
}