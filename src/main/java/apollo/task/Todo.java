package apollo.task;

public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    @Override
    public String getIcon() {
        return "T";
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s", this.getIcon(), this.getStatusIcon(), this.description);
    }

    @Override
    public String toFileString() {
        return String.format("T | %d | %s", this.isDone ? 1 : 0, this.description);
    }
}
