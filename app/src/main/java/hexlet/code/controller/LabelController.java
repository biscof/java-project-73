package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.exception.DeletionException;
import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.service.LabelServiceImpl;
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
@RequestMapping("${base-url}/labels")
public class LabelController {

    @Autowired
    private LabelServiceImpl labelService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getLabelById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(labelService.getLabelById(id));
        } catch (LabelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "")
    public List<Label> getAllLabels() {
        return labelService.getAllLabeles();
    }

    @PostMapping(path = "")
    public ResponseEntity<Object> createLabel(
            @Validated @RequestBody LabelDto labelDto,
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
                .body(labelService.createLabel(labelDto));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateLabel(
            @PathVariable Long id,
            @Validated @RequestBody LabelDto labelDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.getFieldError("name") == null) {
            return ResponseEntity.ok().body(labelService.updateLabel(id, labelDto));
        }
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(bindingResult.getFieldError("name"));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteLabel(@PathVariable Long id) {
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
