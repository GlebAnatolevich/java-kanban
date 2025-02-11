package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(int epicId, String name, Status status, String description) {
        super(name, status, description);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public SubTask(int id, int epicId, String name, Status status, String description) { // конструктор для менеджера
        super(id, name, status, description);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public SubTask(int id, int epicId, String name, Status status, String description, Duration duration,
                   LocalDateTime startTime) {
        // конструктор с полями продолжительности и времени старта
        super(id, name, status, description, duration, startTime);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public int getEpicIdFromSubTasks() {
        return epicId;
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