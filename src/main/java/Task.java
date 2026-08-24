public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    public void markAsDone(boolean bool) {
        this.isDone = bool;
    }

    public abstract String getIcon();
    public abstract String toString();
    public abstract String toFileString();
}
