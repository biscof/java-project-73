package hexlet.code.service.user;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto createUser(UserDto userDto);

    UserResponseDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
