package hexlet.code.exception;

public class TaskStatusNotFoundException extends RuntimeException {
    public TaskStatusNotFoundException(Long id) {
        super("No status found with ID " + id + ".");
    }
}
