package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int seq = 0;

    private int generateId() {
        return ++seq;
    }

    public Task create(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic create(Epic epic) {
        epic.setId(generateId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public SubTask create(SubTask subTask) {
        if (subTask.getEpicFromSubTasks() == null || !epics.containsKey(subTask.getEpicFromSubTasks().getId())) {
            return null;
        }
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
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId()) || epic == null) {
            return;
        }
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId()) || subTask.getEpicFromSubTasks() == null
                || !epics.containsKey(subTask.getEpicFromSubTasks().getId())) {
            return;
        } // (если в Х-Т подзадач нет такой подзадачи) ИЛИ (если эпик этой подзадачи = null) ИЛИ (если в Х-Т эпиков нет
        // эпика, в состав которого входит подзадача) ----> выходим из метода
        subTasks.put(subTask.getId(), subTask); // обновили подзадачу в Х-Т подзадач
        Epic epic = subTask.getEpicFromSubTasks();
        epic.updateSubTaskInEpic(subTask); // обновили подзадачу в списке подзадач эпика
        Epic savedEpic = epics.get(epic.getId());
        calculateStatus(savedEpic);
    }

    public void delete(int id) { tasks.remove(id); }

    public void deleteEpic(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        List<SubTask> subTasksOfEpic = getEpic(id).getSubTasks(); // получили список подзадач удаляемого эпика
        for (SubTask subTask : subTasksOfEpic) {
            subTasks.remove(subTask.getId()); // удалили поочередно каждую подзадачу из Х-Т подзадач
        }
        epics.remove(id); // удалили сам эпик из Х-Т эпиков по id
    }

    public void deleteSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return;
        }
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

    public List<SubTask> getSubTasksOfEpic(int id) { return new ArrayList<>(epics.get(id).getSubTasks()); }

    private void calculateStatus(Epic epic) {
        List<SubTask> subTasksOfEpic = epic.getSubTasks();
        int statusNew = 0;
        int statusDone = 0;
        if (subTasksOfEpic.size() == 0) {
            epic.setStatus(Status.NEW);
        } else {
            for (SubTask subTask : subTasksOfEpic) {
                if (subTask.getStatus() == Status.NEW) {
                    statusNew++;
                } else if (subTask.getStatus() == Status.DONE) {
                    statusDone++;
                }
            }
        }
        if (statusNew == subTasksOfEpic.size()) {
            epic.setStatus(Status.NEW);
        } else if (statusDone == subTasksOfEpic.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}