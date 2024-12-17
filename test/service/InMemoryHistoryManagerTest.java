package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    private Task task1 = new Task("Задача1", NEW, "Забрать товар");
    private Epic epic1 = new Epic("Эпик1", NEW, "Разработать программу");
    private SubTask subTask1 = new SubTask(epic1,"Подзадача1", NEW, "Составить структуру");

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void getHistoryShouldReturnListOfCorrectViewsAmount() {

        for (int i = 0; i < 15; i++) {
            taskManager.create(new Task("Задача", NEW, "Записаться на стрижку"));
        }

        List<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            taskManager.getTask(task.getId());
        }

        assertEquals(15, taskManager.getHistory().size(), "Количество просмотров неверно");
    }

    @Test
    public void getHistoryShouldReturnPreviousTaskAfterUpdate() {
        Task task = new Task("Задача1", NEW, "Записаться на стрижку");
        taskManager.create(task);
        taskManager.getTask(task.getId()); // для пополнения истории просмотров

        Task updatedTask = new Task("Задача уже не 1", IN_PROGRESS, "Записаться на стрижку в тот барбершоп");
        updatedTask.setId(task.getId());
        taskManager.update(updatedTask);

        List<Task> tasks = taskManager.getHistory();
        Task prevTask = tasks.get(0);

        assertEquals(task.getName(), prevTask.getName(), "Сохранена не предыдущая версия");
        assertEquals(task.getDescription(), prevTask.getDescription(), "Сохранена не предыдущая версия");
        assertTrue(NEW == prevTask.getStatus(), "Сохранена не предыдущая версия");
    }

    @Test
    public void getHistoryShouldReturnPreviousEpicAfterUpdate() {
        Epic epic = new Epic("Эпик1", NEW, "Записаться на стрижку");
        taskManager.createEpic(epic);
        taskManager.getEpic(epic.getId()); // для пополнения истории просмотров

        Epic updatedEpic = new Epic("Эпик уже не 1", IN_PROGRESS, "Записаться на стрижку в тот барбершоп");
        updatedEpic.setId(epic.getId());
        taskManager.updateEpic(updatedEpic);

        List<Task> tasks = taskManager.getHistory();
        Task prevTask = tasks.get(0);

        assertEquals(epic.getName(), prevTask.getName(), "Сохранена не предыдущая версия");
        assertEquals(epic.getDescription(), prevTask.getDescription(), "Сохранена не предыдущая версия");
        assertTrue(NEW == prevTask.getStatus(), "Сохранена не предыдущая версия, статус эпика не задается, а пересчитывается программно");
    }

    @Test
    public void getHistoryShouldReturnPreviousSubTaskAfterUpdate() {
        Epic epic = new Epic("Эпик1", NEW, "Записаться на стрижку");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask(epic,"Подзадача1", NEW, "Скачать приложение");
        taskManager.createSubTask(subTask);
        taskManager.getSubTask(subTask.getId()); // для пополнения истории просмотров

        SubTask updatedSubTask = new SubTask(epic,"Подзадача уже не 1", DONE, "Записаться на стрижку в тот барбершоп");
        updatedSubTask.setId(subTask.getId());
        taskManager.updateSubTask(updatedSubTask);

        List<Task> tasks = taskManager.getHistory();
        Task prevTask = tasks.get(0);

        assertEquals(subTask.getName(), prevTask.getName(), "Сохранена не предыдущая версия");
        assertEquals(subTask.getDescription(), prevTask.getDescription(), "Сохранена не предыдущая версия");
        assertTrue(NEW == prevTask.getStatus(), "Сохранена не предыдущая версия");
    }

    @Test
    public void addTask_ShouldAddTaskToHistoryList() {
        taskManager.create(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        historyManager.addTask(task1);
        historyManager.addTask(epic1);
        historyManager.addTask(subTask1);
        historyManager.addTask(task1);

        assertEquals(List.of(epic1, subTask1, task1), historyManager.getHistory(), "При добавлении задачи в " +
                "историю просмотров возникла ошибка");
    }

    @Test
    public void addTask_ShouldReturnEmptyIfTaskIsEmpty() {
        historyManager.addTask(null);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void addTask_ShouldReturnEmptyIfNoHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void removeTask_ShouldRemoveTaskFromHistoryList() {
        taskManager.create(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        historyManager.addTask(task1);
        historyManager.addTask(epic1);
        historyManager.addTask(subTask1);
        historyManager.addTask(task1);

        historyManager.removeTask(subTask1.getId());

        assertEquals(List.of(epic1, task1), historyManager.getHistory(), "При удалении задачи из " +
                "истории просмотров возникла ошибка");
    }
}