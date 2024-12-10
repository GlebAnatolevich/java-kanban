package service;

import model.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {  // код для работы с историей просмотров

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void addTask(Task task) {
        if (task != null) {
            removeTask(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void removeTask(int id) {
        removeNode(historyMap.get(id));
    }

    @Override
    public List<Task> getHistory() { return getTasks(); }

    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(tail, task, null);
        tail = newNode;
        historyMap.put(task.getId(), newNode);
        if (oldTail != null) {
            oldTail.setNext(newNode);
        } else {
            head = newNode;
        }
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> curNode = head;
        while (curNode != null) {
            tasks.add(curNode.getData());
            curNode = curNode.getNext();
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.getNext();
            final Node<Task> prev = node.getPrev();
            node.setData(null);

            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node && tail != node) {
                head = next;
                head.setPrev(null);
            } else if (head != node && tail == node) {
                tail = prev;
                tail.setNext(null);
            } else {
                prev.setNext(next);
                next.setPrev(prev);
            }
        }
    }
}