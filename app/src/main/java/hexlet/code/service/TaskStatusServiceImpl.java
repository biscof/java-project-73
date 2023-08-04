package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(
                () -> new TaskStatusNotFoundException(id)
        );
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public List<TaskStatus> getAllTaskStatuses() {
        return (List<TaskStatus>) taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus createTaskStatus(TaskStatusDto taskStatusDto) {
        return taskStatusRepository.save(new TaskStatus(taskStatusDto.getName()));
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
        if (taskStatusRepository.findById(id).isPresent()) {
            taskStatusRepository.deleteById(id);
        } else {
            throw new TaskStatusNotFoundException(id);
        }
    }
}
