package hexlet.code.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/*
Class which hides all the UserDetails stuff of a User
 */
@Data
@Builder
public class UserResponseDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Date createdAt;
}
