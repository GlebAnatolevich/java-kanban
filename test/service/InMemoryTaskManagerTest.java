package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void updateTaskShouldReturnSameId() {
        Task task = new Task("Задача1", NEW, "Записаться на стрижку");
        taskManager.create(task);
        Task updatedTask = new Task("Задача1", IN_PROGRESS, "Записаться на стрижку в тот барбершоп");
        updatedTask.setId(task.getId());
        taskManager.update(updatedTask);

        assertEquals(task, updatedTask, "Задачи не равны");
        assertEquals(task.getId(), updatedTask.getId(), "Id задач не равны");
    }

    @Test
    public void updateEpicShouldReturnSameId() {
        Epic epic = new Epic("Эпик1", NEW, "Записаться на стрижку");
        taskManager.createEpic(epic);
        Epic updatedEpic = new Epic("Эпик1", NEW, "Записаться на стрижку в тот барбершоп");
        updatedEpic.setId(epic.getId());
        taskManager.updateEpic(updatedEpic);

        assertEquals(epic, updatedEpic, "Эпики не равны");
        assertEquals(epic.getId(), updatedEpic.getId(), "Id эпиков не равны");
    }

    @Test
    public void updateSubTaskShouldReturnSameIdAndChangeEpicStatus() {
        Epic epic = new Epic("Эпик1", NEW, "Записаться на стрижку");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask(epic,"Подзадача1", NEW, "Скачать приложение барбершопа");
        taskManager.createSubTask(subTask);
        SubTask updatedSubTask = new SubTask(epic,"Подзадача1", IN_PROGRESS, "Записаться через официальный сайт");
        updatedSubTask.setId(subTask.getId());
        taskManager.updateSubTask(updatedSubTask);

        assertEquals(subTask, updatedSubTask, "Подзадачи не равны");
        assertEquals(subTask.getId(), updatedSubTask.getId(), "Id подзадач не равны");
        assertTrue(epic.getStatus() == IN_PROGRESS, "СТАТУС ЭПИКА РАССЧИТАН НЕВЕРНО");
    }

    @Test
    public void deleteAllTasksShouldReturnIsEmptyTrue() {
        Task task1 = taskManager.create(new Task("Задача1", NEW, "Записаться на стрижку"));
        Task task2 = taskManager.create(new Task("Задача2", NEW, "Побрить кота"));
        taskManager.deleteAllTasks();
        List<Task> tasks = taskManager.getAllTasks();

        assertTrue(tasks.isEmpty(), "Коллекция задач по-прежнему не пуста");
    }

    @Test
    public void deleteAllEpicsShouldReturnIsEmptyTrue() {
        Epic epic1 = taskManager.createEpic(new Epic("Эпик1", IN_PROGRESS, "Создать бесконечные тесты трекера"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик2", DONE, "Разобраться в связях классов и интерфейсов"));
        taskManager.deleteAllEpics();
        List<Epic> epics = taskManager.getAllEpics();

        assertTrue(epics.isEmpty(), "Коллекция эпиков по-прежнему не пуста");
    }

    @Test
    public void deleteAllSubTasksShouldReturnIsEmptyTrueAndChangeEpicStatusToNEW() {
        Epic epic = taskManager.createEpic(new Epic("Эпик", DONE, "Разобраться в связях классов и интерфейсов"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask(epic,"Подзадача1", DONE, "Было"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(epic,"Подзадача2", DONE, "много"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(epic,"Подзадача3", DONE, "выпитого"));
        SubTask subTask4 = taskManager.createSubTask(new SubTask(epic,"Подзадача4", DONE, "кофе."));
        taskManager.deleteAllSubTasks();
        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertTrue(subTasks.isEmpty(), "Коллекция подзадач по-прежнему не пуста");
        assertTrue(epic.getStatus() == NEW, "СТАТУС ЭПИКА ПОСЛЕ УДАЛЕНИЯ ПОДЗАДАЧ - NEW");
    }

    @Test
    public void tasksCreatedAndSetShouldNotConflictAndDoNotChangeFields() {
        Task task = new Task("Задача", DONE, "Побрить кота");
        task.setId(10);
        taskManager.create(task); // при create срабатывает метод generateId, который должен присвоить 1 вместо 10
        List<Task> tasks = taskManager.getAllTasks();
        Task actualTask = tasks.get(0);

        assertEquals(task.getId(), actualTask.getId());
        assertEquals(1, actualTask.getId(),"Генерация id не отменила заданный вручную id");
        assertEquals(task.getName(), actualTask.getName());
        assertEquals(task.getStatus(), actualTask.getStatus());
        assertEquals(task.getDescription(), actualTask.getDescription());
    }

    @Test
    public void deleteShouldRemoveTaskEpicSubTaskFromHistory() {
        Task task1 = taskManager.create(new Task("Задача1", NEW, "Записаться на стрижку"));
        Task task2 = taskManager.create(new Task("Задача2", NEW, "Побрить кота"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик", DONE, "Разобраться в связях классов и" +
                " интерфейсов"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик", DONE, "Просто эпик"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask(epic1,"Подзадача1", DONE, "Было"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(epic2,"Подзадача2", DONE, "много"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(epic2,"Подзадача3", DONE, "кофе."));

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.getSubTask(subTask3.getId());

        taskManager.delete(task1.getId());
        taskManager.deleteEpic(epic1.getId());
        taskManager.deleteSubTask(subTask2.getId());

        assertEquals(List.of(task2, epic2, subTask3), taskManager.getHistory(), "При удалении задачи из " +
                "истории просмотров возникла ошибка");
    }

    @Test
    public void deleteAllShouldRemoveAllFromHistory() {
        Task task1 = taskManager.create(new Task("Задача1", NEW, "Записаться на стрижку"));
        Task task2 = taskManager.create(new Task("Задача2", NEW, "Побрить кота"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик", DONE, "Разобраться в связях классов и" +
                " интерфейсов"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик", DONE, "Просто эпик"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask(epic1,"Подзадача1", DONE, "Было"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(epic2,"Подзадача2", DONE, "много"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(epic2,"Подзадача3", DONE, "кофе."));

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.getSubTask(subTask3.getId());

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        assertTrue(taskManager.getHistory().isEmpty());
    }
}