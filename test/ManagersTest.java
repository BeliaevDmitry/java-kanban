import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager manager1 = Managers.getDefault();
        manager1.getAllTasks();
        assertNotNull(manager1.getAllTasks());
    }
}