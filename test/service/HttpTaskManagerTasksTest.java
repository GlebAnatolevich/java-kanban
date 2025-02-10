package service;

import com.google.gson.Gson;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    final Epic epic1 = new Epic(1, "эпик 111", NEW, "описание эпика 111",
            Duration.ofMinutes(120), LocalDateTime.of(2025, 12, 25, 10, 0));
    final Epic epic2 = new Epic(2, "эпик 222", NEW, "описание эпика 222",
            Duration.ofMinutes(120), LocalDateTime.of(2025, 12, 25, 12, 0));
    final Epic epic3 = new Epic(3, "эпик 333", NEW, "описание эпика 333",
            Duration.ofMinutes(120), LocalDateTime.of(2025, 12, 25, 14, 0));
    final SubTask subTask2 = new SubTask(4, epic1, "задача 444", NEW, "описание задачи 444",
            Duration.ofMinutes(120), LocalDateTime.of(2025, 12, 25, 10, 0));
    final SubTask subTask3 = new SubTask(5, epic2, "задача 555", NEW, "описание задачи 555",
            Duration.ofMinutes(120), LocalDateTime.of(2025, 12, 25, 12, 0));
    final SubTask subTask4 = new SubTask(6, epic3, "задача 666", NEW, "описание задачи 666",
            Duration.ofMinutes(120), LocalDateTime.of(2025, 12, 25, 14, 0));
    final Task task = new Task(7, "задача 777", NEW, "описание задачи 777", Duration.ofMinutes(5), LocalDateTime.now());

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        // создаём задачу
        manager.create(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение всех задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task(1, "задача 111", NEW, "описание задачи 111", Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task(2, "задача 222", NEW, "описание задачи 222",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1));
        Task task3 = new Task(3, "задача 333", NEW, "описание задачи 333",
                Duration.ofMinutes(15), LocalDateTime.now().plusDays(2));
        manager.create(task1);
        manager.create(task2);
        manager.create(task3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", NEW, "Testing task 2");
        task.setDuration(Duration.ZERO);
        task.setStartTime(LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task(1, "задача 111", NEW, "описание задачи 111", Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task(2, "задача 222", NEW, "описание задачи 222",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1));
        Task task3 = new Task(3, "задача 333", NEW, "описание задачи 333",
                Duration.ofMinutes(15), LocalDateTime.now().plusDays(2));
        manager.create(task1);
        manager.create(task2);
        manager.create(task3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем ответ, отвечающий за удаление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        // создаём эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение всех эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        // создаём эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetSubtasksOfEpic() throws IOException, InterruptedException {
        // создаём эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        // создаём эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем ответ, отвечающий за удаление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();
        assertEquals(2, tasksFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException {
        // создаём эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение всех подзадач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        // создаём эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        // создаём эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/6");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем ответ, отвечающий за удаление подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = manager.getAllSubTasks();
        assertEquals(2, tasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        // создаём задачи и эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);
        manager.create(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение списка задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачи и эпики с подзадачами
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);
        manager.create(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/6"))
                .GET()
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/7"))
                .GET()
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        // вызываем ответ, отвечающий за получение списка истории просмотров
        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response3.statusCode());

        List<Task> tasksFromManager = manager.getHistory();
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }
}