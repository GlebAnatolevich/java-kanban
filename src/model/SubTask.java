package model;

public class SubTask extends Task {
    private Epic epic;
    private Type type = Type.SUBTASK;
    private int epicId;

    public SubTask(Epic epic, String name, Status status, String description) {
        super(name, status, description);
        this.epic = epic;
    }

    public SubTask(int id, String name, Status status, String description, int epicId) { // конструктор для менеджера
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public Epic getEpicFromSubTasks() {
        return epic;
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
                '}';
    }

    @Override
    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription(),
                epic.getId());
    }
}