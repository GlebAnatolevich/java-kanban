package model;

import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void epicsShouldBeEqual_IfIdsEqual() {

        Epic epic = taskManager.createEpic(new Epic("Эпик1", NEW, "Отремонтировать машину"));
        int epicId = epic.getId();
        Epic savedEpic = taskManager.getEpic(epicId);

        SubTask subTask1 = taskManager.createSubTask(new SubTask(epic, "Подзадача1", NEW, "Заказать запчасти"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(epic, "Подзадача2", NEW, "Заказать доставку"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(epic, "Подзадача3", NEW, "Отвезти машину в сервис"));

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков в списке");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают");

        List<SubTask> subTasks = taskManager.getSubTasksOfEpic(epicId);

        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(3, subTasks.size(), "Неверное количество подзадач в списке");
        assertEquals(subTask1, subTasks.get(0), "Подзадачи не совпадают");
        assertEquals(subTask3, subTasks.get(2), "Подзадачи не совпадают");

        List<SubTask> subTasksFromMap = taskManager.getAllSubTasks();
        assertNotNull(subTasksFromMap, "Подзадачи не возвращаются");
        assertEquals(3, subTasksFromMap.size(), "Неверное количество подзадач в списке");
    }
}