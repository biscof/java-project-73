package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    Optional<Task> findTaskById(Long id);

    Optional<Task> findTaskByName(String name);
}
