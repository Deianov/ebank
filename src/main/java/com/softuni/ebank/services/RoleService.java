package com.softuni.ebank.services;

import com.softuni.ebank.entities.Role;
import com.softuni.ebank.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void seedRolesInDb() {
        if (this.roleRepository.count() == 0) {
            Role role = new Role();
            role.setAuthority("USER");
            this.roleRepository.save(role);
        }
    }


}
