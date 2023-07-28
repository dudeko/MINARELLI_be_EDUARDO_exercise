package com.ecore.roles.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class Team {

    @Id
    @JsonProperty
    private UUID id;

    @JsonProperty
    private String name;

    @JsonProperty
    private UUID teamLeadId;

    @JsonProperty
    private List<UUID> teamMemberIds;

    public boolean hasMember(UUID userId) {
        return Stream.concat(getTeamMemberIds().stream(), Stream.of(getTeamLeadId()))
                .collect(Collectors.toList())
                .contains(userId);
    }

    public boolean doesNotHaveMember(UUID userId) {
        return !this.hasMember(userId);
    }
}
