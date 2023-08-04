package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
@DataSet("statuses.yml")
@DBUnit(schema = "public")
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testGetAllTaskStatuses() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/statuses"))
                .andReturn()
                .getResponse();

        List<TaskStatus> taskStatuses = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals(3, taskStatuses.size());
        assertEquals("In progress", taskStatuses.get(1).getName());
    }

    @Test
    void testGetTaskStatusByValidId() throws Exception {
        TaskStatus expectedTaskStatus = taskStatusRepository.findTaskStatusByName("Completed").orElseThrow();

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/statuses/" + expectedTaskStatus.getId()))
                .andReturn()
                .getResponse();

        TaskStatus actualTaskStatus = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals("Completed", actualTaskStatus.getName());
    }

    @Test
    void testGetTaskStatusByInvalidId() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/statuses/9"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser
    void testCreateTaskStatusValidData() throws Exception {
        TaskStatusDto taskStatusDto = new TaskStatusDto("Postponed");

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(taskStatusDto)))
                .andExpect(status().isCreated());

        TaskStatus testTaskStatus = taskStatusRepository.findTaskStatusByName(taskStatusDto.getName()).orElseThrow();

        assertNotNull(testTaskStatus);
        assertEquals("Postponed", testTaskStatus.getName());
    }

    @Test
    @WithMockUser
    void testCreateTaskStatusInvalidData() throws Exception {
        TaskStatusDto taskStatusDto = new TaskStatusDto("");

        MockHttpServletResponse response = mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(taskStatusDto)))
                .andReturn()
                .getResponse();

        assertEquals(422, response.getStatus());
        assertTrue(response.getContentAsString().contains("Status name must contain at least one character."));
    }

    @Test
    void testCreateTaskStatusUnauthenticated() throws Exception {
        TaskStatusDto taskStatusDto = new TaskStatusDto("Postponed");

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(taskStatusDto)))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testUpdateTaskStatus() throws Exception {
        TaskStatus taskStatus = taskStatusRepository.findTaskStatusByName("New").orElseThrow();

        TaskStatusDto taskStatusDto = new TaskStatusDto("In testing");

        mockMvc.perform(put("/api/statuses/" + taskStatus.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(taskStatusDto)))
                .andExpect(status().isOk());

        assertEquals("In testing", taskStatus.getName());
    }

    @Test
    @WithMockUser
    void testDeleteTaskStatus() throws Exception {
        TaskStatus taskStatus = taskStatusRepository.findTaskStatusByName("Completed").orElseThrow();

        mockMvc.perform(delete("/api/statuses/" + taskStatus.getId()))
                .andExpect(status().isOk());

        assertTrue(taskStatusRepository.findTaskStatusByName("Completed").isEmpty());
    }

    @Test
    void testDeleteTaskStatusUnauthenticated() throws Exception {
        TaskStatus taskStatus = taskStatusRepository.findTaskStatusByName("Completed").orElseThrow();

        mockMvc.perform(delete("/api/statuses/" + taskStatus.getId()))
                .andExpect(status().is(403));
    }
}
