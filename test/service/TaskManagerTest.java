package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected final Epic epic = new Epic(1,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(360), null);
    protected final SubTask subTask2 = new SubTask(2,epic,"задача 111", NEW, "описание задачи 222",
            Duration.ofMinutes(120), LocalDateTime.of(2025,12,25,10,0));
    protected final SubTask subTask3 = new SubTask(3,epic,"задача 111", NEW, "описание задачи 333",
            Duration.ofMinutes(120), LocalDateTime.of(2025,12,25,12,0));
    protected final SubTask subTask4 = new SubTask(4,epic,"задача 111", NEW, "описание задачи 444",
            Duration.ofMinutes(120), LocalDateTime.of(2025,12,25,14,0));
    protected final Task task = new Task(5,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(120), LocalDateTime.of(2024,12,24,12,0));

    @Test
    public void updateTaskShouldReturnSameId() {
        manager.create(task);
        Task updatedTask = new Task(6,"задача 111_1", IN_PROGRESS, "описание задачи 111",
                Duration.ofMinutes(60), LocalDateTime.of(2024,12,25,12,0));
        updatedTask.setId(task.getId());
        manager.update(updatedTask);

        assertEquals(task, updatedTask, "Задачи не равны");
        assertEquals(task.getId(), updatedTask.getId(), "Id задач не равны");
    }

    @Test
    public void updateEpicShouldReturnSameId() {
        manager.createEpic(epic);
        Epic updatedEpic = new Epic("Эпик1", NEW, "Записаться на стрижку в тот барбершоп");
        updatedEpic.setId(epic.getId());
        manager.updateEpic(updatedEpic);

        assertEquals(epic, updatedEpic, "Эпики не равны");
        assertEquals(epic.getId(), updatedEpic.getId(), "Id эпиков не равны");
    }

    @Test
    public void updateSubTaskShouldReturnSameIdAndChangeEpicStatus() {
        manager.createEpic(epic);
        manager.createSubTask(subTask2);
        SubTask updatedSubTask = manager.createSubTask(subTask3);
        updatedSubTask.setId(subTask2.getId());
        manager.updateSubTask(updatedSubTask);

        assertEquals(subTask2, updatedSubTask, "Подзадачи не равны");
        assertEquals(subTask2.getId(), updatedSubTask.getId(), "Id подзадач не равны");
        assertEquals(subTask2.getEpicFromSubTasks(), epic);
        assertSame(epic.getStatus(), NEW, "СТАТУС ЭПИКА РАССЧИТАН НЕВЕРНО");
    }

    @Test
    public void deleteAllTasksShouldReturnIsEmptyTrue() {
        manager.create(task);
        manager.create(new Task(6,"задача 222", NEW, "описание задачи 222",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,27,12,0)));
        manager.deleteAllTasks();
        List<Task> tasks = manager.getAllTasks();

        assertTrue(tasks.isEmpty(), "Коллекция задач по-прежнему не пуста");
    }

    @Test
    public void deleteAllEpicsShouldReturnIsEmptyTrue() {
        manager.createEpic(epic);
        manager.createEpic(new Epic(7,"задача 111", NEW, "описание эпика 111", Duration.ofMinutes(10),
                LocalDateTime.of(2024,12,25,16,0)));
        manager.deleteAllEpics();
        List<Epic> epics = manager.getAllEpics();

        assertTrue(epics.isEmpty(), "Коллекция эпиков по-прежнему не пуста");
    }

    @Test
    public void deleteAllSubTasksShouldReturnIsEmptyTrueAndChangeEpicStatusToNEW() {
        manager.createEpic(epic);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.deleteAllSubTasks();
        List<SubTask> subTasks = manager.getAllSubTasks();

        assertTrue(subTasks.isEmpty(), "Коллекция подзадач по-прежнему не пуста");
        assertSame(epic.getStatus(), NEW, "СТАТУС ЭПИКА ПОСЛЕ УДАЛЕНИЯ ПОДЗАДАЧ - NEW");
    }

    @Test
    public void tasksCreatedAndSetShouldNotConflictAndDoNotChangeFields() {
        manager.create(task);
        task.setId(10);
        manager.create(task); // при create срабатывает метод generateId, который должен присвоить 2 вместо 10
        List<Task> tasks = manager.getAllTasks();
        Task actualTask = tasks.get(0);

        assertEquals(task.getId(), actualTask.getId());
        assertEquals(2, actualTask.getId(),"Генерация id не отменила заданный вручную id");
        assertEquals(task.getName(), actualTask.getName());
        assertEquals(task.getStatus(), actualTask.getStatus());
        assertEquals(task.getDescription(), actualTask.getDescription());
    }

    @Test
    public void deleteShouldRemoveTaskEpicSubTaskFromHistory() {
        manager.createEpic(epic);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);
        manager.create(task);

        manager.getEpic(epic.getId());
        manager.getSubTask(subTask2.getId());
        manager.getSubTask(subTask3.getId());
        manager.getSubTask(subTask4.getId());
        manager.getTask(task.getId());

        manager.delete(task.getId());
        manager.deleteSubTask(subTask2.getId());

        assertEquals(List.of(epic, subTask3, subTask4), manager.getHistory(), "При удалении задачи из " +
                "истории просмотров возникла ошибка");
    }

    @Test
    public void deleteAllShouldRemoveAllFromHistory() {
        manager.createEpic(epic);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);
        manager.create(task);

        manager.getEpic(epic.getId());
        manager.getSubTask(subTask2.getId());
        manager.getSubTask(subTask3.getId());
        manager.getSubTask(subTask4.getId());
        manager.getTask(task.getId());

        manager.deleteAllTasks();
        manager.deleteAllEpics();

        assertTrue(manager.getHistory().isEmpty());
    }
}