package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.controller.utils.ControllerUtils;
import hexlet.code.dto.TaskDto;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.service.task.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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

    @Operation(summary = "Get a task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Task.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getTaskById(
            @Parameter(description = "ID of a task to be searched")
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok().body(taskService.getTaskById(id));
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks found",
                content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Task.class))) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @GetMapping(path = "")
    public List<Task> getTasks(
            @Parameter(hidden = true)
            @QuerydslPredicate(root = Task.class) Predicate predicate
    ) {
        return taskService.getTasks(predicate);
    }

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task successfully created",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Task.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "422", description = "Wrong data provided",
                content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @PostMapping(path = "")
    public ResponseEntity<Object> createTask(
            @Validated @RequestBody TaskDto taskDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ControllerUtils.getErrorMessagesFrom(bindingResult));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskDto));
    }

    @Operation(summary = "Update task data by task's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task data successfully updated",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Task.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
        @ApiResponse(responseCode = "422", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateTask(
            @Parameter(description = "ID of a task to be updated")
            @PathVariable Long id,
            @Validated @RequestBody TaskDto taskDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ControllerUtils.getErrorMessagesFrom(bindingResult));
        }
        try {
            return ResponseEntity.ok(taskService.updateTask(id, taskDto));
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteTask(
            @Parameter(description = "ID of a task to be deleted")
            @PathVariable Long id
    ) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
