package model;

public class SubTask extends Task {
    Epic epic;

    public SubTask(String name, Status status, String description) {
        super(name, status, description);
    }

    public Epic getEpicFromSubTasks() { return epic; }
}