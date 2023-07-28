package com.ecore.roles.web.dto;

import com.ecore.roles.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RoleDto {

    @JsonProperty
    private UUID id;
    @JsonProperty
    @NotBlank
    private String name;

    public static RoleDto fromModel(Role role) {
        if (role == null) {
            return null;
        }
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public static List<RoleDto> fromModelList(List<Role> roles) {
        return roles.stream().map(RoleDto::fromModel).collect(Collectors.toList());
    }

    public Role toModel() {
        return Role.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }

}
