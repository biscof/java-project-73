package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private TaskStatusRepository taskStatusRepository;
    private UserRepository userRepository;

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
    }

    @Override
    public List<Task> getAllTasks() {
        return (List<Task>) taskRepository.findAll();
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        Task task = Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .author(getUserFromSecurityContext())
                .taskStatus(getTaskStatus(taskDto))
                .build();

        Optional<User> executor = userRepository.findUserById(taskDto.getExecutorId());
        executor.ifPresent(task::setExecutor);

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(getTaskStatus(taskDto));

        Optional<User> executor = userRepository.findUserById(taskDto.getExecutorId());
        executor.ifPresent(task::setExecutor);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        if (taskRepository.findById(id).isPresent()) {
            taskRepository.deleteById(id);
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    private User getUserFromSecurityContext() {
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
        return userRepository.findUserByEmail(principal.getUsername()).orElseThrow();
    }

    private TaskStatus getTaskStatus(TaskDto taskDto) {
        return taskStatusRepository
                .findTaskStatusById(taskDto.getTaskStatusId())
                .orElseThrow(() -> new TaskNotFoundException(taskDto.getTaskStatusId()));
    }
}
