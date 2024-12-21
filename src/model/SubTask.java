package model;

public class SubTask extends Task {
    private Epic epic;
    private Type type = Type.SUBTASK;

    public SubTask(Epic epic, String name, Status status, String description) {
        super(name, status, description);
        this.epic = epic;
    }

    public SubTask(int id, Epic epic, String name, Status status, String description) { // конструктор для менеджера
        super(id, name, status, description);
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
                '}';
    }

    @Override
    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription(),
                epic.getId());
    }
}