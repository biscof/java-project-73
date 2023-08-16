package hexlet.code.controller;

import hexlet.code.dto.LoginDto;
import hexlet.code.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${base-url}")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "Sign in")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully logged in", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestBody LoginDto request
    ) {
        try {
            return ResponseEntity.ok(loginService.login(request));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials provided.");
        }
    }
}
