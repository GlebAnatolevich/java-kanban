package service;

import exception.ManagerLoadException;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file = File.createTempFile("testfile", ".csv");

    private final Epic epic = new Epic(1,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(100), LocalDateTime.of(2024,12,25,15,0));
    private final SubTask subTask2 = new SubTask(2,epic,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(50), LocalDateTime.of(2024,12,25,15,0));
    private final SubTask subTask3 = new SubTask(3,epic,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(50), LocalDateTime.of(2024,12,25,15,50));
    private final SubTask subTask4 = new SubTask(4,epic,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(180), LocalDateTime.of(2024,12,28,12,0));
    private final Task task = new Task(5,"задача 111", NEW, "описание задачи 111",
            Duration.ofMinutes(120), LocalDateTime.of(2024,12,24,12,0));

    public FileBackedTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(file);
    }

    @Test
    public void saveAndLoadFromFileShouldMakeCorrect() throws ManagerLoadException {
        Epic epicOne = manager.createEpic(epic);
        SubTask subTaskOne = manager.createSubTask(subTask2);
        SubTask subTaskTwo = manager.createSubTask(subTask3);
        Task taskOne = manager.create(task);

        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = manager.getAllTasks();
        List<Task> tasksFromFile = managerFromFile.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<Epic> epicsFromFile = managerFromFile.getAllEpics();
        List<SubTask> subTasks = manager.getAllSubTasks();
        List<SubTask> subTasksFromFile = managerFromFile.getAllSubTasks();

        System.out.println(taskOne);
        System.out.println(epicOne);
        System.out.println(subTaskOne);
        System.out.println(subTaskTwo);

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
    public void saveAndLoadFromFileShouldSaveAndLoadEmptyTasks() throws ManagerLoadException {
        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllSubTasks());
    }
}