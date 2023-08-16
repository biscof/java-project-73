package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank(message = "Task name must contain at least one character.")
    private String name;

    private String description;

    @NotNull(message = "Status field can't be empty.")
    private Long taskStatusId;

    private Long executorId;

    private Set<Long> labelIds;
}
