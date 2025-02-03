import model.Task;
import service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.Status.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.create(new Task(1,"задача 111", NEW, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,24,12,0)));
        Task task2 = taskManager.create(new Task(2,"задача 111", NEW, "описание задачи 111",
                Duration.ofMinutes(10), LocalDateTime.of(2024,12,25,15,0)));
        Task task3 = taskManager.create(new Task(3,"задача 111", NEW, "описание задачи 111",
                Duration.ofMinutes(180), LocalDateTime.of(2024,12,25,12,0)));

        task1.setStatus(DONE);
        task2.setStatus(IN_PROGRESS);
        taskManager.update(task1);
        taskManager.update(task2);

        System.out.println(taskManager.getPrioritizedTasks());
    }
}