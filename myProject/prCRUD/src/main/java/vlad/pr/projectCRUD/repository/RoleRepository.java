package vlad.pr.projectCRUD.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vlad.pr.projectCRUD.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String roles);
}
