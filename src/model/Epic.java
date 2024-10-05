package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void updateSubTaskInEpic(SubTask subTask) {
        SubTask saved = subTask;
        saved.setName(subTask.getName());
        saved.setStatus(subTask.getStatus());
        saved.setDescription(subTask.getDescription());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}