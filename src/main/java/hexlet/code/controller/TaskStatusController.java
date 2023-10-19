package hexlet.code.controller;

import hexlet.code.controller.utils.ControllerUtils;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exception.DeletionException;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.status.TaskStatusServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("${base-url}/statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusServiceImpl taskStatusService;

    @Operation(summary = "Get a task status by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status successfully found",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskStatus.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task status not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getTaskStatusById(
            @Parameter(description = "ID of a task status to be searched")
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(taskStatusService.getTaskStatusById(id));
        } catch (TaskStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get all task statuses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task statuses found",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = TaskStatus.class))) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(path = "")
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusService.getAllTaskStatuses();
    }

    @Operation(summary = "Create a new task status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task status successfully created",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = TaskStatus.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "422", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping(path = "")
    public ResponseEntity<Object> createTaskStatus(
            @Validated @RequestBody TaskStatusDto taskStatusDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ControllerUtils.getErrorMessagesFrom(bindingResult));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskStatusService.createTaskStatus(taskStatusDto));
    }

    @Operation(summary = "Update task status data by status' ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status data successfully updated",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = TaskStatus.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task status not found", content = @Content),
        @ApiResponse(responseCode = "422", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateTaskStatus(
            @Parameter(description = "ID of a task status to be updated") @PathVariable Long id,
            @Validated @RequestBody TaskStatusDto taskStatusDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ControllerUtils.getErrorMessagesFrom(bindingResult));
        }
        try {
            return ResponseEntity.ok(taskStatusService.updateTaskStatus(id, taskStatusDto));
        } catch (TaskStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a task status by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status deleted",
            content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task status not found",
            content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "422", description = "Task status has associated entities",
            content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteTaskStatus(
            @Parameter(description = "ID of a task status to be deleted")
            @PathVariable Long id
    ) {
        try {
            taskStatusService.deleteTaskStatus(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e instanceof TaskStatusNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e instanceof DeletionException) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
            }
        }
    }
}
