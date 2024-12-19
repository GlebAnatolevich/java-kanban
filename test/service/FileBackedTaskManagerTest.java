package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    File file = File.createTempFile("testfile", ".csv");

    private Task task1 = new Task("Задача1", NEW, "Забрать товар");
    private Task task2 = new Task("Задача2", IN_PROGRESS, "Забрать атрибутику");
    private Epic epic1 = new Epic("Эпик1", DONE, "Разработать программу");
    private Epic epic2 = new Epic("Эпик2", NEW, "Разработать программы");
    private SubTask subTask1 = new SubTask(epic1, "Подзадача1", DONE, "Составить структуру");
    private SubTask subTask2 = new SubTask(epic2, "Подзадача2", NEW, "Составить структуры");

    public FileBackedTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(file);
    }

    @Test
    public void saveAndLoadFromFileShouldMakeCorrect() {
        Task taskOne = manager.create(task1);
        Task taskTwo = manager.create(task2);
        Epic epicOne = manager.createEpic(epic1);
        Epic epicTwo = manager.createEpic(epic2);
        SubTask subTaskOne = manager.createSubTask(subTask1);
        SubTask subTaskTwo = manager.createSubTask(subTask2);

        manager.save();
        manager.loadFromFile(file);

        List<Task> tasks = manager.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<SubTask> subTasks = manager.getAllSubTasks();

        assertEquals(List.of(taskOne, taskTwo), tasks);
        assertEquals(List.of(epicOne, epicTwo), epics);
        assertEquals(List.of(subTaskOne, subTaskTwo), subTasks);
    }

    @Test
    public void saveAndLoadFromFileShouldSaveAndLoadEmptyTasks() {
        manager.save();
        manager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubTasks());
    }

    @Test
    public void updateTaskShouldReturnSameId() {
        Task task = new Task("Задача1", NEW, "Записаться на стрижку");
        manager.create(task);
        Task updatedTask = new Task("Задача1", IN_PROGRESS, "Записаться на стрижку в тот барбершоп");
        updatedTask.setId(task.getId());
        manager.update(updatedTask);

        assertEquals(task, updatedTask, "Задачи не равны");
        assertEquals(task.getId(), updatedTask.getId(), "Id задач не равны");
    }

    @Test
    public void updateEpicShouldReturnSameId() {
        Epic epic = new Epic("Эпик1", NEW, "Записаться на стрижку");
        manager.createEpic(epic);
        Epic updatedEpic = new Epic("Эпик1", NEW, "Записаться на стрижку в тот барбершоп");
        updatedEpic.setId(epic.getId());
        manager.updateEpic(updatedEpic);

        assertEquals(epic, updatedEpic, "Эпики не равны");
        assertEquals(epic.getId(), updatedEpic.getId(), "Id эпиков не равны");
    }

    @Test
    public void updateSubTaskShouldReturnSameIdAndChangeEpicStatus() {
        Epic epic = new Epic("Эпик1", NEW, "Записаться на стрижку");
        manager.createEpic(epic);
        SubTask subTask = new SubTask(epic,"Подзадача1", NEW, "Скачать приложение барбершопа");
        manager.createSubTask(subTask);
        SubTask updatedSubTask = new SubTask(epic,"Подзадача1", IN_PROGRESS, "Записаться через официальный сайт");
        updatedSubTask.setId(subTask.getId());
        manager.updateSubTask(updatedSubTask);

        assertEquals(subTask, updatedSubTask, "Подзадачи не равны");
        assertEquals(subTask.getId(), updatedSubTask.getId(), "Id подзадач не равны");
        assertTrue(epic.getStatus() == IN_PROGRESS, "СТАТУС ЭПИКА РАССЧИТАН НЕВЕРНО");
    }

    @Test
    public void deleteAllTasksShouldReturnIsEmptyTrue() {
        Task task1 = manager.create(new Task("Задача1", NEW, "Записаться на стрижку"));
        Task task2 = manager.create(new Task("Задача2", NEW, "Побрить кота"));
        manager.deleteAllTasks();
        List<Task> tasks = manager.getAllTasks();

        assertTrue(tasks.isEmpty(), "Коллекция задач по-прежнему не пуста");
    }

    @Test
    public void deleteAllEpicsShouldReturnIsEmptyTrue() {
        Epic epic1 = manager.createEpic(new Epic("Эпик1", IN_PROGRESS, "Создать бесконечные тесты трекера"));
        Epic epic2 = manager.createEpic(new Epic("Эпик2", DONE, "Разобраться в связях классов и интерфейсов"));
        manager.deleteAllEpics();
        List<Epic> epics = manager.getAllEpics();

        assertTrue(epics.isEmpty(), "Коллекция эпиков по-прежнему не пуста");
    }

    @Test
    public void deleteAllSubTasksShouldReturnIsEmptyTrueAndChangeEpicStatusToNEW() {
        Epic epic = manager.createEpic(new Epic("Эпик", DONE, "Разобраться в связях классов и интерфейсов"));
        SubTask subTask1 = manager.createSubTask(new SubTask(epic,"Подзадача1", DONE, "Было"));
        SubTask subTask2 = manager.createSubTask(new SubTask(epic,"Подзадача2", DONE, "много"));
        SubTask subTask3 = manager.createSubTask(new SubTask(epic,"Подзадача3", DONE, "выпитого"));
        SubTask subTask4 = manager.createSubTask(new SubTask(epic,"Подзадача4", DONE, "кофе."));
        manager.deleteAllSubTasks();
        List<SubTask> subTasks = manager.getAllSubTasks();

        assertTrue(subTasks.isEmpty(), "Коллекция подзадач по-прежнему не пуста");
        assertTrue(epic.getStatus() == NEW, "СТАТУС ЭПИКА ПОСЛЕ УДАЛЕНИЯ ПОДЗАДАЧ - NEW");
    }
}