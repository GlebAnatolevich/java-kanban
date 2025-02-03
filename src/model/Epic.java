package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();
    private final Type type = Type.EPIC;
    private LocalDateTime endTime;

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    public Epic(int id, String name, Status status, String description) { // конструктор для менеджера
        super(id, name, status, description);
    }

    public Epic(int id, String name, Status status, String description, Duration duration, LocalDateTime startTime) {
        // конструктор с полями продолжительности и времени старта
        super(id, name, status, description, duration, startTime);
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", id, type, name, status, description, startTime, duration,
                endTime, "");
    }
}