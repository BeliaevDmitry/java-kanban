import java.util.ArrayList;

import data.Status;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return new ArrayList<> (subtasksIds);
    }

    public void addSubtaskId(int idOfSubtask) {
            if (idOfSubtask != this.getIdOfTask()) { // Проверка на добавление самого себя
            subtasksIds.add(idOfSubtask);
        }
    }

    public void removeSubtaskId(Integer idOfSubtask) {
        subtasksIds.remove(idOfSubtask);
    }
}
