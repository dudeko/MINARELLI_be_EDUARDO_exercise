package com.ecore.roles.api;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.utils.RestAssuredHelper;
import com.ecore.roles.web.dto.TeamDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.ecore.roles.utils.MockUtils.mockGetTeamById;
import static com.ecore.roles.utils.MockUtils.mockGetTeams;
import static com.ecore.roles.utils.RestAssuredHelper.getTeam;
import static com.ecore.roles.utils.RestAssuredHelper.getTeams;
import static com.ecore.roles.utils.TestData.*;
import static com.ecore.roles.web.dto.TeamDto.fromModel;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeamsApiTest {

    private final RestTemplate restTemplate;
    private final RoleRepository roleRepository;

    private MockRestServiceServer mockServer;

    @LocalServerPort
    private int port;

    @Autowired
    public TeamsApiTest(RestTemplate restTemplate, RoleRepository roleRepository) {
        this.restTemplate = restTemplate;
        this.roleRepository = roleRepository;
    }

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        RestAssuredHelper.setUp(port);
    }

    @Test
    void shouldGetAllTeams() {
        List<Team> expectedTeamList = List.of(ORDINARY_CORAL_LYNX_TEAM());
        mockGetTeams(mockServer, expectedTeamList);
        TeamDto[] actualTeams = getTeams()
                .statusCode(200)
                .extract()
                .as(TeamDto[].class);

        assertThat(actualTeams).hasSize(1);
        assertThat(actualTeams[0].getId()).isNotNull();
        assertEquals(actualTeams[0], fromModel(expectedTeamList.get(0)));
    }

    @Test
    void shouldGetAllTeamsButReturnsEmptyList() {
        mockGetTeams(mockServer, List.of());
        TeamDto[] actualTeams = getTeams()
                .statusCode(200)
                .extract().as(TeamDto[].class);

        assertThat(actualTeams).isEmpty();
    }

    @Test
    void shouldGetATeam() {
        Team expectedTeam = ORDINARY_CORAL_LYNX_TEAM();
        mockGetTeamById(mockServer, ORDINARY_CORAL_LYNX_TEAM_UUID, expectedTeam);
        TeamDto actualTeam = getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID)
                .statusCode(200)
                .extract()
                .as(TeamDto.class);

        assertEquals(actualTeam.getId(), expectedTeam.getId());
    }

    @Test
    void shouldFailToGetATeamWhenItDoesNotExist() {
        mockGetTeamById(mockServer, UUID_1, null);
        getTeam(UUID_1)
                .validate(404, format("Team %s not found", UUID_1));
    }
}
