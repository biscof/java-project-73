package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserResponseDto;
import hexlet.code.exception.DeletionException;
import hexlet.code.exception.UserNotFoundException;
import hexlet.code.model.User;
import hexlet.code.model.Role;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
        return convertUserToResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserServiceImpl::convertUserToResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto createUser(UserDto userDto) {
        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.USER)
                .tasksAuthored(new ArrayList<>())
                .tasksToDo(new ArrayList<>())
                .build();
        return convertUserToResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return convertUserToResponseDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        boolean hasNoAssociatedTasks = user.get().getTasksToDo().isEmpty() && user.get().getTasksAuthored().isEmpty();

        if (hasNoAssociatedTasks) {
            userRepository.deleteById(id);
        } else {
            throw new DeletionException("Cannot delete user because there are tasks associated with this user.");
        }
    }

    private static UserResponseDto convertUserToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
