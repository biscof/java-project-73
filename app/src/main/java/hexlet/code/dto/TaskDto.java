package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @Size(min = 1, message = "Task name must contain at least one character.")
    private String name;

    private String description;

    @NotNull(message = "Status field can't be empty.")
    private Long taskStatusId;

    private Long executorId;
}
