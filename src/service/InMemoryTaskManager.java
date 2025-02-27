package service;

import exception.TaskConflictException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    protected final Set<Task> prioritizedTasks = new TreeSet<>(comparator);
    protected int seq = 0;

    private int generateId() {
        return ++seq;
    }

    @Override
    public Task create(Task task) {
        cross(task);
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
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
        if (!epics.containsKey(subTask.getEpicIdFromSubTasks())) {
            return null;
        }
        cross(subTask);
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);
        Epic epic = epics.get(subTask.getEpicIdFromSubTasks());
        epic.addTask(subTask);
        calculateStatus(epic);
        calculateEpicTime(epic);
        return subTask;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.addTask(task); // добавляем задачу в конец истории просмотров
        } else {
            throw new NoSuchElementException("Такой задачи не существует");
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addTask(epic); // добавляем эпик в конец истории просмотров
        } else {
            throw new NoSuchElementException("Такого эпика не существует");
        }
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.addTask(subTask); // добавляем подзадачу в конец истории просмотров
        } else {
            throw new NoSuchElementException("Такой подзадачи не существует");
        }
        return subTask;
    }

    @Override
    public void update(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NoSuchElementException("Такой задачи не существует");
        } else {
            cross(task);
            tasks.put(task.getId(), task);
            prioritizedTasks.remove(task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new NoSuchElementException("Такого эпика не существует");
        } else {
            Epic saved = epics.get(epic.getId());
            saved.setName(epic.getName());
            saved.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicIdFromSubTasks())) {
            throw new NoSuchElementException("Такого эпика не существует");
        } else if (!subTasks.containsKey(subTask.getId())) {
            throw new NoSuchElementException("Такой подзадачи не существует");
        } else {
            cross(subTask);
            SubTask subTaskRemoved = subTasks.get(subTask.getId()); // получили старую подзадачу из Х-Т
            Epic savedEpic = epics.get(subTask.getEpicIdFromSubTasks()); // получили эпик из Х-Т
            savedEpic.removeTask(subTaskRemoved); // удалили старую подзадачу из списка подзадач эпика
            savedEpic.addTask(subTask); // добавили новую подзадачу в список подзадач эпика
            subTasks.put(subTask.getId(), subTask); // обновили подзадачу в Х-Т подзадач
            calculateStatus(savedEpic);
            calculateEpicTime(savedEpic);
            prioritizedTasks.remove(subTask);
            prioritizedTasks.add(subTask);
        }
    }

    @Override
    public void delete(int id) {
        prioritizedTasks.remove(tasks.get(id));
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
            prioritizedTasks.remove(subTask);
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
        Epic epic = epics.get(removeSubTask.getEpicIdFromSubTasks()); // получили эпик (в котором она хранится) по удаляемой подзадаче
        Epic epicSaved = epics.get(epic.getId()); // перезаписали эпик в новый объект (пересохранили)
        epicSaved.getSubTasks().remove(removeSubTask); // получили список подзадач эпика и удалили удаляемую подзадачу из списка
        calculateStatus(epicSaved); // пересчитали статус эпика
        calculateEpicTime(epicSaved);
        historyManager.removeTask(id);
        prioritizedTasks.remove(removeSubTask);
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
            prioritizedTasks.remove(task);
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
                prioritizedTasks.remove(subTask);
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
            epic.setStartTime(null);
            epic.setDuration(null);
            List<SubTask> subTasksOfEpic = epic.getSubTasks();
            for (SubTask subTask : subTasksOfEpic) {
                historyManager.removeTask(subTask.getId());
                prioritizedTasks.remove(subTask);
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
            throw new NoSuchElementException("Такой подзадачи не существует");
        } else {
            return new ArrayList<>(epics.get(id).getSubTasks());
        }
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean cross(Task task) { // проверка на пересечение во времени
        boolean crossed = prioritizedTasks.stream()
                .filter(taskValue -> taskValue.getId() != task.getId())
                .anyMatch(taskValue ->
                        taskValue.getStartTime().isBefore(task.getStartTime())
                                && taskValue.getEndTime().isAfter(task.getEndTime())
                                || taskValue.getStartTime().isBefore(task.getStartTime())
                                && taskValue.getEndTime().isAfter(task.getStartTime())
                                || taskValue.getStartTime().isBefore(task.getEndTime())
                                && taskValue.getEndTime().isAfter(task.getEndTime())
                                || taskValue.getStartTime().isAfter(task.getStartTime())
                                && taskValue.getEndTime().isBefore(task.getEndTime()));
        if (crossed) {
            throw new TaskConflictException("Две задачи не могут выполняться одновременно");
        }
        return crossed;
    }

    private void calculateEpicTime(Epic epic) {
        List<SubTask> subTasks = getSubTasksOfEpic(epic.getId());
        Duration duration = Duration.ofMinutes(0);
        if (subTasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(duration);
        } else {
            LocalDateTime startTime = subTasks.getFirst().getStartTime(); // время старта первой в списке подзадачи
            LocalDateTime endTime = subTasks.getFirst().getEndTime(); // время окончания первой в списке подзадачи

            for (SubTask subTask : subTasks) {
                if (subTask.getStartTime().isBefore(startTime)) {
                    startTime = subTask.getStartTime();
                } else if (subTask.getEndTime().isAfter(endTime)) {
                    endTime = subTask.getEndTime();
                }
                duration = duration.plus(subTask.getDuration());
            }
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);
        }
    }
}