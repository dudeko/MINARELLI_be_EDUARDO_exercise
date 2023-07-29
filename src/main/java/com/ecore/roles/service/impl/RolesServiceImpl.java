package com.ecore.roles.service.impl;

import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.MembershipsService;
import com.ecore.roles.service.RolesService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class RolesServiceImpl implements RolesService {

    private final RoleRepository roleRepository;
    private final MembershipsService membershipsService;

    @Autowired
    public RolesServiceImpl(
            RoleRepository roleRepository,
            MembershipsService membershipsService) {
        this.roleRepository = roleRepository;
        this.membershipsService = membershipsService;
    }

    @Override
    public Role createRole(@NonNull Role role) {
        validateRoleWithSameNameDoesNotExist(role.getName());
        return roleRepository.save(role);
    }

    private void validateRoleWithSameNameDoesNotExist(String name) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new ResourceExistsException(Role.class);
        }
    }

    @Override
    public Role getRole(@NonNull UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, roleId));
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRole(@NonNull UUID userId, @NonNull UUID teamId) {
        return membershipsService.findByUserIdAndTeamId(userId, teamId)
                .map(Membership::getRole)
                .orElse(null);
    }
}
