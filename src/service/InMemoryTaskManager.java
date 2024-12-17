package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int seq = 0;

    private int generateId() {
        return ++seq;
    }

    @Override
    public Task create(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
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

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.addTask(task); // добавляем задачу в конец истории просмотров
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addTask(epic); // добавляем эпик в конец истории просмотров
        }
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.addTask(subTask); // добавляем подзадачу в конец истории просмотров
        }
        return subTask;
    }

    @Override
    public void update(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId()) || epic == null) {
            return;
        }
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId()) || subTask.getEpicFromSubTasks() == null
                || !epics.containsKey(subTask.getEpicFromSubTasks().getId())) {
            return;
        } // (если в Х-Т подзадач нет такой подзадачи) ИЛИ (если эпик этой подзадачи = null) ИЛИ (если в Х-Т эпиков нет
        // эпика, в состав которого входит подзадача) ----> выходим из метода
        SubTask subTaskRemoved = subTasks.get(subTask.getId()); // получили старую подзадачу из Х-Т
        Epic savedEpic = epics.get(subTask.getEpicFromSubTasks().getId()); // получили эпик из Х-Т
        savedEpic.removeTask(subTaskRemoved); // удалили старую подзадачу из списка подзадач эпика
        savedEpic.addTask(subTask); // добавили новую подзадачу в список подзадач эпика
        subTasks.put(subTask.getId(), subTask); // обновили подзадачу в Х-Т подзадач
        calculateStatus(savedEpic);
    }

    @Override
    public void delete(int id) {
        tasks.remove(id);
        historyManager.removeTask(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        Epic epic = epics.get(id);
        List<SubTask> subTasksOfEpic = epic.getSubTasks(); // получили список подзадач удаляемого эпика
        for (SubTask subTask : subTasksOfEpic) {
            subTasks.remove(subTask.getId()); // удалили поочередно каждую подзадачу из Х-Т подзадач
            historyManager.removeTask(subTask.getId());
        }
        epics.remove(id); // удалили сам эпик из Х-Т эпиков по id
        historyManager.removeTask(id);
    }

    @Override
    public void deleteSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return;
        }
        SubTask removeSubTask = subTasks.remove(id); // removeSubTask - удаляемая подзадача, удалили её из Х-Т
        Epic epic = removeSubTask.getEpicFromSubTasks(); // получили эпик (в котором она хранится) по удаляемой подзадаче
        Epic epicSaved = epics.get(epic.getId()); // перезаписали эпик в новый объект (пересохранили)
        epicSaved.getSubTasks().remove(removeSubTask); // получили список подзадач эпика и удалили удаляемую подзадачу из списка
        calculateStatus(epicSaved); // пересчитали статус эпика
        historyManager.removeTask(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.removeTask(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            historyManager.removeTask(epic.getId());
            List<SubTask> subTasksOfEpic = epic.getSubTasks();
            for (SubTask subTask : subTasksOfEpic) {
                historyManager.removeTask(subTask.getId());
            }
            subTasksOfEpic.clear();
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            List<SubTask> subTasksOfEpic = epic.getSubTasks();
            for (SubTask subTask : subTasksOfEpic) {
                historyManager.removeTask(subTask.getId());
            }
            subTasksOfEpic.clear();
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<SubTask> getSubTasksOfEpic(int id) {
        if (!epics.containsKey(id) || epics.get(id) == null) {
            return null;
        }
        return new ArrayList<>(epics.get(id).getSubTasks());
    }

    private void calculateStatus(Epic epic) {
        List<SubTask> subTasksOfEpic = epic.getSubTasks();
        int statusNew = 0;
        int statusDone = 0;
        if (subTasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
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