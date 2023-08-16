package hexlet.code.exception;

public class LabelNotFoundException extends RuntimeException {
    public LabelNotFoundException(Long id) {
        super("No label found with ID " + id + ".");
    }
}
