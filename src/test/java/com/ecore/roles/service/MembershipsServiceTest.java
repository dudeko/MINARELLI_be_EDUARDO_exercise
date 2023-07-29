package com.ecore.roles.service;

import com.ecore.roles.exception.InvalidArgumentException;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.impl.MembershipsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ecore.roles.utils.TestData.DEFAULT_MEMBERSHIP;
import static com.ecore.roles.utils.TestData.DEVELOPER_ROLE;
import static com.ecore.roles.utils.TestData.GIANNI_USER;
import static com.ecore.roles.utils.TestData.GIANNI_USER_UUID;
import static com.ecore.roles.utils.TestData.ORDINARY_CORAL_LYNX_TEAM;
import static com.ecore.roles.utils.TestData.ORDINARY_CORAL_LYNX_TEAM_UUID;
import static com.ecore.roles.utils.TestData.UUID_1;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipsServiceTest {

    @InjectMocks
    private MembershipsServiceImpl membershipsService;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UsersService usersService;
    @Mock
    private TeamsService teamsService;

    @Test
    void shouldCreateMembership() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(roleRepository.findById(expectedMembership.getRole().getId()))
                .thenReturn(Optional.ofNullable(DEVELOPER_ROLE()));
        when(membershipRepository.findByUserIdAndTeamId(expectedMembership.getUserId(),
                expectedMembership.getTeamId()))
                        .thenReturn(Optional.empty());
        when(membershipRepository
                .save(expectedMembership))
                        .thenReturn(expectedMembership);
        when(usersService
                .getUser(expectedMembership.getUserId()))
                        .thenReturn(GIANNI_USER());
        when(teamsService
                .getTeam(expectedMembership.getTeamId()))
                        .thenReturn(ORDINARY_CORAL_LYNX_TEAM());

        Membership actualMembership = membershipsService.assignRoleToMembership(expectedMembership);

        assertNotNull(actualMembership);
        assertEquals(actualMembership, expectedMembership);
        verify(roleRepository).findById(expectedMembership.getRole().getId());
    }

    @Test
    void shouldFailToCreateMembershipWhenMembershipsIsNull() {
        assertThrows(NullPointerException.class,
                () -> membershipsService.assignRoleToMembership(null));
    }

    @Test
    void shouldFailToCreateMembershipWhenItExists() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(membershipRepository.findByUserIdAndTeamId(expectedMembership.getUserId(),
                expectedMembership.getTeamId()))
                        .thenReturn(Optional.of(expectedMembership));

        ResourceExistsException exception = assertThrows(ResourceExistsException.class,
                () -> membershipsService.assignRoleToMembership(expectedMembership));

        assertEquals("Membership already exists", exception.getMessage());
        verify(roleRepository, times(0)).getById(any());
        verify(usersService, times(0)).getUser(any());
        verify(teamsService, times(0)).getTeam(any());
    }

    @Test
    void shouldFailToCreateMembershipWhenItHasInvalidRole() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setRole(null);

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
                () -> membershipsService.assignRoleToMembership(expectedMembership));

        assertEquals("Invalid 'Role' object", exception.getMessage());
        verify(membershipRepository, times(0)).findByUserIdAndTeamId(any(), any());
        verify(roleRepository, times(0)).getById(any());
        verify(usersService, times(0)).getUser(any());
        verify(teamsService, times(0)).getTeam(any());
    }

    @Test
    void shouldFailToCreateMembershipWhenItHasInvalidTeam() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setTeamId(UUID_1);

        when(usersService
                .getUser(expectedMembership.getUserId()))
                        .thenReturn(GIANNI_USER());
        when(teamsService.getTeam(expectedMembership.getTeamId()))
                .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> membershipsService.assignRoleToMembership(expectedMembership));

        assertEquals(format("Team %s not found", expectedMembership.getTeamId()), exception.getMessage());
        verify(roleRepository, times(0)).getById(any());
        verify(membershipRepository, times(0)).save(any());
    }

    @Test
    void shouldFailToCreateMembershipWhenItHasInvalidUser() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setUserId(UUID_1);

        when(usersService
                .getUser(expectedMembership.getUserId()))
                        .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> membershipsService.assignRoleToMembership(expectedMembership));

        assertEquals(format("User %s not found", expectedMembership.getUserId()), exception.getMessage());
        verify(roleRepository, times(0)).getById(any());
        verify(membershipRepository, times(0)).save(any());
    }

    @Test
    void shouldFailToGetMembershipsWhenRoleIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> membershipsService.getMemberships(null));
    }

    @Test
    void shouldFindMembershipByUserIdAndTeamId() {
        when(teamsService
                .getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID))
                        .thenReturn(ORDINARY_CORAL_LYNX_TEAM());
        when(usersService
                .getUser(GIANNI_USER_UUID))
                        .thenReturn(GIANNI_USER());

        membershipsService.findByUserIdAndTeamId(GIANNI_USER_UUID, ORDINARY_CORAL_LYNX_TEAM_UUID);

        verify(membershipRepository, times(1)).findByUserIdAndTeamId(GIANNI_USER_UUID,
                ORDINARY_CORAL_LYNX_TEAM_UUID);
    }

    @Test
    void shouldFailToFindMembershipByUserIdAndTeamIdWhenNoMembershipIsAssociatedToBoth() {
        when(teamsService
                .getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID))
                        .thenReturn(ORDINARY_CORAL_LYNX_TEAM());
        when(usersService
                .getUser(GIANNI_USER_UUID))
                        .thenReturn(GIANNI_USER());
        when(membershipRepository
                .findByUserIdAndTeamId(GIANNI_USER_UUID, ORDINARY_CORAL_LYNX_TEAM_UUID))
                        .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> membershipsService.findByUserIdAndTeamId(GIANNI_USER_UUID,
                        ORDINARY_CORAL_LYNX_TEAM_UUID));

        assertEquals("Membership not found", exception.getMessage());
    }

    @Test
    void shouldFailToFindMembershipWhenUserIdDoesNotExist() {
        when(teamsService
                .getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID))
                        .thenReturn(ORDINARY_CORAL_LYNX_TEAM());
        when(usersService
                .getUser(UUID_1))
                        .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> membershipsService.findByUserIdAndTeamId(UUID_1, ORDINARY_CORAL_LYNX_TEAM_UUID));

        assertEquals(format("User %s not found", UUID_1), exception.getMessage());
        verify(membershipRepository, times(0)).findByUserIdAndTeamId(UUID_1, ORDINARY_CORAL_LYNX_TEAM_UUID);
    }

    @Test
    void shouldFailToFindMembershipWhenTeamIdDoesNotExist() {
        when(teamsService
                .getTeam(UUID_1))
                        .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> membershipsService.findByUserIdAndTeamId(GIANNI_USER_UUID, UUID_1));

        assertEquals(format("Team %s not found", UUID_1), exception.getMessage());
        verify(membershipRepository, times(0)).findByUserIdAndTeamId(GIANNI_USER_UUID, UUID_1);
    }

}
