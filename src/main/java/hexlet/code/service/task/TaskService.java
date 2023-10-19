package hexlet.code.service.task;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;
import com.querydsl.core.types.Predicate;

public interface TaskService {

    Task getTaskById(Long id);

    List<Task> getTasks(Predicate predicate);

    Task createTask(TaskDto taskDto);

    Task updateTask(Long id, TaskDto taskDto);

    void deleteTask(Long id);
}
