package uk.gov.hmcts.reform.dev.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.models.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final Map<Integer, Task> taskRepo = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        if (task.getTitle() == null || task.getStatus() == null || task.getCaseNumber() == null) {
            return ResponseEntity.badRequest().build();
        }

        int id = idGenerator.getAndIncrement();
        task.setId(id);
        task.setCreatedDate(LocalDateTime.now());

        taskRepo.put(id, task);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable int id) {
        Task task = taskRepo.get(id);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(new ArrayList<>(taskRepo.values()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable int id, @RequestBody Map<String, String> body) {
        Task task = taskRepo.get(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        task.setStatus(status);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        if (taskRepo.remove(id) != null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
