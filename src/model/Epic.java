package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();
    private Type type = Type.EPIC;

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    public Epic(int id, String name, Status status, String description) { // конструктор для менеджера
        super(id, name, status, description);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void removeTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    @Override
    public Type getType() {
        return type;
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

    @Override
    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription(), "");
    }
}