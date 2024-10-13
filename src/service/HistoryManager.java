package service;

import model.Task;

import java.util.List;

public interface HistoryManager {

    void addTask(Task task); // добавили задачу в список просмотров

    List<Task> getHistory(); // вызвали метод, выводящий список просмотров
}