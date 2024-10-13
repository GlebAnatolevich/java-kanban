package model;

import service.InMemoryTaskManager;

import org.junit.jupiter.api.Test;

import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void tasksShouldBeEqual_IfIdsEqual() {
        Task task = taskManager.create(new Task("Задача1", NEW, "Записаться на стрижку"));
        int taskId = task.getId();
        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач в списке");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }
}