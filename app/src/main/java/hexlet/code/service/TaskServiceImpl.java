package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.exception.UserNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import com.querydsl.core.types.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private TaskStatusRepository taskStatusRepository;
    private UserRepository userRepository;
    private LabelRepository labelRepository;

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
    }

    @Override
    public List<Task> getTasks(Predicate predicate) {
        if (predicate != null) {
            return (List<Task>) taskRepository.findAll(predicate);
        } else {
            return taskRepository.findAll();
        }
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        Task task = Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .author(getUserFromSecurityContext())
                .taskStatus(getTaskStatusFromDto(taskDto))
                .executor(getExecutorFromDto(taskDto))
                .labels(getLabelsFormDto(taskDto))
                .build();

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(getTaskStatusFromDto(taskDto));
        task.setExecutor(getExecutorFromDto(taskDto));
        task.setLabels(getLabelsFormDto(taskDto));

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

    private TaskStatus getTaskStatusFromDto(TaskDto taskDto) {
        return taskStatusRepository
                .findTaskStatusById(taskDto.getTaskStatusId())
                .orElseThrow(() -> new TaskNotFoundException(taskDto.getTaskStatusId()));
    }

    private User getExecutorFromDto(TaskDto taskDto) {
        Long executorId = taskDto.getExecutorId();
        if (executorId != null) {
            return userRepository
                    .findUserById(executorId)
                    .orElseThrow(() -> new UserNotFoundException(executorId));
        } else {
            return null;
        }
    }

    private Set<Label> getLabelsFormDto(TaskDto taskDto) {
        Set<Long> labelIds = taskDto.getLabelIds();
        if (labelIds != null) {
            return labelIds.stream()
                    .map(id -> labelRepository
                            .findLabelById(id)
                            .orElseThrow(() -> new LabelNotFoundException(id))
                    )
                    .collect(Collectors.toSet());
        } else {
            return null;
        }
    }
}
