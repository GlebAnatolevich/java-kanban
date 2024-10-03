package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, SubTask> subTasks;
    int seq = 0;

    private int generateId() {
        return ++seq;
    }

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public Task create(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic create(Epic epic) {
        Epic epic1 = new Epic(epic.getName(), Status.NEW, epic.getDescription());
        epic1.setId(generateId());
        epics.put(epic1.getId(), epic1);
        return epic1;
    }

    public SubTask create(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicFromSubTasks().getId());
        epic.addTask(subTask);
        calculateStatus(epic);
        return subTask;
    }

    public Task get(int id) { return tasks.get(id); } // получаем по id

    public Epic getEpic(int id) { return epics.get(id); } // получаем по id

    public SubTask getSubTask(int id) { return subTasks.get(id); } // получаем по id

    public void update(Task task) {
        Task saved = tasks.get(task.getId());
        saved.setName(task.getName());
        saved.setStatus(task.getStatus());
        saved.setDescription(task.getDescription());
    }

    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    public void updateSubTask(SubTask subTask) {
        SubTask saved = subTasks.get(subTask.getId());
        saved.setName(subTask.getName());
        saved.setStatus(subTask.getStatus());
        saved.setDescription(subTask.getDescription());
        Epic epic = saved.getEpicFromSubTasks();
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        calculateStatus(savedEpic);
    }

    public void delete(int id) { tasks.remove(id); }

    public void deleteEpic(int id) {
        List<SubTask> subTasksOfEpic = getEpic(id).getSubTasks(); // получили список подзадач удаляемого эпика
        for (SubTask subTask : subTasksOfEpic) {
            subTasks.remove(subTask.getId()); // удалили поочередно каждую подзадачу из Х-Т подзадач
        }
        epics.remove(id); // удалили сам эпик из Х-Т эпиков по id
    }

    public void deleteSubTask(int id) {
        SubTask removeSubTask = subTasks.remove(id); // removeSubTask - удаляемая подзадача, удалили её из Х-Т
        Epic epic = removeSubTask.getEpicFromSubTasks(); // получили эпик (в котором она хранится) по удаляемой подзадаче
        Epic epicSaved = epics.get(epic.getId()); // перезаписали эпик в новый объект (пересохранили)
        epicSaved.getSubTasks().remove(removeSubTask); // получили список подзадач эпика и удалили удаляемую подзадачу из списка
        calculateStatus(epicSaved); // пересчитали статус эпика
    }

    public List<Task> getAllTasks() { return new ArrayList<>(tasks.values()); }

    public List<Task> getAllEpics() { return new ArrayList<>(epics.values()); }

    public List<Task> getAllSubTasks() { return new ArrayList<>(subTasks.values()); }

    public void deleteAllTasks() { tasks.clear(); }

    public void deleteAllEpics() { epics.clear(); subTasks.clear(); }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            List<SubTask> subTasksOfEpic = epic.getSubTasks();
            subTasksOfEpic.clear();
        }
    }

    private void calculateStatus(Epic epic) {
        Status status = Status.NEW;
        List<SubTask> subTasksOfEpic = epic.getSubTasks();
        if (subTasksOfEpic.size() == 0) {
            epic.setStatus(Status.NEW);
        } else {
            for (SubTask subTask : subTasksOfEpic) {
                if (subTask.getStatus() == Status.NEW) {
                    epic.setStatus(Status.NEW);
                }
                if (subTask.getStatus() == Status.DONE) {
                    epic.setStatus(Status.DONE);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        }
    }
}