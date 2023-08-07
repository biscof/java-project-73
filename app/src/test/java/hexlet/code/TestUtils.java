package hexlet.code;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Role;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TestUtils {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    public void setUp() {
        User user1 = User.builder().firstName("John").lastName("Smith").email("smith@mail.com")
                .password("12345").role(Role.USER).tasksAuthored(new ArrayList<>()).tasksToDo(new ArrayList<>())
                .build();

        User user2 = User.builder().firstName("Ivan").lastName("Smirnov").email("smirnov@mail.com")
                .password("qwerty").role(Role.USER).tasksAuthored(new ArrayList<>()).tasksToDo(new ArrayList<>())
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        TaskStatus status1 = new TaskStatus("New", new ArrayList<>());
        TaskStatus status2 = new TaskStatus("Completed", new ArrayList<>());
        taskStatusRepository.save(status1);
        taskStatusRepository.save(status2);

        Task task1 = Task.builder().name("Fix bugs").description("Due by 12.07").author(user1).taskStatus(status2)
                .build();

        Task task2 = Task.builder().name("Clean up code").author(user2).taskStatus(status1)
                .build();
        taskRepository.save(task1);
        taskRepository.save(task2);
    }

    public TaskDto createTaskDto() {
        TaskDto taskDto = new TaskDto();
        taskDto.setName("Clean cache");
        taskDto.setDescription("Due by 04.05");
        taskDto.setTaskStatusId(taskStatusRepository.findTaskStatusByName("Completed").orElseThrow().getId());
        return taskDto;
    }
}
