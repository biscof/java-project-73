package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "First name must contain at least one character.")
    private String firstName;

    @NotBlank(message = "Last name must contain at least one character.")
    private String lastName;

    @NotBlank(message = "Email can't be empty.")
    @Email(message = "Invalid email.")
    private String email;

    @NotBlank(message = "Password can't be empty.")
    @Size(min = 3, message = "Password must be at least three characters long.")
    private String password;
}
