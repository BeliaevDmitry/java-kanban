import data.Status;
public class Subtask extends Task {   // подзадача
    private int epicId;

    public Subtask(String title, String description, Status status, int epicID) {
        super(title, description, status);
        this.epicId = epicID;
    }

    public int getEpicId() {
        return epicId;
    }


}