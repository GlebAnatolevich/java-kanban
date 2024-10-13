package model;

import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void subTasksShouldBeEqual_IfIdsEqual() {

        Epic epic = taskManager.createEpic(new Epic("Эпик1", NEW, "Отремонтировать машину"));

        SubTask subTask1 = taskManager.createSubTask(new SubTask(epic, "Подзадача1", NEW, "Заказать запчасти"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(epic, "Подзадача2", NEW, "Заказать доставку"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(epic, "Подзадача3", NEW, "Отвезти машину в сервис"));
        int subTaskId = subTask1.getId();
        SubTask savedSubTask = taskManager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена");
        assertEquals(subTask1, savedSubTask, "Подзадачи не совпадают");

        Epic gettedEpic = subTask2.getEpicFromSubTasks();

        assertNotNull(gettedEpic, "Эпик не найден");
        assertEquals(gettedEpic, epic, "Эпики не совпадают");
    }
}