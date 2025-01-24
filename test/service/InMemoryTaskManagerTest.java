package service;

import exception.TaskConflictException;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        manager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    public void crossShouldWorkCorrectly() {
        manager.createEpic(epic);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        manager.createSubTask(subTask4);
        manager.create(task);

        assertEquals(List.of(task, subTask2, subTask3, subTask4), manager.getPrioritizedTasks());
    }

    @Test
    public void testManagerLoadException() {
        assertThrows(IOException.class, () -> {
            File file = File.createTempFile("testfile", ".pdf", new File("Java"));
            Files.readString(Path.of(file.getAbsolutePath()));
        }, "Должно быть выброшено исключение");
    }

    @Test
    public void testTaskConflictException() {
        assertThrows(TaskConflictException.class, () -> {
            manager.create(new Task(1,"задача 111_1", IN_PROGRESS, "описание задачи 111",
                    Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,12,0)));
            manager.create(new Task(2,"задача 111_1", IN_PROGRESS, "описание задачи 111",
                    Duration.ofMinutes(120), LocalDateTime.of(2024,12,25,13,0)));
        }, "Должно быть выброшено исключение TaskConflictException");
    }
}