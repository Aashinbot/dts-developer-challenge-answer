package uk.gov.hmcts.reform.dev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public Task create(Task task) {
        task.setCreatedDate(LocalDateTime.now());
        return repository.save(task);
    }

    public Optional<Task> get(int id) {
        return repository.findById(id);
    }

    public List<Task> getAll() {
        return repository.findAll();
    }

    public Optional<Task> updateStatus(int id, String status) {
        return repository.findById(id).map(task -> {
            task.setStatus(status);
            return repository.save(task);
        });
    }

    public boolean delete(int id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}