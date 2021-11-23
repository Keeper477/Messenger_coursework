package messenger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import messenger.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByFirstName(String first_name);
    List<User> findByLastName(String last_name);
    Optional<User> findById(Long Id);
}