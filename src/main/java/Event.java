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
        return String.format("[%s] [%s] %s", this.getIcon(), this.isDone, this.description);
    }
}