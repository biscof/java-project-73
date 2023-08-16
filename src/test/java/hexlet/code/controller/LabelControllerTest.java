package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.TestUtils;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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
@DataSet({"labels.yml", "users.yml", "statuses.yml"})
@DBUnit(schema = "task_manager")
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils testUtils;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BASE_TEST_URL = "/api/labels";

    @Test
    @WithMockUser
    void testGetAllLabels() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL))
                .andReturn()
                .getResponse();

        Set<Label> labels = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals(3, labels.size());
    }

    @Test
    @WithMockUser
    void testGetLabelByValidId() throws Exception {
        Label expectedLabel = labelRepository.findLabelByName("bug").orElseThrow();

        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL + "/" + expectedLabel.getId()))
                .andReturn()
                .getResponse();

        Label actualLabel = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(200, response.getStatus());
        assertEquals("bug", actualLabel.getName());
    }

    @Test
    @WithMockUser
    void testGetLabelByInvalidId() throws Exception {
        long invalidId = -1L;
        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_TEST_URL + "/" + invalidId))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser
    void testCreateLabelValidData() throws Exception {
        LabelDto labelDto = new LabelDto("review");

        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(labelDto)))
                .andExpect(status().isCreated());

        Label testLabel = labelRepository.findLabelByName(labelDto.getName()).orElseThrow();

        assertNotNull(testLabel);
        assertEquals("review", testLabel.getName());
    }

    @Test
    @WithMockUser
    void testCreateLabelInvalidData() throws Exception {
        LabelDto labelDto = new LabelDto("");

        MockHttpServletResponse response = mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(labelDto)))
                .andReturn()
                .getResponse();

        assertEquals(422, response.getStatus());
        assertTrue(response.getContentAsString().contains("Label name must contain at least one character."));
    }

    @Test
    void testCreateLabelUnauthenticated() throws Exception {
        LabelDto labelDto = new LabelDto("test");

        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(labelDto)))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testUpdateLabel() throws Exception {
        Label label = labelRepository.findLabelByName("bug").orElseThrow();

        LabelDto labelDto = new LabelDto("test");

        mockMvc.perform(put(BASE_TEST_URL + "/" + label.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(labelDto)))
                .andExpect(status().isOk());

        assertEquals("test", label.getName());
        assertEquals("test", labelRepository.findLabelById(label.getId()).orElseThrow().getName());
    }

    @Test
    @WithMockUser
    void testDeleteLabel() throws Exception {
        Label label = labelRepository.findLabelByName("bug").orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + label.getId()))
                .andExpect(status().isOk());

        assertTrue(labelRepository.findLabelByName("bug").isEmpty());
    }

    @Test
    void testDeleteLabelUnauthenticated() throws Exception {
        Label label = labelRepository.findLabelByName("question").orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + label.getId()))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testDeleteLabelWithTasks() throws Exception {
        testUtils.setUp();
        Label label = labelRepository.findLabelByName("review").orElseThrow();

        MockHttpServletResponse response = mockMvc.perform(delete(BASE_TEST_URL + "/" + label.getId()))
                .andReturn()
                .getResponse();

        assertEquals(422, response.getStatus());
        assertTrue(response.getContentAsString().contains("Cannot delete label"));
    }
}
