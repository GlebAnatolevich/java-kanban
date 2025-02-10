package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;
    private final Type type = Type.SUBTASK;

    public SubTask(int epicId, String name, Status status, String description) {
        super(name, status, description);
        this.epicId = epicId;
    }

    public SubTask(int id, int epicId, String name, Status status, String description) { // конструктор для менеджера
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public SubTask(int id, int epicId, String name, Status status, String description, Duration duration,
                   LocalDateTime startTime) {
        // конструктор с полями продолжительности и времени старта
        super(id, name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicIdFromSubTasks() {
        return epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public Type getType() {
        return type;
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
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", id, type, name, status, description, startTime, duration,
                getEndTime(), epicId);
    }
}