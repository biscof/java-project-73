package hexlet.code.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("No user found with ID " + id + ".");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
