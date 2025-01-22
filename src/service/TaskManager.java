package service;

import model.Task;
import model.Epic;
import model.SubTask;

import java.util.List;

public interface TaskManager {

    Task create(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void update(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void delete(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    List<Task> getHistory();

    List<SubTask> getSubTasksOfEpic(int id);

    List<Task> getPrioritizedTasks();
}