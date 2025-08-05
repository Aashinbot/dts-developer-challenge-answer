package uk.gov.hmcts.reform.dev;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.controllers.TaskController;
import uk.gov.hmcts.reform.dev.models.Task;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setCaseNumber("CASE-001");
        task.setTitle("Initial Review");
        task.setDescription("Review case files");
        task.setStatus("Open");
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Initial Review"))
                .andExpect(jsonPath("$.caseNumber").value("CASE-001"))
                .andExpect(jsonPath("$.status").value("Open"))
                .andExpect(jsonPath("$.createdDate").exists());
    }

    @Test
    void shouldFailToCreateTaskWithMissingFields() throws Exception {
        Task invalidTask = new Task(); // Missing required fields

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRetrieveTaskById() throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task created = objectMapper.readValue(response, Task.class);

        mockMvc.perform(get("/api/tasks/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Initial Review"));
    }

    @Test
    void shouldReturnNotFoundForInvalidTaskId() throws Exception {
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRetrieveAllTasks() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void shouldUpdateTaskStatus() throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task created = objectMapper.readValue(response, Task.class);

        mockMvc.perform(patch("/api/tasks/" + created.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "In Progress"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("In Progress"));
    }

    @Test
    void shouldFailToUpdateStatusWithEmptyValue() throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task created = objectMapper.readValue(response, Task.class);

        mockMvc.perform(patch("/api/tasks/" + created.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task created = objectMapper.readValue(response, Task.class);

        mockMvc.perform(delete("/api/tasks/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonexistentTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/12345"))
                .andExpect(status().isNotFound());
    }
}
