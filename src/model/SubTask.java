package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Epic epic;
    private Type type = Type.SUBTASK;
    private Duration duration;
    private LocalDateTime startTime;

    public SubTask(Epic epic, String name, Status status, String description) {
        super(name, status, description);
        this.epic = epic;
    }

    public SubTask(int id, Epic epic, String name, Status status, String description) { // конструктор для менеджера
        super(id, name, status, description);
        this.epic = epic;
    }

    public SubTask(int id, Epic epic, String name, Status status, String description, Duration duration,
                   LocalDateTime startTime) {
        // конструктор с полями продолжительности и времени старта
        super(id, name, status, description, duration, startTime);
        this.epic = epic;
    }

    public Epic getEpicFromSubTasks() {
        return epic;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
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
                getStartTime(), getDuration(), epic.getId());
    }
}