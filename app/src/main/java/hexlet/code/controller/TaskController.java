package hexlet.code.controller;

import hexlet.code.dto.TaskDto;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.service.TaskServiceImpl;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${base-url}/tasks")
public class TaskController {

    @Autowired
    private TaskServiceImpl taskService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(taskService.getTaskById(id));
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping(path = "")
    public ResponseEntity<Object> createTask(
            @Validated @RequestBody TaskDto taskDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorMessages);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(taskDto));
    }

    @PutMapping(path = "/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @Validated @RequestBody TaskDto taskDto
    ) {
        return taskService.updateTask(id, taskDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
