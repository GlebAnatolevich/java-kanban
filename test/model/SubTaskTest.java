package model;

import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();
    final Epic epic = new Epic(1,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(10), LocalDateTime.of(2024,12,25,15,0));
    final SubTask subTask2 = new SubTask(2,1,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(180), LocalDateTime.of(2024,12,25,12,0));

    @Test
    public void subTasksShouldBeEqual_IfIdsEqual() {

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask2);
        int subTaskId = subTask2.getId();
        SubTask savedSubTask = taskManager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена");
        assertEquals(subTask2, savedSubTask, "Подзадачи не совпадают");

        int epicId = subTask2.getEpicIdFromSubTasks();

        assertEquals(epic.getId(), epicId, "Эпики не совпадают");
    }
}