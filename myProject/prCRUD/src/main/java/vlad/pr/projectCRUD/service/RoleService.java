package vlad.pr.projectCRUD.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vlad.pr.projectCRUD.model.Role;
import vlad.pr.projectCRUD.repository.RoleRepository;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(String role) {
        return roleRepository.findByRole(role);
    }
}
