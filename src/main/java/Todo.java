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
}
