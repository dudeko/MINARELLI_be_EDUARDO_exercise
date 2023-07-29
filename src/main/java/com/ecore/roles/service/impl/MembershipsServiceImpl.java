package com.ecore.roles.service.impl;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.client.model.User;
import com.ecore.roles.exception.InvalidArgumentException;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.exception.UserIsNotAssignedToMembershipException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.MembershipsService;
import com.ecore.roles.service.TeamsService;
import com.ecore.roles.service.UsersService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class MembershipsServiceImpl implements MembershipsService {

    private final MembershipRepository membershipRepository;
    private final RoleRepository roleRepository;
    private final UsersService usersService;
    private final TeamsService teamsService;

    @Autowired
    public MembershipsServiceImpl(
            MembershipRepository membershipRepository,
            RoleRepository roleRepository,
            UsersService usersService,
            TeamsService teamsService) {
        this.membershipRepository = membershipRepository;
        this.roleRepository = roleRepository;
        this.usersService = usersService;
        this.teamsService = teamsService;
    }

    @Override
    public Membership assignRoleToMembership(@NonNull Membership membership) {
        UUID roleId = getRoleIdAndValidate(membership);
        validate(membership);
        roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, roleId));
        return membershipRepository.save(membership);
    }

    private UUID getRoleIdAndValidate(Membership membership) {
        return ofNullable(membership.getRole()).map(Role::getId)
                .orElseThrow(() -> new InvalidArgumentException(Role.class));
    }

    private void validate(Membership membership) {
        validateMembershipDoesNotExist(membership);
        validateUserExists(membership.getUserId());
        validateTeamExists(membership.getTeamId());
        validateUserIsAssignedToMembership(membership);
    }

    private void validateMembershipDoesNotExist(Membership membership) {
        if (membershipRepository.findByUserIdAndTeamId(membership.getUserId(), membership.getTeamId())
                .isPresent()) {
            throw new ResourceExistsException(Membership.class);
        }
    }

    private void validateUserIsAssignedToMembership(Membership membership) {
        if (teamsService.getTeam(membership.getTeamId())
                .doesNotHaveMemberOrTeamLead(membership.getUserId())) {
            throw new UserIsNotAssignedToMembershipException();
        }
    }

    private void validateTeamExists(UUID teamId) {
        if (ofNullable(teamsService.getTeam(teamId)).isEmpty()) {
            throw new ResourceNotFoundException(Team.class, teamId);
        }
    }

    private void validateUserExists(UUID userId) {
        if (ofNullable(usersService.getUser(userId)).isEmpty()) {
            throw new ResourceNotFoundException(User.class, userId);
        }
    }

    @Override
    public List<Membership> getMemberships(@NonNull UUID roleId) {
        return membershipRepository.findByRoleId(roleId);
    }

    @Override
    public Optional<Membership> findByUserIdAndTeamId(UUID userId, UUID teamId) {
        validateTeamExists(teamId);
        validateUserExists(userId);
        return ofNullable(membershipRepository.findByUserIdAndTeamId(userId, teamId))
                .orElseThrow(() -> new ResourceNotFoundException(Membership.class));
    }
}
