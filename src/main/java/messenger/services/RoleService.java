package messenger.services;

import messenger.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import messenger.models.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Set<Role> getUserRole() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("USER"));
        return roles;
    }
}
