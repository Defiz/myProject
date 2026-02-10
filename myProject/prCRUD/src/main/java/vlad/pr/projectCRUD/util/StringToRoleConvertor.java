package vlad.pr.projectCRUD.util;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import vlad.pr.projectCRUD.model.Role;
import vlad.pr.projectCRUD.service.RoleService;

@AllArgsConstructor
@Component
public class StringToRoleConvertor implements Converter<String, Role> {
    private final RoleService roleService;

    @Override
    public Role convert(String source) {
        return roleService.getRoleByName(source);
    }
}
