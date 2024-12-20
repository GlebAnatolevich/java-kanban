package service;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static model.Status.IN_PROGRESS;
import static model.Status.NEW;
import static model.Type.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epic";

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

        if (elements.length == 6) {
            epicId = Integer.parseInt(elements[5]);
        }

        if (type == TASK) {
            return new Task(id, name, status, description);
        } else if (type == SUBTASK) {
            return new SubTask(id, name, status, description, epicId);
        } else if (type == EPIC) {
            return new Epic(id, name, status, description);
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
        boolean isName = true;
        boolean isTask = true;
        int maxId = 0;
        int id;

        for (String line : lines) {
            if (isName) {
                isName = false;
                continue;
            }
            if (line.isEmpty() || line.equals("\r")) {
                isTask = false;
                continue;
            }
            if (isTask) {
                Type type = valueOf(line.split(",")[1]);
                switch (type) {
                    case EPIC:
                        Epic epic = (Epic) fileBackedTaskManager.fromString(line);
                        id = epic.getId();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTaskManager.epics.put(id, epic);
                        break;

                    case SUBTASK:
                        SubTask subTask = (SubTask) fileBackedTaskManager.fromString(line);
                        id = subTask.getId();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTaskManager.subTasks.put(id, subTask);
                        break;

                    case TASK:
                        Task task = fileBackedTaskManager.fromString(line);

                        id = task.getId();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTaskManager.tasks.put(id, task);
                        break;
                }
            }
        }

        for (SubTask subTask : fileBackedTaskManager.subTasks.values()) {
            int epicId = subTask.getEpicId();
            fileBackedTaskManager.epics.get(epicId).addTask(subTask);
        }

        fileBackedTaskManager.seq = maxId;
        return fileBackedTaskManager;
    }

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(new File("Autosave","autosave.csv"));
        Task task1 = taskManager.create(new Task("задача 111", NEW, "описание задачи 111"));
        Task task2 = taskManager.create(new Task("задача 222", IN_PROGRESS, "описание задачи 222"));
        Epic epic1 = taskManager.createEpic(new Epic("эпик 111 с 1 подзадачей", NEW, "описание " +
                "эпика 111"));
        Epic epic2 = taskManager.createEpic(new Epic("эпик 222 с 2 подзадачами", NEW, "описание " +
                "эпика 222"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask(epic1, "подзадача 111", NEW,
                "описание подзадачи 111"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(epic2, "подзадача 222", NEW,
                "описание подзадачи 222"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(epic2, "подзадача 333", NEW,
                "описание подзадачи 333"));

        System.out.println("Оставшиеся задачи:" + taskManager.getAllTasks());
        System.out.println("Оставшиеся эпики:" + taskManager.getAllEpics());
        System.out.println("Оставшиеся подзадачи:" + taskManager.getAllSubTasks());
    }
}