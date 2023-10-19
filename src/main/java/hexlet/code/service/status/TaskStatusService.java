package hexlet.code.service.status;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {

    TaskStatus getTaskStatusById(Long id);

    List<TaskStatus> getAllTaskStatuses();

    TaskStatus createTaskStatus(TaskStatusDto taskStatusDto);

    TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto);

    void deleteTaskStatus(Long id);
}
