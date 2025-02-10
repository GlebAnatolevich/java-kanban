package service;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static model.Type.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,startTime,duration,endTime,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
        if (!file.isFile()) {
            try {
                Path path = Files.createFile(Paths.get("Autosave", "autosave.csv"));
            } catch (IOException exception) {
                throw new ManagerSaveException("Возникла проблема при создании файла. Возможно, он уже существует");
            }
        }
    }

    @Override
    public Task create(Task task) {
        super.create(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write(HEADER + "\n");

            for (Task task : getAllTasks()) {
                writer.write(String.format("%s\n", task.toStringForFile()));
            }

            for (Epic epic : getAllEpics()) {
                writer.write(String.format("%s\n", epic.toStringForFile()));
            }

            for (SubTask subTask : getAllSubTasks()) {
                writer.write(String.format("%s\n", subTask.toStringForFile()));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Невозможно записать в файл");
        }
    }

    private Task fromString(String text) {
        Task task = new Task();
        int epicId = 0;
        String[] elements = text.split(",");
        int id = Integer.parseInt(elements[0]);
        Type type = valueOf(elements[1]);
        String name = String.valueOf(elements[2]);
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];
        LocalDateTime startTime = LocalDateTime.parse(elements[5]);
        Duration duration = Duration.parse(elements[6]);
        LocalDateTime endTime = LocalDateTime.parse(elements[7]);

        if (elements.length == 9) {
            epicId = Integer.parseInt(elements[8]);
        }

        if (type == TASK) {
            return new Task(id, name, status, description, duration, startTime);
        } else if (type == SUBTASK) {
            Epic epic = epics.get(epicId);
            SubTask subTask = new SubTask(id, epic, name, status, description, duration, startTime);
            epic.addTask(subTask);
            return subTask;
        } else if (type == EPIC) {
            Epic epic = new Epic(id, name, status, description, duration, startTime);
            epic.setEndTime(endTime);
            return epic;
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        String data;
        try {
            data = Files.readString(Path.of(file.getAbsolutePath()));
        } catch (IOException exception) {
            throw new ManagerLoadException("Невозможно прочитать файл");
        }
        String[] lines = data.split("\n");
        int maxId = 0;
        for (int i = 1; i < lines.length; i++) {
            Task task = fileBackedTaskManager.fromString(lines[i]);
            int id = task.getId();
            if (id > maxId) {
                maxId = id;
            }
            switch (task.getType()) {
                case EPIC:
                    fileBackedTaskManager.epics.put(id, (Epic) task);
                    break;
                case SUBTASK:
                    fileBackedTaskManager.subTasks.put(id, (SubTask) task);
                    fileBackedTaskManager.prioritizedTasks.add(task);
                    break;
                case TASK:
                    fileBackedTaskManager.tasks.put(id, task);
                    fileBackedTaskManager.prioritizedTasks.add(task);
                    break;
            }
        }
        fileBackedTaskManager.seq = maxId;
        return fileBackedTaskManager;
    }
}