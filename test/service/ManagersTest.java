package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void getDefault_ShouldInitTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Вернулся null, переделывайте!");
        assertInstanceOf(TaskManager.class, taskManager);
    }

    @Test
    public void getDefaultHistory_ShouldInitHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Вернулся null, переделывайте!");
        assertInstanceOf(HistoryManager.class, historyManager);
    }
}