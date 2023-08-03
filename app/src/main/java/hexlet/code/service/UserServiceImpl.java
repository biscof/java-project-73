package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserResponseDto;
import hexlet.code.exception.UserNotFoundException;
import hexlet.code.model.User;
import hexlet.code.model.Role;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return convertUserToDto(userRepository.save(user));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = (List<User>) userRepository.findAll();
        return users.stream()
                .map(UserServiceImpl::convertUserToDto)
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
                .build();
        userRepository.save(user);
        return convertUserToDto(userRepository.save(user));
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
        return convertUserToDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    private static UserResponseDto convertUserToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
