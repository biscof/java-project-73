package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.TestUtils;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
@DBUnit(schema = "task_manager")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils testUtils;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BASE_TEST_URL = "/api/tasks";

    @BeforeEach
    public void setUp() {
        testUtils.setUp();
    }

    @Test
    void testGetAllTasks() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL))
                .andReturn()
                .getResponse();

        List<Task> tasks = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals(2, tasks.size());
//        assertEquals("Fix bugs", tasks.get(0).getName());
    }

    @Test
    void testGetTaskByValidId() throws Exception {
        Task expectedTask = taskRepository.findTaskByName("Fix bugs").orElseThrow();

        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL + "/" + expectedTask.getId()))
                .andReturn()
                .getResponse();

        Task actualTask = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals("Fix bugs", actualTask.getName());
        assertEquals("smith@mail.com", actualTask.getAuthor().getEmail());
        assertTrue(response.getContentAsString().contains("\"name\":\"Completed\""));
    }

    @Test
    void testGetTaskByInvalidId() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL + "/9"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser(username = "smirnov@mail.com", password = "qwerty")
    void testCreateTaskValidData() throws Exception {
        TaskDto taskDto = testUtils.createTaskDto();

        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().isCreated());

        Optional<Task> testTask = taskRepository.findTaskByName(taskDto.getName());

        assertTrue(testTask.isPresent());
    }

    @Test
    @WithMockUser
    void testCreateTaskInvalidData() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setName("Go shopping");

        MockHttpServletResponse response = mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(taskDto)))
                .andReturn()
                .getResponse();

        assertEquals(422, response.getStatus());
        assertTrue(response.getContentAsString().contains("Status field can't be empty."));
    }

    @Test
    void testCreateTaskUnauthenticated() throws Exception {
        TaskDto taskDto = testUtils.createTaskDto();

        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testUpdateTask() throws Exception {
        Task task = taskRepository.findTaskByName("Fix bugs").orElseThrow();

        TaskDto taskDto = testUtils.createTaskDto();

        mockMvc.perform(put(BASE_TEST_URL + "/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().isOk());

        assertEquals("Due by 04.05", task.getDescription());
        assertEquals("Clean cache", task.getName());
    }

    @Test
    @WithMockUser(username = "smirnov@mail.com", password = "qwerty", roles = "USER")
    void testDeleteTaskByAuthor() throws Exception {
        Task task = taskRepository.findTaskByName("Clean up code").orElseThrow();
        User author = userRepository.findUserByEmail("smirnov@mail.com").orElseThrow();
        TaskStatus taskStatus = task.getTaskStatus();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + task.getId()))
                .andExpect(status().isOk());

        assertTrue(taskRepository.findTaskByName("Clean up code").isEmpty());
        assertFalse(taskStatus.getTasks().contains(task));
        assertFalse(author.getTasksAuthored().contains(task));

    }

    @Test
    @WithMockUser
    void testDeleteTaskAuthenticatedNotAuthor() throws Exception {
        Task task = taskRepository.findTaskByName("Fix bugs").orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + task.getId()))
                .andExpect(status().is(403));
    }

    @Test
    void testDeleteTaskUnauthenticated() throws Exception {
        Task task = taskRepository.findTaskByName("Fix bugs").orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + task.getId()))
                .andExpect(status().is(403));
    }
}
