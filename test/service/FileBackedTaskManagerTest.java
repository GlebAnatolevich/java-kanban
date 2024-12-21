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
    private Epic epic1 = new Epic("Эпик1", DONE, "Разработать программу");
    private SubTask subTask1 = new SubTask(epic1, "Подзадача1", DONE, "Составить структуру");

    public FileBackedTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(file);
    }

    @Test
    public void saveAndLoadFromFileShouldMakeCorrect() {
        Task taskOne = manager.create(task1);
        Epic epicOne = manager.createEpic(epic1);
        SubTask subTaskOne = manager.createSubTask(subTask1);

        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = manager.getAllTasks();
        List<Task> tasksFromFile = managerFromFile.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<Epic> epicsFromFile = managerFromFile.getAllEpics();
        List<SubTask> subTasks = manager.getAllSubTasks();
        List<SubTask> subTasksFromFile = managerFromFile.getAllSubTasks();

        assertEquals(tasksFromFile, tasks);
        assertEquals(epicsFromFile, epics);

        assertEquals(tasksFromFile.getFirst().getId(), tasks.getFirst().getId());
        assertEquals(tasksFromFile.getFirst().getType(), tasks.getFirst().getType());
        assertEquals(tasksFromFile.getFirst().getName(), tasks.getFirst().getName());
        assertEquals(tasksFromFile.getFirst().getStatus(), tasks.getFirst().getStatus());
        assertEquals(tasksFromFile.getFirst().getDescription(), tasks.getFirst().getDescription());

        assertEquals(epicsFromFile.getFirst().getId(), epics.getFirst().getId());
        assertEquals(epicsFromFile.getFirst().getType(), epics.getFirst().getType());
        assertEquals(epicsFromFile.getFirst().getName(), epics.getFirst().getName());
        assertEquals(epicsFromFile.getFirst().getStatus(), epics.getFirst().getStatus());
        assertEquals(epicsFromFile.getFirst().getDescription(), epics.getFirst().getDescription());
        assertEquals(epicsFromFile.getFirst().getSubTasks(), epics.getFirst().getSubTasks());

        assertEquals(subTasksFromFile.getFirst().getId(), subTasks.getFirst().getId());
        assertEquals(subTasksFromFile.getFirst().getType(), subTasks.getFirst().getType());
        assertEquals(subTasksFromFile.getFirst().getName(), subTasks.getFirst().getName());
        assertEquals(subTasksFromFile.getFirst().getStatus(), subTasks.getFirst().getStatus());
        assertEquals(subTasksFromFile.getFirst().getDescription(), subTasks.getFirst().getDescription());
        assertEquals(subTasksFromFile.getFirst().getEpicFromSubTasks(), subTasks.getFirst().getEpicFromSubTasks());
    }

    @Test
    public void saveAndLoadFromFileShouldSaveAndLoadEmptyTasks() {
        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllSubTasks());
    }
}