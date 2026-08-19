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
        return String.format("[%s] [%s] %s", this.getIcon(), this.isDone, this.description);
    }
}
