package com.ecore.roles.utils;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.client.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Log4j2
public class MockUtils {

    public static void mockGetUsers(MockRestServiceServer mockServer, List<User> userList) {
        try {
            mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://test.com/users"))
                    .andExpect(method(GET))
                    .andRespond(
                            withStatus(OK)
                                    .contentType(APPLICATION_JSON)
                                    .body(new ObjectMapper().writeValueAsString(userList)));
        } catch (JsonProcessingException e) {
            log.error(e);
        }
    }

    public static void mockGetUserById(MockRestServiceServer mockServer, UUID userId, User user) {
        try {
            mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://test.com/users/" + userId))
                    .andExpect(method(GET))
                    .andRespond(
                            withStatus(OK)
                                    .contentType(APPLICATION_JSON)
                                    .body(new ObjectMapper().writeValueAsString(user)));
        } catch (JsonProcessingException e) {
            log.error(e);
        }
    }

    public static void mockGetTeams(MockRestServiceServer mockServer, List<Team> teamList) {
        try {
            mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://test.com/teams"))
                    .andExpect(method(GET))
                    .andRespond(
                            withStatus(OK)
                                    .contentType(APPLICATION_JSON)
                                    .body(new ObjectMapper().writeValueAsString(teamList)));
        } catch (JsonProcessingException e) {
            log.error(e);
        }
    }

    public static void mockGetTeamById(MockRestServiceServer mockServer, UUID teamId, Team team) {
        try {
            mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://test.com/teams/" + teamId))
                    .andExpect(method(GET))
                    .andRespond(
                            withStatus(OK)
                                    .contentType(APPLICATION_JSON)
                                    .body(new ObjectMapper().writeValueAsString(team)));
        } catch (JsonProcessingException e) {
            log.error(e);
        }
    }
}
