package hexlet.code.controller;

import hexlet.code.controller.utils.ControllerUtils;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserResponseDto;
import hexlet.code.exception.DeletionException;
import hexlet.code.exception.UserNotFoundException;
import hexlet.code.service.user.UserServiceImpl;
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
@RequestMapping("${base-url}/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @GetMapping(path = "/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "ID of a user to be searched")
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users found",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @GetMapping(path = "")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Create a new user. Sign up")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully created",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)) }),
        @ApiResponse(responseCode = "422", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @PostMapping(path = "")
    public ResponseEntity<Object> createUser(
            @Validated @RequestBody UserDto userDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ControllerUtils.getErrorMessagesFrom(bindingResult));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @Operation(summary = "Update user data by user's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User data successfully updated",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "422", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateUser(
            @Parameter(description = "ID of a user to be updated")
            @PathVariable Long id,
            @Validated @RequestBody UserDto userDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ControllerUtils.getErrorMessagesFrom(bindingResult));
        }
        try {
            return ResponseEntity.ok(userService.updateUser(id, userDto));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "422", description = "User has associated entities",
            content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteUser(
            @Parameter(description = "ID of a user to be deleted")
            @PathVariable Long id
    ) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e instanceof UserNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e instanceof DeletionException) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
            }
        }
    }
}
