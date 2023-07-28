package com.ecore.roles.service;

import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.impl.MembershipsServiceImpl;
import com.ecore.roles.service.impl.RolesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ecore.roles.utils.TestData.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolesServiceTest {

    @InjectMocks
    private RolesServiceImpl rolesService;

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private MembershipsServiceImpl membershipsService;
    @Mock
    private UsersService usersService;
    @Mock
    private TeamsService teamsService;

    @Test
    void shouldCreateRole() {
        Role developerRole = DEVELOPER_ROLE();
        when(roleRepository.save(developerRole)).thenReturn(developerRole);

        Role role = rolesService.createRole(developerRole);

        assertNotNull(role);
        assertEquals(developerRole, role);
    }

    @Test
    void shouldFailToCreateRoleWhenRoleIsNull() {
        assertThrows(NullPointerException.class,
                () -> rolesService.createRole(null));
    }

    @Test
    void shouldReturnRoleWhenRoleIdExists() {
        Role developerRole = DEVELOPER_ROLE();
        when(roleRepository.findById(developerRole.getId())).thenReturn(Optional.of(developerRole));

        Role role = rolesService.getRole(developerRole.getId());

        assertNotNull(role);
        assertEquals(developerRole, role);
    }

    @Test
    void shouldFailToGetRoleWhenRoleIdDoesNotExist() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rolesService.getRole(UUID_1));

        assertEquals(format("Role %s not found", UUID_1), exception.getMessage());
    }

    @Test
    void shouldFailToGetRoleWhenUserIdDoesNotExist() {
        when(teamsService
                .getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID))
                        .thenReturn(ORDINARY_CORAL_LYNX_TEAM());
        when(usersService
                .getUser(UUID_1))
                        .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rolesService.getRole(UUID_1, ORDINARY_CORAL_LYNX_TEAM_UUID));

        assertEquals(format("User %s not found", UUID_1), exception.getMessage());
        verify(membershipsService, times(0)).findByUserIdAndTeamId(any(), any());
    }

    @Test
    void shouldFailToGetRoleWhenTeamIdDoesNotExist() {
        when(teamsService
                .getTeam(UUID_1))
                        .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rolesService.getRole(GIANNI_USER_UUID, UUID_1));

        assertEquals(format("Team %s not found", UUID_1), exception.getMessage());
        verify(membershipsService, times(0)).findByUserIdAndTeamId(any(), any());
    }
}
