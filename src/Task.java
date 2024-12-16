import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import data.Status;
import data.Type;

public class Task {   // задача
    private String title;
    private String description;
    private int idOfTask;
    private Status status;
    protected Type type;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(Task newTask) {
        this.title = newTask.title;
        this.description = newTask.description;
        this.status = newTask.status;
        this.type = Type.TASK;
        this.duration = newTask.duration;
        this.startTime = newTask.startTime;
    }

    public Task(String title, String description, Status status, LocalDateTime startTime, int duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }


    public LocalDateTime getEndTime(){
        return startTime.plus(duration);
    }

    public int getIdOfTask() {
        return idOfTask;
    }

    public void setIdOfTask(int idOfTask) {
        this.idOfTask = idOfTask;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                getIdOfTask(),
                type.toString(),
                getTitle(),
                getStatus(),
                getDescription(),
                getStartTime(),
                getDuration().toMinutes());
            }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idOfTask == task.idOfTask &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, idOfTask, status);
    }
}