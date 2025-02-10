package model;

import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();
    final Epic epic = new Epic(1,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(360), LocalDateTime.of(2024,12,25,10,0));

    @Test
    public void epicsShouldBeEqual_IfIdsEqual() {

        taskManager.createEpic(epic);
        int epicId = epic.getId();
        Epic savedEpic = taskManager.getEpic(epicId);

        SubTask subTask2 = taskManager.createSubTask(new SubTask(2,epic.getId(),"задача 111", NEW,
                "описание задачи 111", Duration.ofMinutes(120),
                LocalDateTime.of(2024,12,25,10,0)));

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков в списке");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают");

        List<SubTask> subTasks = taskManager.getSubTasksOfEpic(epicId);

        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач в списке");
        assertEquals(subTask2, subTasks.getFirst(), "Подзадачи не совпадают");

        List<SubTask> subTasksFromMap = taskManager.getAllSubTasks();
        assertNotNull(subTasksFromMap, "Подзадачи не возвращаются");
        assertEquals(1, subTasksFromMap.size(), "Неверное количество подзадач в списке");
    }

    @Test
    public void epicsStatusShouldBeNew_IfAllSubTasksNew() {
        taskManager.createEpic(epic);
        taskManager.createSubTask(new SubTask(2,epic.getId(),"задача 111", NEW, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,10,0)));
        taskManager.createSubTask(new SubTask(3,epic.getId(),"задача 111", NEW, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,12,0)));
        taskManager.createSubTask(new SubTask(4,epic.getId(),"задача 111", NEW, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,14,0)));

        assertSame(epic.getStatus(), NEW, "СТАТУС ЭПИКА РАССЧИТАН НЕВЕРНО");
    }

    @Test
    public void epicsStatusShouldBeDone_IfAllSubTasksDone() {
        taskManager.createEpic(epic);
        taskManager.createSubTask(new SubTask(2,epic.getId(),"задача 111", DONE, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,10,0)));
        taskManager.createSubTask(new SubTask(3,epic.getId(),"задача 111", DONE, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,12,0)));
        taskManager.createSubTask(new SubTask(4,epic.getId(),"задача 111", DONE, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,14,0)));

        assertSame(epic.getStatus(), DONE, "СТАТУС ЭПИКА РАССЧИТАН НЕВЕРНО");
    }

    @Test
    public void epicsStatusShouldBeInProgress_IfSubTasksNewAndDone() {
        taskManager.createEpic(epic);
        taskManager.createSubTask(new SubTask(2,epic.getId(),"задача 111", NEW, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,10,0)));
        taskManager.createSubTask(new SubTask(3,epic.getId(),"задача 111", DONE, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,12,0)));
        taskManager.createSubTask(new SubTask(4,epic.getId(),"задача 111", DONE, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,14,0)));

        assertSame(epic.getStatus(), IN_PROGRESS, "СТАТУС ЭПИКА РАССЧИТАН НЕВЕРНО");
    }

    @Test
    public void epicsStatusShouldBeInProgress_IfAllSubTasksInProgress() {
        taskManager.createEpic(epic);
        taskManager.createSubTask(new SubTask(2,epic.getId(),"задача 111", IN_PROGRESS, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,10,0)));
        taskManager.createSubTask(new SubTask(3,epic.getId(),"задача 111", IN_PROGRESS, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,12,0)));
        taskManager.createSubTask(new SubTask(4,epic.getId(),"задача 111", IN_PROGRESS, "описание задачи 111",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,14,0)));

        assertSame(epic.getStatus(), IN_PROGRESS, "СТАТУС ЭПИКА РАССЧИТАН НЕВЕРНО");
    }
}