package service;

import model.Task;

import java.util.List;

public interface HistoryManager {

    void addTask(Task task); // добавили задачу в список просмотров

    void removeTask(int id);

    List<Task> getHistory(); // вызвали метод, выводящий список просмотров
}