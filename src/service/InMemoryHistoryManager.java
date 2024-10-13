package service;

import model.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {  // код для работы с историей просмотров

    private final List<Task> history = new ArrayList<>(); // список для хранения просмотров

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }

        if (history.size() >= 10) { // сначала проверили на наличие 10 и более задач в истории, затем добавляем
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() { return new ArrayList<>(history); }
}