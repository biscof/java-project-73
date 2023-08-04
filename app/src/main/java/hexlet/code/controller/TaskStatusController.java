package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exception.InvalidDataException;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${base-url}/statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusServiceImpl taskStatusService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getTaskStatusById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(taskStatusService.getTaskStatusById(id));
        } catch (TaskStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "")
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusService.getAllTaskStatuses();
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createTaskStatus(
            @Validated @RequestBody TaskStatusDto taskStatusDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new InvalidDataException(errorMessages);
        }

        return taskStatusService.createTaskStatus(taskStatusDto);
    }

    @PutMapping(path = "/{id}")
    public TaskStatus updateTaskStatus(
            @PathVariable Long id,
            @Validated @RequestBody TaskStatusDto taskStatusDto
    ) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteTaskStatus(@PathVariable Long id) {
        try {
            taskStatusService.deleteTaskStatus(id);
            return ResponseEntity.ok().build();
        } catch (TaskStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
