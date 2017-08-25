package mihnayan.restexample.users.dao;

import mihnayan.restexample.users.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
