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

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File file = File.createTempFile("testfile", ".csv");

    public FileBackedTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(file);
    }

    @Test
    public void saveAndLoadFromFileShouldMakeCorrect() {
        manager.createEpic(epic);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.create(task);

        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = manager.getAllTasks();
        List<Task> tasksFromFile = managerFromFile.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<Epic> epicsFromFile = managerFromFile.getAllEpics();
        List<SubTask> subTasks = manager.getAllSubTasks();
        List<SubTask> subTasksFromFile = managerFromFile.getAllSubTasks();

        List<Task> managerPrioritizedTasks = manager.getPrioritizedTasks();
        List<Task> managerFromFilePrioritizedTasks = managerFromFile.getPrioritizedTasks();

        assertEquals(tasksFromFile, tasks);
        assertEquals(epicsFromFile, epics);

        assertEquals(tasksFromFile.getFirst().getId(), tasks.getFirst().getId());
        assertEquals(tasksFromFile.getFirst().getType(), tasks.getFirst().getType());
        assertEquals(tasksFromFile.getFirst().getName(), tasks.getFirst().getName());
        assertEquals(tasksFromFile.getFirst().getStatus(), tasks.getFirst().getStatus());
        assertEquals(tasksFromFile.getFirst().getDescription(), tasks.getFirst().getDescription());
        assertEquals(tasksFromFile.getFirst().getStartTime(), tasks.getFirst().getStartTime());
        assertEquals(tasksFromFile.getFirst().getDuration(), tasks.getFirst().getDuration());
        assertEquals(tasksFromFile.getFirst().getEndTime(), tasks.getFirst().getEndTime());

        assertEquals(epicsFromFile.getFirst().getId(), epics.getFirst().getId());
        assertEquals(epicsFromFile.getFirst().getType(), epics.getFirst().getType());
        assertEquals(epicsFromFile.getFirst().getName(), epics.getFirst().getName());
        assertEquals(epicsFromFile.getFirst().getStatus(), epics.getFirst().getStatus());
        assertEquals(epicsFromFile.getFirst().getDescription(), epics.getFirst().getDescription());
        assertEquals(epicsFromFile.getFirst().getSubTasks(), epics.getFirst().getSubTasks());
        assertEquals(epicsFromFile.getFirst().getStartTime(), epics.getFirst().getStartTime());
        assertEquals(epicsFromFile.getFirst().getDuration(), epics.getFirst().getDuration());
        assertEquals(epicsFromFile.getFirst().getEndTime(), epics.getFirst().getEndTime());

        assertEquals(subTasksFromFile.getFirst().getId(), subTasks.getFirst().getId());
        assertEquals(subTasksFromFile.getFirst().getType(), subTasks.getFirst().getType());
        assertEquals(subTasksFromFile.getFirst().getName(), subTasks.getFirst().getName());
        assertEquals(subTasksFromFile.getFirst().getStatus(), subTasks.getFirst().getStatus());
        assertEquals(subTasksFromFile.getFirst().getDescription(), subTasks.getFirst().getDescription());
        assertEquals(subTasksFromFile.getFirst().getEpicIdFromSubTasks(), subTasks.getFirst().getEpicIdFromSubTasks());
        assertEquals(subTasksFromFile.getFirst().getStartTime(), subTasks.getFirst().getStartTime());
        assertEquals(subTasksFromFile.getFirst().getDuration(), subTasks.getFirst().getDuration());
        assertEquals(subTasksFromFile.getFirst().getEndTime(), subTasks.getFirst().getEndTime());

        assertEquals(managerFromFilePrioritizedTasks, managerPrioritizedTasks);
    }

    @Test
    public void saveAndLoadFromFileShouldSaveAndLoadEmptyTasks() {
        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, managerFromFile.getAllSubTasks());
    }
}