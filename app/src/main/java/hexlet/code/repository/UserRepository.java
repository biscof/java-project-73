package hexlet.code.repository;

import hexlet.code.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    //todo:
    // - add more specific repository methods
}
