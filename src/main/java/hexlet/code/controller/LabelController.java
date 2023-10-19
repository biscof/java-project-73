package hexlet.code.controller;

import hexlet.code.controller.utils.ControllerUtils;
import hexlet.code.dto.LabelDto;
import hexlet.code.exception.DeletionException;
import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.service.label.LabelServiceImpl;
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
@RequestMapping("${base-url}/labels")
public class LabelController {

    @Autowired
    private LabelServiceImpl labelService;

    @Operation(summary = "Get a label by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label successfully found",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Label.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Label not found",
            content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getLabelById(
            @Parameter(description = "ID of a label to be searched")
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok().body(labelService.getLabelById(id));
        } catch (LabelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get all labels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Labels found",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Label.class))) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(path = "")
    public List<Label> getAllLabels() {
        return labelService.getAllLabeles();
    }

    @Operation(summary = "Create a new label")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Label successfully created",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Label.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "422", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping(path = "")
    public ResponseEntity<Object> createLabel(
            @Validated @RequestBody LabelDto labelDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ControllerUtils.getErrorMessagesFrom(bindingResult));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(labelService.createLabel(labelDto));
    }

    @Operation(summary = "Update label data by label's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label data successfully updated",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Label.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Label not found", content = @Content),
        @ApiResponse(responseCode = "422", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateLabel(
            @Parameter(description = "ID of a label to be updated") @PathVariable Long id,
            @Validated @RequestBody LabelDto labelDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ControllerUtils.getErrorMessagesFrom(bindingResult));
        }
        try {
            return ResponseEntity.ok(labelService.updateLabel(id, labelDto));
        } catch (LabelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a label by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label deleted",
            content = { @Content(schema = @Schema(implementation = ResponseEntity.class)) }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Label not found",
            content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "422", description = "Label has associated entities",
            content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteLabel(
            @Parameter(description = "ID of a label to be deleted")
            @PathVariable Long id
    ) {
        try {
            labelService.deleteLabel(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e instanceof LabelNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e instanceof DeletionException) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
            }
        }
    }
}
