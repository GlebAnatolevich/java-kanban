package service;

import model.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {  // код для работы с историей просмотров

    private final List<Task> history; // список для хранения просмотров

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public void addTask(Task task) {
        if (history.size() >= 10) { // сначала проверили на наличие 10 и более задач в истории, затем добавляем
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() { return history; }
}