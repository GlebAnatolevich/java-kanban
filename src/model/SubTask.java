package model;

public class SubTask extends Task {
    private final Epic epic;

    public SubTask(Epic epic, String name, Status status, String description) {
        super(name, status, description);
        this.epic = epic;
    }

    public Epic getEpicFromSubTasks() { return epic; }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}