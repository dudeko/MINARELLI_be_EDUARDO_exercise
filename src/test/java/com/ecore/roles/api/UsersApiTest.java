package com.ecore.roles.api;

import com.ecore.roles.client.model.User;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.utils.RestAssuredHelper;
import com.ecore.roles.web.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.ecore.roles.utils.MockUtils.mockGetUserById;
import static com.ecore.roles.utils.MockUtils.mockGetUsers;
import static com.ecore.roles.utils.RestAssuredHelper.getUser;
import static com.ecore.roles.utils.RestAssuredHelper.getUsers;
import static com.ecore.roles.utils.TestData.*;
import static com.ecore.roles.web.dto.UserDto.fromModel;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersApiTest {

    private final RestTemplate restTemplate;
    private final RoleRepository roleRepository;

    private MockRestServiceServer mockServer;

    @LocalServerPort
    private int port;

    @Autowired
    public UsersApiTest(RestTemplate restTemplate, RoleRepository roleRepository) {
        this.restTemplate = restTemplate;
        this.roleRepository = roleRepository;
    }

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        RestAssuredHelper.setUp(port);
    }

    @Test
    void shouldGetAllUsers() {
        List<User> expectedUserList = List.of(GIANNI_USER());
        mockGetUsers(mockServer, expectedUserList);
        UserDto[] actualUsers = getUsers()
                .statusCode(200)
                .extract()
                .as(UserDto[].class);

        assertThat(actualUsers).hasSize(1);
        assertThat(actualUsers[0].getId()).isNotNull();
        assertEquals(actualUsers[0], fromModel(expectedUserList.get(0)));
    }

    @Test
    void shouldGetAllUsersButReturnsEmptyList() {
        mockGetUsers(mockServer, List.of());
        UserDto[] actualUsers = getUsers()
                .statusCode(200)
                .extract().as(UserDto[].class);

        assertThat(actualUsers).isEmpty();
    }

    @Test
    void shouldGetAUser() {
        User expectedUser = GIANNI_USER();
        mockGetUserById(mockServer, GIANNI_USER_UUID, expectedUser);
        UserDto actualUser = getUser(GIANNI_USER_UUID)
                .statusCode(200)
                .extract()
                .as(UserDto.class);

        assertEquals(actualUser.getId(), expectedUser.getId());
    }

    @Test
    void shouldFailToGetAUserWhenItDoesNotExist() {
        mockGetUserById(mockServer, UUID_1, null);
        getUser(UUID_1)
                .validate(404, format("User %s not found", UUID_1));
    }
}
