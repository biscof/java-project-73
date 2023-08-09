package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.TestUtils;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.JwtService;
import hexlet.code.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
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
@DBUnit(schema = "task_manager")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils testUtils;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BASE_TEST_URL = "/api/users";

    @Test
    void testGetAllUsers() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL))
                .andReturn()
                .getResponse();

        List<User> users = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals(3, users.size());
        assertEquals("Jane", users.get(0).getFirstName());
        assertEquals("dupont@test.com", users.get(1).getEmail());
        assertEquals("Mustermann", users.get(2).getLastName());
    }

    @Test
    void testGetUserByValidId() throws Exception {
        User user = userRepository.findUserByEmail("doe@test.com").orElseThrow();
        String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername("doe@test.com"));

        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL + "/" + user.getId())
                        .header("Authorization", "Bearer " + jwt))
                .andReturn()
                .getResponse();

        User testUser = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals("doe@test.com", testUser.getEmail());
        assertEquals("Jane", testUser.getFirstName());
        assertFalse(response.getContentAsString().contains("$2a$12$8W9aoZQaU6jAP2"));
    }

    @Test
    void testGetUserByInvalidId() throws Exception {
        long invalidId = -1L;
        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL + "/" + invalidId))
                .andReturn()
                .getResponse();

        assertEquals(403, response.getStatus());
    }

    @Test
    void testCreateUserValidData() throws Exception {
        UserDto userDto = new UserDto(
                "Eric", "Johnson", "johnson@test.com", "jUi43#Pn@"
        );

        mockMvc.perform(post(BASE_TEST_URL)
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

        MockHttpServletResponse response = mockMvc.perform(post(BASE_TEST_URL)
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
        User user = userRepository.findUserByEmail("doe@test.com").orElseThrow();
        String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername("doe@test.com"));

        UserDto userDto = new UserDto(
                "Olga", "Ivanova", "ivanova@mail.com", "or&uuTyN<eC"
        );

        mockMvc.perform(put(BASE_TEST_URL + "/" + user.getId())
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        assertEquals("Olga", user.getFirstName());
        assertEquals("Ivanova", user.getLastName());
        assertNotEquals("oW&uTyN<eC", user.getPassword());
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = userRepository.findUserByEmail("mustermann@test.com").orElseThrow();
        String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername("mustermann@test.com"));

        mockMvc.perform(delete(BASE_TEST_URL + "/" + user.getId())
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        assertTrue(userRepository.findUserByEmail("mustermann@test.com").isEmpty());
    }

    @Test
    @WithMockUser(username = "ivanov@mail.com", password = "12345")
    void testDeleteUserWithTasks() throws Exception {
        testUtils.setUp();
        User user = userRepository.findUserByEmail("ivanov@mail.com").orElseThrow();

        MockHttpServletResponse response = mockMvc.perform(delete(BASE_TEST_URL + "/" + user.getId()))
                .andReturn()
                .getResponse();

        assertEquals(422, response.getStatus());
        assertTrue(response.getContentAsString().contains("Cannot delete user"));
    }
}
