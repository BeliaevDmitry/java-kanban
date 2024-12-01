import data.Status;
import data.Type;

public class Subtask extends Task {   // подзадача
    private int epicId;

    public Subtask(String title, String description, Status status, int epicID) {
        super(title, description, status);
        if (epicID != this.getIdOfTask()) {
            this.epicId = epicID;
        }
        this.type = Type.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId != this.getIdOfTask()) {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s",
                getIdOfTask(),
                type.toString(),
                getTitle(),
                getStatus(),
                getDescription(),
                getEpicId());
    }
}