package mihnayan.restexample.users.dao;

import mihnayan.restexample.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {

    User getUserById(long id);
}
