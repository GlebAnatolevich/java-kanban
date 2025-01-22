package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();
    private Type type = Type.EPIC;
    private Duration duration;
    private LocalDateTime startTime;
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
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
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription(),
                getStartTime(), getDuration(), "");
    }
}