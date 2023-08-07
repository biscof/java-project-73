package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exception.DeletionException;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(
                () -> new TaskStatusNotFoundException(id)
        );
        return taskStatus;
    }

    @Override
    public List<TaskStatus> getAllTaskStatuses() {
        return (List<TaskStatus>) taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus createTaskStatus(TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus(
                taskStatusDto.getName(),
                new ArrayList<>()
        );
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(
                () -> new TaskStatusNotFoundException(id)
        );
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public void deleteTaskStatus(Long id) {
        Optional<TaskStatus> taskStatus = taskStatusRepository.findById(id);

        if (taskStatus.isEmpty()) {
            throw new TaskStatusNotFoundException(id);
        }

        boolean hasNoAssociatedTasks = taskStatus.get().getTasks().isEmpty();

        if (hasNoAssociatedTasks) {
            taskStatusRepository.deleteById(id);
        } else {
            throw new DeletionException("Cannot delete task status because there are tasks associated with it.");
        }
    }
}
