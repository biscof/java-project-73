package hexlet.code.controller.utils;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

public class ControllerUtils {
    public static List<String> getErrorMessagesFrom(BindingResult bindingResult) {
        List<String> errorMessages = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
        }
        return errorMessages;
    }
}
