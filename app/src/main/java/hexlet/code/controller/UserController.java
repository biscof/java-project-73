package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.exception.InvalidDataException;
import hexlet.code.model.User;
import hexlet.code.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${base-url}/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping(path = "/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping(path = "")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Validated @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new InvalidDataException(errorMessages);
        }

        return userService.createUser(userDto);
    }

    @PutMapping(path = "/{id}")
    public User updateUser(
            @PathVariable Long id,
            @Validated @RequestBody UserDto userDto
    ) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}