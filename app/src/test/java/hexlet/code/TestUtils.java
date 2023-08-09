package hexlet.code;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Role;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TestUtils {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    public void setUp() {
        persistMockTask();
    }

    public TaskDto createTaskDto() {
        TaskDto taskDto = new TaskDto();
        taskDto.setName("Clean cache");
        taskDto.setDescription("Due by 04.05");
        taskStatusRepository.save(new TaskStatus("Postponed"));
        taskDto.setTaskStatusId(taskStatusRepository.findTaskStatusByName("Postponed").orElseThrow().getId());
        return taskDto;
    }

    public User persistMockUser() {
        User user = new User();
        user.setEmail("ivanov@mail.com");
        user.setPassword("12345");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public TaskStatus persistMockTaskStatus() {
        return taskStatusRepository.save(
                new TaskStatus("New")
        );
    }

    public Label persistMockLabel() {
        return labelRepository.save(
                new Label("review")
        );
    }

    public void persistMockTask() {
        User user = persistMockUser();
        TaskStatus status = persistMockTaskStatus();
        Label label = persistMockLabel();
        Task task = taskRepository.save(
                Task.builder()
                        .name("Fix bugs")
                        .description("Due by 12.07")
                        .author(user)
                        .taskStatus(status)
                        .labels(Set.of(label))
                        .build()
        );
        label.getTasks().add(task);
        labelRepository.save(label);
        user.getTasksAuthored().add(task);
        userRepository.save(user);
        status.getTasks().add(task);
        taskStatusRepository.save(status);
    }

    public void cleanUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }
}
