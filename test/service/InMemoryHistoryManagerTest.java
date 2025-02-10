package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    private final Epic epic = new Epic(1,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(360), LocalDateTime.of(2025,12,25,10,0));
    private final SubTask subTask2 = new SubTask(2, epic.getId(), "задача 111", NEW, "описание задачи 222",
            Duration.ofMinutes(120), LocalDateTime.of(2025,12,25,10,0));
    private final SubTask subTask3 = new SubTask(3,epic.getId(),"задача 111", NEW, "описание задачи 333",
            Duration.ofMinutes(120), LocalDateTime.of(2025,12,25,12,0));
    private final SubTask subTask4 = new SubTask(4,epic.getId(),"задача 111", NEW, "описание задачи 444",
            Duration.ofMinutes(120), LocalDateTime.of(2025,12,25,14,0));
    private final Task task = new Task(5,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(120), LocalDateTime.of(2024,12,24,12,0));

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void getHistoryShouldReturnListOfCorrectViewsAmount() {

        for (int i = 0; i < 15; i++) {
            taskManager.create(new Task(6,"задача 111", NEW, "описание задачи 111",
                    Duration.ofMinutes(120), LocalDateTime.of(2024,12,24,12,0)));
        }

        List<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            taskManager.getTask(task.getId());
        }

        assertEquals(15, taskManager.getHistory().size(), "Количество просмотров неверно");
    }

    @Test
    public void getHistoryShouldReturnPreviousTaskAfterUpdate() {
        taskManager.create(task);
        taskManager.getTask(task.getId()); // для пополнения истории просмотров

        Task updatedTask = new Task(6,"задача 666", NEW, "описание задачи 666",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,23,12,0));
        updatedTask.setId(task.getId());
        taskManager.update(updatedTask);

        List<Task> tasks = taskManager.getHistory();
        Task prevTask = tasks.getFirst();

        assertEquals(task.getName(), prevTask.getName(), "Сохранена не предыдущая версия");
        assertEquals(task.getDescription(), prevTask.getDescription(), "Сохранена не предыдущая версия");
        assertSame(NEW, prevTask.getStatus(), "Сохранена не предыдущая версия");
    }

    @Test
    public void getHistoryShouldReturnPreviousEpicAfterUpdate() {
        taskManager.createEpic(epic);
        taskManager.getEpic(epic.getId()); // для пополнения истории просмотров

        Epic updatedEpic = new Epic(7,"эпик 777", NEW, "описание эпика 777",
                Duration.ofMinutes(360), LocalDateTime.of(2026,12,25,10,0));
        updatedEpic.setId(epic.getId());
        taskManager.updateEpic(updatedEpic);

        List<Task> tasks = taskManager.getHistory();
        Task prevTask = tasks.getFirst();

        assertEquals(epic.getName(), prevTask.getName(), "Сохранена не предыдущая версия");
        assertEquals(epic.getDescription(), prevTask.getDescription(), "Сохранена не предыдущая версия");
        assertSame(NEW, prevTask.getStatus(), "Сохранена не предыдущая версия, статус эпика не задается, а пересчитывается программно");
    }

    @Test
    public void getHistoryShouldReturnPreviousSubTaskAfterUpdate() {
        taskManager.createEpic(epic);

        taskManager.createSubTask(subTask2);
        taskManager.getSubTask(subTask2.getId()); // для пополнения истории просмотров

        SubTask updatedSubTask = subTask3;
        updatedSubTask.setId(subTask2.getId());
        taskManager.updateSubTask(updatedSubTask);

        List<Task> tasks = taskManager.getHistory();
        Task prevTask = tasks.getFirst();

        assertEquals(subTask2.getName(), prevTask.getName(), "Сохранена не предыдущая версия");
        assertEquals(subTask2.getDescription(), prevTask.getDescription(), "Сохранена не предыдущая версия");
        assertSame(NEW, prevTask.getStatus(), "Сохранена не предыдущая версия");
    }

    @Test
    public void addTask_ShouldAddTaskToHistoryListWithoutDouble() {
        taskManager.create(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask2);

        historyManager.addTask(task);
        historyManager.addTask(epic);
        historyManager.addTask(subTask2);
        historyManager.addTask(task);

        assertEquals(List.of(epic, subTask2, task), historyManager.getHistory(), "При добавлении задачи в " +
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
    public void removeTaskFromBeginAndEndOfList_ShouldRemoveTaskFromHistoryList() {
        taskManager.create(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask2);

        historyManager.addTask(task);
        historyManager.addTask(epic);
        historyManager.addTask(subTask2);

        historyManager.removeTask(task.getId());
        historyManager.removeTask(subTask2.getId());

        assertEquals(List.of(epic), historyManager.getHistory(), "При удалении задачи из " +
                "начала и конца истории просмотров возникла ошибка");
    }

    @Test
    public void removeTaskFromMiddleOfList_ShouldRemoveTaskFromHistoryList() {
        taskManager.create(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask2);
        Task task1 = new Task(9,"задача 999", NEW, "описание задачи 999",
                Duration.ofMinutes(120), LocalDateTime.of(2024,12,28,12,0));
        taskManager.create(task1);

        historyManager.addTask(task);
        historyManager.addTask(epic);
        historyManager.addTask(subTask2);
        historyManager.addTask(task1);

        historyManager.removeTask(epic.getId());
        historyManager.removeTask(subTask2.getId());

        assertEquals(List.of(task, task1), historyManager.getHistory(), "При удалении задачи из " +
                "середины истории просмотров возникла ошибка");
    }
}