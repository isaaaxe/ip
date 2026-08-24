public class Event extends Task {
    protected String from;
    protected String to;

    public Event(String event, String from, String to) {
        this.from = from;
        this.to = to;
        super(event);
    }

    @Override
    public String getIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (from: %s to: %s)", this.getIcon(), this.getStatusIcon(), this.description, this.from, this.to);
    }

    @Override
    public String toFileString() {
        return String.format("E | %d | %s | %s | %s", this.isDone ? 1 : 0, this.description, this.from, this.to);
    }
}