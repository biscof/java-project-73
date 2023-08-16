package hexlet.code.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InvalidDataException extends RuntimeException {

    private List<String> messages;

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(List<String> messages) {
        this.messages = messages;
    }
}
