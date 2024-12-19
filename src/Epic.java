import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import data.Status;
import data.Type;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        this.type = Type.EPIC;
    }

    public Epic(String title, String description, Status status, LocalDateTime startTime, int duration) {
        super(title, description, status, startTime, duration);
        this.type = Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (endTime == null) {
            return getStartTime();
        } else {
            return endTime;
        }
    }

    public ArrayList<Integer> getSubtasksIds() {
        return new ArrayList<>(subtasksIds);
    }

    public void addSubtaskId(int idOfSubtask) {
        if (idOfSubtask != this.getIdOfTask()) { // Проверка на добавление самого себя
            subtasksIds.add(idOfSubtask);
        }
    }

    public void removeSubtaskId(Integer idOfSubtask) {
        subtasksIds.remove(idOfSubtask);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void epicStartTimeAndDuration(Subtask subtask) {
        if ((subtask.getStartTime() != null) && (subtask.getDuration() != null)) {
            if (subtask.getStartTime().isBefore(this.getStartTime())) {
                this.setStartTime(subtask.getStartTime());
            }
            if (subtask.getEndTime().isAfter(this.getEndTime())) {
                this.setEndTime(subtask.getEndTime());
            }
            this.getDuration().plus(subtask.getDuration());
        }
    }
}