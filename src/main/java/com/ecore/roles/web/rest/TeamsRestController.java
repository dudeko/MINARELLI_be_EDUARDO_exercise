package com.ecore.roles.web.rest;

import com.ecore.roles.service.TeamsService;
import com.ecore.roles.web.TeamsApi;
import com.ecore.roles.web.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.ecore.roles.web.dto.TeamDto.fromModel;
import static com.ecore.roles.web.dto.TeamDto.fromModelList;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/teams")
public class TeamsRestController implements TeamsApi {

    private final TeamsService teamsService;

    @Override
    @GetMapping(
            produces = {"application/json"})
    public ResponseEntity<List<TeamDto>> getTeams() {
        return ResponseEntity
                .status(200)
                .body(fromModelList(teamsService.getTeams()));
    }

    @Override
    @GetMapping(
            path = "/{teamId}",
            produces = {"application/json"})
    public ResponseEntity<TeamDto> getTeam(
            @PathVariable UUID teamId) {
        return ResponseEntity
                .status(200)
                .body(fromModel(teamsService.getTeam(teamId)));
    }

}
