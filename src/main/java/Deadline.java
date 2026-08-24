public class Deadline extends Task{
    protected String by;
    
    public Deadline(String description, String by) {
        this.by = by;
        super(description);
    }

    @Override
    public String getIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (by: %s)", this.getIcon(), this.getStatusIcon(), this.description, this.by);
    }

    @Override
    public String toFileString() {
        return String.format("D | %d | %s | %s", this.isDone ? 1 : 0, this.description, this.by);
    }
}
