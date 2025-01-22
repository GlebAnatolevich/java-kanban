package model;

import service.InMemoryTaskManager;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void tasksShouldBeEqual_IfIdsEqual() {
        Task task = taskManager.create(new Task(5,"задача 111", NEW, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,23,12,0)));
        int taskId = task.getId();
        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач в списке");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают");
    }
}