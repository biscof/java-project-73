package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.JwtService;
import hexlet.code.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DBRider
@DataSet("users.yml")
@DBUnit(schema = "public")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testGetAllUsers() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        List<User> users = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals(3, users.size());
        assertEquals("John", users.get(0).getFirstName());
        assertEquals("petrov@test.com", users.get(1).getEmail());
        assertEquals("Fischer", users.get(2).getLastName());
    }

    @Test
    void testGetUserByValidId() throws Exception {
        User user = userRepository.findUserByEmail("smith@test.com").orElseThrow();
        String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername("smith@test.com"));

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + jwt))
                .andReturn()
                .getResponse();

        User testUser = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals("smith@test.com", testUser.getEmail());
        assertEquals("John", testUser.getFirstName());
        assertFalse(response.getContentAsString().contains("$2a$12$8W9aoZQaU6jAP2"));
    }

    @Test
    void testGetUserByInvalidId() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users/9"))
                .andReturn()
                .getResponse();

        assertEquals(403, response.getStatus());
    }

    @Test
    void testCreateUserValidData() throws Exception {
        UserDto userDto = new UserDto(
                "Eric", "Johnson", "johnson@test.com", "jUi43#Pn@"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(userDto)))
                .andExpect(status().isCreated());

        User testUser = userRepository.findUserByEmail(userDto.getEmail()).orElseThrow();

        assertNotNull(testUser);
        assertEquals("Johnson", testUser.getLastName());
        assertNotEquals("jUi43#Pn@", testUser.getPassword());
    }

    @Test
    void testCreateUserInvalidData() throws Exception {
        UserDto userDto = new UserDto(
                "", "Erikson", "@te.c", "jUi43#Pn@"
        );

        MockHttpServletResponse response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(userDto)))
                .andReturn()
                .getResponse();

        assertEquals(422, response.getStatus());
        assertTrue(response.getContentAsString().contains("First name must contain at least one character."));
        assertTrue(response.getContentAsString().contains("Invalid email."));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = userRepository.findUserByEmail("petrov@test.com").orElseThrow();
        String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername("petrov@test.com"));

        UserDto userDto = new UserDto(
                "Yuri", "Petrov", "yuri@test.com", "or&uuTyN<eC"
        );

        mockMvc.perform(put("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        assertEquals("Yuri", user.getFirstName());
        assertEquals("Petrov", user.getLastName());
        assertNotEquals("oW&uTyN<eC", user.getPassword());
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = userRepository.findUserByEmail("fischer@test.com").orElseThrow();
        String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername("fischer@test.com"));

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        assertTrue(userRepository.findUserByEmail("fischer@test.com").isEmpty());
    }
}
