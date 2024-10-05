import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

import static model.Status.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.create(new Task("задача 111", NEW, "описание задачи 111"));
        Task task2 = taskManager.create(new Task("задача 222", IN_PROGRESS, "описание задачи 222"));
        Epic epic1 = taskManager.create(new Epic("эпик 111 с 1 подзадачей", NEW, "описание эпика 111"));
        Epic epic2 = taskManager.create(new Epic("эпик 222 с 2 подзадачами", NEW, "описание эпика 222"));
        SubTask subTask1 = taskManager.create(new SubTask(epic1,"подзадача 111", NEW, "описание подзадачи 111"));
        SubTask subTask2 = taskManager.create(new SubTask(epic2,"подзадача 222", NEW, "описание подзадачи 222"));
        SubTask subTask3 = taskManager.create(new SubTask(epic2,"подзадача 333", NEW, "описание подзадачи 333"));

        System.out.println("Create task1: " + task1);
        System.out.println("Create task2: " + task2);
        System.out.println("Create epic1: " + epic1);
        System.out.println("Create epic2: " + epic2);
        System.out.println("Create subTask1: " + subTask1);
        System.out.println("Create subTask2: " + subTask2);
        System.out.println("Create subTask3: " + subTask3);
        System.out.println("_________________________________________________________________________________________");

        task1.setStatus(DONE);
        task2.setStatus(IN_PROGRESS);
        subTask1.setStatus(DONE);
        subTask2.setStatus(IN_PROGRESS);
        subTask3.setStatus(DONE);

        taskManager.update(task1);
        taskManager.update(task2);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);

        System.out.println("Update task1: " + task1);
        System.out.println("Update task2: " + task2);
        System.out.println("Update epic1: " + epic1);
        System.out.println("Update epic2: " + epic2);
        System.out.println("Update subTask1: " + subTask1);
        System.out.println("Update subTask2: " + subTask2);
        System.out.println("Update subTask3: " + subTask3);
        System.out.println("_________________________________________________________________________________________");

        taskManager.delete(task1.getId());
        taskManager.deleteEpic(epic2.getId());
        System.out.println("Оставшиеся задачи (должна быть одна под номером 2):" + taskManager.getAllTasks());
        System.out.println("Оставшиеся эпики (должен быть один под номером 3):" + taskManager.getAllEpics());
        System.out.println("Оставшиеся подзадачи (должна быть одна под номером 5):" + taskManager.getAllSubTasks());
    }
}